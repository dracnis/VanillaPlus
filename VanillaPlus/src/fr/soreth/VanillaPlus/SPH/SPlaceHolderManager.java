package fr.soreth.VanillaPlus.SPH;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import fr.soreth.VanillaPlus.Manager;

public class SPlaceHolderManager extends Manager<String, SPH>{

	public SPlaceHolderManager() {
		super(String.class, SPH.class);
		SPHLoader.load(this);
	}
	public HashMap<String, List<SPH>> getPlaceHolders(String message){
		HashMap<String, List<SPH>> result = new HashMap<String, List<SPH>>();
		for(Class<? extends SPH>holder : getLoadedClasses()){
			HashMap<String, List<SPH>> holderResult = new HashMap<String, List<SPH>>();
			try {
				holderResult = holder.newInstance().getPHs(message);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
			for(Entry<String, List<SPH>> entry : holderResult.entrySet())
				if(result.containsKey(entry.getKey())){
					result.get(entry.getKey()).addAll(entry.getValue());
				}else
					result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
}
