package fr.soreth.VanillaPlus.Utils.Minecraft;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.Error;
import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.Player.VPPlayer;
import fr.soreth.VanillaPlus.Utils.ReflectionUtils;
import fr.soreth.VanillaPlus.Utils.Utils;
import fr.soreth.VanillaPlus.Utils.Gson.GSon;

public class MinecraftUtils {
	public static ItemStack loadItem(ConfigurationSection c){
		if(c==null){
			Error.INVALID.add();
			return new ItemStack(Material.BEDROCK);
		}
		Material material = Utils.matchMaterial(c.getString(Node.MATERIAL.get()), true);
		if(material==null){
			ErrorLogger.addError(Node.MATERIAL.get() + " " +c.getString("MATERIAL") + " " + Error.INVALID);
			material = Material.PAPER;
		}
		int amount = c.getInt("AMOUNT", 1);
		if(amount <= 0){
			amount = 1;
		}
		ItemStack result = new ItemStack(material, amount);
		if(c.contains(Node.DATA.get())){
			result.setDurability((short)c.getInt(Node.DATA.get()));
		}
		if(c.contains("EXTRA")){
			result = addMeta(c.getConfigurationSection("EXTRA"), result);
		}
		if(c.contains("UNBREAKABLE")){
			if(c.getBoolean("UNBREAKABLE")){
				NBTItem nbtItem = new NBTItem(result);
				nbtItem.setBoolean("Unbreakable", true);
				result = nbtItem.getItem();
				
			}
		}
		ItemMeta meta = result.getItemMeta();
		if(c.contains(Node.NAME.get())){
			String name = c.getString(Node.NAME.get());
			name = ChatColor.translateAlternateColorCodes('&', name);
			meta.setDisplayName(name);
		}
		if(c.contains("LORE")){
			List<String> loreList = c.getStringList("LORE");
		      for (int i = 0; i <loreList.size(); i++) {
			          loreList.set(i, ChatColor.translateAlternateColorCodes('&', loreList.get(i)));
		      }
			meta.setLore(loreList);
		}
		if(c.contains("FLAG_LIST")){
			for(String s : c.getStringList("FLAG_LIST")){
				ItemFlag flag = Utils.matchEnum(ItemFlag.values(), s, true);
				meta.addItemFlags(flag);
			}
		}
		// Leather armor
		if(c.contains("COLOR")){
			if(meta instanceof LeatherArmorMeta){
				String col = c.getString("COLOR");
				if(col == null)
					ErrorLogger.addError(col + Error.INVALID.getMessage());
				else{
					String[] rgb = col.split(":");
					if(rgb.length==3){
						int red = Utils.parseInt(rgb[0],0, true);
						int green = Utils.parseInt(rgb[1],0, true);
						int blue =  Utils.parseInt(rgb[2],0, true);
						if (red < 0) 
							red = 0;
						else if( red >255)
							red = 255;
						if (green < 0) 
							green = 0;
						else if( green >255)
							green = 255;
						if (blue < 0) 
							blue = 0;
						else if( blue >255)
							blue = 255;
						Color color = Color.fromRGB(red, green, blue);
						((LeatherArmorMeta) meta).setColor(color);
					}else{
						ErrorLogger.addError(col + " must be in the format \"red:green:blue\".");
					}
				}
					
			}
	        
		}
		// Book
		if(meta instanceof BookMeta){
			if(c.contains("AUTHOR")){
				String auth = ChatColor.translateAlternateColorCodes('&', c.getString("AUTHOR"));
				((BookMeta) meta).setAuthor(auth);
			}
			if(c.contains("TITLE")){
				String title = ChatColor.translateAlternateColorCodes('&', c.getString("TITLE"));
				((BookMeta) meta).setAuthor(title);
			}
			if(c.contains("PAGES")){
				for(String s : c.getStringList("PAGES")){
					((BookMeta) meta).addPage(ChatColor.translateAlternateColorCodes('&', s));
				}
			}
		}
		// Banner
		if(meta instanceof BannerMeta){
			if(c.contains("BASE")){
				DyeColor dyeColor = dyeFrom(c.getString("BASE"), true);
				((BannerMeta) meta).setBaseColor(dyeColor);
			}
			if(c.contains("PATTERN")){
				ConfigurationSection pattern = c.getConfigurationSection("PATTERN");
				ErrorLogger.addPrefix("PATTERN");
				if(pattern == null)
					Error.INVALID.add();
				else
				for(String s : pattern.getKeys(false)){
					ErrorLogger.addPrefix(s);
					ConfigurationSection current = pattern.getConfigurationSection(s);
					Pattern p = craftPanner(current);
					if(p != null)
						((BannerMeta) meta).addPattern(p);
					ErrorLogger.removePrefix();
				}
				ErrorLogger.removePrefix();
			}
		}
		//Skull
		if(meta instanceof SkullMeta){
			if(c.contains("OWNER"))
				((SkullMeta) meta).setOwner(c.getString("OWNER"));
		}
		//Potion
		if(meta instanceof PotionMeta){
			if(c.contains(Node.EFFECT.getList())){
				ErrorLogger.addPrefix(Node.EFFECT.getList());
				ConfigurationSection effects = c.getConfigurationSection(Node.EFFECT.getList());
				if(effects == null)
					Error.INVALID.add();
				else{
					for(String name : effects.getKeys(false)){
						ErrorLogger.addPrefix(name);
						ConfigurationSection currentSection = c.getConfigurationSection(name);
						PotionEffect effect = craftPotionEffect(name, currentSection);
						if(effect == null){
							Error.INVALID.add();
						}else{
							if(((PotionMeta)meta).hasCustomEffects())
								((PotionMeta)meta).setMainEffect(effect.getType());
							((PotionMeta)meta).addCustomEffect(effect, true);
						}
						ErrorLogger.removePrefix();
					}
				}
				ErrorLogger.removePrefix();
			}
		}
		result.setItemMeta(meta);
		if(c.contains(Node.ENCHANT.get())){
			ErrorLogger.addPrefix(Node.ENCHANT.get());
			enchantItem(result, c.get(Node.ENCHANT.get()));
			ErrorLogger.removePrefix();
		}
		return result;
	}
	public static Pattern craftPanner(ConfigurationSection section) {
		if(section == null) {
			Error.INVALID.add();
			return null;
		} else {
			DyeColor dyeColor = dyeFrom(section.getString("COLOR"), true);
			PatternType patt = Utils.matchEnum(PatternType.values(), section.getString("TYPE"), true);
			return new Pattern(dyeColor, patt);
		}
	}
	public static DyeColor dyeFrom(String input, boolean log){
		return Utils.matchEnum(DyeColor.values(), input, log);
	}
	public static PotionEffect craftPotionEffect(ConfigurationSection section) {
		if(section == null){
			Error.MISSING.add();
			return null;
		}
		return craftPotionEffect(section.getString(Node.EFFECT.get()), section);
	}
	public static PotionEffect craftPotionEffect(String name, ConfigurationSection section) {
		if(section == null){
			Error.MISSING.add();
			return null;
		}
		PotionEffectType effect = PotionEffectType.getByName(name);
		if( effect == null ) {
			ErrorLogger.addError(name + " is not a valid potion effect type !");
			return null;
		}
		int duration = section.getInt(Node.DURATION.get(), 120)*20;
		int amplifier = section.getInt(Node.LEVEL.get(), 1) - 1;
		boolean ambient = section.getBoolean(Node.AMBIANT.get(), true);
		boolean particles = section.getBoolean(Node.PARTICLE.get(), true);
		return new PotionEffect(effect, duration, amplifier, ambient, particles);
	}
	public static PotionEffect setLevel(PotionEffect effect, int level) {
		return new PotionEffect(effect.getType(), effect.getDuration(), level-1, effect.isAmbient(),
				effect.hasParticles());
	}
	public static PotionEffect setDuration(PotionEffect effect, int duration) {
		return new PotionEffect(effect.getType(), duration, effect.getAmplifier(), effect.isAmbient(),
				effect.hasParticles());
	}
	public static int applyEffect(VPPlayer player, PotionEffect effect) {
		return applyEffect(player, effect, false);
	}
	public static int applyEffect(VPPlayer player, PotionEffect effect, boolean add) {
		List<PotionEffect>current = player.getPotionEffect();
		Iterator<PotionEffect>iterator = current.iterator();
		while(iterator.hasNext()) {
			PotionEffect potionEffect = iterator.next();
			if(potionEffect.getType()==effect.getType()){
				if(potionEffect.getAmplifier()<effect.getAmplifier()){
					iterator.remove();
				}else if(potionEffect.getAmplifier() == effect.getAmplifier()){
					if(add){
						effect = setDuration(effect, (int) (potionEffect.getDuration()+effect.getDuration()));
					}else{
						if(potionEffect.getDuration()<effect.getDuration())
							iterator.remove();
					}
				}else
					return 0;
				break;
				
			}
			
		}
		current.add(effect);
		player.setPotionEffect(current);
		return effect.getDuration()/20;
	}
	@SuppressWarnings({ "unchecked", "rawtypes", "deprecation" })
	public static void enchantItem(ItemStack item, Object enchant) {
		if(enchant instanceof List && !((List) enchant).isEmpty() && ((List) enchant).get(0) instanceof String) {
			List<String>list = (List<String>) enchant;
			for(String s : list){
				int id = Utils.parseInt(s.split(":")[0], -1, false);
				Enchantment e = id != -1 ? Enchantment.getById(id) : Enchantment.getByName(s.split(":")[0]);
				int level = 1;
				if(s.split(":").length==2)
					level = Integer.parseInt(s.split(":")[1]);
				if(e==null)
					ErrorLogger.addError(s.split(":")[0] + " is not a valid enchantment !");
				else
					item.addUnsafeEnchantment(e, level);
			}
		}else if(enchant instanceof ConfigurationSection) {
			ConfigurationSection section = (ConfigurationSection) enchant; 
			for(String s : section.getKeys(false)) {
				int id = Utils.parseInt(s, -1, false);
				Enchantment e = id != -1 ? Enchantment.getById(id) : Enchantment.getByName(s);
				if(e==null)
					ErrorLogger.addError(s + " is not a valid enchantment !");
				else
				item.addUnsafeEnchantment(e, section.getInt(s, 1));
			}
		}
	}
	public static ItemStack addMeta(ConfigurationSection section, ItemStack item){
		NBTItem nbtItem = new NBTItem(item);
		for(String key : section.getKeys(false)){
			Object value = section.get(key);
			if(value instanceof Integer){
				nbtItem.setInteger(key, (int)value);
			}else if(value instanceof Double){
				nbtItem.setDouble(key, (double)value);
			}else if(value instanceof Boolean){
				nbtItem.setBoolean(key, (boolean)value);
			}else if(value instanceof String){
				nbtItem.setString(key, (String)value);
			}else if(value instanceof ConfigurationSection) {
				NBTCompound compound = nbtItem.getCompound(key);
				if(compound == null)
					compound = nbtItem.addCompound(key);
				applyCompound((ConfigurationSection) value, compound);
			}
		}
		return nbtItem.getItem();
	}
	public static void applyCompound(ConfigurationSection section, NBTCompound nbtCompound){
		for(String key : section.getKeys(false)){
			Object value = section.get(key);
			if(value instanceof Integer){
				nbtCompound.setInteger(key, (int)value);
			}else if(value instanceof Double){
				nbtCompound.setDouble(key, (double)value);
			}else if(value instanceof Boolean){
				nbtCompound.setBoolean(key, (boolean)value);
			}else if(value instanceof String){
				nbtCompound.setString(key, (String)value);
			}else if(value instanceof ConfigurationSection) {
				NBTCompound compound = nbtCompound.getCompound(key);
				if(compound == null)
					compound = nbtCompound.addCompound(key);
				applyCompound((ConfigurationSection) value, compound);
			}
		}
		
	}
	@SuppressWarnings("deprecation")
	public static Recipe loadRecipe(ConfigurationSection c) {
		if(c == null)return null;
		String type = c.getString(Node.TYPE.get(), "SHAPED");
		ItemStack item = loadItem(c.getConfigurationSection(Node.ITEM.get()));
		//TODO end
		if(type.equals("SHAPED")){
			ShapedRecipe recipe = new ShapedRecipe(item);
			List<String>list = c.getStringList("SHAPE");
			switch (list.size()) {
			case 1:
				recipe.shape(list.get(0));
				break;
			case 2:
				recipe.shape(list.get(0),list.get(1));
				break;
			case 3:
				recipe.shape(list.get(0),list.get(1), list.get(2));
				break;
			default:
				break;
			}
			ConfigurationSection itemSection = c.getConfigurationSection(Node.ITEM.getList());
			if(itemSection == null)
				Error.INVALID.add();
			else
				for(String key : itemSection.getKeys(false)){
					Object current = itemSection.get(key);
					if(current instanceof String) {
						Material material = Utils.matchMaterial((String)current, true);
						if(material == null) {
							Error.INVALID.add();
							continue;
						}
						recipe.setIngredient(key.charAt(0), material);
					}else if(current instanceof ConfigurationSection) {
						ConfigurationSection section = (ConfigurationSection) current;
						Material material = Utils.matchMaterial(section.getString(Node.MATERIAL.get()), true);
						if(material == null) {
							Error.INVALID.add();
							continue;
						}
						recipe.setIngredient(key.charAt(0), new MaterialData(material, (byte) section.getInt(Node.DATA.get(), 0)));
					}
				}
			return recipe;
		}else if(type.equals("SHAPELESS")){
			ShapelessRecipe recipe = new ShapelessRecipe(item);
			ConfigurationSection itemSection = c.getConfigurationSection(Node.ITEM.getList());
			int amount = 0;
			if(itemSection == null)
				Error.INVALID.add();
			else
				for(String key : itemSection.getKeys(false)){
					Material material = Utils.matchMaterial(key, true);
					if(material == null) {
						Error.INVALID.add();
						continue;
					}
					Object current = itemSection.get(key);
					if(current instanceof Integer) {
						int currentAmount = (int) current;
						if(amount + currentAmount > 9) {
							ErrorLogger.addError("Shapeless recipes cannot have more than 9 ingredients.");
							currentAmount = 9 - amount;
						}
						amount += currentAmount;
						if(currentAmount > 0)
							recipe.addIngredient(amount, material);
					}else if(current instanceof ConfigurationSection) {
						ConfigurationSection section = (ConfigurationSection) current;
						int currentAmount = section.getInt(Node.AMOUNT.get());
						if(amount + currentAmount > 9) {
							ErrorLogger.addError("Shapeless recipes cannot have more than 9 ingredients.");
							currentAmount = 9 - amount;
						}
						amount += currentAmount;
						if(currentAmount > 0)
							recipe.addIngredient(currentAmount, new MaterialData(material, (byte) section.getInt(Node.DATA.get(), 0)));
					}
				}
			return recipe;
		}else if(type.equals("FURNACE")){
			FurnaceRecipe recipe=null;
			Object current = c.get("FROM");
			if(current instanceof String) {
				Material material = Utils.matchMaterial((String)current, true);
				if(material == null) {
					Error.INVALID.add();
					return new FurnaceRecipe(item, Material.PAPER);
				}
				recipe= new FurnaceRecipe(item, material);	
			}else if(current instanceof ConfigurationSection) {
				ConfigurationSection section = (ConfigurationSection) current;
				Material material = Utils.matchMaterial(section.getString(Node.MATERIAL.get()), true);
				if(material == null) {
					Error.INVALID.add();
					return new FurnaceRecipe(item, Material.PAPER);
				}
				recipe = new FurnaceRecipe(item, new MaterialData(material, (byte) section.getInt(Node.DATA.get(), 0)));
			}
			return recipe;
		}else
		return null;
	}
	public static void removeRecipe(Material material, int data) {
		if(VanillaPlusCore.getBukkitVersionID()<101200) {
			Iterator<Recipe> it = Bukkit.getServer().recipeIterator();
	        while(it.hasNext()){
	        	Recipe recipe = it.next();
	    		if (recipe.getResult().getType()==material && ( data == -1 || recipe.getResult().getDurability() == data)){
	    			it.remove();
	    		}
	        }
		}else {
			List<Recipe> backup = new ArrayList<Recipe>();
			{
				Iterator<Recipe> it = Bukkit.getServer().recipeIterator();
				while(it.hasNext()){
					Recipe recipe = it.next();
		    		if (!(recipe.getResult().getType()==material && ( data == -1 || recipe.getResult().getDurability() == data))){
						backup.add(recipe);
		    		}
				}
			}
			Bukkit.getServer().clearRecipes();
			for (Recipe r : backup)
				Bukkit.getServer().addRecipe(r);
		}
	}
	public static ItemStack getRandomEnchant() {
		return getRandomEnchant(Arrays.asList(Enchantment.values()), VanillaPlusCore.getRandom().nextInt(3)+1);
	}
	public static ItemStack getRandomEnchant(List<Enchantment>enchants, int amount){
		ItemStack result = new ItemStack(Material.ENCHANTED_BOOK);
		List<Enchantment>left = new ArrayList<Enchantment>(enchants);
		EnchantmentStorageMeta meta = (EnchantmentStorageMeta) result.getItemMeta();
		if(amount > left.size())
			amount = left.size();
		for(int i = 0 ; i < amount && !left.isEmpty() ; i++){
			Enchantment e = VanillaPlusCore.getRandom(left);
			left.remove(e);
			int level = VanillaPlusCore.getRandom().nextInt(100);
			level = level % (e.getMaxLevel()+1-e.getStartLevel())+e.getStartLevel();
			meta.addStoredEnchant(e, level, false);
		}
		result.setItemMeta(meta);
		return result;
	}
	public static String getExtra(ItemStack item, String key){
		NBTItem nbt = new NBTItem(item);
		return nbt.getString(key);
	}
	public static ItemStack setExtra(ItemStack item, String key, String value){
		NBTItem nbt = new NBTItem(item);
		nbt.setString(key, value);
		return nbt.getItem();
	}
	public static String getExtraType(ItemStack item){
		NBTItem nbt = new NBTItem(item);
		return nbt.getString("EXTRA_TYPE");
	}
	private static final Class<?> craftItemStack = ReflectionUtils.getBukkitClass("inventory.CraftItemStack");
	private static final Class<?> itemStack = ReflectionUtils.getServerClass("ItemStack");
	private static final Class<?> NBTBase = ReflectionUtils.getServerClass("NBTBase");
	private static final Class<?> NBTTagCompound = ReflectionUtils.getServerClass("NBTTagCompound");
	private static final Class<?> enchantmentManager = ReflectionUtils.getServerClass("EnchantmentManager");

	private static final Method getCompound = ReflectionUtils.getMethod("getCompound", NBTTagCompound, String.class);
	private static final Method set = ReflectionUtils.getMethod("set", NBTTagCompound, String.class, NBTBase);
	private static final Method getString = ReflectionUtils.getMethod("getString", NBTTagCompound, String.class);
	private static final Method setString = ReflectionUtils.getMethod("setString", NBTTagCompound, String.class, String.class);
	private static final Method getInt = ReflectionUtils.getMethod("getInt", NBTTagCompound, String.class);
	private static final Method setInt = ReflectionUtils.getMethod("setInt", NBTTagCompound, String.class, int.class);
	private static final Method getDouble = ReflectionUtils.getMethod("getDouble", NBTTagCompound, String.class);
	private static final Method setDouble = ReflectionUtils.getMethod("setDouble", NBTTagCompound, String.class, double.class);
	private static final Method getBoolean = ReflectionUtils.getMethod("getBoolean", NBTTagCompound, String.class);
	private static final Method setBoolean = ReflectionUtils.getMethod("setBoolean", NBTTagCompound, String.class, boolean.class);
	private static final Method remove = ReflectionUtils.getMethod("remove", NBTTagCompound, String.class);
	private static final Method hasKey = ReflectionUtils.getMethod("hasKey", NBTTagCompound, String.class);
	private static final Method a = VanillaPlusCore.getBukkitVersionID() < 10900 ?
			ReflectionUtils.getMethod("a", enchantmentManager, Random.class, itemStack, int.class)
			: ReflectionUtils.getMethod("a", enchantmentManager, Random.class, itemStack, int.class, boolean.class);
	private static final Method c = ReflectionUtils.getMethod("c", NBTTagCompound);

	private static final Method setTag = ReflectionUtils.getMethod("setTag", itemStack, NBTTagCompound);
	private static final Method getTag = ReflectionUtils.getMethod("getTag", itemStack);
	
	private static final Method asNMSCopy = ReflectionUtils.getMethod("asNMSCopy", craftItemStack, ItemStack.class);
	private static final Method asCraftMirror = ReflectionUtils.getMethod("asCraftMirror", craftItemStack, itemStack);
	private static Object getNewNBTTag(){
		return ReflectionUtils.instance(NBTTagCompound);
	}
	private static Object setNBTTag(Object NBTTag, Object NMSItem){
		ReflectionUtils.invoke(setTag, NMSItem, NBTTag);
		return NMSItem;
	}
	public static Object getNMSItemStack(ItemStack item){
		Object result = ReflectionUtils.invoke(asNMSCopy, craftItemStack, item); 
		if(result == null)ErrorLogger.addError("Got null! (Outdated Plugin?)");
		return result;
	}
	public static ItemStack getDongeonBook(){
		return getBukkitItemStack(VanillaPlusCore.getBukkitVersionID() < 1900 ?
				ReflectionUtils.invoke(a, enchantmentManager, VanillaPlusCore.getRandom(), getNMSItemStack(new ItemStack(Material.BOOK)), 30):
					ReflectionUtils.invoke(a, enchantmentManager, VanillaPlusCore.getRandom(),
							getNMSItemStack(new ItemStack(Material.BOOK)), 30, true));
	}
	private static ItemStack getBukkitItemStack(Object item){
		return (ItemStack) ReflectionUtils.invoke(asCraftMirror, craftItemStack, item);
	}
	public static Object getRootNBTTagCompound(Object NMSItem){
		Object result = ReflectionUtils.invoke(getTag, NMSItem);
		if(result == null)
			result = getNewNBTTag();
		return result;
	}
	public static Object getSubNBTTagCompound(Object compound, String name){
		return ReflectionUtils.invoke(getCompound, compound, name);
	}
	public static ItemStack addNBTTagCompound(ItemStack item, NBTCompound comp, String name){
		if(name == null)return remove(item, comp, name);
		Object NMSItem = getNMSItemStack(item);
		if(NMSItem == null)return null;
		Object NBTTag = getRootNBTTagCompound(NMSItem);
		if(!valideCompound(item, comp).booleanValue())
			return item;
		Object workingtag = gettoCompount(NBTTag, comp);
	    ReflectionUtils.invoke(set, workingtag, name, ReflectionUtils.instance(NBTTagCompound));
	    NMSItem = setNBTTag(NBTTag, NMSItem);
	    return getBukkitItemStack(NMSItem);
	}
	public static Boolean valideCompound(ItemStack item, NBTCompound comp){
		Object root = getRootNBTTagCompound(getNMSItemStack(item));
		if (gettoCompount(root, comp) != null) {
			return Boolean.valueOf(true);
		}
		return Boolean.valueOf(false);
	}
	private static Object gettoCompount(Object nbttag, NBTCompound comp){
		Stack<String> structure = new Stack<String>();
		while (comp.getParent() != null){
			structure.add(comp.getName());
			comp = comp.getParent();
		}
		while (!structure.isEmpty()){
			nbttag = getSubNBTTagCompound(nbttag, (String)structure.pop());
			if (nbttag == null) {
				return null;
			}
		}
		return nbttag;
	}  
	public static ItemStack setString(ItemStack item, NBTCompound comp, String key, String text){
		if (text == null)return remove(item, comp, key);
		Object NMSItem = getNMSItemStack(item);
		if(NMSItem == null)return null;
		Object rootnbttag = getRootNBTTagCompound(NMSItem);
		if (!valideCompound(item, comp).booleanValue())return item;
		Object workingtag = gettoCompount(rootnbttag, comp);
	    ReflectionUtils.invoke(setString, workingtag, key, text);
	    NMSItem = setNBTTag(rootnbttag, NMSItem);
	    return getBukkitItemStack(NMSItem);
	  }
	public static String getString(ItemStack item, NBTCompound comp, String key){
		Object NMSItem = getNMSItemStack(item);
		if(NMSItem == null)return null;
		Object rootnbttag = getRootNBTTagCompound(NMSItem);
		if (!valideCompound(item, comp).booleanValue())return null;
		Object workingtag = gettoCompount(rootnbttag, comp);
		return (String)ReflectionUtils.invoke(getString, workingtag, key);
	}
	public static ItemStack setInt(ItemStack item, NBTCompound comp, String key, int i){
		Object NMSItem = getNMSItemStack(item);
		if(NMSItem == null)return null;
		Object rootnbttag = getRootNBTTagCompound(NMSItem);
		if (!valideCompound(item, comp).booleanValue())return item;
		Object workingtag = gettoCompount(rootnbttag, comp);
	    ReflectionUtils.invoke(setInt, workingtag, key, i);
	    NMSItem = setNBTTag(rootnbttag, NMSItem);
	    return getBukkitItemStack(NMSItem);
	}
	public static int getInt(ItemStack item, NBTCompound comp, String key){
		Object NMSItem = getNMSItemStack(item);
		if(NMSItem == null)return 0;
		Object rootnbttag = getRootNBTTagCompound(NMSItem);
		if (!valideCompound(item, comp).booleanValue())return 0;
		Object workingtag = gettoCompount(rootnbttag, comp);
		return (Integer)ReflectionUtils.invoke(getInt, workingtag, key);
	}
	public static ItemStack setDouble(ItemStack item, NBTCompound comp, String key, double d){
		Object NMSItem = getNMSItemStack(item);
		if(NMSItem == null)return null;
		Object rootnbttag = getRootNBTTagCompound(NMSItem);
		if (!valideCompound(item, comp).booleanValue())return item;
		Object workingtag = gettoCompount(rootnbttag, comp);
	    ReflectionUtils.invoke(setDouble, workingtag, key, d);
	    NMSItem = setNBTTag(rootnbttag, NMSItem);
	    return getBukkitItemStack(NMSItem);
	}
	public static double getDouble(ItemStack item, NBTCompound comp, String key){
		Object NMSItem = getNMSItemStack(item);
		if(NMSItem == null)return 0.0;
		Object rootnbttag = getRootNBTTagCompound(NMSItem);
		if (!valideCompound(item, comp).booleanValue())return 0.0;
		Object workingtag = gettoCompount(rootnbttag, comp);
		return (double)ReflectionUtils.invoke(getDouble, workingtag, key);
	}
	public static ItemStack setBoolean(ItemStack item, NBTCompound comp, String key, boolean b){
		Object NMSItem = getNMSItemStack(item);
		if(NMSItem == null)return null;
	    Object rootnbttag = getRootNBTTagCompound(NMSItem);
	    if (!valideCompound(item, comp).booleanValue())return item;
	    Object workingtag = gettoCompount(rootnbttag, comp);
	    ReflectionUtils.invoke(setBoolean, workingtag, key, b);
	    NMSItem = setNBTTag(rootnbttag, NMSItem);
	    return getBukkitItemStack(NMSItem);
	}
	public static boolean getBoolean(ItemStack item, NBTCompound comp, String key){
		Object NMSItem = getNMSItemStack(item);
		if(NMSItem == null)return false;
		Object rootnbttag = getRootNBTTagCompound(NMSItem);
		if (!valideCompound(item, comp).booleanValue())return false;
		Object workingtag = gettoCompount(rootnbttag, comp);
		return (boolean)ReflectionUtils.invoke(getBoolean, workingtag, key);
	}
	public static ItemStack setObject(ItemStack item, NBTCompound comp, String key, Object value){
		String json = GSon.serializeJson(value);
		if(json != null)
			return setString(item, comp, key, json);
		return null;
	}
	public static <T> T getObject(ItemStack item, NBTCompound comp, String key, Class<T> type){
		String json = getString(item, comp, key);
		if (json == null)return null;
		return GSon.deserializeJson(json, type);
	}
	public static ItemStack remove(ItemStack item, NBTCompound comp, String key){
		Object NMSItem = getNMSItemStack(item);
		if(NMSItem == null)return null;
		Object rootnbttag = getRootNBTTagCompound(NMSItem);
		if (!valideCompound(item, comp).booleanValue())return item;
	    Object workingtag = gettoCompount(rootnbttag, comp);
	    ReflectionUtils.invoke(remove, workingtag, key);
	    NMSItem = setNBTTag(rootnbttag, NMSItem);
	    return getBukkitItemStack(NMSItem);
	}
	public static boolean hasKey(ItemStack item, NBTCompound comp, String key){
		Object NMSItem = getNMSItemStack(item);
		if(NMSItem == null)return false;
		Object rootnbttag = getRootNBTTagCompound(NMSItem);
	    if (!valideCompound(item, comp).booleanValue())return false;
	    Object workingtag = gettoCompount(rootnbttag, comp);
		return (boolean)ReflectionUtils.invoke(hasKey, workingtag, key);
	}
	@SuppressWarnings("unchecked")
	public static Set<String> getKeys(ItemStack item, NBTCompound comp){
		Object NMSItem = getNMSItemStack(item);
		if(NMSItem == null)return null;
		Object rootnbttag = getRootNBTTagCompound(NMSItem);
		if (!valideCompound(item, comp).booleanValue())return null;
		Object workingtag = gettoCompount(rootnbttag, comp);
		return (Set<String>) ReflectionUtils.invoke(c, workingtag);
	}
}
