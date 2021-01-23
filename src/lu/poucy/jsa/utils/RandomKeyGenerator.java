package lu.poucy.jsa.utils;

import java.util.Random;

public final class RandomKeyGenerator {

	public static char[] generateRandom() {
		String s = System.currentTimeMillis()+"";
		String _s = s;
		s = "";
		
		for (int i = _s.length()-1; i >= 0; i--)
			s += _s.toCharArray()[i];
		
		Random r = new Random();
		
		for(int i = 0; i < r.nextInt(15); i++)
			try {s += ""+ r.nextInt(i+r.nextInt(15));}catch(IllegalArgumentException retry) {i--;}
		
		char[] ret = new char[s.length()];
		for (int i = 0; i < s.length(); i++)
			ret[i] = s.charAt(i);
		
		return ret;
	}
	
}
