package fr.soreth.VanillaPlus.Event;

import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.soreth.VanillaPlus.Message.Message;
import fr.soreth.VanillaPlus.Player.VPPlayer;

public class VPPJoinEvent extends Event{	
    private static final HandlerList handlers = new HandlerList();
	private final VPPlayer player;
	private Message message;
	private Location spawnLocation;
	private boolean showMessage = true;
	public VPPJoinEvent(VPPlayer player) {
		this.player = player;
	}
	/**
	 * @return the message
	 */
	public Message getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(Message message) {
		this.message = message;
	}
	/**
	 * @return the spawnLocation
	 */
	public Location getSpawnLocation() {
		return spawnLocation;
	}
	/**
	 * @param spawnLocation the spawnLocation to set
	 */
	public void setSpawnLocation(Location spawnLocation) {
		this.spawnLocation = spawnLocation;
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
	public boolean showMessage() {
		return showMessage;
	}
	public void setShowMessage(boolean state){
		showMessage = state;
	}
}
