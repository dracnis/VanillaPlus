package fr.soreth.VanillaPlus.SPH;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.Localizer;
import fr.soreth.VanillaPlus.Player.VPPlayer;
import fr.soreth.VanillaPlus.Player.VPSender;
import fr.soreth.VanillaPlus.Utils.Utils;

public class SPHStatSession extends SPH{
	private static Pattern pattern = Pattern.compile("%" + sender + "_stat_session_([0-9-]+)%");
	private static List<SPHStatSession> instance = new LinkedList<>();
	protected int id;
	public String getPH(String key){
		return "%" + key + "_stat_session_" + id + "%";
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
			int id = Utils.parseInt(m.group(2),0, true);
			if(id == 0)
				continue;
			if(!String.valueOf(id).equals(m.group(2))) {
				ErrorLogger.addError(m.group() + " has invalid value, " + m.group(2) + " should be " + String.valueOf(id));
			}
			s.add(getInstance(id));
		}
		return result;
	}
	private static SPHStatSession getInstance(int id) {
		for(SPHStatSession sphss : instance) {
			if(sphss.id == id)
				return sphss;
		}
		SPHStatSession sphss = new SPHStatSession();
		sphss.id = id;
		instance.add(sphss);
		return sphss;
	}
	public String getReplacement(Localizer lang, VPSender sender){
		return sender instanceof VPPlayer ? String.valueOf(((VPPlayer) sender).getSessionStat(id)) : ZERO_INT;
	}
}
