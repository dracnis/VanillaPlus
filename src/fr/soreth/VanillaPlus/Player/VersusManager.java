package fr.soreth.VanillaPlus.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.VanillaPlusExtension;
import fr.soreth.VanillaPlus.Error;
import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.PlaceH;
import fr.soreth.VanillaPlus.VanillaPlus;
import fr.soreth.VanillaPlus.Event.IPVPDamageEvent;
import fr.soreth.VanillaPlus.Event.OfflineDeathEvent;
import fr.soreth.VanillaPlus.Event.PVEDamageEvent;
import fr.soreth.VanillaPlus.Event.PVPDamageEvent;
import fr.soreth.VanillaPlus.Event.VPPDeathEvent;
import fr.soreth.VanillaPlus.Icon.Icon;
import fr.soreth.VanillaPlus.Message.Message;
import fr.soreth.VanillaPlus.Utils.Utils;
import fr.soreth.VanillaPlus.Utils.Minecraft.ConfigUtils;
import fr.soreth.VanillaPlus.Utils.Minecraft.MinecraftUtils;

public class VersusManager implements Listener {
	public enum RespawnType { WORLD_SPAWN, SERVER_SPAWN, PLAYER_SPAWN, SPEC };
	private HashMap<DamageCause, Integer>modifier = new HashMap<EntityDamageEvent.DamageCause, Integer>();
	private HashMap<DamageCause, Message>pvpMessage = new HashMap<EntityDamageEvent.DamageCause, Message>();
	private HashMap<DamageCause, Message>ipvpMessage = new HashMap<EntityDamageEvent.DamageCause, Message>();
	private HashMap<DamageCause, Message>pveMessage = new HashMap<EntityDamageEvent.DamageCause, Message>();
	private HashMap<EntityType, Message>ipvpTypeMessage = new HashMap<EntityType, Message>();
	private HashMap<EntityType, Message>pveTypeMessage = new HashMap<EntityType, Message>();
	private HashMap<String, Message>customPvpMessage = new HashMap<String, Message>();
	private HashMap<String, Message>customIpvpMessage = new HashMap<String, Message>();
	private HashMap<String, Message>customPveMessage = new HashMap<String, Message>();
	private Message pveDefault, pvpDefault, ipvpDefault, offlineDeath;
	private RespawnType respawn = RespawnType.SPEC;
	private boolean log;
	public void init(VanillaPlusCore core) {
		if(core == null)return;
		ConfigurationSection section = ConfigUtils.getYaml(core.getInstance(), "Versus", false);
		if(section == null)return;
		ErrorLogger.addPrefix("Versus.yml");
		ConfigurationSection settings = section.getConfigurationSection(Node.SETTINGS.get());
		ErrorLogger.addPrefix(Node.SETTINGS.get());
		if(settings == null){
			Error.INVALID.add();
		}else{
			ipvpDefault		= core.getMessageManager().get(settings.getString("IPVP_DEFAULT"));
			pveDefault		= core.getMessageManager().get(settings.getString("PVE_DEFAULT"));
			pvpDefault		= core.getMessageManager().get(settings.getString("PVP_DEFAULT"));
			offlineDeath	= core.getMessageManager().get(settings.getString("OFFLINE_DEATH"));
			log				= settings.getBoolean("LOG", false);
			respawn = RespawnType.valueOf(settings.getString("RESPAWN_TYPE", "SPEC"));
		}
		ErrorLogger.removePrefix();
		ErrorLogger.removePrefix();
		
	}
	public void init(VanillaPlusExtension extension) {
		if(extension == null)return;
		ConfigurationSection section = ConfigUtils.getYaml(extension.getInstance(), "Versus", false);
		if(section == null)return;
		ErrorLogger.addPrefix("Versus.yml");
		ConfigurationSection modifiers = section.getConfigurationSection("DAMAGE_MODIFIERS");
		ErrorLogger.addPrefix("DAMAGE_MODIFIERS");
		if(modifiers == null){
			Error.INVALID.add();
		}else{
			for(String s : modifiers.getKeys(false)){
				modifier.put(Utils.matchEnum(DamageCause.values(), s, true), modifiers.getInt(s));
			}
		}
		ErrorLogger.removePrefix();
		ConfigurationSection pvp = section.getConfigurationSection("PVP");
		ErrorLogger.addPrefix("PVP");
		if(pvp != null){
			for(String s : pvp.getKeys(false)){
				this.pvpMessage.put(Utils.matchEnum(DamageCause.values(), s, true), extension.getMessageManager().get(pvp.getString(s)));
			}
		}
		ErrorLogger.removePrefix();
		ConfigurationSection ipvp = section.getConfigurationSection("IPVP");
		ErrorLogger.addPrefix("IPVP");
		if(ipvp != null){
			for(String s : ipvp.getKeys(false)){
				if(s.equals("WITHER_BOSS")) {
					this.ipvpTypeMessage.put(EntityType.WITHER, extension.getMessageManager().get(ipvp.getString(s)));
					continue;
				}
				DamageCause cause = Utils.matchEnum(DamageCause.values(), s, null, false);
				if(cause != null)
					this.ipvpMessage.put(cause, extension.getMessageManager().get(ipvp.getString(s)));
				else{
					this.ipvpTypeMessage.put(Utils.matchEnum(EntityType.values(), s, true),  extension.getMessageManager().get(ipvp.getString(s)));
				}
			}
		}
		ErrorLogger.removePrefix();
		ConfigurationSection pve = section.getConfigurationSection("PVE");
		ErrorLogger.addPrefix("PVE");
		if(pve != null){
			for(String s : pve.getKeys(false)){
				if(s.equals("WITHER_BOSS")) {
					this.pveTypeMessage.put(EntityType.WITHER, extension.getMessageManager().get(pve.getString(s)));
					continue;
				}
				DamageCause cause = Utils.matchEnum(DamageCause.values(), s, null, false);
				if(cause != null)
					this.pveMessage.put(cause, extension.getMessageManager().get(pve.getString(s)));
				else{
					this.pveTypeMessage.put(Utils.matchEnum(EntityType.values(), s, true),  extension.getMessageManager().get(pve.getString(s)));
				}
			}
		}
		ErrorLogger.removePrefix();
		ConfigurationSection cpvp = section.getConfigurationSection("CUSTOM_PVP");
		ErrorLogger.addPrefix("CUSTOM_PVP");
		if(cpvp != null){
			for(String s : cpvp.getKeys(false)){
				this.customPvpMessage.put(s, extension.getMessageManager().get(cpvp.getString(s)));
			}
		}
		ErrorLogger.removePrefix();
		ConfigurationSection cipvp = section.getConfigurationSection("CUSTOM_IPVP");
		ErrorLogger.addPrefix("CUSTOM_IPVP");
		if(cipvp != null){
			for(String s : cipvp.getKeys(false)){
				this.customIpvpMessage.put(s, extension.getMessageManager().get(cipvp.getString(s)));
			}
		}
		ErrorLogger.removePrefix();
		ConfigurationSection cpve = section.getConfigurationSection("CUSTOM_PVE");
		ErrorLogger.addPrefix("CUSTOM_PVE");
		if(cpve != null){
			for(String s : cpve.getKeys(false)){
				this.customPveMessage.put(s, extension.getMessageManager().get(cpve.getString(s)));
			}
		}
		ErrorLogger.removePrefix();
		ErrorLogger.removePrefix();
		Bukkit.getPluginManager().registerEvents(this, VanillaPlus.getInstance());
	}
	public void addModifier(DamageCause cause, int modifier){
		if(modifier == 100){
			this.modifier.remove(cause);
		}else{
			this.modifier.put(cause, modifier);
		}
	}
	public void reset() {
		modifier = new HashMap<EntityDamageEvent.DamageCause, Integer>();
		
	}
	@EventHandler(priority=EventPriority.LOWEST)
	public void splashPlayer(PotionSplashEvent event) {
		if(!(event.getPotion().getShooter()instanceof Player))
				return;
		Player shooter = (Player) event.getPotion().getShooter();
		boolean poison = false;
		boolean harm = false;
		boolean wither = false;
		for(PotionEffect effect : event.getPotion().getEffects()){
			if(effect.getType().equals(PotionEffectType.POISON)){
				poison = true;
			}else if(effect.getType().equals(PotionEffectType.HARM)){
				harm = true;
			}else if(effect.getType().equals(PotionEffectType.WITHER)){
				wither = true;
			}
		}
		if(poison || harm || wither){
			VPPlayer sender = VanillaPlusCore.getPlayerManager().getPlayer(shooter);
			for(Entity e : event.getAffectedEntities()){
				if(!(e instanceof Player)) continue;
				VPPlayer player = VanillaPlusCore.getPlayerManager().getPlayer((Player) e);
				VersusState versus = player.getVersus();
				if(poison)
					versus.setPoison(sender);
				if(harm)
					versus.setHarm(sender);
				if(wither)
					versus.setWither(sender);
			}
		}
	}
	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled = true)
	public void PlayerDamage(final EntityDamageEvent event) {
		if(!(event.getEntity() instanceof Player))
			return;
		if(((Player)event.getEntity()).getGameMode() == GameMode.SPECTATOR || ((Player)event.getEntity()).getGameMode() == GameMode.CREATIVE){
			if(event.getCause() == DamageCause.VOID)
				event.getEntity().teleport(event.getEntity().getLocation().add(0.0, 150.0, 0.0));
			event.setCancelled(true);
			return;
		}
		Integer modifier = this.modifier.get(event.getCause());
		if(modifier!=null){
			if(modifier == -1){
				event.setCancelled(true);
				return;
			}else{
				event.setDamage(event.getDamage()*modifier/100);
			}
		}
		VPPlayer damaged = VanillaPlusCore.getPlayerManager().getPlayer((Player) event.getEntity());
		VersusState versus = damaged.getVersus();
		VPPlayer damager = versus.getLastDamager();
		Entity pveDamage = null;
		boolean ipvp = false;
		double finalDamage = -event.getDamage(DamageModifier.ABSORPTION);
		finalDamage += event.getFinalDamage();
		switch (event.getCause()) {
		case ENTITY_EXPLOSION:
			ipvp = true;
		case ENTITY_ATTACK:
		case THORNS:
			EntityDamageByEntityEvent entityDamagedEvent = (EntityDamageByEntityEvent) event;
			if(entityDamagedEvent.getDamager()instanceof Player){
				damager = VanillaPlusCore.getPlayerManager().getPlayer((Player) entityDamagedEvent.getDamager());
				
				PVPDamageEvent e = new PVPDamageEvent(damager, damaged, finalDamage, event.getCause(), null);
				Bukkit.getPluginManager().callEvent(e);
				
				if(e.isCancelled()){
					event.setCancelled(true);
					return;
				}

				versus.setDamager(damager);
				if(((Player) entityDamagedEvent.getDamager()).getItemInHand().getEnchantments().containsKey(Enchantment.FIRE_ASPECT)){
					versus.setFire(damager);
				}
			}else if(damager != null){
				
				ipvp = true;
				pveDamage = entityDamagedEvent.getDamager();
				IPVPDamageEvent e = new IPVPDamageEvent(damaged, damager, finalDamage, event.getCause(), null);
				Bukkit.getPluginManager().callEvent(e);
				
				if(e.isCancelled()){
					event.setCancelled(true);
					return;
				}
			
				
			}else{
				
				pveDamage = entityDamagedEvent.getDamager();
				PVEDamageEvent e = new PVEDamageEvent(damaged, finalDamage, event.getCause(), null);
				Bukkit.getPluginManager().callEvent(e);
				
				if(e.isCancelled()){
					event.setCancelled(true);
					return;
				}
				
			}
			break;
		case PROJECTILE:
			EntityDamageByEntityEvent entityDamagedEvent1 = (EntityDamageByEntityEvent) event;
			if(((Projectile)entityDamagedEvent1.getDamager()).getShooter()instanceof Player){
				damager = VanillaPlusCore.getPlayerManager().getPlayer((Player) ((Projectile)entityDamagedEvent1.getDamager()).getShooter());

				PVPDamageEvent e = new PVPDamageEvent(damager, damaged, finalDamage, event.getCause(), (Projectile) entityDamagedEvent1.getDamager());
				Bukkit.getPluginManager().callEvent(e);
				
				if(e.isCancelled()){
					event.setCancelled(true);
					return;
				}
				
				versus.setShooter(damager);
				if(((Projectile)entityDamagedEvent1.getDamager()).getFireTicks()!=0){
					versus.setFire(damager);
				}
			}else if(damager != null){
				
				ipvp = true;
				pveDamage = ( ((Projectile) entityDamagedEvent1.getDamager()).getShooter() instanceof Entity ) ?
						(Entity) ((Projectile) entityDamagedEvent1.getDamager()).getShooter() :
							entityDamagedEvent1.getDamager();
				IPVPDamageEvent e = new IPVPDamageEvent(damaged, damager, finalDamage, event.getCause(), null);
				Bukkit.getPluginManager().callEvent(e);

				if(e.isCancelled()){
					event.setCancelled(true);
					return;
				}
			
				
			}else{
				
				pveDamage = ( ((Projectile) entityDamagedEvent1.getDamager()).getShooter() instanceof Entity ) ?
					(Entity) ((Projectile) entityDamagedEvent1.getDamager()).getShooter() :
						entityDamagedEvent1.getDamager();
				PVEDamageEvent e = new PVEDamageEvent(damaged, finalDamage, event.getCause(),  (Projectile) entityDamagedEvent1.getDamager());
				Bukkit.getPluginManager().callEvent(e);

				if(e.isCancelled()){
					event.setCancelled(true);
					return;
				}
				
			}
			break;
		case FIRE_TICK:
		case POISON:
		case MAGIC:
		case WITHER:
			damager = versus.getLastDamager(event.getCause());
			if(damager != null){
				
				PVPDamageEvent e = new PVPDamageEvent(damager, damaged, finalDamage, event.getCause(), null);
				Bukkit.getPluginManager().callEvent(e);

				if(e.isCancelled()){
					event.setCancelled(true);
					return;
				}
				
			}else{

				PVEDamageEvent e = new PVEDamageEvent(damaged, finalDamage, event.getCause(), null);
				Bukkit.getPluginManager().callEvent(e);

				if(e.isCancelled()){
					event.setCancelled(true);
					return;
				}
				
			}
			break;
		default:
			if(damager != null){

				ipvp = true;
				IPVPDamageEvent ie = new IPVPDamageEvent(damager, damaged, finalDamage, event.getCause(), null);
				Bukkit.getPluginManager().callEvent(ie);
				
			}else{

				PVEDamageEvent pve = new PVEDamageEvent(damaged, finalDamage, event.getCause(), null);
				Bukkit.getPluginManager().callEvent(pve);
				
			}
			break;
		}
		if(damaged.getPlayer().getHealth() <= event.getFinalDamage()){
			kill(damaged, damager, pveDamage, event.getCause(), ipvp);
			event.setCancelled(true);
			if(event.getCause() == DamageCause.PROJECTILE){
				((EntityDamageByEntityEvent) event).getDamager().remove();;
			}
		}
		
	}
	public void kill(VPPlayer player, VPPlayer damager, Entity e, DamageCause cause, boolean ipvp){
		if(cause == null)cause = DamageCause.CUSTOM;
		if(damager == null){
			damager = player.getVersus().getLastDamager();
			ipvp = damager != null;
		}
		Message deathMessage = null;
		if(damager != null && player.isOnline()){
			if(ipvp){
				if(cause == DamageCause.CUSTOM || (cause == DamageCause.WITHER &&  player.getVersus().getLastWitherType() != null))
					if(cause == DamageCause.WITHER)
						deathMessage = customIpvpMessage.get(player.getVersus().getLastWitherType());
					else
						deathMessage = customIpvpMessage.get(player.getVersus().getCustom());
				else if(e != null && (cause == DamageCause.ENTITY_EXPLOSION || cause == DamageCause.ENTITY_ATTACK) ||
						(e instanceof Entity && cause == DamageCause.PROJECTILE))
					deathMessage = ipvpTypeMessage.get(e.getType());
				else
					deathMessage = ipvpMessage.get(cause);
				if(deathMessage == null){
					deathMessage = ipvpDefault;
					if(log)
					ErrorLogger.addError(cause.name() + " ipvp "+
						(cause == DamageCause.CUSTOM ? player.getVersus().getCustom() : (cause == DamageCause.WITHER
						&& player.getVersus().getLastWitherType() != null) ?
								player.getVersus().getLastWitherType() : ""));
				}
			}else{
				if(cause == DamageCause.CUSTOM || (cause == DamageCause.WITHER &&  player.getVersus().getLastWitherType() != null))
					if(cause == DamageCause.WITHER)
						deathMessage = customPvpMessage.get(player.getVersus().getLastWitherType());
					else
						deathMessage = customPvpMessage.get(player.getVersus().getCustom());
				else
					deathMessage = pvpMessage.get(cause);
				if(deathMessage == null){
					deathMessage = pvpDefault;
					if(log)
					ErrorLogger.addError(cause.name() + " pvp "+
						(cause == DamageCause.CUSTOM ? player.getVersus().getCustom() : (cause == DamageCause.WITHER
						&& player.getVersus().getLastWitherType() != null) ?
								player.getVersus().getLastWitherType() : ""));
				}
			}
		}else if(!player.isOnline()){
			deathMessage = offlineDeath;
		}else{
			if(cause == DamageCause.CUSTOM || (cause == DamageCause.WITHER &&  player.getVersus().getLastWitherType() != null))
				if(cause == DamageCause.WITHER)
					deathMessage = customPveMessage.get(player.getVersus().getLastWitherType());
				else
					deathMessage = customPveMessage.get(player.getVersus().getCustom());
			else if(e != null && (cause == DamageCause.ENTITY_EXPLOSION || cause == DamageCause.ENTITY_ATTACK ||
					(e instanceof Entity && cause == DamageCause.PROJECTILE)))
				deathMessage = pveTypeMessage.get(e.getType());
			else
				deathMessage = pveMessage.get(cause);
			if(deathMessage == null){
				deathMessage = pveDefault;
				if(log)
				ErrorLogger.addError(cause.name() + " pve "+
					(cause == DamageCause.CUSTOM ? player.getVersus().getCustom() : (cause == DamageCause.WITHER
					&& player.getVersus().getLastWitherType() != null) ?
							player.getVersus().getLastWitherType() : ""));
			}
		}
		List<ItemStack>loots = new ArrayList<ItemStack>();
		Location deathLocation = player.getPlayer().getLocation();
		if(deathLocation.getWorld().getGameRuleValue("keepInventory").equalsIgnoreCase("false")){
			ItemStack[] toAdd = player.getArmor();
			for(int i = 0 ; i < toAdd.length ; i++){
				if(toAdd[i] == null || toAdd[i].getType() == Material.AIR || MinecraftUtils.getExtra(toAdd[i], Icon.FREEZE).equals("1"))
					continue;
				loots.add(toAdd[i]);
			}
			toAdd = player.getInventory(true);
			for(int i = 0 ; i < toAdd.length ; i++){
				if(toAdd[i] == null || toAdd[i].getType() == Material.AIR || MinecraftUtils.getExtra(toAdd[i], Icon.FREEZE).equals("1"))
					continue;
				loots.add(toAdd[i]);
			}
		}
		VPPDeathEvent pve = player.isOnline() ? new VPPDeathEvent(damager, player, cause, loots,
			!deathLocation.getWorld().getGameRuleValue("keepInventory").equalsIgnoreCase("false")) :
				new OfflineDeathEvent(damager, player, cause, loots,
					!deathLocation.getWorld().getGameRuleValue("keepInventory").equalsIgnoreCase("false"));
		if(deathMessage != null){
			if(damager != null)
				deathMessage.addSReplacement(PlaceH.SENDER.get(), damager);
			deathMessage.addSReplacement(PlaceH.RECEIVER.get(), player).send();
		}
		Bukkit.getPluginManager().callEvent(pve);
		ItemStack[] inv = player.getInventory(true);
		for (int i = 9 ; i < inv.length ; i++) {
			if(inv[i] == null || inv[i].getType() == Material.AIR || MinecraftUtils.getExtra(inv[i], Icon.FREEZE).equals("1"))
				continue;
			if(!pve.keepInventory())
				inv[i] = new ItemStack(Material.AIR);
			else
				pve.getLoots().remove(inv[i]);
		}
		for (int i = 0 ; i < 9 ; i++) {
			if(inv[i] == null || inv[i].getType() == Material.AIR || MinecraftUtils.getExtra(inv[i], Icon.FREEZE).equals("1"))
				continue;
			if(!pve.keepHotbar())
				inv[i] = new ItemStack(Material.AIR);
			else
				pve.getLoots().remove(inv[i]);
		}
		player.setInventory(inv, true);
		ItemStack armor [] = player.getArmor();
		for (int i = 0 ; i < armor.length ; i++) {
			if(armor[i] == null || armor[i].getType() == Material.AIR || MinecraftUtils.getExtra(armor[i], Icon.FREEZE).equals("1"))
				continue;
			if(!pve.keepArmor())
				armor[i] = new ItemStack(Material.AIR);
			else
				pve.getLoots().remove(inv[i]);
		}
		player.setArmor(armor);
		if(!pve.keepXp()){
			int xp = player.getLevel()*7;
			if(xp != 0){
				ExperienceOrb orb = deathLocation.getWorld().spawn(deathLocation, ExperienceOrb.class);
				orb.setExperience(xp > 100 ? 100 : xp);
			}
			player.setLevel(0);
		}
		for (ItemStack itemStack : pve.getLoots()){
			if(itemStack != null && itemStack.getType() != Material.AIR)
				deathLocation.getWorld().dropItem(deathLocation, itemStack);
		}
		player.clearEffect();
		player.getPlayer().setHealth(player.getPlayer().getMaxHealth());
		player.getPlayer().setFoodLevel(20);
		player.getPlayer().setSaturation(20);
		player.getPlayer().setFireTicks(0);
		player.getPlayer().damage(0);
		player.getVersus().death();
		switch (respawn) {
		case PLAYER_SPAWN:
			Location toSend = player.getPlayer().getBedSpawnLocation();
			if(toSend!=null){
				player.teleport(toSend);
				break;
			}
		case SERVER_SPAWN:
			player.teleport(VanillaPlusCore.getPlayerManager().getServerSpawn());
			break;
		case WORLD_SPAWN:
			player.teleport(player.getPlayer().getWorld().getSpawnLocation());
			break;
		case SPEC:
			player.getPlayer().setGameMode(GameMode.SPECTATOR);
			if(cause == DamageCause.VOID){
				deathLocation.setY(deathLocation.getWorld().getSpawnLocation().getY());
			}
			player.teleport(deathLocation.add(0, 2, 0));
			break;
		default:
			break;
		}
	}
}
