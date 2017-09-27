package fr.soreth.VanillaPlus.Command;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.Message.MessageManager;
import fr.soreth.VanillaPlus.Localizer;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.Player.VPSender;

/**
 * This command allow you change your language.
 * TYPE: LANG
 * LANG: Language's code // default server language, log if invalid.
 * 
 * Usage : <label>
 *
 * @author Soreth.
 */

public class CPLang extends CPSimple{
	private final Localizer local;
	public CPLang(ConfigurationSection section, MessageManager manager){
		this(section, manager, section.getName());
	}
	public CPLang(ConfigurationSection section, MessageManager manager, String name){
		super(section, manager, name);
		Localizer lang = Localizer.getByCode(section.getString(Node.LANG.get(),VanillaPlusCore.getDefaultLang().getCode())); 
		local = VanillaPlusCore.isUsed(lang) ? lang : VanillaPlusCore.getDefaultLang();
	}
	protected CommandResult apply(VPSender receiver, String label, List<String> args) {
		if( receiver.getLanguage() != local ){
			receiver.setLanguage(local);
			return CommandResult.SUCCESS;
		}
		return CommandResult.CANCELED;
		
	}
}
