package fr.soreth.VanillaPlus.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.Extension;
import fr.soreth.VanillaPlus.Localizer;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.PlaceH;
import fr.soreth.VanillaPlus.MComponent.MComponent;
import fr.soreth.VanillaPlus.Player.PlayerManager;
import fr.soreth.VanillaPlus.Player.VPPlayer;
import fr.soreth.VanillaPlus.Player.VPSender;

public class Message extends Extension{
	private List<MComponent> messages = new ArrayList<MComponent>();
	public Message(){}
	public Message(MessageManager manager, ConfigurationSection messageNode) {
		if(messageNode == null)
			return;
		for(String key : messageNode.getKeys(false)){
			if(key.equalsIgnoreCase(Node.TYPE.get()))
				continue;
			ErrorLogger.addPrefix(key);
			MComponent message = manager.getComponentManager().get(key, messageNode.getCurrentPath()+"."+key);
			ErrorLogger.removePrefix();
			if(message != null)
				messages.add(message);
		}
	}
	public Message(MessageManager manager, String message) {
		if(message == null)
			return;
		MComponent component = manager.getComponentManager().get(message);
		if(component != null)
			messages.add(component);
	}
	public void sendTo(VPSender receiver){
		if(receiver instanceof VPPlayer)
			send(Arrays.asList((VPPlayer)receiver));
		else {
    		String message = getMessage();
    		if(message != null && !message.isEmpty())
			receiver.sendMessage(message);
		}
	}
	public void sendTo(VPPlayer receiver){
		send(Arrays.asList(receiver));
	}
	public void send(List<VPPlayer> receiver){
		VanillaPlusCore.getPlayerManager();
		HashMap<Localizer, List<VPPlayer>>receivers = PlayerManager.toHashMap(receiver);
		send(receivers);
	}
	protected void send(HashMap<Localizer, List<VPPlayer>> receivers){
		for(MComponent message : messages)
			message.sendMessage(receivers);
	}
	public void send(){
		send(getPlayers(null));
	}
	public void send(VPPlayer sender){
		addSReplacement(PlaceH.SENDER.get(), sender);
		send(getPlayers(sender));
	}
	List<VPPlayer> getPlayers(VPPlayer sender){
		return sender == null ? VanillaPlusCore.getPlayerManager().getOnlinePlayers() : sender.getChannel().getListeners(sender, false);
	}
	public String getMessage(){
		return getMessage(VanillaPlusCore.getDefaultLang());
	}
	public String getMessage(Localizer loc){
		for(MComponent component : messages) {
			String result = component.getMessage(loc);
			if(!result.isEmpty())
				return result;
		}
		return  null;
	}
	/**
	 * 
	 * @param placeholder Raw PlaceHolder ( without % it will be added )
	 * @param replacement
	 */
	public Message addReplacement(String placeholder, String replacement){
		for(MComponent message : messages)
			message.addReplacement(placeholder, replacement);
		return this;
	}
	/**
	 * 
	 * @param placeholder Raw PlaceHolder ( without % )
	 * @param sender replacement
	 */
	public Message addSReplacement(String placeholder, VPSender sender){
		for(MComponent message : messages)
			message.addSenderReplacement(placeholder, sender);
		return this;
	}
	/**
	 * 
	 * @param placeholder Raw PlaceHolder ( without % )
	 * @param message replacement
	 */
	public Message addCReplacement(String placeholder, MComponent base){
		for(MComponent message : messages)
			message.addCReplacement(placeholder, base);
		return this;
	}

}
