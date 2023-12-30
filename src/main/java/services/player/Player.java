/*
 * Copyright (c) Joseph Prichard 2023.
 */

package services.player;

import net.dv8tion.jda.api.entities.User;

public record Player(long id, String name) {

    public static class Bot {

        public static final int MAX_BOT_LEVEL = 6;

        public static int getDepthFromId(long id) {
            return switch ((int) id) {
                case 1 -> 1;
                case 2 -> 3;
                case 3 -> 5;
                case 4 -> 8;
                case 5 -> 10;
                case 6 -> 15;
                default -> throw new IllegalStateException("Invalid bot id: " + id);
            };
        }

        public static boolean isValidLevel(long level) {
            return level >= 1 && level <= MAX_BOT_LEVEL;
        }

        public static String name(long id) {
            return "OthelloBot level " + id;
        }

        public static Player create(long level) {
            return new Player(level, name(level));
        }

        public static boolean isBotId(long id) {
            return id <= MAX_BOT_LEVEL;
        }
    }

    public static long parseDiscordID(String id) {
        // removes all non digit characters
        var strippedId = id.replaceAll("\\D", "");
        return Long.parseLong(strippedId);
    }

    public Player(long id) {
        this(id, "");
    }

    public Player(User user) {
        this(parseDiscordID(user.getId()), user.getName());
    }

    public boolean isBot() {
        return Bot.isBotId(id);
    }

    public long id() {
        return id;
    }

    public String name() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var player = (Player) o;
        return id == player.id();
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return Long.toString(id);
    }
}
