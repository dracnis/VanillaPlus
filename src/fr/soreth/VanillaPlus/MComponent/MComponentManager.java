package fr.soreth.VanillaPlus.MComponent;

import java.io.File;
import java.util.HashMap;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import fr.soreth.VanillaPlus.Error;
import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.Localizer;
import fr.soreth.VanillaPlus.Manager;
import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.Message.Message;
import fr.soreth.VanillaPlus.Message.MessageManager;
import fr.soreth.VanillaPlus.Utils.Minecraft.ConfigUtils;

public class MComponentManager extends Manager<String, MComponent>{
	private HashMap<Localizer, YamlConfiguration> config = new HashMap<Localizer, YamlConfiguration>();
	private Plugin plugin;
	private static final MComponent Void = new MCNote(VanillaPlusCore.getDefaultLang(), null);
	private static MComponent enabled, disabled, secondNull, secondOne, second, minuteNull, minuteOne, minute, hourNull, hourOne, hour;
	public MComponentManager(Plugin plugin) {
		super(String.class, MComponent.class);
		MCLoader.load(this);
		if(plugin != null) {
			this.plugin = plugin;
			for(Localizer loc : VanillaPlusCore.getLangs()){
				config.put(loc, ConfigUtils.getYaml(plugin, MessageManager.LANGUAGE + File.separatorChar + loc.getCode(), true));
			}
		}
	}
	public MComponent get(String node){
		return get("BASE", node);
	}
	public MComponent get(String type, String node) {
		if(node == null)
			return Void;
		MComponent result = super.get(node, false);
		if(result!=null)return result;
		else{
			YamlConfiguration config =  this.config.get(VanillaPlusCore.getDefaultLang());
			if(config.contains(node)){
				ErrorLogger.addPrefix(node);
				result = create(type, VanillaPlusCore.getDefaultLang(), config.getString(node));
				ErrorLogger.removePrefix();
			}
			if(result!=null){
				addLangs(result, node);
				super.register(node, result, false);
				return result;
			}
			else{
				Error.MISSING_NODE.add(node);
				return Void;
			}
		}
	}
	public void addLangs(MComponent message, String node){
		for(Localizer loc : VanillaPlusCore.getLangs()){
			if(loc == VanillaPlusCore.getDefaultLang())
				continue;
			YamlConfiguration config = this.config.get(loc);
			if(config.contains(node)){
				message.addLang(loc, config.getString(node));
			}else{
				message.addLang(loc, Error.MISSING_NODE.add(" " + loc.getCode() + " => " + node));
			}
		}
	}
	public void init(ConfigurationSection section) {
		if(section == null)
			enabled = disabled = secondNull = secondOne = second = minuteNull = minuteOne = minute = get(null);
		else{
			enabled		= get(section.getString("ENABLED"));
			disabled	= get(section.getString("DISABLED"));
			secondNull	= get(section.getString("SECOND_NULL"));
			secondOne	= get(section.getString("SECOND_ONE"));
			second		= get(section.getString("SECOND"));
			minuteNull	= get(section.getString("MINUTE_NULL"));
			minuteOne	= get(section.getString("MINUTE_ONE"));
			minute		= get(section.getString("MINUTE"));
			hourNull	= get(section.getString("HOUR_NULL"));
			hourOne		= get(section.getString("HOUR_ONE"));
			hour		= get(section.getString("HOUR"));
		}
	}
	public static MComponent getEnabled(){
		return enabled;
	}
	public static MComponent getDisabled(){
		return disabled;
	}
	public static void addTime(Message message, int second) {
		int hour = second/3600;
		second -= hour*3600;
		int minute = second/60;
		second = second%60;
		message.addCReplacement("second", getSecond(second))
		.addCReplacement("minute", getMinute(minute))
		.addCReplacement("hour", getHour(hour));
	}
	public static void addTime(MComponent component, int second) {
		int hour = second/3600;
		second -= hour*3600;
		int minute = second/60;
		second = second%60;
		component.addCReplacement("second", getSecond(second))
		.addCReplacement("minute", getMinute(minute))
		.addCReplacement("hour", getHour(hour));
	}
	private static MComponent getSecond(int value){
		if(value <= 0)
			return secondNull.addReplacement("value", value+"");
		else if(value == 1)
			return secondOne.addReplacement("value", value+"");
		else
			return second.addReplacement("value", value+"");
	}
	private static MComponent getMinute(int value){
		if(value <= 0)
			return minuteNull.addReplacement("value", value+"");
		else if(value == 1)
			return minuteOne.addReplacement("value", value+"");
		else
			return minute.addReplacement("value", value+"");
	}
	private static MComponent getHour(int value){
		if(value <= 0)
			return hourNull.addReplacement("value", value+"");
		else if(value == 1)
			return hourOne.addReplacement("value", value+"");
		else
			return hour.addReplacement("value", value+"");
	}
	public Plugin getPlugin() {
		return plugin;
	}
}
