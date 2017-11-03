package fr.soreth.VanillaPlus.IRequirement;

import org.bukkit.configuration.ConfigurationSection;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.VanillaPlusExtension;
import fr.soreth.VanillaPlus.Error;
import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.Manager;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.MComponent.MComponent;
import fr.soreth.VanillaPlus.Utils.Minecraft.ConfigUtils;

public class IRequirementManager extends Manager<String, IRequirement>{
	private static MComponent own, lack;
	public IRequirementManager() {
		super(String.class, IRequirement.class);
		RequirementLoader.load(this);
	}
	public void init(VanillaPlusCore core) {
		ConfigurationSection section = ConfigUtils.getYaml(core.getInstance(), "Requirement", false);
		ErrorLogger.addPrefix("Requirement.yml");
		ConfigurationSection settings = section == null ? null : section.getConfigurationSection(Node.SETTINGS.get());
		if(settings!=null && own == null) {
			ErrorLogger.addPrefix(Node.SETTINGS.get());
			own							= core.getMessageCManager().get(settings.getString("OWN"));
			lack						= core.getMessageCManager().get(settings.getString("LACK"));
			RequirementPermission.init(	core.getMessageCManager().get(settings.getString("PERMISSION")));
			Requirement.setAnd(			core.getMessageCManager().get(settings.getString("AND")));
			Requirement.setNot(			core.getMessageCManager().get(settings.getString("NOT")));
			Requirement.setOr(			core.getMessageCManager().get(settings.getString("OR")));
			ErrorLogger.removePrefix();
		} else if ( own == null ) {
			own = lack = core.getMessageCManager().get(null);
			Error.MISSING_NODE.add(Node.SETTINGS.get());
		}
		ErrorLogger.removePrefix();
	}
	public void init(VanillaPlusExtension extension) {
		ConfigurationSection section = ConfigUtils.getYaml(extension.getInstance(), "Requirement", false);
		if(section == null)return;
		ErrorLogger.addPrefix("Requirement.yml");
		ConfigurationSection temp = section.getConfigurationSection("SIMPLE_"+Node.REQUIREMENT.getList());
		if(temp != null) {
			ErrorLogger.addPrefix("SIMPLE_"+Node.REQUIREMENT.getList());
			for(String s : temp.getKeys(false)){
				ErrorLogger.addPrefix(s);
				String current = temp.getString(s);
				MComponent component = extension.getMessageCManager().get(current);
				if(current == null) {
					Error.INVALID.add();
				}else {
					IRequirement requirement = get(s, true);
					if(SimpleRequirement.class.isAssignableFrom(requirement.getClass())) {
						((SimpleRequirement) requirement).init(component);
					}else {
						ErrorLogger.addError(" is not a valid SimpleRequirement !");
					}
				}
				ErrorLogger.removePrefix();
			}
			ErrorLogger.removePrefix();
		}
		temp = section.getConfigurationSection(Node.REQUIREMENT.getList());
		if(temp != null) {
			ErrorLogger.addPrefix(Node.REQUIREMENT.getList());
			for(String s : temp.getKeys(false)){
				ErrorLogger.addPrefix(s);
				register(s, new Requirement(temp.get(s), extension.getMessageCManager()), true);
				ErrorLogger.removePrefix();
			}
			ErrorLogger.removePrefix();
		}
		ErrorLogger.removePrefix();
	}
	public static MComponent getState(boolean has) {
		return has ? own : lack;
	}
}
