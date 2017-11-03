package fr.soreth.VanillaPlus.IReward;

import org.bukkit.configuration.ConfigurationSection;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.Error;
import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.Localizer;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.MComponent.MComponent;
import fr.soreth.VanillaPlus.MComponent.MComponentManager;
import fr.soreth.VanillaPlus.Player.Title;
import fr.soreth.VanillaPlus.Player.VPPlayer;

public class RewardTitle implements IReward {
	private final Title title;
	private final MComponent format;
	public RewardTitle(ConfigurationSection section, MComponentManager manager) {
		this.title = VanillaPlusCore.getTitleManager().get(section.getInt(Node.ID.get()));
		if(this.title == null)
			ErrorLogger.addError(Node.ID.get() + " => " + Error.INVALID);
		this.format = manager.get(section.getString(Node.FORMAT.get(), "REWARD.TITLE"));
	}
	@Override
	public void give(VPPlayer player) {
		VanillaPlusCore.getTitleManager().unlock(player, title);
	}
	@Override
	public void give(VPPlayer player, int time) {
		VanillaPlusCore.getTitleManager().unlock(player, title);
	}
	@Override
	public String format(Localizer loc) {
		return format(loc, 1);
	}
	@Override
	public String format(Localizer loc, int time) {
		return format.addCReplacement("name", title.getName()).getMessage(loc);
	}
}
