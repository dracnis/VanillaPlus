package fr.soreth.VanillaPlus.StatType;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import fr.soreth.VanillaPlus.VanillaPlus;
import fr.soreth.VanillaPlus.Event.VPPDeathEvent;
import fr.soreth.VanillaPlus.MComponent.MComponentManager;

public class StatDeath extends Stat implements Listener{
	public StatDeath(Short id, ConfigurationSection section, MComponentManager manager) {
		super(id, section, manager);
		Bukkit.getServer().getPluginManager().registerEvents(this, VanillaPlus.getInstance());
	}
	@EventHandler
	public void onStatUpdate(VPPDeathEvent event){
		increase(event.getPlayer(), 1);
	}
}
