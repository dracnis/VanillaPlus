package fr.soreth.VanillaPlus.Event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import fr.soreth.VanillaPlus.Player.VPPlayer;

public class VPPLoginEvent extends Event{	
    private static final HandlerList handlers = new HandlerList();
	private final VPPlayer player;
	private org.bukkit.event.player.PlayerLoginEvent.Result result;
	public VPPLoginEvent(VPPlayer player) {
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
	public void setResult(org.bukkit.event.player.PlayerLoginEvent.Result allowed) {
		result = allowed;
	}
	public org.bukkit.event.player.PlayerLoginEvent.Result getResult() {
		return result;
	}
}
