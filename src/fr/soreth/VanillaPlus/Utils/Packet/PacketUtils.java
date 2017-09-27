package fr.soreth.VanillaPlus.Utils.Packet;
import java.util.List;
import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.Player.VPPlayer;
import fr.soreth.VanillaPlus.Utils.ReflectionObject;
import fr.soreth.VanillaPlus.Utils.ReflectionUtils;
public class PacketUtils {
	private static Class<?> packet = ReflectionUtils.getServerClass("Packet"); 
	private static Class<?> craftPlayer = ReflectionUtils.getBukkitClass("entity.CraftPlayer");
	public static ReflectionObject craftPacket(String packetName, Class<?>... classes){
		Class<?>packetClass = ReflectionUtils.getServerClass(packetName);
		if(packetClass==null)
			return null;
		return craftPacket(packetClass, classes);
	}
	public static ReflectionObject craftPacket(Class<?>packetClass, Class<?>... classes){
		if(packetClass == null){
			return null;
		}
		ReflectionObject result = new ReflectionObject(packetClass, classes);
		if(result.isGoodType(packet))
			return result;
		return null;
	}
   public static void sendPacket(ReflectionObject packetToSend, VPPlayer p){
	   if(packetToSend==null || !p.isOnline())
		return;
   	
	   if(!packetToSend.isGoodType(packet)){
		   ErrorLogger.addError("Packet isn't a packet");
			return;
	   } 
	   Object cp = craftPlayer.cast(p.getPlayer());
	   Object handle = ReflectionUtils.invoke("getHandle", cp);
	   Object con = ReflectionUtils.getField("playerConnection", handle);
	   ReflectionUtils.invoke(ReflectionUtils.getMethod("sendPacket", con.getClass(), packet), con, packetToSend.getObject());
    }
 
    public static void sendPackets(ReflectionObject packetToSend, List<VPPlayer>players){
    	if(packetToSend==null)
    		return;
 	   if(!packet.isAssignableFrom(packetToSend.getClazz())){
 			ErrorLogger.addError("Packet isn't a packet");
 			return;
 	   }
    	for(VPPlayer player : players){
    		if(player==null)
    			continue;
    		if(!player.isOnline())
    			continue;
    		sendPacket(packetToSend, player);
    				
    	}
    }
}
