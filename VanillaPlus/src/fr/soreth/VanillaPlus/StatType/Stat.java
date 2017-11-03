package fr.soreth.VanillaPlus.StatType;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.Event.VPPStatChangeEvent;
import fr.soreth.VanillaPlus.MComponent.MComponent;
import fr.soreth.VanillaPlus.MComponent.MComponentManager;
import fr.soreth.VanillaPlus.Player.VPPlayer;

public class Stat {
	private final String alias;
	private final short id;
	private final MComponent name;
	public Stat(Short id, ConfigurationSection section, MComponentManager manager){
		this.id = id;
		this.alias = section.getString(Node.ALIAS.get());
		this.name = manager.get(section.getString(Node.NAME_PATH.get()));
	}
	public String getAlias(){
		return this.alias;
	}
	public MComponent getName(){
		return this.name;
	}
	public void increase(VPPlayer player, int amount){
		if(amount == 0)return;
		player.upStat(id, amount);
		Bukkit.getServer().getPluginManager().callEvent(new VPPStatChangeEvent(player, this, amount));
	}
	public void decrease(VPPlayer player, int amount) {
		if(amount == 0)return;
		player.upStat(id, -amount);
		Bukkit.getServer().getPluginManager().callEvent(new VPPStatChangeEvent(player, this, -amount));
	}
	//Only for minecraft stat ?
	public void update(VPPlayer player){
		
	}
	public short getID() {
		return id;
	}
	public void set(VPPlayer player, int value){
		int current = player.getStat(id);
		if(current == value)return;
		player.upStat(id, value - current);
		Bukkit.getServer().getPluginManager().callEvent(new VPPStatChangeEvent(player, this, value - current));
	}
}
