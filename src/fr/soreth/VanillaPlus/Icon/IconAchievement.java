package fr.soreth.VanillaPlus.Icon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.Error;
import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.Localizer;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.PlaceH;
import fr.soreth.VanillaPlus.Menu.MenuLink;
import fr.soreth.VanillaPlus.Message.MessageManager;
import fr.soreth.VanillaPlus.MComponent.MComponent;
import fr.soreth.VanillaPlus.Player.Achievement;
import fr.soreth.VanillaPlus.Player.VPPlayer;
import fr.soreth.VanillaPlus.Utils.LiteEntry;

public class IconAchievement extends Icon{
	private Achievement achievement; 
	private LiteEntry<Icon, Icon> display;
	private MComponent requirementMessage, rewardMessage;
	private boolean showUnlockedRequirement, showUnlockedReward, showLockedRequirement, showLockedReward;
	public IconAchievement(ConfigurationSection section, MessageManager manager) {
		this.achievement = VanillaPlusCore.getAchievementManager().get((short) section.getInt(Node.ID.get(), 0));
		if(achievement == null){
			ErrorLogger.addError(Node.ID.get() + Error.INVALID.getMessage());
			return;
		}
		this.display = VanillaPlusCore.getAchievementManager().getDisplay((byte) section.getInt(Node.DISPLAY.get(), 0));
		if(display == null)
			ErrorLogger.addError(Node.DISPLAY.get() + Error.INVALID.getMessage());
		showLockedRequirement 	= section.getBoolean("SHOW_LOCKED_REQUIREMENT", true);
		showLockedReward 		= section.getBoolean("SHOW_LOCKED_REWARD", true);
		showUnlockedRequirement	= section.getBoolean("SHOW_UNLOCKED_REQUIREMENT", false);
		showUnlockedReward		= section.getBoolean("SHOW_UNLOCKED_REWARD", false);
		if((showLockedRequirement || showUnlockedRequirement) && achievement.getRequirement() == null){
			ErrorLogger.addError("Can't show unset requirement !");
			showLockedRequirement = false;
			showUnlockedRequirement = false;
		}else if(showLockedRequirement || showUnlockedRequirement){
			if(section.contains("REQUIREMENT_MESSAGE"))
				this.requirementMessage = manager.getComponentManager().get(section.getString("REQUIREMENT_MESSAGE"));
		}
		if((showLockedReward || showUnlockedReward) && achievement.getReward() == null){
			ErrorLogger.addError("Can't show unset reward !");
			showLockedReward = false;
			showUnlockedReward = false;
		}else if (showLockedReward || showUnlockedReward) {
			if(section.contains("REWARD_MESSAGE"))
				this.rewardMessage = manager.getComponentManager().get(section.getString("REWARD_MESSAGE"));
		}
		
	}
	@Override
	public boolean isStatic(){
		return false;
	}
	@Override
	public boolean closeOnClick() {
		return false;
	}
	@Override
	public boolean hasLore() {
		return true;
	}
	@Override
	public ItemStack getItemstack(VPPlayer player, Localizer loc){
		Icon curent = null;
		boolean has = false;
		if(player.hasAchievement(achievement.getID())){
			curent = display.getKey();
			has = true;
		}else{
			curent = display.getValue();
		}
		MComponent base = curent.name;
		if(base != null)
			base.addReplacement(PlaceH.NAME.get(), achievement.getName().getMessage(player.getLanguage()));
		if(curent.hasLore())
		for(MComponent lore : curent.lore){
			 lore.addReplacement(PlaceH.DESCRIPTION.get(), achievement.getLore().getMessage(player.getLanguage()));
			 lore.setSplit(true);
		}
		ItemStack result = curent.getItemstack(player, loc); 
		if(has && (showUnlockedRequirement || showUnlockedReward)){
			ItemMeta meta = result.getItemMeta();
			List<String>lores = meta.getLore();
			if(lores == null){
				lores = new ArrayList<String>();
			}
			if(showLockedRequirement){
				if(requirementMessage != null)
					lores.addAll(Arrays.asList(requirementMessage.getMessage(player, loc).split("\n")));
				lores.addAll(Arrays.asList(achievement.getRequirement().format(player, player.getLanguage()).split("\n")));
			}
			if(showLockedReward){
				if(rewardMessage != null)
					lores.addAll(Arrays.asList(rewardMessage.getMessage(player, loc).split("\n")));
				lores.addAll(achievement.getReward().format(player.getLanguage()));
			}
			meta.setLore(lores);
			result.setItemMeta(meta);
		}else if(!has && (showLockedRequirement || showLockedReward)){
			ItemMeta meta = result.getItemMeta();
			List<String>lores = meta.getLore();
			if(lores == null){
				lores = new ArrayList<String>();
			}
			if(showLockedRequirement){
				if(requirementMessage != null)
					lores.addAll(Arrays.asList(requirementMessage.getMessage(player, loc).split("\n")));
				lores.addAll(Arrays.asList(achievement.getRequirement().format(player, player.getLanguage()).split("\n")));
			}
			if(showLockedReward){
				if(rewardMessage != null)
					lores.addAll(Arrays.asList(rewardMessage.getMessage(player, loc).split("\n")));
				lores.addAll(achievement.getReward().format(player.getLanguage()));
			}
			meta.setLore(lores);
			result.setItemMeta(meta);
		}
		return result;
	}
	@Override
	public boolean canMove(VPPlayer clicker) {
		return false;
	}
	@Override
	public Icon getIcon(VPPlayer player) {
		return this;
	}
	@Override
	public boolean onClick(VPPlayer viewer, ClickType type, MenuLink ml){
		return false;
	}
}
