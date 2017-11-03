package fr.soreth.VanillaPlus.Message;

public class MessageLoader {
	private static boolean init;

	public static void load(MessageManager manager) {
		if(init)return;
		init = true;
	}
	
}