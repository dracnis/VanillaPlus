package fr.soreth.VanillaPlus.Utils.Minecraft;

import org.bukkit.inventory.ItemStack;
public class NBTItem extends NBTCompound{
	private ItemStack bukkitItem;
	public NBTItem(ItemStack item){
		super(null, null);
		this.bukkitItem = item;
	}
	public ItemStack getItem(){
		return this.bukkitItem;
	}
	protected void setItem(ItemStack item){
		this.bukkitItem = item;
	}
}