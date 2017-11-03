package fr.soreth.VanillaPlus.PH;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import fr.soreth.VanillaPlus.Localizer;
import fr.soreth.VanillaPlus.MComponent.MComponentManager;

public class PlaceHolder{
	protected String pH;
	private String replacement = "";
	private final static List<PlaceHolder>placeHolders = new ArrayList<PlaceHolder>();
	public PlaceHolder(){}
	public PlaceHolder(ConfigurationSection section, MComponentManager manager) {
		this.pH = "%" + section.getString("PLACEHOLDER", "") + "%";
		this.replacement = section.getString("REPLACEMENT", "");
		placeHolders.add(this);
	}
	/**
	 * @return the PlaceHolder's string;
	 */
	public String getPH(){
		return pH;
	}
	/**
	 * @return the PlaceHolders in the given String;
	 */
	public List<PlaceHolder> getPHs(String message){
		List<PlaceHolder> result = new ArrayList<PlaceHolder>();
		for(PlaceHolder hold : placeHolders){
			if(message.contains(hold.getPH()))
				result.add(hold);
		}
		return result;
	}
	/**
	 * @param lang 
	 * @return the PlaceHolder's Replacement's string;
	 */
	public String getReplacement(Localizer lang){
		return replacement;
	}
	public boolean isStatic() {
		return true;
	}
}
