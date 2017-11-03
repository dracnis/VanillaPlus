package fr.soreth.VanillaPlus.Utils.Gson;

import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSyntaxException;

public class GSon_1_8 extends GSon {
	private final Gson gson = new Gson();
	@Override
	public <T> T deserialize(String json, Class<T> type) {
		if (json == null) {
			return null;
		}
		try {
			T obj = gson.fromJson(json, type);
			return (T)type.cast(obj);
		}catch (JsonSyntaxException ex){
			ex.printStackTrace();
		}
		return null;
	}
	@Override
	public String serialize(Object value) {
		if (value == null) {
			return null;
		}
		try {
			return gson.toJson(value);
		}catch (JsonSyntaxException ex){
			ex.printStackTrace();
		}
		return null;
	}

}
