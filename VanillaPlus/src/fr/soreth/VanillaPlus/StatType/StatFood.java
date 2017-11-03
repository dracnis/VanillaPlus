package fr.soreth.VanillaPlus.StatType;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import fr.soreth.VanillaPlus.VanillaPlus;
import fr.soreth.VanillaPlus.Error;
import fr.soreth.VanillaPlus.Event.VPPConsumeFoodEvent;
import fr.soreth.VanillaPlus.MComponent.MComponentManager;

public class StatFood extends Stat implements Listener{
	private String food;
	public StatFood(Short id, ConfigurationSection section, MComponentManager manager) {
		super(id, section, manager);
		food = section.getString("FOOD");
		if(food == null || food.isEmpty()){
			Error.MISSING_NODE.add("FOOD");
			return;
		}
		Bukkit.getServer().getPluginManager().registerEvents(this, VanillaPlus.getInstance());
	}
	@EventHandler
	public void onStatUpdate(VPPConsumeFoodEvent event){
		if(event.getType().equalsIgnoreCase(food))
			increase(event.getPlayer(), 1);
	}
}
