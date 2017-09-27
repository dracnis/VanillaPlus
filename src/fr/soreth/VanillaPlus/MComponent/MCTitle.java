package fr.soreth.VanillaPlus.MComponent;

import java.util.regex.Matcher;

import fr.soreth.VanillaPlus.Localizer;
import fr.soreth.VanillaPlus.Player.VPPlayer;
import fr.soreth.VanillaPlus.Utils.ReflectionObject;
import fr.soreth.VanillaPlus.Utils.ReflectionUtils;
import fr.soreth.VanillaPlus.Utils.Utils;
import fr.soreth.VanillaPlus.Utils.Packet.PacketUtils;

public class MCTitle extends MComponent{
	private static ReflectionObject packetPlayOutTitle = PacketUtils.craftPacket("PacketPlayOutTitle",
			ReflectionUtils.getServerClass("PacketPlayOutTitle$EnumTitleAction"), iChat, int.class, int.class, int.class);
	private static Object titleClass = ReflectionUtils.invoke(ReflectionUtils.getMethod("valueOf",
			ReflectionUtils.getServerClass("PacketPlayOutTitle$EnumTitleAction"), String.class),
			ReflectionUtils.getServerClass("PacketPlayOutTitle$EnumTitleAction"), "TITLE");
	private int fadeIn, stay, fadeOut = 10;
	public MCTitle(Localizer localizer, String message) {
		super(localizer, message);
		String[] temp = message.split(":", 4);
		if(temp.length==4){
			fadeIn = Utils.parseInt(temp[0], 1, true);
			stay = Utils.parseInt(temp[1], 1, true);
			fadeOut = Utils.parseInt(temp[2], 1, true);
			addLang(localizer, temp[3]);
		}
	}
	@Override
	public void addLang(Localizer localizer, String message) {
		String[] temp = message.split(":", 4);
		if(temp.length==4){
			super.addLang(localizer, Matcher.quoteReplacement(temp[3]).replaceAll("\"", "\\\"").replace("'", "\\'"));
		}else
			super.addLang(localizer, Matcher.quoteReplacement(message).replaceAll("\"", "\\\"").replace("'", "\\'"));
	}
	@Override
	protected void send(VPPlayer player, String message) {
	    Object titleJSON = parseChatbaseComponent("{\"text\": \"" + message + "\"}");
		packetPlayOutTitle.instance(titleClass, iChat.cast(titleJSON), fadeIn, stay, fadeOut);
		PacketUtils.sendPacket(packetPlayOutTitle, player);
	}
}
