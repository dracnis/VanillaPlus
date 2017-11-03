package fr.soreth.VanillaPlus.Utils.Gson;

import fr.soreth.VanillaPlus.VanillaPlusCore;

public abstract class GSon {
	private static final GSon gson = VanillaPlusCore.getBukkitVersionID() < 10802 ? new GSon_1_8() : new GSon_1_8_2();
	public abstract <T> T deserialize(String json, Class<T> type);

	public static <T> T deserializeJson(String json, Class<T> type) {
		return gson.deserialize(json, type);
	}

	public abstract String serialize(Object value);
	public static String serializeJson(Object value) {
		return gson.serialize(value);
	}
}
