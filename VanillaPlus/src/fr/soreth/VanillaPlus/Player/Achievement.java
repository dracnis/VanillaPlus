package fr.soreth.VanillaPlus.Player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.IRequirement.Requirement;
import fr.soreth.VanillaPlus.IReward.Reward;
import fr.soreth.VanillaPlus.MComponent.MComponent;
import fr.soreth.VanillaPlus.MComponent.MComponentManager;

public class Achievement {
	private static boolean warn = true;
	private List<Short>listen = new ArrayList<>();
	private MComponent name, description;
	private short id;
	private Requirement requirement;
	private Reward reward;
	private boolean announce;
	Achievement(short id,ConfigurationSection section, MComponentManager manager){
		this.id = id;
		name = manager.get(section.getString(Node.NAME_PATH.get()));
		description = manager.get(section.getString(Node.LORE_PATH.get()));
		announce = section.getBoolean("ANNOUNCE", true);
		listen = section.getShortList("LISTEN");
	}
	public void initPost(ConfigurationSection section, MComponentManager manager) {
		Object requirement = section.get(Node.REQUIREMENT.get());
		ErrorLogger.addPrefix(Node.REQUIREMENT.get());
		if(requirement!=null){
			this.requirement = new Requirement(requirement, manager);
		}else if (warn){
			ErrorLogger.addError("Free Achievement should be avoided !");
			warn = false;
		}
		ErrorLogger.removePrefix();
		ErrorLogger.addPrefix(Node.REWARD.get());
		ConfigurationSection reward = section.getConfigurationSection(Node.REWARD.get());
		if(reward!=null)
			this.reward = new Reward(reward, manager);
		ErrorLogger.removePrefix();
		
	}
	public List<Short> getListen(){
		return listen;
	}
	public void tryUnlock(VPPlayer player){
		if(player.hasAchievement(id))
			return;
		if(requirement!= null && requirement.has(player)){
			unlock(player, false);
		}
	}
	public void unlock(VPPlayer player, boolean testHas){
		if(testHas && player.hasAchievement(id))
			return;
		if(reward != null)
			player.addAchievements(id);
		if(announce)
			VanillaPlusCore.getAchievementManager().Announce(player, this);
		reward.give(player);
	}
	public MComponent getName(){
		return name;
	}
	public MComponent getLore(){
		return description;
	}
	public short getID(){
		return id;
	}
	public Requirement getRequirement() {
		return requirement;
	}
	public Reward getReward() {
		return reward;
	}
}
