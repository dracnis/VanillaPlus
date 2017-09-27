package fr.soreth.VanillaPlus.Event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.soreth.VanillaPlus.Player.VPPlayer;

public class VPPLoadEvent extends Event{	
    private static final HandlerList handlers = new HandlerList();
	private final VPPlayer player;
	private final boolean firstJoin;
	public VPPLoadEvent(VPPlayer player, boolean firstJoin) {
		this.player = player;
		this.firstJoin = firstJoin;
	}
	public VPPlayer getPlayer(){
		return player;
	}
	public boolean isFirstJoin() {
		return firstJoin;
	}
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
