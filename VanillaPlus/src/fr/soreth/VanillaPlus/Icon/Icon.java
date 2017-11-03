package fr.soreth.VanillaPlus.Icon;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.Localizer;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.Menu.MenuLink;
import fr.soreth.VanillaPlus.Message.MessageManager;
import fr.soreth.VanillaPlus.MComponent.MComponent;
import fr.soreth.VanillaPlus.Player.VPPlayer;
import fr.soreth.VanillaPlus.Utils.Minecraft.MinecraftUtils;

public class Icon{
	public static final String FREEZE = "FREEZE";
	public static final ItemStack air = new ItemStack(Material.AIR);
	public MComponent name;
	public List<MComponent> lore;
	public boolean skullSelf, closeOnClick, canMove;
	public boolean isStatic = true;
	public ItemStack item;
	protected Icon(){}
	public Icon(ConfigurationSection section, MessageManager manager) {
		if(section.getConfigurationSection(Node.ITEM.get())!=null){
			ErrorLogger.addPrefix(Node.ITEM.get());
			item = MinecraftUtils.loadItem(section.getConfigurationSection(Node.ITEM.get()));
			ErrorLogger.removePrefix();
		}
		skullSelf 		= section.getBoolean("SKULL_SELF", false);
		closeOnClick 	= section.getBoolean(Node.CLOSE.get(), false);
		canMove 		= section.getBoolean(Node.MOVE.get(), false);
		if(!canMove && item != null && item.getType() != Material.AIR)
			item = MinecraftUtils.setExtra(item, FREEZE, "1");
		if(item == null)
			item = air;
		if(skullSelf && item.getType() != Material.SKULL_ITEM)
			skullSelf = false;
		if(skullSelf)
			item.setDurability((short) 3);
		if(section.contains(Node.NAME_PATH.get())){
			name = manager.getComponentManager().get(section.getString(Node.NAME_PATH.get()));
			if(isStatic && name != null)
				isStatic = false;
		}
		if(section.contains(Node.LORE_PATH.get())){
			lore = new ArrayList<MComponent>();
			if(!section.getStringList(Node.LORE_PATH.get()).isEmpty()) {
				for(String key : section.getStringList(Node.LORE_PATH.get())){
					MComponent temp = manager.getComponentManager().get(key);
					if(temp == null)
						continue;
					lore.add(temp);
					if(isStatic)
						isStatic = false;
				}
			}else if(section.getString(Node.LORE_PATH.get()) != null) {
				lore.add(manager.getComponentManager().get(section.getString(Node.LORE_PATH.get())));
			}
		}
	}
	public boolean isStatic(){
		return isStatic && ! skullSelf;
	}
	public boolean skullSelf(){
		return skullSelf;
	}
	public boolean closeOnClick() {
		return closeOnClick;
	}
	public boolean hasLore() {
		return lore != null && lore.size() > 0;
	}	
	public String getName(VPPlayer player, Localizer loc){
		if(name==null)
			return null;
		String result = name.getMessage(player, loc);
		if(result.isEmpty())
			return ChatColor.WHITE.toString();
		return result;
	}
	public List<String> getLores(VPPlayer player, Localizer loc){
		List<String> lores = null;
		if (lore != null) {
			lores = new ArrayList<String>();
			for(MComponent message : lore){
				if(message.split()){
					for(String s : message.getSplitMessage(player, loc))
						lores.add(s);
				}else 
					lores.add(message.getMessage(player, loc));
			}
		}
		return lores;
	}
	public ItemStack getItemstack(VPPlayer player, Localizer loc){
		ItemStack result = item;
		ItemMeta itemMeta = item.getItemMeta();
		if(name != null)
			itemMeta.setDisplayName(getName(player, loc));
		if(hasLore())
			itemMeta.setLore(getLores(player, loc));
		result.setItemMeta(itemMeta);
		if(skullSelf){
			SkullMeta meta = (SkullMeta) item.getItemMeta();
			meta.setOwner(player.getName());
			result.setItemMeta(meta);
		}
		return result;
	}
	public boolean canMove(VPPlayer clicker) {
		return canMove;
	}
	public Icon getIcon(VPPlayer player) {
		return this;
	}
	public boolean onClick(VPPlayer viewer, ClickType type, MenuLink ml){
		return true;
	}
	protected Icon clone(){
		Icon result = new Icon();
		result.item = item;
		result.canMove = canMove;
		result.closeOnClick = closeOnClick;
		result.skullSelf = skullSelf;
		result.lore = new ArrayList<MComponent>();
		result.lore.addAll(lore);
		result.name = name;
		return result;
	}
}
