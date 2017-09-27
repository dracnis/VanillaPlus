package fr.soreth.VanillaPlus.Event;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import fr.soreth.VanillaPlus.VanillaPlusExtension;

public class SimpleLoadEvent extends Event{
    private static final HandlerList handlers = new HandlerList();
    private final List<VanillaPlusExtension>load = new ArrayList<>();
    
    public void addExtension(VanillaPlusExtension extension) {
    	if(!load.contains(extension))
    		load.add(extension);
    }
    
    public List<VanillaPlusExtension> getExtensions(){
    	List<VanillaPlusExtension>result = new ArrayList<>(load);
    	return result;
    }
    
	public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
