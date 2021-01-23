package lu.poucy.jsa.exceptions;

@SuppressWarnings("serial")
public class InvalidKeyException extends Exception {
	
	public InvalidKeyException(int[] key) {
		super("Key: "+get(key)+", is invalid");
	}

	private static String get(int[] c) {String s = "";for(int e : c)s += e;return s;}

}
