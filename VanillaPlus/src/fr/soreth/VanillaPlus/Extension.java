package fr.soreth.VanillaPlus;

public abstract class Extension {
    /**
     * Test the extension.
     *
     * @return True if can use the extension.
     */
	public boolean validate(){
		return true;
	}
    /**
     * Say to the extension it can be used.
     *
     * @return True if can use the extension.
     */
	public void init(){}
    /**
     * Unload the extension.
     *
     * @return the extension's alias.
     */
	public void unload(){}
}
