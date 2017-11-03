package fr.soreth.VanillaPlus.IReward;

import org.bukkit.configuration.ConfigurationSection;

import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.Manager;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.VanillaPlusExtension;
import fr.soreth.VanillaPlus.Utils.Minecraft.ConfigUtils;

public class IRewardManager extends Manager<String, IReward>{
	public IRewardManager() {
		super(String.class, IReward.class);
		RewardLoader.load(this);
	}
	public void init(VanillaPlusExtension extension) {
		ConfigurationSection section = ConfigUtils.getYaml(extension.getInstance(), "Reward", false);
		if(section == null) return;
		ErrorLogger.addPrefix("Reward.yml");
		ErrorLogger.addPrefix(Node.REWARD.getList());
		super.init(section.getConfigurationSection(Node.REWARD.getList()), extension.getMessageCManager());
		ErrorLogger.removePrefix();
		ErrorLogger.removePrefix();
	}
}
