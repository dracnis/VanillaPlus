package fr.soreth.VanillaPlus.IRequirement;

import java.util.HashMap;

import org.bukkit.permissions.Permission;

import fr.soreth.VanillaPlus.Localizer;
import fr.soreth.VanillaPlus.MComponent.MComponent;
import fr.soreth.VanillaPlus.Player.VPPlayer;

public class RequirementPermission implements IRequirement {
	private static HashMap<String, Permission>list = new HashMap<String, Permission>();
	private final Permission perm;
	protected static MComponent format;
	public RequirementPermission(String permission){
		Permission p = list.get(permission);
		if(p == null){
			p = new Permission(permission);
			list.put(permission, p);
		}
		perm = p;
	}
	@Override
	public String format(VPPlayer player, Localizer lang){
		return format(player, lang, 1);
	}
	@Override
	public String format(VPPlayer player, Localizer lang, int amount) {
		boolean has = has(player);
		return format.addCReplacement("state", IRequirementManager.getState(has))
				.addReplacement("permission", perm.getName())
				.getMessage(player);
	}
	@Override
	public int getMax(VPPlayer player){
		return has(player) ? -1 : 0;
	}
	@Override
	public boolean has(VPPlayer player) {
		return player.getPlayer().hasPermission(perm);
	}
	public static void init(MComponent format) {
		RequirementPermission.format = format;
	}
	@Override
	public void take(VPPlayer player, int multiplier) {}

}
