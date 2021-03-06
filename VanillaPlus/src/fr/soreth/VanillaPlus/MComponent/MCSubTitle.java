package fr.soreth.VanillaPlus.MComponent;

import java.util.regex.Matcher;

import fr.soreth.VanillaPlus.Localizer;
import fr.soreth.VanillaPlus.Player.VPPlayer;
import fr.soreth.VanillaPlus.Utils.ReflectionObject;
import fr.soreth.VanillaPlus.Utils.ReflectionUtils;
import fr.soreth.VanillaPlus.Utils.Packet.PacketUtils;

public class MCSubTitle extends MComponent{
	private static ReflectionObject packetPlayOutTitle = PacketUtils.craftPacket("PacketPlayOutTitle",
			MCTitle.Title_Action_Class, iChat);
	private static Object subTitleClass = ReflectionUtils.invoke(ReflectionUtils.getMethod("valueOf", 
			MCTitle.Title_Action_Class, String.class), 
			MCTitle.Title_Action_Class, "SUBTITLE");
	public MCSubTitle(Localizer localizer, String message) {
		super(localizer, Matcher.quoteReplacement(message));
	}
	@Override
	public void addLang(Localizer localizer, String message) {
		super.addLang(localizer, Matcher.quoteReplacement(message).replaceAll("\"", "\\\"").replace("'", "\\'"));
	}
	@Override
	protected void send(VPPlayer player, String message) {
	    Object subTitleJSON = parseChatbaseComponent("{\"text\": \"" + message + "\"}");
	    packetPlayOutTitle.instance(subTitleClass, iChat.cast(subTitleJSON) );
	    PacketUtils.sendPacket(packetPlayOutTitle, player);
	}
}
