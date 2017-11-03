package fr.soreth.VanillaPlus.IRequirement;

import org.bukkit.configuration.ConfigurationSection;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.Error;
import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.Localizer;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.MComponent.MComponent;
import fr.soreth.VanillaPlus.MComponent.MComponentManager;
import fr.soreth.VanillaPlus.Player.Currency;
import fr.soreth.VanillaPlus.Player.VPPlayer;

public class RequirementCurrency implements IRequirement {
	private final Currency money;
	private final boolean keep;
	private final double amount;
	private final MComponent format;
	public RequirementCurrency(ConfigurationSection section, MComponentManager manager) {
		ErrorLogger.addPrefix(Node.ID.get());
		this.money = VanillaPlusCore.getCurrencyManager().get((short) section.getInt(Node.ID.get()));
		ErrorLogger.removePrefix();
		boolean keep = section.getBoolean("KEEP", false);
		this.amount = ((int)(section.getDouble(Node.AMOUNT.get(), 0)*1000))/1000.0;
		if(amount <= 0){
			ErrorLogger.addError(Node.AMOUNT.get() + " " + Error.INVALID.getMessage());
			keep = true;
		}
		this.keep = keep;
		format = manager.get(section.getString(Node.FORMAT.get(), "REQUIREMENT.CURRENCY"));
	}
	@Override
	public String format(VPPlayer player, Localizer lang) {
		return format(player, lang, 1);
	}
	@Override
	public String format(VPPlayer player, Localizer lang, int amount) {
		double currentAmount = getAmount(player);
		boolean has = currentAmount >= ( this.amount * amount );
		double rewardAmount = this.amount * amount;
		return format.addCReplacement("state", IRequirementManager.getState(has))
				.addReplacement("current", money.format(currentAmount))
				.addCReplacement("current_name", money.getName(rewardAmount))
				.addReplacement("required", money.format(rewardAmount))
				.addCReplacement("required_name", money.getName(rewardAmount))
				.getMessage(player);
				
	}
	public double getAmount(VPPlayer player) {
		return player.getCurrency(money.getID());
	}
	@Override
	public int getMax(VPPlayer player) {
		return keep ? ( getAmount(player) >= amount ? -1 : 0 ) :
				(int) ( getAmount(player) / amount );
	}
	@Override
	public boolean has(VPPlayer player) {
		return getAmount(player)>=amount;
	}
	@Override
	public void take(VPPlayer player, int multiplier) {
		if(keep)return;
		money.paiement(player, amount*multiplier,  VanillaPlusCore.getVPConsole());
	}

}
