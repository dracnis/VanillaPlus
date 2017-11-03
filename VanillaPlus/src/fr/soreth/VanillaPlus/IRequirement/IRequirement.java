package fr.soreth.VanillaPlus.IRequirement;

import fr.soreth.VanillaPlus.Localizer;
import fr.soreth.VanillaPlus.Player.VPPlayer;

public interface IRequirement {
	public String format(VPPlayer player, Localizer lang);
	public String format(VPPlayer player, Localizer lang, int amount);
	public int getMax(VPPlayer player);
	public boolean has(VPPlayer player);
	public void take(VPPlayer player, int multiplier);
}
