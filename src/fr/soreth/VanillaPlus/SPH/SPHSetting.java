package fr.soreth.VanillaPlus.SPH;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.soreth.VanillaPlus.Localizer;
import fr.soreth.VanillaPlus.MComponent.MComponentManager;
import fr.soreth.VanillaPlus.Player.PlayerSettings;
import fr.soreth.VanillaPlus.Player.VPPlayer;
import fr.soreth.VanillaPlus.Player.VPSender;

public class SPHSetting extends SPH{
	private static Pattern pattern = Pattern.compile("%" + sender + "_setting_([A-Z]+)%");
	private static List<SPHSetting> instance = new LinkedList<>();
	protected PlayerSettings setting;
	public String getPH(String key){
		return "%" + key + "_setting_" + setting + "%";
	}
	public HashMap<String, List<SPH>> getPHs(String message){
		Matcher m = pattern.matcher(message);
		HashMap<String, List<SPH>>result = new HashMap<String, List<SPH>>();
		while (m.find()) {
			String key = m.group(1);
			List<SPH>s = result.get(key);
			if(s==null){
				s=new ArrayList<SPH>();
				result.put(key, s);
			}
			PlayerSettings setting = PlayerSettings.valueOf(m.group(2));
			if(setting != null)
				s.add(getInstance(setting));
		}
		return result;
	}
	private static SPHSetting getInstance(PlayerSettings setting) {
		for(SPHSetting ps : instance) {
			if(ps.setting == setting)
				return ps;
		}
		SPHSetting ps = new SPHSetting();
		ps.setting = setting;
		instance.add(ps);
		return ps;
	}
	public String getReplacement(Localizer lang, VPSender sender){
		return ( sender instanceof VPPlayer && ((VPPlayer) sender).getSetting(setting) ) ? MComponentManager.getEnabled().getMessage(lang) : MComponentManager.getDisabled().getMessage(lang);
	}
}
