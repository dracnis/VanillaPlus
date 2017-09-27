package fr.soreth.VanillaPlus.Event;

import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import fr.soreth.VanillaPlus.Player.VPPlayer;

public class IPVPDamageEvent extends PVPDamageEvent{

	public IPVPDamageEvent(VPPlayer damager,
			VPPlayer damaged, double finalDamage, DamageCause cause,
			Projectile projectile) {
		super(damager, damaged, finalDamage, cause, projectile);
	}
}
