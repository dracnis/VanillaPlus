package fr.soreth.VanillaPlus.StatType;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.VanillaPlus;
import fr.soreth.VanillaPlus.Event.VPPCurrencyChangeEvent;
import fr.soreth.VanillaPlus.MComponent.MComponentManager;

public class StatCurrency extends Stat implements Listener{
	private int id;
	private boolean win;
	public StatCurrency(Short id, ConfigurationSection section, MComponentManager manager) {
		super(id, section, manager);
		this.id = section.getInt(Node.ID.get());
		win = section.getBoolean("WIN", true);
		Bukkit.getServer().getPluginManager().registerEvents(this, VanillaPlus.getInstance());
	}
	@EventHandler
	public void onStatUpdate(VPPCurrencyChangeEvent event){
		if(event.getCurency().getID()==id)
			if(win && event.getAmount()>0)
				increase(event.getPlayer(), (int) event.getAmount());
			else if(!win && event.getAmount()<0)
				increase(event.getPlayer(), (int) -event.getAmount());
	}
}
