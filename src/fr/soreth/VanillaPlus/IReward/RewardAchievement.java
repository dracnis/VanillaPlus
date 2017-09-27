package fr.soreth.VanillaPlus.IReward;

import org.bukkit.configuration.ConfigurationSection;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.Localizer;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.MComponent.MComponent;
import fr.soreth.VanillaPlus.MComponent.MComponentManager;
import fr.soreth.VanillaPlus.Player.Achievement;
import fr.soreth.VanillaPlus.Player.VPPlayer;

public class RewardAchievement implements IReward {
	private final Achievement achievement;
	private final MComponent format;
	public RewardAchievement(ConfigurationSection section, MComponentManager manager) {
		this.achievement = VanillaPlusCore.getAchievementManager().get((short) section.getInt(Node.ID.get()));
		this.format = manager.get(section.getString(Node.FORMAT.get(), "REWARD.ACHIEVEMENT"));
	}
	@Override
	public void give(VPPlayer player) {
		achievement.unlock(player, true);
	}
	@Override
	public void give(VPPlayer player, int time) {
		give(player);
	}
	@Override
	public String format(Localizer loc) {
		return format(loc, 1);
	}
	@Override
	public String format(Localizer loc, int time) {
		return format.addCReplacement("name", achievement.getName()).getMessage(loc);
	}

}
