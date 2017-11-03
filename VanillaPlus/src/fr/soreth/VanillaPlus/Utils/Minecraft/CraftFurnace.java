package fr.soreth.VanillaPlus.Utils.Minecraft;

import java.lang.reflect.Method;

import org.bukkit.block.Furnace;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.Utils.ReflectionUtils;

public class CraftFurnace {
	private static Class<?> craftFurnace = ReflectionUtils.getBukkitClass("block.CraftFurnace");
	private static Method getProperty = ReflectionUtils.getDeclaredMethod("getProperty", ReflectionUtils.getServerClass("TileEntityFurnace"), int.class);
	private static Method setProperty = ReflectionUtils.getDeclaredMethod(VanillaPlusCore.getBukkitVersionID() < 10900 ? "b" : "setProperty",
			ReflectionUtils.getServerClass("TileEntityFurnace"), int.class, int.class);
	public static Object getNMSFurnace(Furnace furnace){
		if(furnace == null)return null;
        Object cp = ReflectionUtils.castObject(furnace, craftFurnace);
        Object entityPlayer = ReflectionUtils.invoke("getTileEntity", cp);
        return entityPlayer;
	}
	public static int getProperty(Furnace furnace, int i){
		if(furnace == null)return 0;
		Object nmsFurnace = getNMSFurnace(furnace);
        return (int)ReflectionUtils.invoke(getProperty, nmsFurnace, i);
	}
	public static void setProperty(Furnace furnace, int i, int j){
		if(furnace == null)return;
		Object nmsFurnace = getNMSFurnace(furnace);
        ReflectionUtils.invoke(setProperty, nmsFurnace, i, j);
	}
}
