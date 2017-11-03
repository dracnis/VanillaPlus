package fr.soreth.VanillaPlus.Event;

import java.util.List;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import fr.soreth.VanillaPlus.Player.VPPlayer;

public class VPPDeathEvent extends Event{
    private static final HandlerList handlers = new HandlerList();
    private boolean keepInventory, keepXp, keepArmor, keepHotbar;
    private DamageCause cause;
    private VPPlayer player, killer;
    private List<ItemStack> loots;
	public VPPDeathEvent(VPPlayer killer, VPPlayer player, DamageCause cause, List<ItemStack> loots, boolean keep) {
		this.killer = killer;
		this.player = player;
    	this.cause = cause;
    	this.loots = loots;
    	setKeepInventory(keep);
    	setKeepXp(keep);
    	setKeepArmor(keep);
    	setKeepHotbar(keep);
	}
	public List<ItemStack> getLoots(){
		return loots;
	}
	public DamageCause getCause() {
        return cause;
    }
	public VPPlayer getPlayer() {
        return player;
    }
	public VPPlayer getKiller() {
        return killer;
    }
	public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
	/**
	 * @return the keepInventory
	 */
	public boolean keepInventory() {
		return keepInventory;
	}
	/**
	 * @param keepInventory the keepInventory to set
	 */
	public void setKeepInventory(boolean keepInventory) {
		this.keepInventory = keepInventory;
	}
	/**
	 * @return the keepXp
	 */
	public boolean keepXp() {
		return keepXp;
	}
	/**
	 * @param keepXp the keepXp to set
	 */
	public void setKeepXp(boolean keepXp) {
		this.keepXp = keepXp;
	}
	/**
	 * @return the keepArmor
	 */
	public boolean keepArmor() {
		return keepArmor;
	}
	/**
	 * @param keepArmor the keepArmor to set
	 */
	public void setKeepArmor(boolean keepArmor) {
		this.keepArmor = keepArmor;
	}
	/**
	 * @return the keepHotbar
	 */
	public boolean keepHotbar() {
		return keepHotbar;
	}
	/**
	 * @param keepHotbar the keepHotbar to set
	 */
	public void setKeepHotbar(boolean keepHotbar) {
		this.keepHotbar = keepHotbar;
	}
}
