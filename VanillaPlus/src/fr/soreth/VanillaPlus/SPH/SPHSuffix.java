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

public class SPHSuffix extends SPH{
	private static Pattern pattern = Pattern.compile("%" + sender + "_suffix(?:_\"((?:(?!\"%).)*)\")?%");
	private static List<SPHSuffix> instance = new LinkedList<>();
	private String na;
	@Override
	public String getPH(String key) {
		return "%"+key+"_suffix"+ (na == null ? EMPTY : ("_\"" + na + "\"")) +"%";
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
			String suffix = null;
			if(m.groupCount() > 1){
				suffix = m.group(2);
			}
			s.add(getInstance(suffix));
		}
		return result;
	}
	private static SPHSuffix getInstance(String suffix) {
		for(SPHSuffix sphp : instance) {
			if(sphp.na == suffix || sphp.na.equals(suffix))
				return sphp;
		}
		SPHSuffix sphp = new SPHSuffix();
		sphp.na = suffix;
		instance.add(sphp);
		return sphp;
	}
	@Override
	public String getReplacement(Localizer lang, VPSender sender) {
        return  ( sender instanceof VPPlayer && !((VPPlayer) sender).isNick() ) ? ((VPPlayer) sender).getSuffix() : na == null ? EMPTY : na;
	}
}
