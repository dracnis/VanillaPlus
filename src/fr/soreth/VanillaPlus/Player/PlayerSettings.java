package fr.soreth.VanillaPlus.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import fr.soreth.VanillaPlus.Data.SessionValue.BooleanSession;


public enum PlayerSettings {
	ADVERTISMENT(1),
	CHAT		(2),
	MP			(4),
	PARTICLES	(8);
	private static List<PlayerSettings>enums = Arrays.asList(PARTICLES, MP, CHAT, ADVERTISMENT);
	private int binary;
	private PlayerSettings(int binary) {
		this.binary = binary;
	}
	public static int toIntChange(HashMap<PlayerSettings, BooleanSession>settings){
		int change = 0;
		for(Entry<PlayerSettings, BooleanSession>entry : settings.entrySet()){
			if(entry.getValue().changed())
				if(entry.getValue().get())
					change += entry.getKey().binary;
				else
					change -= entry.getKey().binary;
		}
		return change;
	}
	public static HashMap<PlayerSettings, BooleanSession> toSettings(int save){
		HashMap<PlayerSettings, BooleanSession>result = new HashMap<PlayerSettings, BooleanSession>();
		for(PlayerSettings setting : enums){
			if(save > setting.binary){
				result.put(setting, new BooleanSession(true));
				save -= setting.binary;
			}
			else
				result.put(setting, new BooleanSession(false));
		}
		return result;
	}
	public static String toSettings() {
		String result = "";
		int value = 0;
		for(PlayerSettings setting : enums){
			value += setting.binary;
			
		}
		return result + value;
	}
}
