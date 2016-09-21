package org.dedicatedmemers.kraken;

import org.dedicatedmemers.kraken.tab.PlayerTab;
import org.dedicatedmemers.kraken.tab.event.PlayerTabRemoveEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class Kraken implements Listener {

    private final Plugin plugin;

    private final Options options;

    private final Map<Player, PlayerTab> byPlayer = new HashMap<>();

    public Kraken(Plugin plugin) {
        this(plugin, new Options());
    }

    public Kraken(Plugin plugin, Options options) {

        this.plugin = plugin;
        
        // TODO: fiiix
        if (this.plugin.getServer().getMaxPlayers() < 60) {
            throw new NumberFormatException("Player limit must be at least 60!");
        }
        
        this.options = options;
        // Do we need the delay? Not really but if we want to modify
        // it async we can delay it 4 ticks - the server sends it on 
        // login so to avoid concurrent modification we can wait 4 ticks
        this.plugin.getServer().getScheduler().runTask(this.plugin, () -> this.plugin.getServer().getOnlinePlayers().forEach(this::checkPlayer));
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private Optional<PlayerTab> getPlayerTab(Player player) {
        return Optional.ofNullable(this.byPlayer.get(player));
    }

    private Optional<PlayerTab> getPlayerTab(Player player, boolean remove) {
        Function<Player, PlayerTab> action = remove ? this.byPlayer::remove : this.byPlayer::get;
        return action.andThen(Optional::ofNullable).apply(player);
    }

    @EventHandler
    private void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.plugin.getServer().getScheduler().runTask(this.plugin, () -> checkPlayer(player));
    }

    @EventHandler
    private void onPlayerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        getPlayerTab(player, true).ifPresent(playerTab -> {
            player.getScoreboard().getTeams().forEach(this::unregisterFromScoreboard);
            this.plugin.getServer().getPluginManager().callEvent(new PlayerTabRemoveEvent(playerTab));
        });
    }

    private void unregisterFromScoreboard(Team team) {
        try {
            team.unregister();
        } catch (IllegalStateException ex) {
            // NOP
        }
    }

    private void checkPlayer(Player player) {
        this.byPlayer.computeIfAbsent(player, $ -> new PlayerTab(this.options, player)).update(); // TODO: change to "clear and update?"
    }

    public static class Options {
        private boolean supressTextOverflow = false;

        public boolean suppressTextOverflow() {
            return this.supressTextOverflow;
        }

        public Options suppressTextOverflow(boolean state) {
            this.supressTextOverflow = state;
            return this;
        }
    }
}
