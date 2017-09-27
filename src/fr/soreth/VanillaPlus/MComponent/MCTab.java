package fr.soreth.VanillaPlus.MComponent;

import fr.soreth.VanillaPlus.Localizer;
import fr.soreth.VanillaPlus.Player.VPPlayer;
import fr.soreth.VanillaPlus.Utils.ReflectionObject;
import fr.soreth.VanillaPlus.Utils.Packet.PacketUtils;

public class MCTab extends MComponent{
	private static ReflectionObject packetPlayOutPlayerListHeaderFooter = PacketUtils.craftPacket("PacketPlayOutPlayerListHeaderFooter");
	public MCTab(Localizer localizer, String message) {
		super(localizer, message);
	}
	@Override
	protected void send(VPPlayer player, String message) {
		String[]messages = message.split("\n\n", 2);
		if(messages.length==2){
		    packetPlayOutPlayerListHeaderFooter.instance();
		    packetPlayOutPlayerListHeaderFooter.setDeclaredField("a", parseChatbaseComponent("{\"text\": \"" + messages[0] + "\"}"));
		    packetPlayOutPlayerListHeaderFooter.setDeclaredField("b", parseChatbaseComponent("{\"text\": \"" + messages[1] + "\"}"));
		    PacketUtils.sendPacket(packetPlayOutPlayerListHeaderFooter, player);
		}
	}
}
