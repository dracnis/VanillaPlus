package fr.soreth.VanillaPlus.Utils.Minecraft;

import java.util.Set;

import org.bukkit.inventory.ItemStack;
public class NBTCompound{
	private String compundname;
	private NBTCompound parent;
	protected NBTCompound(NBTCompound owner, String name){
		this.compundname = name;
		this.parent = owner;
	}
	public String getName(){
		return this.compundname;
	}
	public ItemStack getItem(){
		return this.parent.getItem();
	}
	public NBTCompound getParent(){
		return this.parent;
	}
	protected void setItem(ItemStack item){
		this.parent.setItem(item);
	}
	public void setString(String key, String value){
		setItem(MinecraftUtils.setString(getItem(), this, key, value));
	}
	public String getString(String key){
		return MinecraftUtils.getString(getItem(), this, key);
	}
	public void setInteger(String key, int value){
		setItem(MinecraftUtils.setInt(getItem(), this, key, Integer.valueOf(value)));
	}
	public Integer getInteger(String key){
		return MinecraftUtils.getInt(getItem(), this, key);
	}
	public void setDouble(String key, double value){
		setItem(MinecraftUtils.setDouble(getItem(), this, key, Double.valueOf(value)));
	}
	public double getDouble(String key){
		return MinecraftUtils.getDouble(getItem(), this, key);
	}
	public void setBoolean(String key, boolean value){
		setItem(MinecraftUtils.setBoolean(getItem(), this, key, Boolean.valueOf(value)));
	}
	public boolean getBoolean(String key){
		return MinecraftUtils.getBoolean(getItem(), this, key);
	}
	public void setObject(String key, Object value){
		setItem(MinecraftUtils.setObject(getItem(), this, key, value));
	}
	public <T> T getObject(String key, Class<T> type){
		return (T)MinecraftUtils.getObject(getItem(), this, key, type);
	}
	public boolean hasKey(String key){
		return MinecraftUtils.hasKey(getItem(), this, key);
	}
	public void removeKey(String key){
		setItem(MinecraftUtils.remove(getItem(), this, key));
	}
	public Set<String> getKeys(){
		return MinecraftUtils.getKeys(getItem(), this);
	}
	public NBTCompound addCompound(String name){
		setItem(MinecraftUtils.addNBTTagCompound(getItem(), this, name));
		return getCompound(name);
	}
	public NBTCompound getCompound(String name){
		NBTCompound next = new NBTCompound(this, name);
		if (MinecraftUtils.valideCompound(getItem(), next).booleanValue()) {
			return next;
		}
		return null;
	}
}
