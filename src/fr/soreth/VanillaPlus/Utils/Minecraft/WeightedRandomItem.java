package fr.soreth.VanillaPlus.Utils.Minecraft;

import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.soreth.VanillaPlus.VanillaPlusCore;
import fr.soreth.VanillaPlus.Utils.WeightedRandom;
public class WeightedRandomItem extends WeightedRandom.RestrictedChoice {

    private ItemStack loot;
    private int min;
    private int max;

    public WeightedRandomItem(Material item, short data, int min, int max, int weight, int restrictionId) {
        super(weight, restrictionId);
        this.loot = new ItemStack(item, 1, data);
        this.min = min;
        this.max = max;
    }

    public WeightedRandomItem(ItemStack itemstack, int min, int max, int weight, int restrictionId) {
        super(weight, restrictionId);
        this.loot = itemstack;
        this.min = min;
        if(max < min)
        	this.max = min;
        else
        	this.max = max;
    }
    public WeightedRandomItem(ConfigurationSection current) {
		this(MinecraftUtils.loadItem(current),
				current.getInt("MIN", 1),
				current.getInt("MAX", 1), 
				current.getInt("WEIGHT", 1),
				current.getInt("RESTRICTION_ID"));
	}

	public ItemStack get(){
		ItemStack item = loot.clone();
		item.setAmount( max == min ? max : VanillaPlusCore.getRandom().nextInt(max - min + 1 ) + min);
    	return item;
    }
    public static void fill(Random random, List<WeightedRandomItem> list, Inventory inventory, int amount) {
    	for (int j = 0; j < amount; ++j) {
    		WeightedRandomItem loot = (WeightedRandomItem) WeightedRandom.getChoice(random, list);
    		int k = loot.min + random.nextInt(loot.max - loot.min + 1);
    		for (int l = 0; l < k; l += loot.loot.getMaxStackSize()) {
    			ItemStack item = loot.loot.clone();
    			if(item.getType() == Material.ENCHANTED_BOOK && ! item.hasItemMeta())
    				item = MinecraftUtils.getDongeonBook();
    			item.setAmount(loot.loot.getMaxStackSize() > k ? k : loot.loot.getMaxStackSize());
    			inventory.setItem(random.nextInt(inventory.getSize()), item);
    		}
        }

    }
}