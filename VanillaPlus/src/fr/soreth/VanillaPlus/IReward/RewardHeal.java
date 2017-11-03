package fr.soreth.VanillaPlus.IReward;

import org.bukkit.configuration.ConfigurationSection;

import fr.soreth.VanillaPlus.Localizer;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.Heal.Heal;
import fr.soreth.VanillaPlus.Heal.HealManager;
import fr.soreth.VanillaPlus.MComponent.MComponent;
import fr.soreth.VanillaPlus.MComponent.MComponentManager;
import fr.soreth.VanillaPlus.Player.VPPlayer;

public class RewardHeal implements IReward{
	private final Heal heal;
	private final MComponent format;
	public RewardHeal(ConfigurationSection section, MComponentManager manager) {
		heal = HealManager.create(section.getConfigurationSection("HEAL"));
		format = manager.get(section.getString(Node.FORMAT.get(), "REWARD.HEAL"));
	}

	@Override
	public void give(VPPlayer player) {
		heal.heal(player);
	}
	@Override
	public void give(VPPlayer player, int time) {
		for(int i = 0 ; i < time ; i++)
			heal.heal(player);
	}
	@Override
	public String format(Localizer loc) {
		return format(loc, 1);
	}
	@Override
	public String format(Localizer loc, int amount) {
		return format.addReplacement("amount", String.valueOf(heal.getAmount()*amount))
				.addReplacement("amount_heart", String.valueOf(heal.getAmount()*amount*0.5))
				.getMessage(loc);
	}

}
