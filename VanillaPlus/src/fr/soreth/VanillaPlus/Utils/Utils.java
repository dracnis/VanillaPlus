package fr.soreth.VanillaPlus.Utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import fr.soreth.VanillaPlus.ErrorLogger;

public class Utils {
	private static final Map<String, Material> materialMap = new HashMap<String, Material>();
	private static final Map<String, Material> itemMap = new HashMap<String, Material>();
	
	static {
		for (Material mat : Material.values()) {
			materialMap.put(mat.toString().replaceAll("_", "").toLowerCase(), mat);
		}
		materialMap.put("ironbar",				Material.IRON_FENCE);
		materialMap.put("ironbars",				Material.IRON_FENCE);
		materialMap.put("glasspane",			Material.THIN_GLASS);
		materialMap.put("netherwart",			Material.NETHER_STALK);
		materialMap.put("netherwarts",			Material.NETHER_STALK);
		materialMap.put("slab",					Material.STEP);
		materialMap.put("doubleslab",			Material.DOUBLE_STEP);
		materialMap.put("stonebrick",			Material.SMOOTH_BRICK);
		materialMap.put("stonebricks",			Material.SMOOTH_BRICK);
		materialMap.put("stonestair",			Material.SMOOTH_STAIRS);
		materialMap.put("stonestairs",			Material.SMOOTH_STAIRS);
		materialMap.put("carrotonstick",		Material.CARROT_STICK);
		materialMap.put("carrotonastick",		Material.CARROT_STICK);
		materialMap.put("cobblestonewall",		Material.COBBLE_WALL);
		materialMap.put("acaciawoodstairs",		Material.ACACIA_STAIRS);
		materialMap.put("darkoakwoodstairs",	Material.DARK_OAK_STAIRS);
		materialMap.put("woodslab",				Material.WOOD_STEP);
		materialMap.put("doublewoodslab",		Material.WOOD_DOUBLE_STEP);
		materialMap.put("repeater",				Material.DIODE);
		materialMap.put("piston",				Material.PISTON_BASE);
		materialMap.put("stickypiston",			Material.PISTON_STICKY_BASE);
		materialMap.put("flowerpot",			Material.FLOWER_POT_ITEM);
		materialMap.put("woodshowel",			Material.WOOD_SPADE);
		materialMap.put("stoneshowel",			Material.STONE_SPADE);
		materialMap.put("goldshowel",			Material.GOLD_SPADE);
		materialMap.put("ironshowel",			Material.IRON_SPADE);
		materialMap.put("diamondshowel",		Material.DIAMOND_SPADE);
		materialMap.put("steak",				Material.COOKED_BEEF);
		materialMap.put("cookedporkchop",		Material.GRILLED_PORK);
		materialMap.put("rawporkchop",			Material.PORK);
		materialMap.put("hardenedclay",			Material.HARD_CLAY);
		materialMap.put("hugebrownmushroom",	Material.HUGE_MUSHROOM_1);
		materialMap.put("hugeredmushroom",		Material.HUGE_MUSHROOM_2);
		materialMap.put("mycelium",				Material.MYCEL);
		materialMap.put("poppy",				Material.RED_ROSE);
		materialMap.put("comparator",			Material.REDSTONE_COMPARATOR);
		materialMap.put("skull",				Material.SKULL_ITEM);
		materialMap.put("head",					Material.SKULL_ITEM);
		materialMap.put("redstonetorch",		Material.REDSTONE_TORCH_ON);
		materialMap.put("redstonelamp",			Material.REDSTONE_LAMP_OFF);
		materialMap.put("glisteringmelon",		Material.SPECKLED_MELON);
		materialMap.put("acacialeaves", 		Material.LEAVES_2);
		materialMap.put("acacialog", 			Material.LOG_2);
		materialMap.put("gunpowder",			Material.SULPHUR);
		materialMap.put("lilypad",				Material.WATER_LILY);
		materialMap.put("commandblock",			Material.COMMAND);
		materialMap.put("dye",					Material.INK_SACK);
		materialMap.put("diamondarmor",			Material.DIAMOND_BARDING);
		materialMap.put("diamondhorsearmor",	Material.DIAMOND_BARDING);
		materialMap.put("ironarmor",			Material.IRON_BARDING);
		materialMap.put("ironhorsearmor",		Material.IRON_BARDING);
		materialMap.put("goldarmor",			Material.GOLD_BARDING);
		materialMap.put("goldenhorsearmor",		Material.GOLD_BARDING);

		itemMap.put("potato",				Material.POTATO_ITEM);
		itemMap.put("carrot",				Material.CARROT_ITEM);
		itemMap.put("cauldron",				Material.CAULDRON_ITEM);
		itemMap.put("brewingstand",			Material.BREWING_STAND_ITEM);
	}
	public static String toString(Collection<?>toString){
		return toString(toString, " ");
	}
	public static String toString(Collection<?>toString, String spacer){
		String result = "";
		if(toString == null)return result;
		for(Object s : toString)
			result += s + spacer;
		if(!result.isEmpty())
			result = result.substring(0, result.length()-spacer.length());
		return result;
	}
	public static String addColors(String input) {
		if (input == null || input.isEmpty()) return input;
		return ChatColor.translateAlternateColorCodes('&', input);
	}
	public static String capitalize(String input) {
		if (input == null || input.isEmpty()) return input;
		boolean color = false;
		char[] inputChar = input.toCharArray();
		for(int i = 0 ; i < input.length() ; i++){
			if(color){
				color = false;
				continue;
			}
			if(inputChar[i] == '§' || inputChar[i] == '&'){
				color = true;
				continue;
			}
			char c = inputChar[i];
			c = Character.toUpperCase(c);
			if(c != inputChar[i]){
				inputChar[i] = c;
				break;
			}
		}
		return String.valueOf(inputChar);
	}
	
	public static List<String> addColors(List<String> input) {
		if (input == null || input.isEmpty()) return input;
		for (int i = 0; i < input.size(); i++) {
			input.set(i, addColors(input.get(i)));
		}
		return input;
	}
	@SuppressWarnings("deprecation")
	public static Material matchMaterial(String input, boolean item) {
		if (input == null) return null;
		
		input = input.toLowerCase().replaceAll("[ _-]", "");
		Material result = null; 
		if(item)
			result = itemMap.get(input); 
		if(result == null)
			result = materialMap.get(input);
		if (result == null && isValidInteger(input)) {
			result = Material.getMaterial(Integer.parseInt(input));
		}
		if(result == null){
			ErrorLogger.addError(input + " isn't a valid material !");
		}
		return materialMap.get(input);
	}
	@SuppressWarnings("rawtypes")
	public static <T extends Enum> T matchEnum(T[] enumList, String input, boolean log) {
		T result = firstEnum(enumList, input);
		if(result != null)
			return result;
		if(log)
			ErrorLogger.addMissingEnum(enumList, input);
		return enumList.length == 0 ? null : enumList[0];
	}
	@SuppressWarnings("rawtypes")
	public static <T extends Enum> T matchEnum(T[] enumList, String input, T defaultValue, boolean log) {
		T result = firstEnum(enumList, input);
		if(result != null)
			return result;
		if(log)
			ErrorLogger.addMissingEnum(enumList, input);
		return defaultValue;
	}
	@SuppressWarnings("rawtypes")
	private static <T extends Enum> T firstEnum(T[] enumList, String input) {
		if (input == null || enumList == null) return null;
		
		input = input.toLowerCase().replaceAll(" ", "")
				.replaceAll("-", "")
				.replaceAll("_", "");
		for(T type : enumList){
			if(type.name().replaceAll(" ", "").replaceAll("-", "")
					.replaceAll("_", "").equalsIgnoreCase(input))
				return type;
		}
		return null;
	}
	
	public static int makePositive(int i) {
		return i < 0 ? i*-1 : i;
	}
	public static int parseInt(String value, int def, boolean log){
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			if(log)
				ErrorLogger.addError(value + " isn't a valid int");
			return def;
		}
	}
	public static double parseDouble(String value, double def, boolean log){
		value = value.replace(',', '.');
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException e) {
			if(log)
				ErrorLogger.addError(value + " isn't a valid double");
			return def;
		}
	}
	public static float parseFloat(String value, float def){
		value = value.replace(',', '.');
		try {
			return Float.parseFloat(value);
		} catch (NumberFormatException e) {
			ErrorLogger.addError(value + " isn't a valid float");
			return def;
		}
	}
	public static boolean isValidInteger(String input) {
		try {
			Integer.parseInt(input);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}
	public static boolean isValidDouble(String input) {
		try {
			Double.parseDouble(input);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}
	public static boolean isValidShort(String input) {
		try {
			Short.parseShort(input);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}
	
	public static boolean isValidPositiveDouble(String input) {
		input = input.replace(',', '.');
		try {
			return Double.parseDouble(input) > 0.0;
		} catch (NumberFormatException ex) {
			return false;
		}
	}
	public static long parseLong(String value, long def, boolean log){
		value = value.replace(',', '.');
		try {
			return Long.parseLong(value);
		} catch (NumberFormatException e) {
			if(log)
				ErrorLogger.addError(value + " isn't a valid long");
			return def;
		}
	}
}
