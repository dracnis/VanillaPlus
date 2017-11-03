package fr.soreth.VanillaPlus.IRequirement;

import org.bukkit.configuration.ConfigurationSection;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.Localizer;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.MComponent.MComponent;
import fr.soreth.VanillaPlus.MComponent.MComponentManager;
import fr.soreth.VanillaPlus.Player.Title;
import fr.soreth.VanillaPlus.Player.VPPlayer;

public class RequirementTitle implements IRequirement {
	private final Title title;
	private final boolean keep;
	private final MComponent format;
	public RequirementTitle(ConfigurationSection section, MComponentManager manager) {
		ErrorLogger.addPrefix(Node.ID.get());
		this.title = VanillaPlusCore.getTitleManager().get(section.getInt(Node.ID.get()));
		ErrorLogger.removePrefix();
		boolean keep = section.getBoolean("KEEP", false);
		this.keep = keep;
		format = manager.get(section.getString(Node.FORMAT.get(), "REQUIREMENT.TITLE"));
	}
	@Override
	public String format(VPPlayer player, Localizer lang) {
		return format(player, lang, 1);
	}
	@Override
	public String format(VPPlayer player, Localizer lang, int amount) {
		boolean has = has(player);
		return format.addCReplacement("state", IRequirementManager.getState(has))
				.addCReplacement("name", this.title.getName()).getMessage(player);
				
	}
	@Override
	public int getMax(VPPlayer player) {
		return has(player) ? -1 : 0;
	}
	@Override
	public boolean has(VPPlayer player) {
		return player.hasTitle(title.getID());
	}
	@Override
	public void take(VPPlayer player, int multiplier) {
		if(keep) return;
		player.removeTitle(title);
	}
}
