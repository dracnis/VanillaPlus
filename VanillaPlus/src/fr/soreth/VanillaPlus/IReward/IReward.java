package fr.soreth.VanillaPlus.IReward;

import fr.soreth.VanillaPlus.Localizer;
import fr.soreth.VanillaPlus.Player.VPPlayer;

public interface IReward {
	public void give(VPPlayer player);
	public void give(VPPlayer player, int time);
	public String format(Localizer loc);
	public String format(Localizer loc, int time);
}
