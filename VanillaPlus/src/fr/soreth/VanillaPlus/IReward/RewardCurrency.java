package fr.soreth.VanillaPlus.IReward;

import org.bukkit.configuration.ConfigurationSection;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.Localizer;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.MComponent.MComponent;
import fr.soreth.VanillaPlus.MComponent.MComponentManager;
import fr.soreth.VanillaPlus.Player.Currency;
import fr.soreth.VanillaPlus.Player.VPPlayer;

public class RewardCurrency implements IReward {
	private final Currency currency;
	private final MComponent format;
	private final double amount;
	private final boolean force;
	public RewardCurrency(ConfigurationSection section, MComponentManager manager) {
		this.currency = VanillaPlusCore.getCurrencyManager().get((short) section.getInt(Node.ID.get()));
		double amount = section.getDouble(Node.AMOUNT.get());
		if(amount < 0) {
			amount = - amount;
			ErrorLogger.addError("Amount can't be negative.");
		}else if(amount == 0) {
			ErrorLogger.addError("Amount can't be 0.");
		}
		this.amount = amount;
		this.force = section.getBoolean(Node.FORCE.get(), false);
		this.format = manager.get(section.getString(Node.FORMAT.get(), "REWARD.CURRENCY"));
	}
	@Override
	public void give(VPPlayer player) {
		give(player, 1);
	}
	@Override
	public void give(VPPlayer player, int time) {
		currency.deposit(player, amount*time, force, VanillaPlusCore.getVPConsole());
	}
	@Override
	public String format(Localizer loc) {
		return format(loc, 1);
	}
	@Override
	public String format(Localizer loc, int amount) {
		return format.addReplacement("current", currency.format(this.amount * amount))
				.addReplacement("current_form", currency.format(this.amount * amount) + " " + currency.getName(this.amount * amount).getMessage(loc))
				.getMessage(loc);
	}

}
