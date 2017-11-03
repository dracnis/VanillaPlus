package fr.soreth.VanillaPlus.IRequirement;

import org.bukkit.configuration.ConfigurationSection;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.Localizer;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.MComponent.MComponent;
import fr.soreth.VanillaPlus.MComponent.MComponentManager;
import fr.soreth.VanillaPlus.Player.VPPlayer;

public class RequirementLang implements IRequirement {
	private final Localizer lang;
	private final MComponent format;
	public RequirementLang(ConfigurationSection section, MComponentManager manager) {
		ErrorLogger.addPrefix(Node.LANG.get());
		Localizer lang = Localizer.getByCode(section.getString(Node.LANG.get()));
		if(VanillaPlusCore.isUsed(lang))
			this.lang = lang;
		else {
			ErrorLogger.addError(" not used by the server !");
			this.lang = VanillaPlusCore.getDefaultLang();
		}
		ErrorLogger.removePrefix();
		format = manager.get(section.getString(Node.FORMAT.get(), "REQUIREMENT.LANG"));
	}
	@Override
	public String format(VPPlayer player, Localizer lang) {
		return format(player, lang, 1);
	}
	@Override
	public String format(VPPlayer player, Localizer lang, int amount) {
		boolean has = has(player);
		return format.addCReplacement("state", IRequirementManager.getState(has))
				.addReplacement("lang", this.lang.getName()).getMessage(player);
				
	}
	@Override
	public int getMax(VPPlayer player) {
		return has(player) ? -1 : 0;
	}
	@Override
	public boolean has(VPPlayer player) {
		return player.getLanguage()==lang;
	}
	@Override
	public void take(VPPlayer player, int multiplier) {
	}
}
