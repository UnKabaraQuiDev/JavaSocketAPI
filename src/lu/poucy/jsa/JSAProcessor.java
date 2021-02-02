package lu.poucy.jsa;

import java.io.IOException;

import lu.poucy.jsa.exceptions.InvalidKeyException;

public interface JSAProcessor extends Runnable {

	void process() throws IOException, InvalidKeyException;
	
}
