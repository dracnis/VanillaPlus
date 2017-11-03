package fr.soreth.VanillaPlus.IReward;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.MComponent.MComponentManager;
import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.Localizer;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.Player.VPPlayer;
public class Reward {
	private List<IReward>reward = new ArrayList<IReward>();
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Reward(Object object, MComponentManager manager){
		if(object == null)return;
		else if(object instanceof String) {
			IReward iReward = VanillaPlusCore.getIRewardManager().get(object, true);
			if(iReward != null)
				reward.add(iReward); 
		}else if(object instanceof List && !((List) object).isEmpty() && ((List) object).get(0) instanceof String) {
			List<String>list = (List<String>) object;
			for(String current : list) {
				IReward iReward = VanillaPlusCore.getIRewardManager().get(current, true);
				if(iReward != null)
					reward.add(iReward); 
			}
		}else if( object instanceof ConfigurationSection ) {
			ConfigurationSection section = (ConfigurationSection) object;
			for(String key : section.getKeys(false)){
				ConfigurationSection sub = section.getConfigurationSection(key);
				if(sub == null) {
					String temp = section.getString(key);
					if(temp != null)
						reward.add(VanillaPlusCore.getIRewardManager().get(temp, true));
					continue;
				}
				ErrorLogger.addPrefix(key);
				reward.add(VanillaPlusCore.getIRewardManager().create(sub.getString(Node.TYPE.get()), sub, manager));
				ErrorLogger.removePrefix();
			}
		}
	}
	public void give(VPPlayer player){
		for(IReward requirement : this.reward){
			requirement.give(player);
		}
	}
	public List<String> format(Localizer lang){
		List<String>result = new ArrayList<String>();
		for(IReward temp : reward){
			String tempS = temp.format(lang);
			if(temp!=null && !tempS.isEmpty())
				result.addAll(Arrays.asList(tempS.split("\n")));
		}
		result.add("");
		return result;
	}
	public void give(VPPlayer player, int time){
		for(IReward requirement : this.reward){
			requirement.give(player, time);
		}
	}
}
