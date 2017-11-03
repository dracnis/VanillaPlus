package fr.soreth.VanillaPlus.IReward;

import org.bukkit.configuration.ConfigurationSection;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.Localizer;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.MComponent.MComponent;
import fr.soreth.VanillaPlus.MComponent.MComponentManager;
import fr.soreth.VanillaPlus.Player.VPPlayer;
import fr.soreth.VanillaPlus.StatType.Stat;

public class RewardStat implements IReward{
	private final Stat stat;
	private final int amount;
	private final MComponent format;
	public RewardStat(ConfigurationSection section, MComponentManager manager) {
		ErrorLogger.addPrefix(Node.ID.get());
		this.stat = VanillaPlusCore.getStatManager().get((short)section.getInt(Node.ID.get()), true);
		ErrorLogger.removePrefix();
		int amount = section.getInt(Node.AMOUNT.get());
		if(amount < 0) {
			amount = - amount;
			ErrorLogger.addError("Amount can't be negative.");
		}else if(amount == 0) {
			ErrorLogger.addError("Amount can't be 0.");
		}
		this.amount = amount;
		ErrorLogger.addPrefix(Node.FORMAT.get());
		this.format = manager.get(section.getString(Node.FORMAT.get(), "REWARD.STAT"));
		ErrorLogger.removePrefix();
	}

	@Override
	public void give(VPPlayer player) {
		stat.increase(player, amount);
	}
	@Override
	public void give(VPPlayer player, int time) {
		stat.increase(player, amount*time);
	}
	@Override
	public String format(Localizer loc) {
		return format(loc, 1);
	}
	@Override
	public String format(Localizer loc, int amount) {
		return format.addCReplacement("name", stat.getName())
				.addReplacement("amount", String.valueOf(this.amount*amount))
				.getMessage(loc);
	}
}