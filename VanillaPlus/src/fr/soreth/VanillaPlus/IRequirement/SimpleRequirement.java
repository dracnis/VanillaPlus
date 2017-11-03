package fr.soreth.VanillaPlus.IRequirement;

import fr.soreth.VanillaPlus.Localizer;
import fr.soreth.VanillaPlus.MComponent.MComponent;
import fr.soreth.VanillaPlus.Player.VPPlayer;

public abstract class SimpleRequirement implements IRequirement{
	protected MComponent format;
	@Override
	public String format(VPPlayer player, Localizer lang){
		return format(player, lang, 1);
	}
	@Override
	public String format(VPPlayer player, Localizer lang, int amount) {
		if(format == null) return "";
		boolean has = has(player);
		return format.addCReplacement("state", IRequirementManager.getState(has))
				.getMessage(player);
	}
	@Override
	public int getMax(VPPlayer player){
		return has(player) ? -1 : 0;
	}
	public void init(MComponent format) {
		if(format == null)
			this.format = format;
	}
	@Override
	public void take(VPPlayer player, int multiplier){}
}
