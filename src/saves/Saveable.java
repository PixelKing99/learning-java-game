package saves;


public abstract class Saveable  {
	
	/**	make sure to make public or else stuff will break
	 * @param bytes
	 */
	Saveable(byte[] bytes) {}
	
	/**
	 * has to be here so instances can initialize without using the constructor with byte[]
	 */
	protected Saveable() {
	}
	
	abstract byte[] getBinary();
}
