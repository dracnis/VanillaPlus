package fr.soreth.VanillaPlus.Menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.Error;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.Command.CommandPlus;
import fr.soreth.VanillaPlus.Icon.Icon;
import fr.soreth.VanillaPlus.Icon.IconManager;
import fr.soreth.VanillaPlus.Message.MessageManager;
import fr.soreth.VanillaPlus.MComponent.MComponent;
import fr.soreth.VanillaPlus.Player.VPPlayer;
import fr.soreth.VanillaPlus.Utils.Utils;
import fr.soreth.VanillaPlus.Utils.Minecraft.MinecraftUtils;

public class Menu{
	public final MComponent title;
	public final Icon[] icons;
	public final InventoryType type;
	private CommandPlus open, close, change;
	private byte refresh;
	public Menu(MComponent title, Icon[] icons, InventoryType type){
		this.title = title;
		this.icons = icons;
		this.type = type;
	}
	public Menu(MessageManager messageManager, YamlConfiguration section) {
		ConfigurationSection settings = section.getConfigurationSection(Node.SETTINGS.get());
		if(settings == null){
			Error.MISSING_NODE.add(Node.SETTINGS.get());
			title = new MComponent(VanillaPlusCore.getDefaultLang(), " ");
			icons = new Icon[37];
			type = InventoryType.CHEST;
			refresh = 0;
			return;
		}else{
			title = messageManager.getComponentManager().get(settings.getString(Node.NAME_PATH.get()));
			type = InventoryType.valueOf(settings.getString(Node.TYPE.get(), "CHEST"));
			if(type == InventoryType.CHEST) {
				int size = settings.getInt("ROWS");
				if(size < 0 || size > 12)
					ErrorLogger.addError("ROWS must be between 0 and 12 inclulsive !");
				icons = new Icon[9*size+1];
			}
			else
				icons = new Icon[type.getDefaultSize()+1];
			refresh = (byte) settings.getInt("REFRESH", 0);
			
		}
	}
	public void init(MessageManager messageManager, ConfigurationSection section){
		if(type == InventoryType.CHEST){
			Icon temp = VanillaPlusCore.getIconManager().getFillIcon();
			if(temp != null)
				for(int pos = 0 ; pos < icons.length; pos ++)
					this.icons[pos] = temp;
		}
		ConfigurationSection fill = section.getConfigurationSection(Node.FILL.get());
		if(fill != null){
			for(String key : fill.getKeys(false)){
				Icon temp = VanillaPlusCore.getIconManager().get(key, true);
				if(temp == null)
					continue;
				for(int pos : fill.getIntegerList(key)){
					if(pos>this.icons.length || pos<0)
						continue;
					this.icons[pos] = temp;
				}
			}
		}
		ConfigurationSection action = section.getConfigurationSection(Node.ACTION.get());
		if(action != null){
			ErrorLogger.addPrefix(Node.ACTION.get());
			ConfigurationSection open = action.getConfigurationSection(Node.OPEN.get());
			if(open!=null){
				ErrorLogger.addPrefix(Node.OPEN.get());
				this.open = VanillaPlusCore.getCommandManager().create(open.getString(Node.TYPE.get()), open);
				ErrorLogger.removePrefix();
			}
			ConfigurationSection close = action.getConfigurationSection(Node.CLOSE.get());
			if(close!=null){
				ErrorLogger.addPrefix(Node.CLOSE.get());
				this.close = VanillaPlusCore.getCommandManager().create(close.getString(Node.TYPE.get()), close);
				ErrorLogger.removePrefix();
			}
			ConfigurationSection change = action.getConfigurationSection(Node.SWITCH.get());
			if(change!=null){
				ErrorLogger.addPrefix(Node.SWITCH.get());
				this.change = VanillaPlusCore.getCommandManager().create(change.getString(Node.TYPE.get()), change);
				ErrorLogger.removePrefix();
			}
			ErrorLogger.removePrefix();
		}
		if(section.contains(Node.ICON.getList())){
			ConfigurationSection icons = section.getConfigurationSection(Node.ICON.getList());
			IconManager manager = VanillaPlusCore.getIconManager();
			ErrorLogger.addPrefix(Node.ICON.getList());
			if(icons != null)
			for(String key : icons.getKeys(false)){
				ErrorLogger.addPrefix(key);
				if(Utils.isValidInteger(key)){
					int pos = Utils.parseInt(key, 1, true);
					ConfigurationSection iconSection = icons.getConfigurationSection(key);
					if(iconSection == null)
						Error.INVALID.add();
					else{
						Icon temp = manager.create(iconSection.getString(Node.TYPE.get(), Node.BASE.get()), iconSection, messageManager);
						if(pos >= 0 && pos < this.icons.length)
							this.icons[pos] = temp;
					}
				}else
					Error.INVALID.add();
				ErrorLogger.removePrefix();
			}
			ErrorLogger.removePrefix();
		}
		
		
	}
	public byte getRefresh(){
		return refresh;
	}
	public void closed(VPPlayer player, MenuLink menu){
		if(close != null)
			close.onExecute(player, "", menu.getArgs());
	}
	public void changed(VPPlayer player, MenuLink menu){
		if(change != null)
			change.onExecute(player, "", menu.getArgs());
	}
	public Icon getIcon(VPPlayer player, int slot) {
		if (slot >= 0 && slot < icons.length)  {
			return icons[slot] == null ? null : icons[slot].getIcon(player);
		}
		return null;
	}
	public int getSize() {
		return icons.length-1;
	}
	public MComponent getTitle() {
		return title;
	}
	public Inventory open(VPPlayer player, VPPlayer view, List<String>args){
		if(player == null){
			ErrorLogger.addError("Player 'null' can't open menu");
			return null;
		}
		args = args == null ? new ArrayList<String>() : args;
		view = view == null ? player : view; 
		MenuLink link = new MenuLink(this, view, args);
		if(type == InventoryType.PLAYER)
			player.setMenu(this);
		Inventory inventory = type == InventoryType.CHEST ? Bukkit.createInventory(link, getSize(), title.getMessage(view, player.getLanguage())) :
			type == InventoryType.PLAYER ? player.getPlayer().getInventory() :
			Bukkit.createInventory(link, type, title.getMessage(view, player.getLanguage()));
		for (int i = 1; i < icons.length; i++) {
			if (icons[i] != null){
				Icon temp = icons[i].getIcon(view);
				if(temp != null)
					inventory.setItem(i-1, temp.getItemstack(view, player.getLanguage()));
				else
					inventory.setItem(i-1, new ItemStack(Material.AIR));
			}
		}
		link.setInventory(inventory);
		if(type != InventoryType.PLAYER)
			player.getPlayer().openInventory(inventory);
		else{
			player.getPlayer().updateInventory();
			player.setMenu(this);
		}
		if(open!=null)
			open.onExecute(player, "", args);
		return inventory;
	}

	@Override
	public String toString() {
		return "IconMenu [title=" + title + ", icons=" + Arrays.toString(icons) + "]";
	}
	public void refresh(VPPlayer viewer, Inventory inv){
		MenuLink ml = (MenuLink) ((inv.getHolder() instanceof MenuLink) ? inv.getHolder() : null); 
		for (int i = 1; i < icons.length; i++) {
			if (icons[i] != null && !icons[i].isStatic()){
				Icon icon = icons[i].getIcon(viewer);
				if(icon == null) {
					if(type == InventoryType.PLAYER) {
						ItemStack[] memory = viewer.getInventory(false);
						if(memory[i-1] != null) {
							inv.setItem(i-1, memory[i-1]);
							memory[i-1] = null;
							viewer.setInventory(memory, false);
						}
					}else
						inv.setItem(i-1, new ItemStack(Material.AIR));
					continue;
				}
				if(icon.skullSelf()){
					ItemStack item = inv.getItem(i-1);
					if(item != null && item.getType() == Material.SKULL_ITEM){
						ItemMeta itemMeta = item.getItemMeta();
						itemMeta.setLore(icon.getLores(ml == null ? viewer : ml.getView(), viewer.getLanguage()));
						itemMeta.setDisplayName(icon.getName(ml == null ? viewer : ml.getView(), viewer.getLanguage()));
						((SkullMeta)itemMeta).setOwner(ml == null ? viewer.getName() : ml.getView().getName());
						item.setItemMeta(itemMeta);
						continue;
					}
				}
				if(icon != null)
					inv.setItem(i-1, icon.getItemstack(ml == null ? viewer : ml.getView(), viewer.getLanguage()));
			}
		}
	}
	public boolean onClick(VPPlayer clicker, int slot, Long coolDownUntil, MenuLink ml,
			InventoryClickEvent event) {
		Icon icon = this.getIcon(clicker, slot);
		if (icon != null) {
			event.setCancelled(!icon.canMove(clicker));
			long now = System.currentTimeMillis();
			if (coolDownUntil == null || coolDownUntil < now) {
				if(icon.closeOnClick())
					clicker.getPlayer().closeInventory();
				if(icon.onClick(clicker, event.getClick(), ml)){
					return true;
				}
			}
		}
		if(!event.isCancelled()) {

			if(event.getClick() == ClickType.DOUBLE_CLICK || event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT || event.getClick() == ClickType.NUMBER_KEY) {
				ItemStack item = event.getCurrentItem();
				if(item != null && item.getType() != Material.AIR && MinecraftUtils.getExtra(item, Icon.FREEZE).equals("1"))
					event.setCancelled(true);
				else if(event.getClick() == ClickType.NUMBER_KEY){
					item = event.getWhoClicked().getInventory().getItem(event.getHotbarButton());
					if(item != null && item.getType() != Material.AIR && MinecraftUtils.getExtra(item, Icon.FREEZE).equals("1"))
						event.setCancelled(true);
				}
			}
		}
		return false;
	}
}
