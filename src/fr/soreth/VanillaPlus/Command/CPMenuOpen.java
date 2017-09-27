package fr.soreth.VanillaPlus.Command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.Error;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.Menu.Menu;
import fr.soreth.VanillaPlus.Message.MessageManager;
import fr.soreth.VanillaPlus.Player.VPPlayer;
import fr.soreth.VanillaPlus.Player.VPSender;

/**
 * This command allow you to open a menu.
 * TYPE: MENU_OPEN
 * MENU: name of the menu // log if invalid.
 * VIEW: boolean // default false, if true, will open inventory as selected player else will open menu to selected player.
 *
 * Usage :
 * view is false : <label> [player]
 * view is true : <label> [player] <player>
 * 
 * @author Soreth.
 */
public class CPMenuOpen extends CPOther{
	private Menu menu;
	private boolean view, close;
	public CPMenuOpen(ConfigurationSection section, MessageManager manager){
		this(section, manager, section.getName());
	}
	public CPMenuOpen(ConfigurationSection section, MessageManager manager, String name){
		super(section, manager, name);
		close = section.getBoolean("CLOSE", false);
		if(close)return;
		String menuName = section.getString(Node.MENU.get());
		if(menuName == null || menuName.isEmpty()){
			Error.MISSING_NODE.add(Node.MENU.get());
		}else{
			menu = VanillaPlusCore.getMenuManager().get(section.getString(Node.MENU.get()), true);
		}
		view = section.getBoolean("VIEW", false);
		if(view)
			argumentRequired = 1;
	}
	@Override
	public boolean is(String name) {
		if(this.menu == null && !close)return false;
		return super.is(name);
	}
	@Override
	protected CommandResult apply(VPSender receiver, String label, List<String> args, boolean other, VPSender sender) {
		if(this.menu == null && ! close)return CommandResult.CANCELED_OTHER;
		if(view) {
			if(receiver instanceof VPPlayer) {
				VPPlayer viewPlayer = VanillaPlusCore.getPlayerManager().getPlayer(args.get(0));
				if( menu.open((VPPlayer) receiver, viewPlayer, args) != null ) {
					return CommandResult.SUCCESS;
				}
			}
		}else {
			if(receiver instanceof VPPlayer) {
				if(close) {
					if(((VPPlayer) receiver).getMenu() == null)
						return CommandResult.CANCELED;
					else {
						((VPPlayer) receiver).setMenu(null);
						return CommandResult.SUCCESS;
					}
				}
				else
				if( menu.open((VPPlayer) receiver, null, args) != null ) {
					return CommandResult.SUCCESS;
				}
			}
		}
		return CommandResult.FAIL;
	}
	@Override
	public List<String> onTab(VPSender sender, String label, List<String> args){
		if ( args == null ) args = new ArrayList<>();
		List<String>result = new ArrayList<>();
		int size = argumentRequired + 1;
		if(otherRequirement.has(sender))
			size ++;
		if ( args.size() < size ) {
			result.addAll(VanillaPlusCore.getPlayerManager().getPlayersList(args.isEmpty() ? "" : args.get( args.size() - 1 ), false));
			result.remove(sender.getName());
		}
		return result;
	}
}
