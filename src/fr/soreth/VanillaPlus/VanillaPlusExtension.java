package fr.soreth.VanillaPlus;

import org.bukkit.plugin.Plugin;

import fr.soreth.VanillaPlus.MComponent.MComponentManager;
import fr.soreth.VanillaPlus.Message.MessageManager;

public interface VanillaPlusExtension {
	public MComponentManager getMessageCManager();
	public MessageManager getMessageManager();
	public Plugin getInstance();
}
