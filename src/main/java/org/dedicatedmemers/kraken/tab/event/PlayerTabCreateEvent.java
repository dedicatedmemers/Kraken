package org.dedicatedmemers.kraken.tab.event;

import org.dedicatedmemers.kraken.tab.PlayerTab;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerTabCreateEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    private final PlayerTab playerTab;

    public PlayerTabCreateEvent(PlayerTab playerTab) {
        super(playerTab.getPlayer());
        this.playerTab = playerTab;
    }

    public PlayerTab getPlayerTab() {
        return this.playerTab;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
