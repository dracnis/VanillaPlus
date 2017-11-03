package fr.soreth.VanillaPlus.IRequirement;
import java.util.HashMap;
import java.util.regex.Pattern;

import org.bukkit.configuration.ConfigurationSection;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.Error;
import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.Localizer;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.MComponent.MComponent;
import fr.soreth.VanillaPlus.MComponent.MComponentManager;
import fr.soreth.VanillaPlus.Player.VPPlayer;
import fr.soreth.VanillaPlus.Player.VPSender;
import fr.soreth.VanillaPlus.Utils.Utils;
public class Requirement implements IRequirement{
	private IRequirement requirement;
	private static MComponent formatAnd, formatNot, formatOr;
	public Requirement(Object o, MComponentManager manager){
		if(o == null){
			return;
		}
		if(o instanceof String){
			if(((String)o).isEmpty())return;
			requirement = parseString((String) o, new HashMap<String, IRequirement>());
		}else if(o instanceof ConfigurationSection){
			ConfigurationSection section = (ConfigurationSection) o;
			if(section.contains(Node.TYPE.get())) {
				VanillaPlusCore.getIRequirementManager().create(section.getString(Node.TYPE.get()), section, manager);
			} else {
				HashMap<String, IRequirement> map = new HashMap<String, IRequirement>();
				for(String key : section.getKeys(false)){
					if(key.equals("RULE"))continue;
					ErrorLogger.addPrefix(key);
					ConfigurationSection sub = section.getConfigurationSection(key);
					if(sub == null){
						String s = section.getString(key);
						if(s == null)
						Error.INVALID.add();
						else {
							IRequirement r = parseString(s, map);
							if(r == null)
								Error.INVALID.add();
							else
								map.put(key, r);
						}
					}else{
						IRequirement r = VanillaPlusCore.getIRequirementManager().create(sub.getString(Node.TYPE.get()), sub, manager);
						if(r == null)
							Error.INVALID.add();
						else
							map.put(key, r);
					}
					ErrorLogger.removePrefix();
				}
				requirement = parseString(section.getString("RULE", Utils.toString(map.keySet(), "&&")), map);
			}
		}
	}
	public static void setAnd(MComponent component) {
		formatAnd = component;
	}
	public static void setNot(MComponent component) {
		formatNot = component;
	}
	public static void setOr(MComponent component) {
		formatOr = component;
	}
	private class NotComponent implements IRequirement{
		IRequirement requirement;
		public NotComponent(String expression, HashMap<String, IRequirement> component){
			requirement = parseString(expression, component);
		}
		@Override
		public boolean has(VPPlayer viewer){
			return !requirement.has(viewer);
		}
		@Override
		public void take(VPPlayer player, int amount){}
		@Override
		public String format(VPPlayer player, Localizer lang){
			return format(player, lang, 1);
		}
		@Override
		public String format(VPPlayer player, Localizer lang, int amount){
			return formatNot.addReplacement("not", requirement.format(player, lang, amount)).getMessage(player);
		}
		@Override
		public int getMax(VPPlayer player) {
			return has(player) ? -1 : 0;
		}
	}
	private class AndComponent implements IRequirement{
		IRequirement requirement;
		IRequirement requirement2;
		public AndComponent(String value1, String value2, HashMap<String, IRequirement> component){
			requirement = parseString(value1, component);
			requirement2 = parseString(value2, component);
		}
		@Override
		public boolean has(VPPlayer viewer){
			return requirement.has(viewer) && requirement2.has(viewer);
		}
		@Override
		public void take(VPPlayer player, int amount){
			requirement.take(player, amount);
			requirement2.take(player, amount);
		}
		@Override
		public String format(VPPlayer player, Localizer lang){
			return format(player, lang, 1);
		}
		@Override
		public String format(VPPlayer player, Localizer lang, int amount){
			return formatAnd.addReplacement("and1", requirement.format(player, lang, amount))
					.addReplacement("and2", requirement2.format(player, lang)).getMessage(player);
		}
		@Override
		public int getMax(VPPlayer player) {
			int i = requirement.getMax(player);
			int i2 = requirement2.getMax(player);
			if( i == -1 ) i = i2;
			if( i > i2 && i2 != -1) i = i2;
			if( i == -1 ) i = 1;
			return i;
		}
	}
	private class OrComponent implements IRequirement{
		IRequirement requirement;
		IRequirement requirement2;
		public OrComponent(String value1, String value2, HashMap<String, IRequirement> component){
			requirement = parseString(value1, component);
			requirement2 = parseString(value2, component);
		}
		@Override
		public boolean has(VPPlayer viewer){
			return requirement.has(viewer) || requirement2.has(viewer);
		}
		@Override
		public void take(VPPlayer player, int amount){
			if(requirement.has(player))
				requirement.take(player, amount);
			else if(requirement2.has(player))
				requirement2.take(player, amount);
		}
		@Override
		public String format(VPPlayer player, Localizer lang){
			return format(player, lang, 1);
		}
		@Override
		public String format(VPPlayer player, Localizer lang, int amount){
			return formatOr.addReplacement("or1", requirement.format(player, lang, amount))
					.addReplacement("or2", requirement2.format(player, lang)).getMessage(player);
		}
		@Override
		public int getMax(VPPlayer player) {
			int i = requirement.getMax(player);
			if( i == -1 || i > 0){
				if( i == -1 ) i = 1;
				return i;
			}
			i = requirement2.getMax(player);
			if( i == -1 || i > 0){
				if( i == -1 ) i = 1;
				return i;
			}
			return 0;
		}
	}
	
	private static final String and				= "&&";
	private static final String or				= "||";
	private static final Pattern start			= Pattern.compile("^ *\\( *+");
	private static final Pattern end			= Pattern.compile(" *\\) *+$");
	
	private IRequirement parseString(String input, HashMap<String, IRequirement>parts){
		//Loop while start with "(" with or without space(s) and end with ")" with or without space(s).  
		// Ex : "   (  (( (  lead && ( premium || fonda ) ) )))"  became : "lead && ( premium || fonda )"
		while(start.matcher(input).find() && end.matcher(input).find()){
			boolean useless = false;
			int score = 0;
			//Read input, can be improved ?
			for(int i = 0 ; i < input.length() ; i++){
				char c = input.charAt(i);
				if(c == '('){
					//That mean we already found ( ... ) somewhere so it is useful
					if(score == 0 && useless){
						useless = false;
						break;
					}
					score ++;
				}
				else if( c == ')'){
					score --;
					//That mean we closed the first '('.
					if(score == 0){
						useless = true;
					}else if( score < 0) {
						ErrorLogger.addError("Invalid value :" + input + " can't close parentheses without open first !");
						return null;
					}
				}
			}
			//if first '(' is for the last ')' we can remove them, ex: ( lead && ( premium || fonda ) ) became : lead && ( premium || fonda )
			if(useless)
				input = input.replaceFirst(start.pattern(), "").replaceFirst(end.pattern(), "");
			else
				break;
		}
		//if start or end with '&&' or '||' or end with ! it's an error
		if(input.startsWith(or) || input.startsWith(and) || input.endsWith(or) || input.endsWith(and) || input.endsWith("!")){
			ErrorLogger.addError("Invalid value :" + input);
			return null;
		}
		input = input.replaceFirst("^ *", "").replaceFirst(" *$", "");
		String[]temp = input.split("\\|\\|");
		String value1 = "";
		String value2 = "";
		boolean valid = false;
		if(temp.length > 1)
		//Loop over temp values
		for(String s : temp){
			//if not valid add them to value1 with || between them.
			if(!valid){
				value1 += value1.isEmpty() ? s : (or + s);
				//if value1 has same amount of '(' and ')' we assume it's well splitted.
				if(value1.replace("(", "").length() == value1.replace(")", "").length() && !value1.isEmpty())
					valid = true;
			}
			//else add them to value2 with || between them.
			else value2 += value2.isEmpty() ? s : (or + s);
		}
		//if valid and value1 and value2 aren't empty we create new orComponent.
		if(valid && !value2.isEmpty() && !value1.isEmpty()){
			return new OrComponent(value1, value2, parts);
		}else{
			temp = input.split(and);
			value1 = "";
			value2 = "";
			valid = false;
			if(temp.length > 1)
			for(String s : temp){
				//System.out.println(input + " start: " + start + " v1 : " + value1 + " v2 : " + value2 + " s:" + s + value1.replace("(", "").length() 
					//	+ " " + value1.replace(")", "").length() + " " + !value1.isEmpty());
				if(!valid){
					value1 += value1.isEmpty() ? s : ("&&" + s);
					if(value1.replace("(", "").length() == value1.replace(")", "").length() && !value1.isEmpty())
						valid = true;
				}
				else value2 += value2.isEmpty() ? s : ("&&" + s);
			}
			if(valid && !value2.isEmpty() && !value1.isEmpty()){
				return new AndComponent(value1, value2, parts);
			}else if(input.startsWith("!")){
				return new NotComponent(input.replaceFirst("!", ""), parts);
			}else{
				value1 = input.replaceAll(" ", "");
				if(value1.length() != input.length()){
					ErrorLogger.addError("'"+input + "' is invalid using : '" + value1 + "'");
					input = value1;
				}
				IRequirement requirement = parts.get(input);
				if(requirement != null){
					return requirement;
				}
				requirement = VanillaPlusCore.getIRequirementManager().get(input, false);
				if(requirement != null){
					return requirement;
				}
				return new RequirementPermission(input);
			}
		}
	}
	public String format(VPPlayer player, Localizer lang){
		if(requirement == null)return "";
		return requirement.format(player, lang, 1);
	}
	public String format(VPPlayer player, Localizer lang, int amount){
		if(requirement == null)return "";
		return requirement.format(player, lang, amount);
	}
	public boolean has(VPPlayer sender){
		if(requirement == null)return true;
		 return requirement.has((VPPlayer) sender);
	}
	public boolean has(VPSender sender){
		if(requirement == null || !(sender instanceof VPPlayer))return true;
		 return requirement.has((VPPlayer) sender);
	}
	public void take(VPPlayer player){
		if(requirement == null)return; 
		requirement.take(player, 1);
	}
	public void take(VPPlayer player, int amount){
		if(amount < 1 || requirement == null)return;
		requirement.take(player, amount);
	}
	public int takeAll(VPPlayer player){
		int i = requirement == null ? 0 : requirement.getMax(player);
		if( i == 0 )return 0;
		take(player, i);
		return i;
	}
	@Override
	public int getMax(VPPlayer player) {
		return requirement == null ? -1 : requirement.getMax(player);
	}
}
