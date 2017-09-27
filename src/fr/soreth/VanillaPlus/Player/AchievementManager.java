package fr.soreth.VanillaPlus.Player;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import fr.soreth.VanillaPlus.Error;
import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.PlaceH;
import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.VanillaPlusExtension;
import fr.soreth.VanillaPlus.Data.Column;
import fr.soreth.VanillaPlus.Data.IConnection;
import fr.soreth.VanillaPlus.Data.IResultQuery;
import fr.soreth.VanillaPlus.Data.Table;
import fr.soreth.VanillaPlus.Data.Table.IUpdateQuery;
import fr.soreth.VanillaPlus.Data.Column.Type;
import fr.soreth.VanillaPlus.Data.SessionValue.BooleanSession;
import fr.soreth.VanillaPlus.Event.VPPJoinEvent;
import fr.soreth.VanillaPlus.Event.VPPStatChangeEvent;
import fr.soreth.VanillaPlus.Icon.Icon;
import fr.soreth.VanillaPlus.Message.Message;
import fr.soreth.VanillaPlus.Utils.LiteEntry;
import fr.soreth.VanillaPlus.Utils.Utils;
import fr.soreth.VanillaPlus.Utils.Minecraft.ConfigUtils;

public class AchievementManager implements Listener {
	private HashMap<Short, Achievement>achievements = new HashMap<>();
	private HashMap<Short, List<Achievement>>listeners = new HashMap<>();
	private HashMap<Byte, LiteEntry<Icon, Icon>>display = new HashMap<>();
	private Table table; 
	private int bigger = 0;
	private Message announce;
	private boolean unlockJoin;
	private DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private Column id = new Column("id", Type.INTEGER, null, true, false, false, true);
	public void init(VanillaPlusExtension extension) {
		ConfigurationSection section = ConfigUtils.getYaml(extension.getInstance(), "Achievement", false);
		if(section == null)return;
		ErrorLogger.addPrefix("Achievement.yml");
		ConfigurationSection achievementSub = section.getConfigurationSection(Node.ACHIEVEMENT.getList());
		ErrorLogger.addPrefix(Node.ACHIEVEMENT.getList());
		if(achievementSub != null){
			for(String key : achievementSub.getKeys(false)){
				ErrorLogger.addPrefix(key);
				ConfigurationSection sub = achievementSub.getConfigurationSection(key);
				if(sub == null){
					Error.INVALID.add();
				}else{
					int id = Utils.parseInt(key, 0, true);
					if(id< Short.MIN_VALUE || id == 0 || id > Short.MAX_VALUE || achievements.containsKey((short)id)){
						Error.INVALID.add();
					}else{
						if(id>bigger)
							bigger = id;
						Achievement achievement = new Achievement((short) id, sub, extension.getMessageCManager());
						achievements.put(achievement.getID(), achievement);
					}
				}
				ErrorLogger.removePrefix();
			}
				
		}
		ErrorLogger.removePrefix();
		ErrorLogger.removePrefix();
	}
	public void initPost(VanillaPlusExtension extension) {
		ConfigurationSection section = ConfigUtils.getYaml(extension.getInstance(), "Achievement", false);
		if(section == null)return;
		ErrorLogger.addPrefix("Achievement.yml");
		ConfigurationSection achievementSub = section.getConfigurationSection(Node.ACHIEVEMENT.getList());
		ErrorLogger.addPrefix(Node.ACHIEVEMENT.getList());
		if(achievementSub != null){
			for(String key : achievementSub.getKeys(false)){
				ErrorLogger.addPrefix(key);
				ConfigurationSection sub = achievementSub.getConfigurationSection(key);
				if(sub == null){
					Error.INVALID.add();
				}else{
					int id = Utils.parseInt(key, 0, true);
					if(id< Short.MIN_VALUE || id == 0 || id > Short.MAX_VALUE){
						Error.INVALID.add();
					}else{
						Achievement achievement = achievements.get((short)id);
						if(achievement == null)continue;
						achievement.initPost(sub, extension.getMessageCManager());
					}
				}
				ErrorLogger.removePrefix();
			}
				
		}
		ErrorLogger.removePrefix();
		ErrorLogger.removePrefix();
	}
	public void init(VanillaPlusCore core) {
		if(core == null)return;
		ConfigurationSection section = ConfigUtils.getYaml(core.getInstance(), "Achievement", false);
		if(section == null)return;
		ErrorLogger.addPrefix("Achievement.yml");
		ConfigurationSection settings = section.getConfigurationSection(Node.SETTINGS.get());
		if(settings==null){
			Error.MISSING_NODE.add(Node.SETTINGS.get());
			announce = core.getMessageManager().get(null);
			startDataBase(VanillaPlusCore.getIConnectionManager().get(null));
		}else{
			announce = core.getMessageManager().get(settings.getString("ANNOUNCE"));
			startDataBase(VanillaPlusCore.getIConnectionManager().get(settings.getString("STORAGE")));
			unlockJoin = settings.getBoolean("ACHIEVEMENT_ON_JOIN");
		}

		ErrorLogger.addPrefix(Node.DISPLAY.get());
		ConfigurationSection display = section.getConfigurationSection(Node.DISPLAY.get());
		if(display != null){
			for(String key : display.getKeys(false)){
				ErrorLogger.addPrefix(key);
				ConfigurationSection sub = display.getConfigurationSection(key);
				if(sub == null){
					Error.INVALID.add();
				}else{
					int id = Utils.parseInt(key, 0, true);
					if( id == 0 || id > Byte.MAX_VALUE){
						Error.INVALID.add();
					}else{
						Icon iOwned = null;
						Icon iUnOwned = null;
						ConfigurationSection owned = sub.getConfigurationSection(Node.OWNED.get());
						ErrorLogger.addPrefix(Node.OWNED.get());
						if(owned == null){
							Error.INVALID.add();
						}else{
							iOwned = VanillaPlusCore.getIconManager().create(core.getMessageManager(), owned);
						}
						ErrorLogger.removePrefix();
						ConfigurationSection unOwned = sub.getConfigurationSection(Node.UNOWNED.get());
						ErrorLogger.addPrefix(Node.UNOWNED.get());
						if(unOwned == null){
							Error.INVALID.add();
						}else{
							iUnOwned = VanillaPlusCore.getIconManager().create(core.getMessageManager(), unOwned);
						}
						ErrorLogger.removePrefix();
						if(iOwned != null && iUnOwned != null){
							this.display.put((byte) id, new LiteEntry<Icon, Icon>(iOwned, iUnOwned));
						}
					}
				}
				ErrorLogger.removePrefix();
			}
		}
		ErrorLogger.removePrefix();
		ErrorLogger.removePrefix();
		if(listeners != null){
			if(listeners.isEmpty())
				listeners = null;
			else
				start(core.getInstance());
		}
	}
	@EventHandler
	public void onStatUpdate(VPPStatChangeEvent event){
		VPPlayer player = event.getPlayer();
		List<Achievement> toTry = listeners.get(event.getStat().getID());
		if(toTry == null || toTry.isEmpty())
			return;
		for(Achievement current : toTry){
			current.tryUnlock(player);
		}
	}
	@EventHandler
	public void onJoin(VPPJoinEvent event) {
		if(unlockJoin)
			for(Achievement a : achievements.values())
				a.tryUnlock(event.getPlayer());
	}
	private void startDataBase(IConnection connection){
		if(achievements.isEmpty())return;
		table = connection.getTable("VPPlayer_achievement");
		table.addColumn(id);
		for(int i = 1; i <= bigger ; i++){
			table.addColumn(new Column(i+"", Type.DATE));
		}
		table.validate();
	}
	void load(VPPlayer player){
		if(achievements.isEmpty())return;
		IResultQuery result = table.select().where(id, player.getID()).execute();
		if(result.next()){
			Map<Integer, BooleanSession>resultAchievement = new LinkedHashMap<Integer, BooleanSession>();
			for(int i : achievements.keySet()){
				if(i<0)continue;
				Object object = result.get(table.getColumn(null, "" + i ));
				if(object == null)
					continue;
				resultAchievement.put(i, new BooleanSession(true));
			}
			player.setAchievements(resultAchievement);
			result.close();
		}else{
			result.close();
			create(player.getID());
			load(player);
		}
	}
	void save(VPPlayer player){
		if(achievements.isEmpty())return;
		IUpdateQuery result = table.update();
		String now = LocalDateTime.now().format(format);
		for(Entry<Integer, BooleanSession>entry : player.getAchievements().entrySet()){
			if(entry.getValue().changed() && entry.getKey() > 0){
				result.set(table.getColumn(null, ""  +(entry.getKey())), entry.getValue().get() ? now : null);
				entry.getValue().save();
			}
		}
		result.where(id, player.getID()).execute();
	}
	void create(int i){
		if(achievements.isEmpty())return;
		table.insert().insert(id, i).execute();;
	}
	public Achievement get(short id){
		return achievements.get(id);
	}
	public void Announce(VPPlayer p, Achievement achievement) {
		announce.addCReplacement("achievement", achievement.getName())
		.addCReplacement("achievement_lore", achievement.getLore())
		.addSReplacement(PlaceH.SENDER.get(), p)
		.send();
	}
	/**
	 * 
	 * @param id The display'id
	 * @return ListEntry< Owned Icon, UnOwned Icon >
	 */
	public LiteEntry<Icon, Icon> getDisplay(byte id){
		return display.get(id);
	}
	public void stop(){
		HandlerList.unregisterAll(this);
	}
	private void start(Plugin instance){
		Bukkit.getServer().getPluginManager().registerEvents(this, instance);
	}
}
