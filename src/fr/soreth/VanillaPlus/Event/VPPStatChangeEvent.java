package fr.soreth.VanillaPlus.Event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.soreth.VanillaPlus.Player.VPPlayer;
import fr.soreth.VanillaPlus.StatType.Stat;

public class VPPStatChangeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final VPPlayer player;
    private final Stat stat;
    private final int amount;
    public VPPStatChangeEvent(VPPlayer player, Stat stat, int amount) {
    	this.player = player;
    	this.stat = stat;
    	this.amount = amount;
    }
    public VPPlayer getPlayer(){
    	return player;
    }
    public Stat getStat(){
    	return stat;
    }
    public int getAmount(){
    	return amount;
    }
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
