package fr.soreth.VanillaPlus.Menu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import fr.soreth.VanillaPlus.Player.VPPlayer;
public class MenuLink  implements InventoryHolder {
	private final Menu iconMenu;
	private Inventory inv;
	private final List<String> args;
	private final VPPlayer view;
	public MenuLink(Menu iconMenu, VPPlayer view, List<String> args) {
		this.iconMenu = iconMenu;
		this.view = view;
		this.args = args == null ? new ArrayList<String>() : args;
	}
	public void setInventory(Inventory inventory){
		this.inv = inventory;
	}
	public List<String> getArgs(){
		return args;
	}
	public Menu getIconMenu(){
		return iconMenu;
	}
	public VPPlayer getView(){
		return view;
	}
	@Override
	public Inventory getInventory(){
		return inv;
	}
}
