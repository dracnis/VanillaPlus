package fr.soreth.VanillaPlus.Damage;

import org.bukkit.configuration.ConfigurationSection;

import fr.soreth.VanillaPlus.Manager;
import fr.soreth.VanillaPlus.Node;

public class DamageManager extends Manager<String, Damage>{
	private final static DamageManager instance = new DamageManager();
	public DamageManager() {
		super(String.class, Damage.class);
		DamageLoader.load(this);
	}
	/**
	 * @return the instance
	 */
	public static DamageManager getInstance() {
		return instance;
	}
	/**
	 * @return the instance
	 */
	public static Damage create(ConfigurationSection section) {
		return instance.create(section.getString(Node.TYPE.get(), "CLASSIC"), section);
	}
}
