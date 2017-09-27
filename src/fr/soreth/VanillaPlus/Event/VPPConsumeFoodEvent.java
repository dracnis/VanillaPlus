package fr.soreth.VanillaPlus.Event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.soreth.VanillaPlus.ExtraManager.FoodStatus;
import fr.soreth.VanillaPlus.Player.VPPlayer;
public class VPPConsumeFoodEvent extends Event implements Cancellable{
    private static final HandlerList handlers = new HandlerList();
    private final VPPlayer player;
    private final FoodStatus food;
    private final String type;
    private boolean cancelled = false;
    public VPPConsumeFoodEvent(VPPlayer playerPlus, FoodStatus food, String type) {
    	this.player = playerPlus;
    	this.food = food;
    	this.type = type;
	}
	@Override
	public boolean isCancelled() {
		return cancelled;
	}
    public FoodStatus getFoodStatus(){
    	return food;
    }
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
	public VPPlayer getPlayer(){
    	return player;
    }
	public String getType(){
    	return type;
    }
	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}
}
