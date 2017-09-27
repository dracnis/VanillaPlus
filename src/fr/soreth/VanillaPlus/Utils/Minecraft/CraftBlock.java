package fr.soreth.VanillaPlus.Utils.Minecraft;

import java.lang.reflect.Method;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.Utils.ReflectionUtils;

public class CraftBlock {
	public static Class<?>block = ReflectionUtils.getServerClass("Block");
	public static Object REGISTRY = ReflectionUtils.getDeclaredField("REGISTRY", block);
	public static Method get = ReflectionUtils.getDeclaredMethod(VanillaPlusCore.getBukkitVersionID() < 10900 ? "a" : "getId", REGISTRY.getClass(), int.class);
	public static void setResistance(int id, float amount){
		ReflectionUtils.setDeclaredField("durability", block, ReflectionUtils.invoke(get, REGISTRY, id), amount);
	}
	public static float getDurability(int id){
		return (float) ReflectionUtils.getDeclaredField("strength", block, ReflectionUtils.invoke(get, REGISTRY, id));
	}
	public static void setDurability(int id, float amount){
		ReflectionUtils.setDeclaredField("strength", block, ReflectionUtils.invoke(get, REGISTRY, id), amount);
	}
	public static void setCanExplode(int id, boolean can){
		ReflectionUtils.setDeclaredField(VanillaPlusCore.getBukkitVersionID() < 10900 ? "y" : "s", block, ReflectionUtils.invoke(get, REGISTRY, id), can);
	}
}
