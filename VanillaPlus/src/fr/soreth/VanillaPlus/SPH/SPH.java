package fr.soreth.VanillaPlus.SPH;

import java.util.HashMap;
import java.util.List;

import fr.soreth.VanillaPlus.Localizer;
import fr.soreth.VanillaPlus.Player.VPSender;

public abstract class SPH{
	public final static String EMPTY = "", ZERO_INT = "0", ZERO_DOUBLE = "0.0";
	public final static String sender = "([0-9a-zA-Z]+)";
	/**
	 * @return the PlaceHolder's string;
	 */
	public abstract String getPH(String key);
	/**	 * 
	 * @return the PlaceHolders in the given String;
	 */
	public abstract HashMap<String, List<SPH>> getPHs(String message);
	/**
	 * @param lang 
	 * @return the PlaceHolder's Replacement's string;
	 */
	public abstract String getReplacement(Localizer lang, VPSender player);
	/**
	 * @return If we need the player be online ore not.
	 */
	public boolean needOnline(){
		return false;
	}
}
