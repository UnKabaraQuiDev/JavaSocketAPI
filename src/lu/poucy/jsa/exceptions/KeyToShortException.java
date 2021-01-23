package lu.poucy.jsa.exceptions;

@SuppressWarnings("serial")
public class KeyToShortException extends Exception {

	public KeyToShortException(int[] key) {
		super("Key: "+get(key)+", is to short to crypt the packet");
	}

	private static String get(int[] c) {String s = "";for(int e : c)s += e;return s;}
	
}
