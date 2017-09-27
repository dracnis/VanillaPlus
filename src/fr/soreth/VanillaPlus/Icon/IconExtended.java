package fr.soreth.VanillaPlus.Icon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.Error;
import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.Localizer;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.Command.CommandPlus;
import fr.soreth.VanillaPlus.IRequirement.Requirement;
import fr.soreth.VanillaPlus.IReward.Reward;
import fr.soreth.VanillaPlus.MComponent.MComponent;
import fr.soreth.VanillaPlus.Menu.MenuLink;
import fr.soreth.VanillaPlus.Message.Message;
import fr.soreth.VanillaPlus.Message.MessageManager;
import fr.soreth.VanillaPlus.Player.VPPlayer;
public class IconExtended extends Icon{
	private Icon noViewIcon;
	private int random;
	private MComponent rewardMessage;
	private boolean showRequirement, showReward;
	private HashMap<ClickType, CommandPlus>actions;
	private Requirement requirement, viewRequirement;
	private Reward reward;
	private Message noRequirement;
	public IconExtended(ConfigurationSection section, MessageManager messageManager) {
		super(section, messageManager);
		if(section.contains(Node.NO_VIEW_ICON.get())){
			ErrorLogger.addPrefix(Node.NO_VIEW_ICON.get());
			noViewIcon = VanillaPlusCore.getIconManager().create(messageManager, section.getConfigurationSection(Node.NO_VIEW_ICON.get()));
			ErrorLogger.removePrefix();
		}
		actions = new HashMap<ClickType, CommandPlus>();
		if(section.contains(Node.COMMAND.get())){
			ErrorLogger.addPrefix(Node.COMMAND.get());
			ConfigurationSection commands = section.getConfigurationSection(Node.COMMAND.get());
			for(String key : commands.getKeys(false)){
				ErrorLogger.addPrefix(key);
				ConfigurationSection command = commands.getConfigurationSection(key);
				ClickType type = ClickType.valueOf(key);
				if(type == null || command == null){
					Error.INVALID.add();
				}else{
					String commandType = command.getString(Node.TYPE.get());
					if(commandType == null || commandType.isEmpty()){
						Error.MISSING_NODE.add(Node.TYPE.get());
					}else{
						CommandPlus sub = VanillaPlusCore.getCommandManager().create(commandType, command, messageManager);
						if(sub != null)
							actions.put(type, sub);
					}
				}
				ErrorLogger.removePrefix();
			}
			ErrorLogger.removePrefix();
		}
		Object requirement = section.get(Node.VIEW_REQUIREMENT.get());
		if(requirement!=null){
			ErrorLogger.addPrefix(Node.VIEW_REQUIREMENT.get());
			this.viewRequirement = new Requirement(requirement, messageManager.getComponentManager());
			ErrorLogger.removePrefix();
		}
		requirement = section.get(Node.REQUIREMENT.get());
		if(requirement!=null){
			ErrorLogger.addPrefix(Node.REQUIREMENT.get());
			this.requirement = new Requirement(requirement, messageManager.getComponentManager());
			ErrorLogger.removePrefix();
		}
		this.showRequirement = requirement == null ? false : section.getBoolean("SHOW_REQUIREMENT", false);
		ConfigurationSection reward = section.getConfigurationSection(Node.REWARD.get());
		if(reward!=null){
			ErrorLogger.addPrefix(Node.REWARD.get());
			this.reward = new Reward(reward, messageManager.getComponentManager());
			ErrorLogger.removePrefix();
		}
		this.rewardMessage = reward == null ? null : messageManager.getComponentManager().get(section.getString("REWARD_MESSAGE"));
		this.showReward = reward == null ? false : section.getBoolean("SHOW_REWARD", false);
		random = section.getInt("RANDOM", 0);
		if(random < 0 ){
			ErrorLogger.addError("RANDOM " + random + Error.INVALID.getMessage());
			random = 0;
		}
		if(showRequirement || showReward)
			isStatic = false;
		noRequirement = messageManager.get(section.getString(Node.NO_REQUIREMENT.get()));
	}
	@Override
	public ItemStack getItemstack(VPPlayer player, Localizer loc){
		ItemStack result = super.getItemstack(player, loc);
		if(random != 0)
			result.setDurability((short) VanillaPlusCore.getRandom().nextInt(random));
		return result;
	}
	@Override
	public List<String> getLores(VPPlayer player, Localizer loc){
		if(!showRequirement && !showReward){
			return super.getLores(player, loc);
		}
		List<String>lores = super.getLores(player, loc);
		if(lores == null)
			lores = new ArrayList<String>();
		if(showRequirement)
			lores.addAll(Arrays.asList(requirement.format(player, loc).split("\n")));
		if(showReward){
			if(rewardMessage != null)
				lores.addAll(Arrays.asList(rewardMessage.getMessage(player, loc).split("\n")));
			lores.addAll(reward.format(player.getLanguage()));
		}
		return lores;
	}
	public boolean canView(VPPlayer viewer) {
		return viewRequirement == null || viewRequirement.has(viewer);
	}
	public boolean canClick(VPPlayer viewer){
		return requirement == null || requirement.has(viewer);
	}
	@Override
	public Icon getIcon(VPPlayer viewer) {
		if(canView(viewer)){
			return super.getIcon(viewer);
		}
		return noViewIcon == null ? null : noViewIcon.getIcon(viewer);
	}
	@Override
	public boolean isStatic() {
		return (super.isStatic()&&viewRequirement==null);
	}
	@Override
	public boolean onClick(VPPlayer viewer, ClickType type, MenuLink ml) {
		if(canClick(viewer)){
			if(requirement != null)
				requirement.take(viewer);
			boolean execute = true;
			if(type != ClickType.LEFT){
				CommandPlus command = actions.get(type);
				if(command!=null){
					command.onExecute(viewer, "", ml == null ? new ArrayList<String>() : ml.getArgs());
					execute = false;
				}
			}
			if(execute){
				CommandPlus command = actions.get(ClickType.LEFT);
				if(command!=null){
					execute = false;
					command.onExecute(viewer, "", ml == null ? new ArrayList<String>() : ml.getArgs());
				}
			}
			if(reward != null)
				reward.give(viewer);
			VanillaPlusCore.getMenuManager().refresh(viewer);
			return !execute;
		}else{
			noRequirement.sendTo(viewer);
			return false;
		}
	}
}
