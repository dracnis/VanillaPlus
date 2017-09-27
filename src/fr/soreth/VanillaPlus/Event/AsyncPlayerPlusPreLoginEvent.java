package fr.soreth.VanillaPlus.Event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import fr.soreth.VanillaPlus.Player.VPPlayer;

public class AsyncPlayerPlusPreLoginEvent extends Event{	
    private static final HandlerList handlers = new HandlerList();
	private final VPPlayer player;
	private org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result result;
	public AsyncPlayerPlusPreLoginEvent(VPPlayer player) {
		this.player = player;
	}
	public VPPlayer getPlayer(){
		return player;
	}
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
	public org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result getLoginResult() {
		return result;
	}
	public void setLoginResult(org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result allowed) {
		result = allowed;
	}
}
