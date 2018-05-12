import java.io.*;
import java.net.*; 
import java.util.AbstractMap.*;
import java.util.concurrent.ConcurrentHashMap;

public class GameInterface {

	//Variables
	private static ConcurrentHashMap<Integer, Thread> currentGames = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<Integer, PipedOutputStream> currentGamesOutput = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<Integer, PipedInputStream> currentGamesInput = new ConcurrentHashMap<>();


	/********************************************************************************
	 * 
	 *
	 * ARGUMENTS :
	 *	-
	 *	-
	 *
	 * RETURNS : 
	 ********************************************************************************/
	public static String submitGuess(int cookie, String guess){

		PipedOutputStream gameOut = currentGamesOutput.get(cookie);

		byte[] formattedGuess = formatGuessToByte(guess);
		try{
			gameOut.write(formattedGuess);
			gameOut.flush();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}

		return getResponse(cookie);
	}


	/********************************************************************************
	 * 
	 *
	 * ARGUMENTS :
	 *	-
	 *
	 * RETURNS : 
	 ********************************************************************************/
	public static String getPreviousExchanges(int cookie){

		PipedOutputStream gameOut = currentGamesOutput.get(cookie);

		//The formatGuessTobyte function automatically prepends "12"
		byte[] formattedPrevExchanges = formatGuessToByte("");

		try{
			gameOut.write(formattedPrevExchanges);
			gameOut.flush();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}

		return getResponse(cookie);
	}


	/********************************************************************************
	 * 
	 *
	 * ARGUMENTS :
	 *	-
	 *
	 * RETURNS : /
	 ********************************************************************************/
	public static void deleteGame(int cookie){
		currentGames.remove(cookie);
		currentGamesOutput.remove(cookie);
		currentGamesInput.remove(cookie);
	}


	/********************************************************************************
	 * 
	 *
	 * ARGUMENTS :
	 *	-
	 *
	 * RETURNS : /
	 ********************************************************************************/
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
			interfaceOut.write("10".getBytes());
			interfaceOut.flush();

			//Getting response to flush the inputStream
			getResponse(cookie);
			

		}catch(IOException ioe){
			ioe.printStackTrace();
		}	
	}


	/********************************************************************************
	 * 
	 *
	 * ARGUMENTS :
	 *	-
	 *
	 * RETURNS : 
	 ********************************************************************************/
	private static String getResponse(int cookie) {
		PipedInputStream gameIn = currentGamesInput.get(cookie);
		byte[] rawGuess = new byte[128];
		int length = 0;
		try{
			length = gameIn.read(rawGuess);

		}catch(IOException ioe){
			ioe.printStackTrace();
		}	
		
		String formattedGuess = formatGuessToString(length, rawGuess);
		
		return formattedGuess;
	}


	/********************************************************************************
	 * 
	 *
	 * ARGUMENTS :
	 *	-
	 *
	 * RETURNS : 
	 ********************************************************************************/
	private static byte[] formatGuessToByte(String guess){
		StringBuilder builder = new StringBuilder("12");
		builder.append(guess);

		return builder.toString().getBytes();
	}


	/********************************************************************************
	 * 
	 *
	 * ARGUMENTS :
	 *	-
	 *
	 * RETURNS : 
	 ********************************************************************************/
	private static String formatGuessToString(int length, byte[] guess){
		String rawGuess = new String(guess);
		rawGuess = rawGuess.substring(0, length);
		String colors = rawGuess.substring(2); //Remove the header
		return new String(colors);
	}
}