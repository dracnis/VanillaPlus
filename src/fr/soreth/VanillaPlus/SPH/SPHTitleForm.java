package fr.soreth.VanillaPlus.SPH;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.soreth.VanillaPlus.Localizer;
import fr.soreth.VanillaPlus.Player.VPPlayer;
import fr.soreth.VanillaPlus.Player.VPSender;

public class SPHTitleForm extends SPH{
	private static Pattern pattern = Pattern.compile("%\"((?:(?!\"_).)*)\"_" + sender + "_title_name_form_\"((?:(?!\"%).)*)\"%");
	private static List<SPHTitleForm> instance = new LinkedList<>();
	private String prefix, suffix;
	public String getPH(String key){
		return "%\"" + prefix + "\"_" + key + "_title_name_form_\"" + suffix + "%";
	}
	public HashMap<String, List<SPH>> getPHs(String message){
		Matcher m = pattern.matcher(message);
		HashMap<String, List<SPH>>result = new HashMap<String, List<SPH>>();
		while (m.find()) {
			String prefix = m.group(1);
			String key = m.group(2);
			String suffix = m.group(3);
			List<SPH>s = result.get(key);
			if(s==null){
				s=new ArrayList<SPH>();
				result.put(key, s);
			}
			s.add(getInstance(prefix, suffix));
		}
		return result;
	}
	private static SPHTitleForm getInstance(String prefix, String suffix) {
		for(SPHTitleForm sph : instance) {
			if(sph.prefix.equals(prefix) && sph.suffix.equals(suffix))
				return sph;
		}
		SPHTitleForm sph = new SPHTitleForm();
		sph.prefix = prefix;
		sph.suffix = suffix;
		instance.add(sph);
		return sph;
	}
	public String getReplacement(Localizer lang, VPSender sender){
		if(!(sender instanceof VPPlayer) || ((VPPlayer) sender).getTitle()==null)
			return EMPTY;
		return prefix+((VPPlayer) sender).getTitle().getName().getMessage(lang)+suffix;
	}
}
