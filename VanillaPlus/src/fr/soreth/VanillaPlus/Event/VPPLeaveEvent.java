package fr.soreth.VanillaPlus.Event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.soreth.VanillaPlus.Message.Message;
import fr.soreth.VanillaPlus.Player.VPPlayer;

public class VPPLeaveEvent extends Event{	
    private static final HandlerList handlers = new HandlerList();
	private final VPPlayer player;
	private Message message;
	private boolean save;
	public VPPLeaveEvent(VPPlayer player) {
		this.player = player;
		this.save = true;
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
	public VPPlayer getPlayer(){
		return player;
	}
	public boolean save(){
		return this.save;
	}
	public void save(boolean state){
		this.save = state;
	}
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
