/*
 * Copyright (c) Joseph Prichard 2023.
 */

package commands;

import commands.context.CommandContext;
import commands.messaging.MessageSender;
import othello.BoardRenderer;
import services.challenge.Challenge;
import services.challenge.ChallengeManager;
import services.game.GameStorage;
import services.game.exceptions.AlreadyPlayingException;
import services.player.Player;

import java.util.Objects;

import static utils.Logger.LOGGER;

public class ChallengeCommand extends Command {

    private final GameStorage gameStorage;
    private final ChallengeManager challengeManager;

    public ChallengeCommand(GameStorage gameStorage, ChallengeManager challengeManager) {
        super("challenge");
        this.gameStorage = gameStorage;
        this.challengeManager = challengeManager;
    }

    public String buildChallengeStr(Player challenged, Player challenger) {
        return "<@" +
            challenged.id() +
            ">, " +
            challenger.name() +
            " has challenged you to a game of Othello. " +
            "Type `/accept` " +
            "<@" +
            challenger.id() +
            ">, " +
            "or ignore to decline.";
    }

    @Override
    public void onCommand(CommandContext ctx) {
        switch (ctx.subcommand()) {
            case "bot" -> doBotCommand(ctx);
            case "user" -> doUserCommand(ctx);
            default -> throw new IllegalStateException("Invalid subcommand for the challenge command");
        }
    }

    public void doBotCommand(CommandContext ctx) {
        var level = ctx.getLongParam("level");
        if (level == null) {
            level = 3L;
        }

        if (!Player.Bot.isValidLevel(level)) {
            ctx.reply("Invalid level. Type !help analyze for valid levels.");
            return;
        }

        var player = ctx.getPlayer();

        try {
            var game = gameStorage.createBotGame(player, level);
            var image = BoardRenderer.drawBoardMoves(game.board());

            var sender = MessageSender.createGameStartSender(game, image);
            ctx.replyWithSender(sender);
        } catch (AlreadyPlayingException ex) {
            ctx.reply("You're already in a game");
        }

        LOGGER.info("Player " + player + " challenged the bot");
    }

    public void doUserCommand(CommandContext ctx) {
        var opponent = Objects.requireNonNull(ctx.getPlayerParam("opponent"));

        var player = ctx.getPlayer();

        var id = player.id();
        Runnable onExpiry = () -> ctx.sendMessage("<@" + id + "> Challenge timed out!");
        challengeManager.createChallenge(new Challenge(opponent, player), onExpiry);

        var message = buildChallengeStr(opponent, player);
        ctx.reply(message);

        LOGGER.info("Player " + player + " challenged opponent " + opponent);
    }
}
