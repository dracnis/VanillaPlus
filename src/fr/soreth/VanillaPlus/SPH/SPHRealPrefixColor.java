package fr.soreth.VanillaPlus.SPH;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.soreth.VanillaPlus.Localizer;
import fr.soreth.VanillaPlus.Player.VPPlayer;
import fr.soreth.VanillaPlus.Player.VPSender;

public class SPHRealPrefixColor extends SPH{
	private static Pattern pattern = Pattern.compile("%" + sender + "_real_prefix_color%");
	private static SPHRealPrefixColor instance = new SPHRealPrefixColor();
	@Override
	public String getPH(String key) {
		return "%"+key+"_real_prefix_color%";
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
	@Override
	public String getReplacement(Localizer lang, VPSender sender) {
        return sender instanceof VPPlayer ? ((VPPlayer) sender).getPrefixColor() : EMPTY;
	}
}
