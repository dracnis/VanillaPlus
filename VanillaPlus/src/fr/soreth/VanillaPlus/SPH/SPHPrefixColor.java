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

public class SPHPrefixColor extends SPH{
	private static Pattern pattern = Pattern.compile("%" + sender + "_prefix_color(?:_\"((?:(?!\"%).)*)\")?%");
	private static List<SPHPrefixColor> instance = new LinkedList<>();
	private String na;
	@Override
	public String getPH(String key) {
		return "%"+key+"_prefix_color"+ (na == null ? "" : ("_\"" + na + "\"")) +"%";
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
	private static SPHPrefixColor getInstance(String prefix) {
		for(SPHPrefixColor sphpc : instance) {
			if(sphpc.na == prefix || sphpc.na.equals(prefix))
				return sphpc;
		}
		SPHPrefixColor sphpc = new SPHPrefixColor();
		sphpc.na = prefix;
		instance.add(sphpc);
		return sphpc;
	}
	@Override
	public String getReplacement(Localizer lang, VPSender sender) {
        return  ( sender instanceof VPPlayer && !((VPPlayer) sender).isNick() ) ? ((VPPlayer) sender).getPrefixColor() : na == null ? EMPTY : na;
	}
}
