package org.dedicatedmemers.kraken.tab;

import org.bukkit.ChatColor;
import org.dedicatedmemers.kraken.Kraken;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

public class TabEntry {

    private static final String[][] PLACEHOLDER_NAMES;

    private final Kraken.Options options;

    private int x;

    private int y;

    private String prefix;

    private String suffix;

    TabEntry(PlayerTab playerTab, Kraken.Options options, int x, int y) {
        this.options = options;
        this.x = x;
        this.y = y;
        this.prefix = "";
        this.suffix = "";
    }

    public void setText(String text) {
        class Format {
            private ChatColor last;

            private Set<ChatColor> formats = EnumSet.noneOf(ChatColor.class);

            private void record(ChatColor colour) {
                if (colour == null) {
                    return;
                }
                if (colour.isColor()) {
                    last = colour;
                } else if (colour == ChatColor.RESET) {
                    last = null;
                    formats.clear();
                } else {
                    formats.add(colour);
                }
            }

            private Optional<ChatColor> getColour() {
                return Optional.ofNullable(this.last);
            }
        }

        StringBuilder builder = new StringBuilder(Math.min(16, text.length()));
        boolean cc = false;

        // Capture two sets of 16 characters
        char[] chars = text.toCharArray();
        Format format = new Format();
        for (int index = 0; index < Math.min(16, chars.length); index++) {
            builder.append(chars[index]);
            if (chars[index] == ChatColor.COLOR_CHAR) {
                cc = true;
            } else if (cc) {
                ChatColor colour = ChatColor.getByChar(chars[index]);
                format.record(colour);
                cc = false;
            }
        }
        // Last character was a colour/format char, handle it separately for the suffix
        int offset = 16;
        if (cc) {
            ChatColor colour;
            if (chars.length > 16 && ((colour = ChatColor.getByChar(chars[16])) != null)) { // we got chars[16]
                builder.deleteCharAt(15); // Delete last colour char
                offset += 1;
                format.record(colour);
            }
        }

        this.prefix = builder.toString();
        if (offset >= text.length()) {
            this.suffix = "";
        } else {
            builder.delete(0, 15);
            assert builder.length() == 0;
            format.getColour().ifPresent(builder::append);
            format.formats.forEach(builder::append);
            if (!this.options.suppressTextOverflow() && text.length() - offset > 16 - builder.length()) {
                // TODO: add more information
                throw new IllegalArgumentException("Text did not fit the tablist entry");
            }
            builder.append(text.substring(offset, Math.min(offset + 16 - builder.length(), text.length())));
            this.suffix = builder.toString();
        }
    }

    public String getPlaceholderName() {
        return PLACEHOLDER_NAMES[x][y];
    }

    void clear() {
        this.prefix = "";
        this.suffix = "";
    }

    static {
        PLACEHOLDER_NAMES = new String[PlayerTab.WIDTH][PlayerTab.HEIGHT];
        int index = 0;
        StringBuilder sb = new StringBuilder("      " + ChatColor.RESET);
        assert sb.length() == 8;
        for (ChatColor first : ChatColor.values()) {
            sb.setCharAt(1, first.getChar());
            for (ChatColor second : ChatColor.values()) {
                sb.setCharAt(3, second.getChar());
                for (ChatColor third : ChatColor.values()) {
                    sb.setCharAt(5, third.getChar());
                    int x = index % PlayerTab.WIDTH;
                    int y = index / PlayerTab.WIDTH;
                    PLACEHOLDER_NAMES[x][y] = sb.toString();
                    if (++index >= 60) {
                        break;
                    }
                }
            }
        }
    }

}
