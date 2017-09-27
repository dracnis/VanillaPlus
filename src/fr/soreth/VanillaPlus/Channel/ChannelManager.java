package fr.soreth.VanillaPlus.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import fr.soreth.VanillaPlus.Error;
import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.Manager;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.VanillaPlus;
import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.VanillaPlusExtension;
import fr.soreth.VanillaPlus.IRequirement.Requirement;
import fr.soreth.VanillaPlus.Player.VPPlayer;
import fr.soreth.VanillaPlus.Utils.MediumEntry;
import fr.soreth.VanillaPlus.Utils.Minecraft.ConfigUtils;

/**
 * This is the channel's manager. Configuration in Channel.yml
 * SETTINGS:
 *  BYPASS_MUTE_REQUIREMENT: requirement // default VOID, requirement required to bypass channel mute.
 *  DEFAULT: channel id // log if invalid. Will be used as default channel on player join.
 *  CHANNEL_LIST: // not required.
 *   default: channel id // log if invalid.
 *    REQUIREMENT: requirement // default VOID, requirement required join this channel at player_join.
 *    TALK: false // default false. If true player who can join will talk in this channel.
 * CHANNEL_LIST:
 *  //Channel list
 *
 * @author Soreth.
 */
public class ChannelManager extends Manager<String, Channel> implements Listener{
	private static final String bmp = "BYPASS_MUTE_REQUIREMENT"; 
	Requirement bypassMute;
	Channel defaultChannel;
	List<MediumEntry<Requirement, Channel, Boolean>>alternate = new ArrayList<MediumEntry<Requirement,Channel,Boolean>>();
	public ChannelManager(VanillaPlus instance) {
		super(String.class, Channel.class);
		if(instance == null)return;
		ChannelLoader.load(this);
		Bukkit.getServer().getPluginManager().registerEvents(this, instance);
	}
	public void init(VanillaPlusCore core) {
		if(core == null)return;
		ConfigurationSection section = ConfigUtils.getYaml(core.getInstance(), "Channel", false);
		if(section == null)return;
		ErrorLogger.addPrefix("Channel.yml");
		ConfigurationSection settings = section.getConfigurationSection(Node.SETTINGS.get());
		ErrorLogger.addPrefix(Node.SETTINGS.get());
		if(settings == null){
			Error.MISSING.add();
			for(Channel c : getLoaded()) {
				defaultChannel = c;
				break;
			}
				
		}else{
			bypassMute = new Requirement(settings.get(bmp), core.getMessageCManager());
			defaultChannel = get(settings.getString(Node.DEFAULT.get()), true);	
			ConfigurationSection channels = settings.getConfigurationSection(Node.CHANNEL.getList());
				ErrorLogger.addPrefix(Node.CHANNEL.getList());
				if(channels != null){
					for(String key : channels.getKeys(false)){
						ConfigurationSection current = channels.getConfigurationSection(key);
						Requirement r = new Requirement(current.get(Node.REQUIREMENT.get()), core.getMessageCManager());
						boolean talk = current.getBoolean("TALK", false);
						Channel c = get(key, true);
						if(c == null)continue;
						alternate.add(new MediumEntry<Requirement, Channel, Boolean>(r, c, talk));
					}
				}
				ErrorLogger.removePrefix();
		}
		if(getLoaded().isEmpty())
			ErrorLogger.addError("At least one channel is required, please fix it !");
		ErrorLogger.removePrefix();
		ErrorLogger.removePrefix();
	}
	public void init(VanillaPlusExtension extension) {
		ConfigurationSection section = ConfigUtils.getYaml(extension.getInstance(), "Channel", false);
		if(section == null)return;
		ErrorLogger.addPrefix("Channel.yml");
		ErrorLogger.addPrefix(Node.CHANNEL.getList());
		ConfigurationSection list = section.getConfigurationSection(Node.CHANNEL.getList()); 
		if(list == null)
			Error.INVALID.add();
		else
			super.init(list, extension.getMessageManager());
		ErrorLogger.removePrefix();
		ErrorLogger.removePrefix();
	}
    /**
     * Test if sender can bypass mute.
     *
     * @param sender The player to test.
     * @return true if sender can bypass mute.
     */
	public boolean canBypassMute(VPPlayer sender){
		return bypassMute.has(sender);
	}
	
    /**
     * Get the default server channel.
     *
     * @return The default server channel.
     */
	public Channel getDefaultChannel(){
		return defaultChannel;
	}
    /**
     * Initialize player channel. 
     *
     * @param player The player initialize.
     */
	public void init(VPPlayer player){
		player.setChannel(defaultChannel);
		for(MediumEntry<Requirement, Channel, Boolean>entry : alternate){
			if(entry.getKey().has(player)){
				if(entry.getExtraValue())
					player.setChannel(entry.getValue());
				else
					entry.getValue().addPlayer(player);
			}
		}
	}
		
	@EventHandler
	public void playerTalk(AsyncPlayerChatEvent event){
		event.setCancelled(true);
		VPPlayer player = VanillaPlusCore.getPlayerManager().getPlayer(event.getPlayer());
		player.getChannel().sendMessage(player, Matcher.quoteReplacement(event.getMessage()), false);
	}
}
