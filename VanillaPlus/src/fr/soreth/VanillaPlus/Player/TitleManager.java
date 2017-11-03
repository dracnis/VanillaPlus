package fr.soreth.VanillaPlus.Player;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.VanillaPlusExtension;
import fr.soreth.VanillaPlus.Error;
import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.Data.Column;
import fr.soreth.VanillaPlus.Data.IConnection;
import fr.soreth.VanillaPlus.Data.IResultQuery;
import fr.soreth.VanillaPlus.Data.Table;
import fr.soreth.VanillaPlus.Data.Table.IUpdateQuery;
import fr.soreth.VanillaPlus.Data.Column.Type;
import fr.soreth.VanillaPlus.Data.SessionValue.BooleanSession;
import fr.soreth.VanillaPlus.IRequirement.Requirement;
import fr.soreth.VanillaPlus.Icon.Icon;
import fr.soreth.VanillaPlus.Message.Message;
import fr.soreth.VanillaPlus.Utils.Utils;
import fr.soreth.VanillaPlus.Utils.Minecraft.ConfigUtils;

public class TitleManager{
	private HashMap<Integer, Title>titles = new HashMap<>();
	private Icon owned, unOwned, current;
	private String prefix;
	private Table table; 
	private int bigger;
	private Message unlock;
	private Requirement adminUse, adminSwitch, use;
	private DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private Column id = new Column("id", Type.INTEGER, null, true, false, false, true);
	public void init(VanillaPlusCore core) {
		if(core == null)return;
		YamlConfiguration section = ConfigUtils.getYaml(core.getInstance(), "Title", false);
		if(section == null)return;
		ErrorLogger.addPrefix("Title.yml");
		ErrorLogger.addPrefix(Node.SETTINGS.get());
		ConfigurationSection settings = section.getConfigurationSection(Node.SETTINGS.get());
		if(settings==null){
			Error.MISSING_NODE.add(Node.SETTINGS.get());
			adminUse			= new Requirement("", core.getMessageCManager());
			use					= new Requirement("", core.getMessageCManager());
			adminSwitch			= new Requirement("", core.getMessageCManager());
			startDataBase(VanillaPlusCore.getIConnectionManager().get(null));
		}else{
			this.adminUse		= new Requirement(settings.get("ADMIN_USE"), core.getMessageCManager());
			this.use			= new Requirement(settings.get("USE"), core.getMessageCManager());
			this.adminSwitch	= new Requirement(settings.get("SWITCH"), core.getMessageCManager());
			ConfigurationSection current = settings.getConfigurationSection("CURRENT");
			ErrorLogger.addPrefix("CURRENT");
			if(current != null){
				this.current = VanillaPlusCore.getIconManager().create(core.getMessageManager(), current);
			}else{
				Error.INVALID.add();
			}
			ErrorLogger.removePrefix();
			ConfigurationSection owned = settings.getConfigurationSection(Node.OWNED.get());
			ErrorLogger.addPrefix(Node.OWNED.get());
			if(owned != null){
				this.owned = VanillaPlusCore.getIconManager().create(core.getMessageManager(), owned);
			}else{
				Error.INVALID.add();
			}
			ErrorLogger.removePrefix();
			ConfigurationSection unOwned = settings.getConfigurationSection(Node.UNOWNED.get());
			ErrorLogger.addPrefix(Node.UNOWNED.get());
			if(unOwned != null){
				this.unOwned = VanillaPlusCore.getIconManager().create(core.getMessageManager(), unOwned);
			}else{
				Error.INVALID.add();
			}
			ErrorLogger.removePrefix();
			this.unlock			= core.getMessageManager().get(settings.getString("UNLOCK"));
			this.prefix			= settings.getString(Node.PREFIX.get(), "VPPlayer_");
			startDataBase(VanillaPlusCore.getIConnectionManager().get(settings.getString("STORAGE")));
		}
		ErrorLogger.removePrefix();
		ErrorLogger.removePrefix();
	}
	public void init(VanillaPlusExtension extension) {
		if(extension == null)
			return;
		YamlConfiguration section = ConfigUtils.getYaml(extension.getInstance(), "Title", false);
		if(section == null)return;
		ErrorLogger.addPrefix("Title.yml");
		ConfigurationSection titleSub = section.getConfigurationSection(Node.TITLE.getList());
		if(titleSub != null){
			ErrorLogger.addPrefix(Node.TITLE.getList());
			for(String key : titleSub.getKeys(false)){
				ConfigurationSection sub = titleSub.getConfigurationSection(key);
				if(sub == null)
					continue;
				int id = Utils.parseInt(key, 0, true);
				if( id <= 0 || id > Short.MAX_VALUE)
					continue;
				if(id>bigger)
					bigger = id;
				Title title = new Title(id, sub, extension.getMessageCManager());
				titles.put(title.getID(), title);
			}
			ErrorLogger.removePrefix();
		}
		ErrorLogger.removePrefix();
	}
	public boolean hasUse(VPPlayer player){
		return use.has(player);
	}
	public boolean hasAdminUse(VPPlayer player){
		return adminUse.has(player);
	}
	public boolean hasSwitch(VPPlayer player){
		return adminSwitch.has(player);
	}
	private void startDataBase(IConnection connection){
		if(titles.isEmpty())return;
		table = connection.getTable(prefix+"title");
		table.addColumn(id);
		for(int i = 1; i <= bigger ; i++){
			table.addColumn(new Column(i+"", Type.DATE));
		}
		table.validate();
	}
	void load(VPPlayer player){
		if(titles.isEmpty())return;
		IResultQuery result = table.select().where(id, player.getID()).execute();
		if(result.next()){
			Map<Integer, BooleanSession>resultTitle = new LinkedHashMap<Integer, BooleanSession>();
			for(int i : titles.keySet()){
				Object object = result.get(table.getColumn(null, "" + i));
				if(object == null)
					continue;
				resultTitle.put(i, new BooleanSession(true));
			}
			player.setTitles(resultTitle);
			result.close();
		}else{
			result.close();
			create(player.getID());
			load(player);
		}
	}
	void save(VPPlayer player){
		if(titles.isEmpty())return;
		IUpdateQuery result = table.update();
		String now = LocalDateTime.now().format(format);
		for(Entry<Integer, BooleanSession>entry : player.getTitles().entrySet()){
			if(entry.getValue().changed()){
				result.set(table.getColumn(null, "" +(entry.getKey())), entry.getValue().get() ? now : null);
				entry.getValue().save();
			}
		}
		result.where(id, player.getID()).execute();
	}
	void create(int i){
		if(titles.isEmpty())return;
		table.insert().insert(id, i).execute();;
	}
	public Title get(int id){
		return titles.get(id);
	}
	public Icon getOwned(){
		return owned;
	}
	public Icon getUnOwned(){
		return unOwned;
	}
	public Icon getCurrent(){
		return current;
	}
	public void unlock(VPPlayer p, Title t){
		p.addTitle(t.getID());
		unlock.addCReplacement("title_name", t.getName()).addCReplacement("title_lore", t.getLore()).sendTo(p);
	}
}
