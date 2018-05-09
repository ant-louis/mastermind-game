import java.io.*;
import java.net.*; 
import java.util.Map;
import java.util.HashMap;

public class GameInterface {

	private static Map<Integer, Thread> currentGames = new HashMap<>();
	private static Map<Integer, PipedOutputStream> currentGamesOutput = new HashMap<>();
	private static Map<Integer, PipedInputStream> currentGamesInput = new HashMap<>();

	public static void submitGuess(int cookie,String guess){
		System.out.println("Submitting guess");

		PipedOutputStream gameOut = currentGamesOutput.get(cookie);

		byte[] formattedGuess = formatGuessToByte(guess);
		try{
			gameOut.write(formattedGuess);
			gameOut.flush();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	public static void createGame(int cookie){
		try{
			//Pipes from the interface to the worker and vice versa
			//Interface is going to send through interfaceOut and
			//Worker is going to send through workerOut
			//Both are going to listen to their respective inputstreams
			PipedOutputStream interfaceOut =  new PipedOutputStream();
			PipedInputStream interfaceIn = new PipedInputStream();
			PipedOutputStream workerOut =  new PipedOutputStream();
			PipedInputStream workerIn = new PipedInputStream();

			// InterfaceOut <------> WorkerIn
			// WorkerOut <-------> InterfaceIn
			interfaceOut.connect(workerIn);
			workerOut.connect(interfaceIn);

			System.out.println("Creating new game for cookie " + cookie);
			Thread t = new Thread(new Worker(workerOut,workerIn));
			currentGames.put(cookie,t);
			currentGamesOutput.put(cookie,interfaceOut);
			currentGamesInput.put(cookie,interfaceIn);
			t.start();

			//Start the game
			interfaceOut.write(formatGuessToByte("10"));
			interfaceOut.flush();
			

		}catch(IOException ioe){
			ioe.printStackTrace();
		}	
	}

	/*
	public static boolean gameExists(String cookie){
		if(currentGames.get(cookie) != null){
			return true;
		}else{
			return false;
		}
	}
	*/
	public static String getResponse(int cookie) {
		System.out.println("Getting response");
		PipedInputStream gameIn = currentGamesInput.get(cookie);
		byte[] rawGuess = new byte[128];
		
		try{
			gameIn.read(rawGuess);

		}catch(IOException ioe){
			ioe.printStackTrace();
		}	
		
		String formattedGuess = formatGuessToString(rawGuess);
		System.out.println(formattedGuess);
		return formattedGuess;
	}


	private static byte[] formatGuessToByte(String guess){
		System.out.println("Formatting String");

		return guess.getBytes();
	}

	private static String formatGuessToString(byte[] guess){
		System.out.println("Re-Formatting to String");

		return new String(guess);
	}

}