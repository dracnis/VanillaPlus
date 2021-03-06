package fr.soreth.VanillaPlus.SPH;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.soreth.VanillaPlus.Localizer;
import fr.soreth.VanillaPlus.Player.VPPlayer;
import fr.soreth.VanillaPlus.Player.VPSender;

public class SPHTitleLore extends SPH{
	private static Pattern pattern = Pattern.compile("%" + sender + "_title_lore%");
	private static SPHTitleLore instance = new SPHTitleLore();
	public String getPH(String key){
		return "%" + key + "_title_lore%";
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
			s.add(instance);
		}
		return result;
	}
	public String getReplacement(Localizer lang, VPSender sender){
		if(!(sender instanceof VPPlayer) || ((VPPlayer) sender).getTitle()==null)
			return "";
		return ((VPPlayer) sender).getTitle().getLore().getMessage(lang);
	}
}
