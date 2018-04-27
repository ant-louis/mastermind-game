import java.io.*;
import java.net.*; 
import java.util.Map;
import java.util.HashMap;

public class GameInterface implements Runnable {

	private Map<String, Thread> currentGames = new HashMap<>();
	private Map<String, OutputStream> currentGamesOutput = new HashMap<>();
	private Map<String, InputStream> currentGamesInput = new HashMap<>();

	private static int i;

	public void submitGuess(String cookie,String guess){
		OutputStream gameOut = currentGamesOutput.get(cookie);
		InputStream gameIn = currentGamesInput.get(cookie);

		//byte[] guessFormat = formatGuess(guess);
		//gameIn.write(guessFormat)
		//gameOut.read();
		//

	}
	public void createGame(String cookie){
		Thread t = new Thread(new Worker(out,in));
		System.out.println("Game "+ i +"started");
		currentGames.put(cookie,t);
		currentGamesOutput.put(cookie,new OutputStream());
		currentGamesInput.put(cookie,new InputStream());
		i++; 
		t.start();	
	}

}