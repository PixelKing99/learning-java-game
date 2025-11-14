package saves;


public abstract class Saveable  {
	
	Saveable(byte[] bytes) {}
	
	/**
	 * has to be here so instances can initialize without using the constructor with byte[]
	 */
	protected Saveable() {
	}
	
	abstract byte[] getBinary();
}
