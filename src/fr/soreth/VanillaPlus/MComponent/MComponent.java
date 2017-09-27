package fr.soreth.VanillaPlus.MComponent;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.Localizer;
import fr.soreth.VanillaPlus.PH.PlaceHolder;
import fr.soreth.VanillaPlus.Player.VPPlayer;
import fr.soreth.VanillaPlus.Player.VPSender;
import fr.soreth.VanillaPlus.SPH.SPH;
import fr.soreth.VanillaPlus.Utils.ReflectionObject;
import fr.soreth.VanillaPlus.Utils.ReflectionUtils;
import fr.soreth.VanillaPlus.Utils.Packet.PacketUtils;

public class MComponent{
	protected static Class<?> iChat = ReflectionUtils.getServerClass("IChatBaseComponent");
	private boolean split = false;
	protected static ReflectionObject packetPlayOutChat = PacketUtils.craftPacket("PacketPlayOutChat", iChat, 
			VanillaPlusCore.getBukkitVersionID() < 101200 ? byte.class : ReflectionUtils.getServerClass("ChatMessageType"));
	private static Class<?> chat = ReflectionUtils.getServerClass(VanillaPlusCore.getBukkitVersionID() < 10802 ? "ChatSerializer"
			: "IChatBaseComponent$ChatSerializer");
	protected static Method parseMethod = ReflectionUtils.getMethod("a", chat, String.class);
	private HashMap<Localizer, String> message = new HashMap<Localizer, String>();
	
	private List<PlaceHolder> pHs = new ArrayList<PlaceHolder>();
	private List<SPH> receiversPHs = new ArrayList<SPH>();
	private Map<String, List<SPH>> sPHs = new HashMap<String, List<SPH>>();

	private HashMap<String, String> replacement = new HashMap<String, String>();
	private HashMap<String, MComponent> replacementComponent = new HashMap<String, MComponent>();
	private HashMap<String, VPSender> tempSReplacement = new HashMap<String, VPSender>();
	
	public MComponent(Localizer localizer, String message){
		if(message == null)
			message = "";
		if(message.length()==0) {
			return;
		}
		split = message.contains("\n");
		addLang(localizer, message);
	}
	public void addLang(Localizer localizer, String message){
		if(this.message.containsKey(localizer))
			return;
		for(PlaceHolder placeholder : VanillaPlusCore.getPHManager().getPlaceHolders(message)){
			if(placeholder.isStatic())
				message = message.replaceAll(placeholder.getPH(), placeholder.getReplacement(localizer));
			else
				this.pHs.add(placeholder);
		}
		if(this.message.isEmpty()){
			sPHs = VanillaPlusCore.getSPlaceHolderManager().getPlaceHolders(message);
			receiversPHs = sPHs.get("player");
			sPHs.remove("player");
			if(receiversPHs == null)
				receiversPHs = new ArrayList<SPH>();
		}
		this.message.put(localizer, message == null ? "" : ChatColor.translateAlternateColorCodes('&', message));
	}
	
	public void sendMessage(HashMap<Localizer, List<VPPlayer>> receivers){
		for(Localizer lang : receivers.keySet()){
			String message = getMessage(lang);
			for(VPPlayer player : receivers.get(lang)){
				send(player, getMessage(message, player, lang));
			}
		}
	}
	protected void send(VPPlayer player, String message){
		player.sendMessage(message);
	}	
	public MComponent addReplacement(String placeholder, String replacement){
		placeholder = "%" + placeholder + "%";
		this.replacement.put(placeholder, replacement);
		return this;
	}
	public MComponent addSenderReplacement(String placeholder, VPSender replacement){
		tempSReplacement.put(placeholder, replacement);
		return this;
	}
	public MComponent addCReplacement(String placeholder, MComponent component) {
		if(component == null)return this;
		placeholder = "%" + placeholder + "%";
		this.replacementComponent.put(placeholder, component);
		return this;
	}
	public String getMessage() {
		return getMessage(VanillaPlusCore.getDefaultLang());
	}
	protected static Object parseChatbaseComponent(String s){
		return ReflectionUtils.invoke(parseMethod, chat, s);
	}
	public boolean isStatic(){
		if(tempSReplacement.isEmpty())
		if(replacement.isEmpty())
		if(pHs.isEmpty())
		if(receiversPHs.isEmpty())
		if(sPHs.isEmpty())
			return true;
		return false;
	}
	public String getMessage(Localizer loc) {
		String message = this.message.get(loc);
		return getMessage(loc, message);
	}
	public String getRawMessage(Localizer loc) {
		return this.message.get(loc);
	}
	public String getMessage(Localizer loc, String message) {
		for(Entry<String, MComponent> entry : replacementComponent.entrySet())
			if(entry.getValue() != null)
				message = message.replace(entry.getKey(), entry.getValue().getMessage(loc));
		for(PlaceHolder place : pHs)
			message = message.replaceAll(place.getPH(), place.getReplacement(loc));
		for(Entry<String, List<SPH>> entry : sPHs.entrySet()){
			VPSender player = tempSReplacement.get(entry.getKey());
			if(player != null)
				for(SPH place : entry.getValue())
					message = message.replaceAll(place.getPH(entry.getKey()), place.getReplacement(loc, player));
		}
		//Moved down to support replacement
		for(Entry<String, String> entry : replacement.entrySet())
			message = message.replace(entry.getKey(), entry.getValue());
		return message;
	}
	public String getMessage(String message, VPSender receiver, Localizer lang) {
		for(SPH place : receiversPHs)
			message = message.replaceAll(place.getPH("player"), place.getReplacement(lang, receiver));
		return message;
	}
	public String getMessage(VPSender player){
		Localizer loc = player.getLanguage();
		return getMessage(player, loc);
	}
	public String getMessage(VPSender player, Localizer loc){
		return getMessage(getMessage(loc), player, loc);
	}
	public boolean split(){
		return split;
	}
	public void setSplit(boolean split){
		this.split = split;
	}
	public List<PlaceHolder> getPlaceHolders() {
		return pHs;
	}
	public List<SPH> getPlayerPlaceHolders() {
		return receiversPHs;
	}
	public List<String> getSplitMessage(VPPlayer player, Localizer loc) {
		String result = getMessage(player, loc);
		if(result == null)
			return new ArrayList<String>();
		return Arrays.asList(result.split("\n"));
	}
}
