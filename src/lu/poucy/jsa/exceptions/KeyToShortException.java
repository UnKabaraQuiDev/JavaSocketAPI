package lu.poucy.jsa.exceptions;

@SuppressWarnings("serial")
public class KeyToShortException extends Exception {

	public KeyToShortException(char[] key) {
		super("Key: "+get(key)+", is to short to crypt the packet");
	}

	private static String get(char[] c) {String s = "";for(char e : c)s += e;return s;}
	
}
