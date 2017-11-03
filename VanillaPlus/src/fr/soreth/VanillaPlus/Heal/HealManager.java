package fr.soreth.VanillaPlus.Heal;

import org.bukkit.configuration.ConfigurationSection;

import fr.soreth.VanillaPlus.Manager;
import fr.soreth.VanillaPlus.Node;

public class HealManager extends Manager<String, Heal>{
	private final static HealManager instance = new HealManager();
	public HealManager() {
		super(String.class, Heal.class);
		HealLoader.load(this);
	}
	/**
	 * @return the instance
	 */
	public static HealManager getInstance() {
		return instance;
	}
	/**
	 * @return the instance
	 */
	public static Heal create(ConfigurationSection section) {
		return instance.create(section == null ? null : section.getString(Node.TYPE.get()), section);
	}
}
