package fr.soreth.VanillaPlus.MComponent;

public class MCLoader {
	public static void load(MComponentManager manager) {
		manager.register(MCNote.class, 			"NOTE");
		manager.register(MCSubTitle.class, 		"SUBTITLE");
		manager.register(MCTab.class, 			"TAB");
		manager.register(MCTitle.class, 		"TITLE");
	}
	
}