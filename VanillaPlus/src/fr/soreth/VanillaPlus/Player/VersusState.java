package fr.soreth.VanillaPlus.Player;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class VersusState {
	private HashMap<DamageCause, PvPCause>pvpCauses;
	private String customName;
	private Long lastDeath;
	private HashMap<String, Long>witherCauses;
	public VersusState(){
		pvpCauses = new HashMap<DamageCause, PvPCause>();
		witherCauses = new HashMap<String, Long>();
	}
	public void setPoison(VPPlayer damager) {
		pvpCauses.put(DamageCause.POISON, new PvPCause(damager, System.currentTimeMillis()+30000));
	}
	public void setHarm(VPPlayer damager) {
		pvpCauses.put(DamageCause.MAGIC, new PvPCause(damager, System.currentTimeMillis()+10000));
		
	}
	public void setWither(VPPlayer damager) {
		pvpCauses.put(DamageCause.WITHER, new PvPCause(damager, System.currentTimeMillis()+30000));
		
	}
	public void setFire(VPPlayer damager) {
		pvpCauses.put(DamageCause.FIRE_TICK, new PvPCause(damager, System.currentTimeMillis()+25000));
		
	}
	public void setShooter(VPPlayer shooter) {
		pvpCauses.put(DamageCause.PROJECTILE, new PvPCause(shooter, System.currentTimeMillis()+15000));
	}
	public void setDamager(VPPlayer shooter) {
		pvpCauses.put(DamageCause.ENTITY_ATTACK, new PvPCause(shooter, System.currentTimeMillis()+20000));
	}
	public String getCustom(){
		return customName;
	}
	public int getLastDeath(){
		return (int) ((System.currentTimeMillis() - lastDeath) / 1000);
	}
	public void death() {
		lastDeath = System.currentTimeMillis();
	}
	public void setCustom(String name){
		customName = name;
	}
	public void setCustomWither(String name, int duration){
		witherCauses.put(name, System.currentTimeMillis() + duration);
		customName = name;
	}
	public VPPlayer getLastDamager(DamageCause cause){
		PvPCause pvpCause = pvpCauses.get(cause);
		if(pvpCause == null)
			return null;
		if(pvpCause.end<System.currentTimeMillis()){
			pvpCauses.remove(cause);
			return null;
		}
		return pvpCause.player;
	}
	public VPPlayer getLastDamager(){
		VPPlayer damager = null;
		long recent = 0;
		for(PvPCause causes : pvpCauses.values()){
			if(causes.end > recent){
				recent = causes.end;
				damager = causes.player;
			}
		}
		if(recent < System.currentTimeMillis())
			return null;
		return damager;
	}
	public String getLastWitherType(){
		String cause = null;
		long recent = 0;
		for(Entry<String, Long>entry : witherCauses.entrySet()){
			if(entry.getValue() > recent){
				recent = entry.getValue();
				cause = entry.getKey();
			}
		}
		if(recent < System.currentTimeMillis())
			return null;
		return cause;
	}
	public class PvPCause{
		VPPlayer player;
		long end;
		PvPCause(VPPlayer player, long end){
			this.player = player;
			this.end = end;
		}
	}
}
