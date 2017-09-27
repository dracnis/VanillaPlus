package fr.soreth.VanillaPlus.IRequirement;

import org.bukkit.configuration.ConfigurationSection;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.Error;
import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.Localizer;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.MComponent.MComponent;
import fr.soreth.VanillaPlus.MComponent.MComponentManager;
import fr.soreth.VanillaPlus.Player.VPPlayer;
import fr.soreth.VanillaPlus.StatType.Stat;

public class RequirementStat implements IRequirement{
	private final Stat stat;
	private final boolean keep, reset;
	private final int amount;
	private final MComponent format;
	public RequirementStat(ConfigurationSection section, MComponentManager manager) {
		ErrorLogger.addPrefix(Node.ID.get());
		this.stat = VanillaPlusCore.getStatManager().get((short)section.getInt(Node.ID.get()), true);
		ErrorLogger.removePrefix();
		boolean keep = section.getBoolean("KEEP", false);
		reset = section.getBoolean("RESET", false);
		this.amount = section.getInt(Node.AMOUNT.get(), 0);
		if(amount <= 0 && !reset){
			ErrorLogger.addError(Node.AMOUNT.get() + " " + Error.INVALID.getMessage());
			keep = true;
		}
		this.keep = keep;
		format = manager.get(section.getString(Node.FORMAT.get(), "REQUIREMENT.STAT"));
	}
	@Override
	public String format(VPPlayer player, Localizer lang) {
		return format(player, lang, 1);
	}
	@Override
	public String format(VPPlayer player, Localizer lang, int amount) {
		int currentAmount = getAmount(player);
		boolean has = ( this.amount * amount ) <= currentAmount;
		return format.addCReplacement("state", IRequirementManager.getState(has))
				.addCReplacement("name", stat.getName())
				.addReplacement("current", String.valueOf(currentAmount))
				.addReplacement("required", String.valueOf(this.amount * amount)).getMessage(player);
				
	}
	public int getAmount(VPPlayer player) {
		return player.getStat(stat.getID());
	}
	@Override
	public int getMax(VPPlayer player){
		return keep ? getAmount(player) >= amount ? -1 : 0 :
				reset ? getAmount(player) >= amount ?  1 : 0 :
					getAmount(player)/amount;
	}
	@Override
	public boolean has(VPPlayer player) {
		return player.getStat(stat.getID())>=amount;
	}
	@Override
	public void take(VPPlayer player, int multiplier) {
		if(keep)return;
		if(reset)
			stat.set(player, 0);
		else
			stat.decrease(player, reset ? player.getStat(stat.getID()) : amount*multiplier);
		
	}
}
