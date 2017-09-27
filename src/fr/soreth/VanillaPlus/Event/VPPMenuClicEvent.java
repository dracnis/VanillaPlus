package fr.soreth.VanillaPlus.Event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;

import fr.soreth.VanillaPlus.Menu.Menu;
import fr.soreth.VanillaPlus.Player.VPPlayer;

public class VPPMenuClicEvent extends Event implements Cancellable{
    private static final HandlerList handlers = new HandlerList();
    private VPPlayer player;
    private InventoryClickEvent event;
    private final Menu menu;
    private boolean cancelled = false;
	public VPPMenuClicEvent(VPPlayer player, Menu menu, InventoryClickEvent event) {
		this.menu = menu;
    	this.player = player;
    	this.event = event;
	}
	public VPPlayer getPlayer() {
        return player;
    }
	public Menu getMenu() {
        return menu;
    }
	@Override
	public boolean isCancelled() {
		return cancelled;
	}
	@Override
	public void setCancelled(boolean cancel) {
		cancelled = cancel;
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
}
