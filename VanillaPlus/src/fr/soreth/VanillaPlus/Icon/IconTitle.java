package fr.soreth.VanillaPlus.Icon;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.Error;
import fr.soreth.VanillaPlus.ErrorLogger;
import fr.soreth.VanillaPlus.Localizer;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.PlaceH;
import fr.soreth.VanillaPlus.Menu.MenuLink;
import fr.soreth.VanillaPlus.Message.MessageManager;
import fr.soreth.VanillaPlus.MComponent.MComponent;
import fr.soreth.VanillaPlus.Player.Title;
import fr.soreth.VanillaPlus.Player.VPPlayer;

public class IconTitle extends Icon{
	private Title title; 
	public IconTitle(ConfigurationSection section, MessageManager manager) {
		this.title = VanillaPlusCore.getTitleManager().get(section.getInt(Node.ID.get(), 0));
		if(title == null)
			ErrorLogger.addError(Node.ID.get() + Error.INVALID.getMessage());
	}
	@Override
	public boolean isStatic(){
		return false;
	}
	@Override
	public boolean closeOnClick() {
		return false;
	}
	@Override
	public boolean hasLore() {
		return true;
	}
	@Override
	public ItemStack getItemstack(VPPlayer player, Localizer loc){
		Icon curent = null;
		if(player.getTitle() != null && player.getTitle().getID()==title.getID()){
			curent = VanillaPlusCore.getTitleManager().getCurrent();
		}else if(player.hasTitle(title.getID())){
			curent = VanillaPlusCore.getTitleManager().getOwned();
		}else{
			curent = VanillaPlusCore.getTitleManager().getUnOwned();
		}
		MComponent base = curent.name;
		if(base != null)
			base.addReplacement(PlaceH.NAME.get(), title.getName().getMessage(player.getLanguage()));
		if(curent.hasLore())
		for(MComponent lore : curent.lore){
			 lore.addReplacement(PlaceH.DESCRIPTION.get(), title.getLore().getMessage(player.getLanguage()));
			 lore.setSplit(true);
		}
		return curent.getItemstack(player, loc);
	}
	@Override
	public boolean canMove(VPPlayer clicker) {
		return false;
	}
	@Override
	public Icon getIcon(VPPlayer player) {
		return this;
	}
	@Override
	public boolean onClick(VPPlayer viewer, ClickType type, MenuLink ml){
		if(type == ClickType.RIGHT){
			if(VanillaPlusCore.getTitleManager().hasAdminUse(viewer)){
				viewer.setTitle(title);
				VanillaPlusCore.getMenuManager().refresh(viewer);
				return true;
			}
		}else if(type == ClickType.MIDDLE){
			if(VanillaPlusCore.getTitleManager().hasSwitch(viewer)){
				if(viewer.hasTitle(title.getID()))
					viewer.removeTitle(title);
				else
					viewer.addTitle(title.getID());
				VanillaPlusCore.getMenuManager().refresh(viewer);
				return true;
			}
		}
		if(VanillaPlusCore.getTitleManager().hasUse(viewer)){
			if(viewer.getTitle() != null && viewer.getTitle().getID()==title.getID()){
				viewer.setTitle(null);
			}else if(viewer.hasTitle(title.getID())){
				viewer.setTitle(title);
			}
			else
				return true;
			VanillaPlusCore.getMenuManager().refresh(viewer);
		}
		return true;
	}
}
