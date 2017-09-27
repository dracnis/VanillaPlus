package fr.soreth.VanillaPlus.Icon;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.VanillaPlusExtension;
import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.Manager;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.Message.MessageManager;
import fr.soreth.VanillaPlus.Utils.Minecraft.ConfigUtils;

public class IconManager extends Manager<String, Icon>{
	private Icon fillIcon;
	public IconManager() {
		super(String.class, Icon.class);
		register(IconAchievement.class, "ACHIEVEMENT");
		register(IconExtended.class, 	"EXTENDED");
		register(IconTitle.class, 		"TITLE");
	}
	public Icon create(MessageManager manager, ConfigurationSection section) {
		if(section == null)
			return null;
		return super.create(section.getString(Node.TYPE.get(), Node.BASE.get()), section, manager);
	}
	public void init(VanillaPlusCore core) {
		if(core == null)return;
		YamlConfiguration section = ConfigUtils.getYaml(core.getInstance(), "Icon", false);
		ErrorLogger.addPrefix("Icon.yml");
		ConfigurationSection settings = section.getConfigurationSection(Node.SETTINGS.get());
		if(settings != null){
			ErrorLogger.addPrefix(Node.SETTINGS.get());
			fillIcon = get(settings.getString(Node.FILL.get()), true);
			ErrorLogger.removePrefix();
		}
		ErrorLogger.removePrefix();
	}
	public void init(VanillaPlusExtension extension) {
		if(extension == null)return;
		YamlConfiguration section = ConfigUtils.getYaml(extension.getInstance(), "Icon", false);
		if(section == null)return;
		ConfigurationSection icons = section.getConfigurationSection(Node.ICON.getList());
		ErrorLogger.addPrefix("Icon.yml");
		if(icons != null){
			super.init(icons, extension.getMessageManager());
		}
		ErrorLogger.removePrefix();
	}
	public Icon getFillIcon(){
		return fillIcon;
	}
}
