package org.dedicatedmemers.kraken.tab;


import org.dedicatedmemers.kraken.Kraken;
import org.dedicatedmemers.kraken.tab.event.PlayerTabCreateEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Arrays;
import java.util.Objects;

public class PlayerTab {

    public static final int WIDTH = 3;

    public static final int HEIGHT= 20;

    private final Player player;

    private Scoreboard scoreboard;

    private TabEntry[][] entries;

    public PlayerTab(Kraken.Options options, Player player) {
        this.player = player;
        this.entries = new TabEntry[WIDTH][HEIGHT];
        for (int column = 0; column < this.entries.length; column++) {
            this.entries[column] = new TabEntry[HEIGHT];
            for (int row = 0; row < this.entries.length; row++) {
                this.entries[column][row] = new TabEntry(this, options, column, row);
            }
        }

        this.scoreboard = player.getScoreboard();
        Bukkit.getPluginManager().callEvent(new PlayerTabCreateEvent(this));
    }

    public Player getPlayer() {
        return this.player;
    }

    public void update() {
        if (!Objects.equals(this.scoreboard, this.player.getScoreboard())) {
            this.scoreboard = this.player.getScoreboard();
            // Send placeholders!
        }
    }

    public void clear() {
        Arrays.stream(this.entries).flatMap(Arrays::stream).forEach(TabEntry::clear);
    }

    public TabEntry getByPosition(int x, int y) {
        if (x < 0 || x >= WIDTH) {
            throw new IndexOutOfBoundsException("The tablist has columns between " + 0 + " and " + (WIDTH - 1));
        }

        if (y < 0 || y >= HEIGHT) {
            throw new IndexOutOfBoundsException("The tablist has columns between " + 0 + " and " + (HEIGHT - 1));
        }
        return this.entries[x][y];
    }
}
