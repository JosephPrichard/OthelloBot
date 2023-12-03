/*
 * Copyright (c) Joseph Prichard 2023.
 */

package messaging.senders;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import services.game.GameResult;
import services.player.Player;

public class GameOverSender extends MessageSender {
    private String resultDesc = "";
    private String messageDesc = "";
    private String scoreDesc = "";

    public GameOverSender setGame(GameResult result) {
        resultDesc = result.getWinner().getName() +
            "'s new rating is " + result.getWinnerElo() +
            " (" + result.formatWinnerDiffElo() + ") \n" +
            result.getLoser().getName() +
            "'s new rating is " + result.getLoserElo() +
            " (" + result.formatLoserDiffElo() + ") \n";
        getEmbedBuilder().setTitle("Game has ended");
        return this;
    }

    public GameOverSender addForfeitMessage(Player winner) {
        messageDesc = winner.getName() + " won by forfeit \n";
        return this;
    }

    public GameOverSender addScoreMessage(int whiteScore, int blackScore) {
        scoreDesc = "Score: " + blackScore + " - " + whiteScore + "\n";
        return this;
    }

    public GameOverSender addMoveMessage(Player winner, String move) {
        messageDesc = winner.getName() + " won with " + move + "\n";
        return this;
    }

    public GameOverSender setTag(GameResult result) {
        var tag = "";
        if (!result.getWinner().isBot()) {
            tag += "<@" + result.getWinner() + "> ";
        }
        if (!result.getLoser().isBot()) {
            tag += "<@" + result.getLoser() + "> ";
        }
        setMessage(tag);
        return this;
    }

    @Override()
    public void sendReply(SlashCommandInteractionEvent event) {
        var desc = messageDesc;
        if (!scoreDesc.isEmpty()) {
            desc += scoreDesc;
        }
        desc += "\n" + resultDesc;
        getEmbedBuilder().setDescription(desc);
        super.sendReply(event);
    }
}
