package fr.soreth.VanillaPlus.Utils.Gson;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class GSon_1_8_2 extends GSon {
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
