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

public class SPHPrefix extends SPH{
	private static Pattern pattern = Pattern.compile("%" + sender + "_prefix(?:_\"((?:(?!\"%).)*)\")?%");
	private static List<SPHPrefix> instance = new LinkedList<>();
	private String na;
	@Override
	public String getPH(String key) {
		return "%"+key+"_prefix"+ (na == null ? "" : ("_\"" + na + "\"")) +"%";
	}
	public HashMap<String, List<SPH>> getPHs(String message){
		Matcher m = pattern.matcher(message);
		HashMap<String, List<SPH>>result = new HashMap<String, List<SPH>>();
		while (m.find()) {
			String key = m.group(1);
			String prefix = null;
			List<SPH>s = result.get(key);
			if(s==null){
				s=new ArrayList<SPH>();
				result.put(key, s);
			}
			if(m.groupCount() > 1){
				prefix = m.group(2);
			}
			s.add(getInstance(prefix));
		}
		return result;
	}
	private static SPHPrefix getInstance(String prefix) {
		for(SPHPrefix sphp : instance) {
			if(sphp.na == prefix || sphp.na.equals(prefix))
				return sphp;
		}
		SPHPrefix sphp = new SPHPrefix();
		sphp.na = prefix;
		instance.add(sphp);
		return sphp;
	}
	@Override
	public String getReplacement(Localizer lang, VPSender sender) {
        return  ( sender instanceof VPPlayer && !((VPPlayer) sender).isNick() ) ? ((VPPlayer) sender).getPrefix() : na == null ? EMPTY : na;
	}
}
