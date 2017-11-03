package fr.soreth.VanillaPlus.Event;

import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import fr.soreth.VanillaPlus.Player.VPPlayer;

public class PVEDamageEvent extends Event implements Cancellable{
    private static final HandlerList handlers = new HandlerList();
    private DamageCause cause;
    private double damage;
    private VPPlayer damaged;
    private Projectile projectile;
    private boolean cancelled = false;
	public PVEDamageEvent(VPPlayer damaged,
			double finalDamage, DamageCause cause, Projectile projectile) {
    	this.damaged = damaged;
    	this.damage = finalDamage;
    	this.cause = cause;
    	this.projectile = projectile;
	}
	public DamageCause getCause() {
        return cause;
    }
	public double getDamage() {
        return damage;
    }
	public VPPlayer getDamaged() {
        return damaged;
    }
	public Projectile getProjectile() {
        return projectile;
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
		cancelled = cancel;
	}

}
