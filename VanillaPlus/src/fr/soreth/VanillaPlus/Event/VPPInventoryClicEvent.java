package fr.soreth.VanillaPlus.Event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;

import fr.soreth.VanillaPlus.Player.VPPlayer;

public class VPPInventoryClicEvent extends Event implements Cancellable{
    private static final HandlerList handlers = new HandlerList();
    private VPPlayer player;
    private InventoryClickEvent event;
    private boolean cancelled = false;
	public VPPInventoryClicEvent(VPPlayer player, InventoryClickEvent event) {
    	this.player = player;
    	this.event = event;
	}
	public VPPlayer getPlayer() {
        return player;
    }
	public InventoryClickEvent getVanillaEvent() {
        return event;
    }
	public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
	@Override
	public boolean isCancelled() {
		return cancelled;
	}
	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}
}
