package fr.soreth.VanillaPlus.PH;

import java.util.ArrayList;
import java.util.List;

import fr.soreth.VanillaPlus.Manager;

public class PlaceHolderManager extends Manager<String, PlaceHolder>{
	public PlaceHolderManager() {
		super(String.class, PlaceHolder.class);
		PHLoader.load(this);
	}
	public List<PlaceHolder> getPlaceHolders(String message){
		List<PlaceHolder> result = new ArrayList<PlaceHolder>();
		for(Class<? extends PlaceHolder> holder : getLoadedClasses()){
			try {
				result.addAll(holder.newInstance().getPHs(message));
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}
