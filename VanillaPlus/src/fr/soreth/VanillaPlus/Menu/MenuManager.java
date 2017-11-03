package fr.soreth.VanillaPlus.Menu;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.VanillaPlusExtension;
import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.Manager;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.VanillaPlus;
import fr.soreth.VanillaPlus.Event.VPPInventoryClicEvent;
import fr.soreth.VanillaPlus.Event.VPPLeaveEvent;
import fr.soreth.VanillaPlus.Event.VPPMenuClicEvent;
import fr.soreth.VanillaPlus.Icon.Icon;
import fr.soreth.VanillaPlus.Message.MessageManager;
import fr.soreth.VanillaPlus.Player.VPPlayer;
import fr.soreth.VanillaPlus.Utils.LiteEntry;
import fr.soreth.VanillaPlus.Utils.MediumEntry;
import fr.soreth.VanillaPlus.Utils.Minecraft.MinecraftUtils;

public class MenuManager extends Manager<String, Menu> implements Listener{
	private int anti_click_spam_delay = 5000;
	private Menu defaultMenu;
	private List<VanillaPlusExtension>toLoad = new ArrayList<>();
	private HashMap<String, MediumEntry<MessageManager, Menu, ConfigurationSection>>toInit;
	private static Map<VPPlayer, Long> antiClickSpam = new HashMap<VPPlayer, Long>();
	public MenuManager() {
		super(String.class, Menu.class);
		MenuLoader.load(this);
	}
	public void add(VanillaPlusExtension extension){
		if(toLoad != null && extension != null)
			toLoad.add(extension);
	}
	/**
	 * Loads all the configuration files recursively into a list.
	 */
	private HashMap<String, YamlConfiguration> loadMenus(File file) {
		HashMap<String, YamlConfiguration> list = new HashMap<String, YamlConfiguration>();
		if (file.isDirectory()) {
			for (File subFile : file.listFiles()) {
				list.putAll(loadMenus(subFile));
			}
		} else if (file.isFile()) {
			if (file.getName().endsWith(".yml")) {
				list.put(file.getName().substring(0, file.getName().length()-4), YamlConfiguration.loadConfiguration(file));
			}
		}
		return list;
	}
	public void start(ConfigurationSection section) {
		HashMap<String, LiteEntry<MessageManager,YamlConfiguration>> menusList = new HashMap<String, LiteEntry<MessageManager,YamlConfiguration>>();
		for(VanillaPlusExtension extension : toLoad){
			for(Entry<String, YamlConfiguration>c : loadMenus(new File(extension.getInstance().getDataFolder(),"Menu")).entrySet()){
				if(menusList.containsKey(c.getKey())){
					ErrorLogger.addError("Menu " + c.getKey() + " already exist !");
				}else{
					menusList.put(c.getKey(), new LiteEntry<MessageManager, YamlConfiguration>(extension.getMessageManager(), c.getValue()));
				}
			}
		}
		toInit = new HashMap<String, MediumEntry<MessageManager, Menu, ConfigurationSection>>();
		for (Entry<String, LiteEntry<MessageManager, YamlConfiguration>> entry : menusList.entrySet()) {
			if(entry.getKey() == null || entry.getKey() == null || entry.getKey().isEmpty())
				continue;
			ErrorLogger.addPrefix(entry.getKey() + ".yml");
			Menu menu = create(entry.getValue().getValue().getString(Node.TYPE.get(), Node.BASE.get()), entry.getValue().getKey(),
					entry.getValue().getValue());
			ErrorLogger.removePrefix();
			if(menu == null)
				continue;
			register(entry.getKey(), menu, true);
			toInit.put(entry.getKey()+".yml", new MediumEntry<MessageManager, Menu, ConfigurationSection>(entry.getValue().getKey(),
					menu, entry.getValue().getValue()));
		}
		Bukkit.getServer().getPluginManager().registerEvents(this, VanillaPlus.getInstance());
		Bukkit.getScheduler().scheduleSyncRepeatingTask(VanillaPlus.getInstance(), new Runnable() {
			private byte elapsedTenths;
			@Override
			public void run() {
				for (VPPlayer player : VanillaPlusCore.getPlayerManager().getOnlinePlayers()) {
					if(defaultMenu != null){
						if (defaultMenu.getRefresh() > 0)
							if (elapsedTenths % defaultMenu.getRefresh() == 0)
								defaultMenu.refresh(player, player.getPlayer().getInventory());
					}
					InventoryView view = player.getPlayer().getOpenInventory();
					if (view == null) {
						return;
					}
					Inventory topInventory = view.getTopInventory();
					if (topInventory.getHolder() instanceof MenuLink) {
						MenuLink menuHolder = (MenuLink) topInventory.getHolder();
						if (menuHolder.getIconMenu() instanceof Menu) {
							Menu extMenu = (Menu) menuHolder.getIconMenu();
							if (extMenu.getRefresh() > 0) {
								if (elapsedTenths % extMenu.getRefresh() == 0) {
									extMenu.refresh(player, topInventory);
								}
							}
						}
					}
				}
				elapsedTenths++;
				if(elapsedTenths > 30)
					elapsedTenths = 1;
			}
		}, 1, 20);
		anti_click_spam_delay = section.getInt(Node.DELAY.get(), 1000);
		if(section.contains("PLAYER_MENU")){
			ErrorLogger.addPrefix("PLAYER_MENU");
			defaultMenu = get(section.getString("PLAYER_MENU"), true);
			if(defaultMenu != null){
				if(defaultMenu.getSize()!=36)
					ErrorLogger.addError("Invalid size must be 36");
			}
			ErrorLogger.removePrefix();
		}
		
	}
	@Deprecated
	public void postIconInit() {
		if(toInit == null)return;
		for (Entry<String, MediumEntry<MessageManager, Menu, ConfigurationSection>> entry : toInit.entrySet()) {
			ErrorLogger.addPrefix(entry.getKey());
			entry.getValue().getValue().init(entry.getValue().getKey(), entry.getValue().getExtraValue());
			ErrorLogger.removePrefix();
		}
		toInit = null;
	}
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onClose(InventoryCloseEvent event) {
		if (!(event.getInventory().getHolder() instanceof MenuLink))
			return;
		final VPPlayer player = VanillaPlusCore.getPlayerManager().getPlayer((Player) event.getPlayer());
		final MenuLink menu = ((MenuLink) event.getInventory().getHolder());
		Bukkit.getScheduler().scheduleSyncDelayedTask(VanillaPlus.getInstance(), new Runnable() {
			@Override
			public void run() {
				if(player.getPlayer().getOpenInventory().getTopInventory().getType() == InventoryType.CRAFTING){
					menu.getIconMenu().closed(player, menu);
				}else{
					menu.getIconMenu().changed(player, menu);
				}
			}
		});
	}
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {
		Menu menu = null;
		MenuLink ml = null;
		if(event.getInventory().getHolder() instanceof MenuLink)
			ml = (MenuLink) event.getInventory().getHolder();
		int slot = event.getSlot();
		slot = slot == -999 ? 0 : slot+1;
		VPPlayer clicker = VanillaPlusCore.getPlayerManager().getPlayer((Player) event.getWhoClicked());
		if(slot == 0){
			menu =  ml != null ? ml.getIconMenu() : clicker.getMenu();
		}else if (event.getClickedInventory() == event.getWhoClicked().getOpenInventory().getTopInventory()){
			if(ml != null)
				menu = ml.getIconMenu();
		}else if(clicker.getMenu() != null){
			menu = clicker.getMenu();
		}
		if(menu == null){
			VPPInventoryClicEvent clickEvent = new VPPInventoryClicEvent(clicker, event);
			Bukkit.getServer().getPluginManager().callEvent(clickEvent);
			if(clickEvent.isCancelled())
				event.setCancelled(true);
			return;
		}
		VPPMenuClicEvent e = new VPPMenuClicEvent(clicker, menu, event);
		Bukkit.getServer().getPluginManager().callEvent(e);
		if(e.isCancelled()) {
			event.setCancelled(true);
			return;
		}
		Long cooldownUntil = antiClickSpam.get(clicker);
		if(menu.onClick(clicker, slot, cooldownUntil, ml, event)){
			antiClickSpam.put(clicker, System.currentTimeMillis() + anti_click_spam_delay);
		}
	}
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onDrop(PlayerDropItemEvent event) {
		ItemStack item = event.getItemDrop().getItemStack();
		if(item != null && item.getType() != Material.AIR && MinecraftUtils.getExtra(item, Icon.FREEZE).equals("1"))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onQuit(VPPLeaveEvent event) {
		antiClickSpam.remove(event.getPlayer());
	}
	public void refresh(VPPlayer viewer) {
		if(viewer.getMenu() != null)
			viewer.getMenu().refresh(viewer, viewer.getPlayer().getInventory());
		Inventory inv = viewer.getPlayer().getOpenInventory().getTopInventory();
		if(inv == null || inv.getType() == InventoryType.CRAFTING) {
			return;
		}
		if (inv.getHolder() instanceof MenuLink)
			((MenuLink) inv.getHolder()).getIconMenu().refresh(viewer, inv);
	}
	public Menu getDefault() {
		return defaultMenu;
	}
	public void disable() {
		for (VPPlayer player : VanillaPlusCore.getPlayerManager().getOnlinePlayers()) {
			InventoryView view = player.getPlayer().getOpenInventory();
			if (view == null) {
				return;
			}
			Inventory topInventory = view.getTopInventory();
			if (topInventory.getHolder() instanceof MenuLink) {
				player.getPlayer().closeInventory();
			}
		}
	}
}
