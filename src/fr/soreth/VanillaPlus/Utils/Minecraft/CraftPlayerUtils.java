package fr.soreth.VanillaPlus.Utils.Minecraft;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.Player.VPPlayer;
import fr.soreth.VanillaPlus.Utils.ReflectionObject;
import fr.soreth.VanillaPlus.Utils.ReflectionUtils;
import fr.soreth.VanillaPlus.Utils.Packet.PacketUtils;


public class CraftPlayerUtils {
	//private static Class<?> craftPlayer = ReflectionUtils.getBukkitClass("entity.CraftPlayer");
	private static Class<?> nmsPlayer = ReflectionUtils.getServerClass("EntityPlayer");
	private static Class<?> PacketPlayOutPlayerInfo = ReflectionUtils.getServerClass("PacketPlayOutPlayerInfo");
	private static Class<?> PacketPlayOutRespawn = ReflectionUtils.getServerClass("PacketPlayOutRespawn");
	private static Object removePlayerEnum = ReflectionUtils.invoke(ReflectionUtils.getMethod("valueOf",
			ReflectionUtils.getServerClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction"), String.class),
			ReflectionUtils.getServerClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction"), "REMOVE_PLAYER");
	private static Object addPlayerEnum = ReflectionUtils.invoke(ReflectionUtils.getMethod("valueOf",
			ReflectionUtils.getServerClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction"), String.class),
			ReflectionUtils.getServerClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction"), "ADD_PLAYER");
	private static Class<?> enumDifficulty = ReflectionUtils.getServerClass("EnumDifficulty");
	private static Method getDifficulty = ReflectionUtils.getMethod("getById", enumDifficulty, int.class);
	private static Class<?> worldType = ReflectionUtils.getServerClass("WorldType");
	private static Class<?> enumGamemode = ReflectionUtils.getServerClass(VanillaPlusCore.getBukkitVersionID() < 101000 ? "WorldSettings$EnumGamemode" : "EnumGamemode");
	private static Method getGamemode = ReflectionUtils.getMethod("getById", enumGamemode, int.class);
	private static Method getAbsorptionHearts = ReflectionUtils.getDeclaredMethod("getAbsorptionHearts", ReflectionUtils.getServerClass("EntityHuman"));
	private static Method setAbsorptionHearts = ReflectionUtils.getDeclaredMethod("setAbsorptionHearts", ReflectionUtils.getServerClass("EntityHuman"), float.class);
	
	//private static HashMap<String, String>uuidLink = new HashMap<String, String>();
	//private static HashMap<String, LiteEntry<String, String>>textureLink = new HashMap<>();
	public static Float getAbso(Player player){
		return (Float) ReflectionUtils.invoke(getAbsorptionHearts, getNMSPlayer(player));
	}
	public static Float setAbso(Player player, float abso){
		return (Float) ReflectionUtils.invoke(setAbsorptionHearts, getNMSPlayer(player), abso);
	}
	public static Object getNMSPlayer(Player player){
		return ReflectionUtils.invoke("getHandle", player);
	}
	private static GameProfile getProfile(Player player){
		return (GameProfile) ReflectionUtils.invoke("getProfile", getNMSPlayer(player));
	}
	public static void setName(VPPlayer player, String nick){
		if((player == null) || (!player.isOnline())) {
			return;
		}
		GameProfile gp = getProfile(player.getPlayer());
		if(gp != null){
			ReflectionUtils.setDeclaredField("name", gp, nick);
		}
	}
	public static void hide(VPPlayer player){
		Player p = player.getPlayer();
		Object nmsPlay = getNMSPlayer(p);
		Object arrayPlayer = ReflectionUtils.getArrayOf(nmsPlayer, nmsPlay);
		ReflectionObject del = PacketUtils.craftPacket(PacketPlayOutPlayerInfo, removePlayerEnum.getClass(), arrayPlayer.getClass())
				.instance(removePlayerEnum, arrayPlayer);
		for(VPPlayer vp : VanillaPlusCore.getPlayerManager().getOnlinePlayers())
			PacketUtils.sendPacket(del, vp);
	}
	@SuppressWarnings("deprecation")
	public static void show(VPPlayer player) {
		Player p = player.getPlayer();
		Object nmsPlay = getNMSPlayer(p);
		Object arrayPlayer = ReflectionUtils.getArrayOf(nmsPlayer, nmsPlay);
		final ReflectionObject add = PacketUtils.craftPacket(PacketPlayOutPlayerInfo, addPlayerEnum.getClass(), arrayPlayer.getClass())
				.instance(addPlayerEnum, arrayPlayer);
		Object difficulty = ReflectionUtils.invoke(getDifficulty, enumDifficulty, player.getLocation().getWorld().getDifficulty().getValue());
		Object type = ReflectionUtils.invoke("getType", worldType, player.getLocation().getWorld().getWorldType().getName());
		Object gamemode = ReflectionUtils.invoke(getGamemode, enumGamemode, player.getGameMode().getValue());
		final ReflectionObject respawn = PacketUtils.craftPacket(PacketPlayOutRespawn, int.class, difficulty.getClass(), type.getClass(), gamemode.getClass())
				.instance(0, difficulty, type, gamemode);
		boolean flying = p.isFlying();
		Location location = player.getLocation();
		int level = player.getLevel();
		float xp = p.getExp();
		double maxHealth = p.getMaxHealth();
		double health = p.getHealth();
		PacketUtils.sendPacket(respawn, player);        
		p.setFlying(flying);
		player.teleport(location);
		p.updateInventory();
		player.setLevel(level);
		p.setExp(xp);
		p.setMaxHealth(maxHealth);
		p.setHealth(health);
		for(VPPlayer vp : VanillaPlusCore.getPlayerManager().getOnlinePlayers())
			PacketUtils.sendPacket(add, vp);
	}
	@SuppressWarnings("deprecation")
	public static void updateSelf(VPPlayer player){
		Player p = player.getPlayer();
		Object nmsPlay = getNMSPlayer(p);
		Object arrayPlayer = ReflectionUtils.getArrayOf(nmsPlayer, nmsPlay);
		ReflectionObject del = PacketUtils.craftPacket(PacketPlayOutPlayerInfo, removePlayerEnum.getClass(), arrayPlayer.getClass())
				.instance(removePlayerEnum, arrayPlayer);
		final ReflectionObject add = PacketUtils.craftPacket(PacketPlayOutPlayerInfo, addPlayerEnum.getClass(), arrayPlayer.getClass())
				.instance(addPlayerEnum, arrayPlayer);
		Object difficulty = ReflectionUtils.invoke(getDifficulty, enumDifficulty, player.getLocation().getWorld().getDifficulty().getValue());
		Object type = ReflectionUtils.invoke("getType", worldType, player.getLocation().getWorld().getWorldType().getName());
		Object gamemode = ReflectionUtils.invoke(getGamemode, enumGamemode, player.getGameMode().getValue());
		final ReflectionObject respawn = PacketUtils.craftPacket(PacketPlayOutRespawn, int.class, difficulty.getClass(), type.getClass(), gamemode.getClass())
				.instance(0, difficulty, type, gamemode);
		PacketUtils.sendPacket(del, player);
		boolean flying = p.isFlying();
		Location location = player.getLocation();
		int level = player.getLevel();
		float xp = p.getExp();
		double maxHealth = p.getMaxHealth();
		double health = p.getHealth();
		PacketUtils.sendPacket(respawn, player);        
		p.setFlying(flying);
		player.teleport(location);
		p.updateInventory();
		player.setLevel(level);
		p.setExp(xp);
		p.setMaxHealth(maxHealth);
		p.setHealth(health);
		PacketUtils.sendPacket(add, player);
	}
	public static void setSkin(VPPlayer player, String name){
		if((player == null) || (!player.isOnline())) {
			return;
		}
		GameProfile gp = getProfile(player.getPlayer());
		if(gp != null){
			PropertyMap pro = gp.getProperties();
			try {
				HttpURLConnection httpConnection = (HttpURLConnection)new URL("https://use.gameapis.net/mc/player/profile/"+name).openConnection();
			    httpConnection.setConnectTimeout(3000);
			    httpConnection.setReadTimeout(6000);
			    httpConnection.setRequestProperty("Content-Type", "application/json");
			    httpConnection.setRequestProperty("User-Agent", "VanillPlus-Bukkit-Plugin");
			    BufferedReader reader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
			    String line = "";
			    while (true){
			    	String l = reader.readLine();
			    	if(l!=null)
			    		line += l;
			    	else
			    		break;
			    }
			    if (!line.isEmpty() && (!line.startsWith("{\"error\""))){
			    	JSONObject json =  (JSONObject) ((JSONArray) ((JSONObject) new JSONParser().parse(line)).get("properties")).get(0);
			    	pro.clear();
			    	pro.put("textures", new Property("textures", json.get("value").toString(), json.get("signature").toString()));
			    }
				/*
				String uuid = uuidLink.get(name);
				if(uuid == null){
					HttpURLConnection httpConnection = (HttpURLConnection)new URL("https://api.mojang.com/users/profiles/minecraft/"+name).openConnection();
				    httpConnection.setConnectTimeout(3000);
				    httpConnection.setReadTimeout(6000);
				    httpConnection.setRequestProperty("Content-Type", "application/json");
				    httpConnection.setRequestProperty("User-Agent", "ChangeSkin-Bukkit-Plugin");
				    BufferedReader reader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
				    String line = reader.readLine();
				    if ((line != null) && (!line.equals("null"))){
				    	uuid = (String)((JSONObject) new JSONParser().parse(line)).get("id");
				    	if( uuid == null) return;
				    	else uuidLink.put(name, uuid);
				    }else
				    	return;
				}
				LiteEntry<String, String>texture = textureLink.get(uuid);
				if(texture == null) {
					HttpURLConnection httpConnection = (HttpURLConnection)new URL("https://sessionserver.mojang.com/session/minecraft/profile/"+uuid+"?unsigned=false").openConnection();
				    httpConnection.setConnectTimeout(3000);
				    httpConnection.setReadTimeout(6000);
				    httpConnection.setRequestProperty("Content-Type", "application/json");
				    httpConnection.setRequestProperty("User-Agent", "ChangeSkin-Bukkit-Plugin");
				    BufferedReader reader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
				    String line = reader.readLine();
				    if ((line != null) && (!line.equals("null"))){
				    	JSONObject json =  (JSONObject) ((JSONArray) ((JSONObject) new JSONParser().parse(line)).get("properties")).get(0);
				    	pro.clear();
				    	textureLink.put(uuid, new LiteEntry<String, String>(json.get("value").toString(), json.get("signature").toString()));
				    	pro.put("textures", new Property("textures", json.get("value").toString(), json.get("signature").toString()));
				    }
				}
				*/
			} catch (ParseException | IOException e) {
				e.printStackTrace();
			}
		}	
	}
	public static int getPing(Player player) {
		return (int) ReflectionUtils.getDeclaredField("ping", getNMSPlayer(player));
	}
}
