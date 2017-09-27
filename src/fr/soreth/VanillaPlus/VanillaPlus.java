package fr.soreth.VanillaPlus;

import java.util.Arrays;

import org.bukkit.plugin.java.JavaPlugin;

import fr.soreth.VanillaPlus.Utils.Minecraft.ConfigUtils;

public class VanillaPlus extends JavaPlugin {
	private static VanillaPlus instance;
	@Override
	public void onEnable() {
		instance = this;
		ConfigUtils.copyFiles(Arrays.asList("Achievement.yml", "Channel.yml", "Command.yml", "config.yml", "Currency.yml", "Extra.yml",
			"Icon.yml", "PlaceHolder.yml", "Requirement.yml", "Reward.yml", "Stat.yml", "Storage.yml", "Title.yml"), instance);
		new VanillaPlusCore();
	}
	@SuppressWarnings("deprecation")
	@Override
	public void onDisable() {
		VanillaPlusCore.disable();
	}
	public static VanillaPlus getInstance() {
		return instance;
	}
}
