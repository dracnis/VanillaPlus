package fr.soreth.VanillaPlus.Message;

import java.util.Map.Entry;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.Error;
import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.Manager;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.MComponent.MComponentManager;
import fr.soreth.VanillaPlus.Utils.Minecraft.ConfigUtils;

public class MessageManager extends Manager<String, Message>{
	private static Message voidMessage = new Message();
	public final static String LANGUAGE = "Locale";
	private YamlConfiguration config;
	private MComponentManager baseManager;
	public MessageManager(MComponentManager manager){
		super(String.class, Message.class);
		MessageLoader.load(this);
		this.baseManager = manager;
		register(Node.VOID.get(), voidMessage, false);
		config = ConfigUtils.getYaml(manager.getPlugin(), LANGUAGE + "/" + VanillaPlusCore.getDefaultLang().getCode(), true);
	}
	public Message get(String node){
		if(node == null || node.isEmpty())
			return voidMessage;
		Message result = super.get(node, false);
		if(result!=null)
			return result;
		else{
			Object o = config.get(node);
			if(o != null){
				if(o instanceof ConfigurationSection) {
					ErrorLogger.addPrefix(node);
					ConfigurationSection messageNode = (ConfigurationSection) o;
					result = create(messageNode.getString(Node.TYPE.get(), Node.BASE.get()), this, messageNode);
					ErrorLogger.removePrefix();
				}else if(o instanceof String)
					result = new Message(this, node);
			}
			if(result!=null){
				super.register(node, result, false);
				return result;
			}
			else{
				Error.MISSING_NODE.add(node);
				return voidMessage;
			}
		}
	}
	public String getPath(Message message){
		for(Entry<String, Message>entry : extensions.entrySet())
			if(message == entry.getValue())
				return entry.getKey();
		return null;
	}
	public MComponentManager getComponentManager(){
		return baseManager;
	}
}
