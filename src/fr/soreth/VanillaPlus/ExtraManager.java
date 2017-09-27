package fr.soreth.VanillaPlus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.soreth.VanillaPlus.Event.VPPConsumeFoodEvent;
import fr.soreth.VanillaPlus.Player.VPPlayer;
import fr.soreth.VanillaPlus.Utils.Utils;
import fr.soreth.VanillaPlus.Utils.Minecraft.ConfigUtils;
import fr.soreth.VanillaPlus.Utils.Minecraft.CraftBlock;
import fr.soreth.VanillaPlus.Utils.Minecraft.CraftPlayerUtils;
import fr.soreth.VanillaPlus.Utils.Minecraft.MinecraftUtils;

/**
 * Manager for custom in-game edible.
 *
 * @author Soreth.
 */
public class ExtraManager implements Listener{
	private static final HashMap<String, FoodStatus>foods = new HashMap<>();
	private static VanillaPlus instance;
	private static boolean registered = false;
	public ExtraManager(VanillaPlus instance) {
		if(ExtraManager.instance == null)
			ExtraManager.instance = instance;
	}
	void init(VanillaPlusExtension extension) {
		if(extension == null)return;
		ConfigurationSection section = ConfigUtils.getYaml(extension.getInstance(), "Extra", false);
		if(section == null)return;
		ErrorLogger.addPrefix("Extra.yml");
		ConfigurationSection extra = section.getConfigurationSection("FOOD_LIST");
		ErrorLogger.addPrefix("FOOD_LIST");
			if(extra != null) {
			if(!ExtraManager.registered && ExtraManager.instance != null) {
				ExtraManager.registered = true;
				Bukkit.getServer().getPluginManager().registerEvents(this, instance);
			}
			for(String key : extra.getKeys(false)){
				ErrorLogger.addPrefix(key);
				ConfigurationSection sub = extra.getConfigurationSection(key);
				if(sub != null){
					ExtraManager.foods.put(key, new FoodStatus(sub));
				}else {
					Error.INVALID.add();
				}
				ErrorLogger.removePrefix();
			}
			}
		ErrorLogger.removePrefix();
		ErrorLogger.addPrefix(Node.REMOVE.getList());
			extra = section.getConfigurationSection(Node.REMOVE.getList());
			if(extra != null){
			for(String key : extra.getKeys(false)){
			ErrorLogger.addPrefix(key);
				ConfigurationSection recipe = extra.getConfigurationSection(key);
				if(recipe != null) {
					MinecraftUtils.removeRecipe(Utils.matchMaterial(recipe.getString(Node.MATERIAL.get()), true), recipe.getInt(Node.DATA.get()));
				}else{
					Error.INVALID.add();
				}
			ErrorLogger.removePrefix();
			}
			}
		ErrorLogger.removePrefix();
		ErrorLogger.addPrefix("RECIPE_LIST");
			extra = section.getConfigurationSection("RECIPE_LIST");
			if(extra != null){
			for(String key : extra.getKeys(false)){
			ErrorLogger.addPrefix(key);
				ConfigurationSection recipe = extra.getConfigurationSection(key);
				if(recipe != null) {
					Bukkit.addRecipe(MinecraftUtils.loadRecipe(recipe));
				}else{
					Error.INVALID.add();
				}
			ErrorLogger.removePrefix();
			}
			}
		ErrorLogger.removePrefix();
		ErrorLogger.addPrefix("BLOCK");
			extra = section.getConfigurationSection("BLOCK");
			if(extra != null){
			for(String key : extra.getKeys(false)){
			ErrorLogger.addPrefix(key);
				ConfigurationSection block = extra.getConfigurationSection(key);
				int value = Utils.parseInt(key, 0, true);
				if(value <= 0 || block == null){
				Error.INVALID.add();
				}else{
				if(block.contains("RESISTANCE")){
					float tempFloat = (float) block.getDouble("RESISTANCE", -1);
					if(tempFloat >= 0)
					CraftBlock.setResistance(value, tempFloat);
					else
					ErrorLogger.addError("RESISTANCE invalid !");
				}
				if(block.contains("DURABILITY")){
					float tempFloat = (float) block.getDouble("DURABILITY", -1);
					if(tempFloat >= 0)
					CraftBlock.setDurability(value, tempFloat);
					else
					ErrorLogger.addError("DURABILITY invalid !");
				}
				if(block.contains("EXPLODE")){
					boolean tempBoolean = block.getBoolean("EXPLODE", false);
					CraftBlock.setCanExplode(value, tempBoolean);
				}
			}
			ErrorLogger.removePrefix();
			}
			}
		ErrorLogger.removePrefix();
		ErrorLogger.removePrefix();
	}
	@EventHandler
	public void onEat(PlayerItemConsumeEvent event){
		String type = MinecraftUtils.getExtraType(event.getItem());
		FoodStatus food = ExtraManager.foods.get(type);
		if( food == null )
		if(event.getItem().getType() == Material.GOLDEN_APPLE){
			if(event.getItem().getDurability() == 1){
				type = "napple";
			}else{
				type = "apple";
			}
			food = ExtraManager.foods.get(type);
		}
		if( food == null )
			return;
		event.setCancelled(true);
		VPPlayer playerPlus = VanillaPlusCore.getPlayerManager().getPlayer(event.getPlayer());
		VPPConsumeFoodEvent e = new VPPConsumeFoodEvent(playerPlus, food, type);
		Bukkit.getServer().getPluginManager().callEvent(e);
		if(e.isCancelled())
			return;
		ItemStack item = event.getItem();
		if(item.getAmount() == 1)
			event.getPlayer().setItemInHand(null);
		else{
			item.setAmount(item.getAmount()-1);
			event.getPlayer().setItemInHand(item);
		}
		food.apply(e.getPlayer());
		
	}
    /**
     * Reset all food to original effects.
     */
	public void reset() {
		for(FoodStatus food : ExtraManager.foods.values()){
			food.reset();
		}
	}
    /**
     * Reset the specified food to original effects.
     *
     * @param name Name of food.
     */
	public void reset(String name) {
		FoodStatus food = ExtraManager.foods.get(name);
		if(food != null)
			food.reset();
	}
    /**
     * Set the specified food's absorption.
     *
     * @param name Name of food.
     * @param value New value.
     */
	public void setFoodAbso(String name, int value) {
		FoodStatus food = ExtraManager.foods.get(name);
		if(food != null)
			food.setAbso(value);
	}

    /**
     * Set the specified food's food.
     *
     * @param name Name of food.
     * @param value New value.
     */
	public void setFoodFood(String name, int value) {
		FoodStatus food = ExtraManager.foods.get(name);
		if(food != null)
			food.setFood(value);
	}

    /**
     * Set the specified food's saturation.
     *
     * @param name Name of food.
     * @param value New value.
     */
	public void setFoodSaturation(String name, int value) {
		FoodStatus food = ExtraManager.foods.get(name);
		if(food != null)
			food.setSaturation(value);
	}
	
	/**
 	* Custom food attributes.
 	*
 	* @author Soreth.
 	*/
	public class FoodStatus{
		int abso, defaultAbso, food, defaultFood, saturation, defaultSaturation;
		List<PotionEffect>effects;
		PotionEffect absoEffect;
		Sound sound;
		private float volume, pitch = 1;
		FoodStatus(ConfigurationSection section){
			abso = section.getInt(Node.ABSORPTION.get(), -1);
			defaultAbso = abso;
			food = section.getInt(Node.FOOD.get(), 0);
			defaultFood = food;
			saturation = section.getInt(Node.SATURATION.get(), 0);
			defaultSaturation = saturation;
			volume = (float) section.getDouble("VOLUME", 1);
			pitch = (float) section.getDouble("SPEED", 1);
			sound = Utils.matchEnum(Sound.values(), section.getString("SOUND"), true);
			ConfigurationSection potion = section.getConfigurationSection(Node.EFFECT.getList());
			effects = new ArrayList<PotionEffect>();
			if(potion == null)
				return;
			for(String key : potion.getKeys(false)){
				ConfigurationSection sub = potion.getConfigurationSection(key);
				if(sub == null){
					ErrorLogger.addError(key + " invalid !");
					continue;
				}
				PotionEffect effect = MinecraftUtils.craftPotionEffect(key, sub);
				if(effect.getType().equals(PotionEffectType.ABSORPTION)) {
					absoEffect = effect;
					reset();
				}else
					this.effects.add(effect);
			}
		}
		private void apply(VPPlayer player) {
			if(sound != null)
				player.getPlayer().getWorld().playSound(player.getLocation(), sound, volume, pitch);
			player.setSaturation(player.getSaturation()+saturation > 20 ? 20 : player.getSaturation()+saturation);
			player.setFood(player.getFood()+food > 20 ? 20 : player.getFood()+food);
			float tempAbso = CraftPlayerUtils.getAbso(player.getPlayer()); 
			if(tempAbso <= abso){
				if(absoEffect != null)
					player.getPlayer().addPotionEffect(absoEffect, true);
				CraftPlayerUtils.setAbso(player.getPlayer(), abso);
			}
			for(PotionEffect effect : effects){
				MinecraftUtils.applyEffect(player, effect);
			}
		}
		private void reset(){
			absoEffect = MinecraftUtils.setLevel(absoEffect, (int) Math.ceil(defaultAbso/4.0));
			abso = defaultAbso;
		}
	    /**
	     * Get food's custom effects.
	     *
	     * @return Current amount.
	     */
		public List<PotionEffect> getEffects(){
			return effects;
		}
	    /**
	     * Get food's level.
	     *
	     * @return Current amount.
	     */
		public int getFood(){
			return food;
		}
	    /**
	     * Get food's saturation amount.
	     *
	     * @return Current amount.
	     */
		public int getSaturation(){
			return saturation;
		}
		private void setAbso(int amount){
			absoEffect = new PotionEffect(PotionEffectType.ABSORPTION, absoEffect.getDuration(), (amount/4)-1, absoEffect.isAmbient(),
					absoEffect.hasParticles());
			abso = amount;
		}
		private void setFood(int amount){
			food = amount;
		}
		private void setSaturation(int amount){
			saturation = amount;
		}
	}
}
