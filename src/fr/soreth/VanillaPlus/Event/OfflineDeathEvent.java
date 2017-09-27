package fr.soreth.VanillaPlus.Event;

import java.util.List;

import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import fr.soreth.VanillaPlus.Player.VPPlayer;

public class OfflineDeathEvent extends VPPDeathEvent {

	public OfflineDeathEvent(VPPlayer killer,
			VPPlayer player, DamageCause cause, List<ItemStack> loots,
			boolean keep) {
		super(killer, player, cause, loots, keep);
	}
}
