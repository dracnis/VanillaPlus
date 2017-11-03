package fr.soreth.VanillaPlus.StatType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.VanillaPlusExtension;
import fr.soreth.VanillaPlus.Error;
import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.Manager;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.VanillaPlus;
import fr.soreth.VanillaPlus.Data.Column;
import fr.soreth.VanillaPlus.Data.IConnection;
import fr.soreth.VanillaPlus.Data.IResultQuery;
import fr.soreth.VanillaPlus.Data.Table;
import fr.soreth.VanillaPlus.Data.Table.IUpdateQuery;
import fr.soreth.VanillaPlus.Data.Column.Type;
import fr.soreth.VanillaPlus.Data.SessionValue.IntSession;
import fr.soreth.VanillaPlus.Player.VPPlayer;
import fr.soreth.VanillaPlus.Utils.Utils;
import fr.soreth.VanillaPlus.Utils.Minecraft.ConfigUtils;

public class StatManager extends Manager<Short, Stat> {
	private Column id = new Column("id", Type.INTEGER, null, true, false, false, true);
	public StatManager(){
		super(Short.class, Stat.class);
		StatLoader.load(this);
	}
	private Table statTable; 
	private int bigger;
	public void init(VanillaPlusCore core) {
		ConfigurationSection section = ConfigUtils.getYaml(core.getInstance(), "Stat", false);
		ErrorLogger.addPrefix("Stat.yml");
		ConfigurationSection settings = section == null ? null : section.getConfigurationSection(Node.SETTINGS.get());
		ErrorLogger.addPrefix(Node.SETTINGS.get());
		if(settings==null){
			startDataBase(VanillaPlusCore.getIConnectionManager().get(null));
			
		}else{
			startDataBase(VanillaPlusCore.getIConnectionManager().get(settings.getString("STORAGE")));
		}
		ErrorLogger.removePrefix();
		if(!extensions.isEmpty()) {
			new BukkitRunnable() {
				
				@Override
				public void run() {
					for(VPPlayer player : VanillaPlusCore.getPlayerManager().getOnlinePlayers())
						update(player);
						
				}
			}.runTaskTimer(VanillaPlus.getInstance(), 20*60, 20*60);
		}
		ErrorLogger.removePrefix();
	}
	public void init(VanillaPlusExtension extension) {
		ConfigurationSection section = ConfigUtils.getYaml(extension.getInstance(), "Stat", false);
		if(section == null)return;
		ErrorLogger.addPrefix("Stat.yml");
		ConfigurationSection statSub = section.getConfigurationSection("STAT_LIST");
		if(statSub != null){
			ErrorLogger.addPrefix("STAT_LIST");
			for(String key : statSub.getKeys(false)){
				ErrorLogger.addPrefix(key);
				ConfigurationSection sub = statSub.getConfigurationSection(key);
				if(sub == null){
					Error.INVALID.add();
				}else{
					int id = Utils.parseInt(key, 0, true);
					if(id==0 || id < Short.MIN_VALUE || id > Short.MAX_VALUE)
						Error.INVALID.add();
					else{
						if(id>bigger)
							bigger = id;
						Stat stat = create(sub.getString(Node.TYPE.get(), Node.BASE.get()), (short)id, sub, extension.getMessageCManager());
						register((short) id, stat, true);
					}
				}
				ErrorLogger.removePrefix();
			}
			ErrorLogger.removePrefix();
		}
		ErrorLogger.removePrefix();
	}
	private void startDataBase(IConnection connection){
		if(extensions.isEmpty())return;
		statTable = connection.getTable("VPPlayer_stat");
		statTable.addColumn(id);
		for(int i = 1; i <= bigger ; i++){
			statTable.addColumn(new Column(i+"", Type.INTEGER, 0, true, false, false, false));
		}
		statTable.validate();
		
	}
	public void load(VPPlayer player){
		if(extensions.isEmpty())return;
		IResultQuery result = statTable.select().where(id, player.getID()).execute();
		if(result.next()){
			Map<Integer, IntSession>playerStat = player.getStatistics();
			for(int i : extensions.keySet()){
				IntSession temp = playerStat.get(i);
				Integer amount = 0;
				if(i>0)
					amount = result.getInt(statTable.getColumn(null, "" + i ));
				if(temp == null)
					temp = new IntSession(amount);
				else
					temp.setLast(amount);
				playerStat.put(i, temp);
			}
			player.setStatistics(playerStat);
		}else{
			result.close();
			create(player.getID());
			load(player);
		}
	}
	void update(VPPlayer player){
		for(Stat stat : extensions.values())
			stat.update(player);
	}
	public void save(VPPlayer player){
		if(extensions.isEmpty())return;
		for(Stat stat : extensions.values())
			stat.update(player);
		IUpdateQuery result = statTable.update();
		for(Entry<Integer, IntSession>entry : player.getStatistics().entrySet()){
			if(entry.getKey()<1)continue;
			if(entry.getValue().changed()){
				if(entry.getValue().set())
					result.set(statTable.getColumn(null, ""  +(entry.getKey())), ""+entry.getValue().getChange());
				else
					result.add(statTable.getColumn(null, ""  +(entry.getKey())), entry.getValue().getChange());
				entry.getValue().save();
			}
		}
		result.where(id, player.getID()).execute();
	}
	void create(int i){
		if(extensions.isEmpty())return;
		statTable.insert().insert(id, i).execute();;
	}
	public Stat get(String name){
		for(Stat stat : extensions.values())
			if(stat.getAlias().equalsIgnoreCase(name))
				return stat;
		return null;
	}
	public List<String> getStatList(String prefix) {
		List<String>result = new ArrayList<String>();
		for(Stat stat : getStat(prefix))
			result.add(stat.getAlias());
		return result;
	}
	public List<Stat> getStat(String prefix) {
		List<Stat>result = new ArrayList<Stat>();
		for(Stat stat : extensions.values()){
			if(stat.getAlias().toLowerCase().startsWith(prefix))
				result.add(stat);
		}
		return result;
	}
}
