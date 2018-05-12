import java.io.*;
import java.net.*; 
import java.util.AbstractMap.*;
import java.util.concurrent.ConcurrentHashMap;

//Interface between the Worker Class and the WebServerWorker class
public class GameInterface {

	//Maps to hold information about all the games
	private static ConcurrentHashMap<Integer, Thread> currentGames = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<Integer, PipedOutputStream> currentGamesOutput = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<Integer, PipedInputStream> currentGamesInput = new ConcurrentHashMap<>();


	/********************************************************************************
	 * Submits a guess to the game associated with a given cookie
	 *
	 * ARGUMENTS :
	 *	- cookie associated to a game, used as a key in the maps
	 *	- guess consisting of a String of 4 numbers representing colors
	 *
	 * RETURNS : The response of the Worker class, which has analyzed the guess
	 ********************************************************************************/
	public static String submitGuess(int cookie, String guess){

		//Get the outputstream from the map
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
	 * Get all the exchanges that have been made since the start of the game
	 *
	 * ARGUMENTS :
	 *	- cookie associated to a game, used as a key in the maps
	 *
	 * RETURNS : An exchange (String) consisting of the number of exchanges followed by 
	 * pairs of guesses and their results
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
	 * Deletes all information about a given game with the cookie associated
	 *
	 * ARGUMENTS :
	 *	- cookie associated to a game, used as a key in the maps
	 *
	 * RETURNS : /
	 ********************************************************************************/
	public static void deleteGame(int cookie){
		currentGames.remove(cookie);
		currentGamesOutput.remove(cookie);
		currentGamesInput.remove(cookie);
	}


	/********************************************************************************
	 * Creates Piped In- and Outputstreams to communicated with the Worker class
	 * handling the Mastermind Game algorithm
	 * Creates a thread of a Worker instance and starts the game
	 * ARGUMENTS :
	 *	- cookie to be associated to a game, used as a key in the maps
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

			//Creating a new Worker instance in a thread and putting
			//it along the associated input- and output-streams in a map
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
	 * Reads the response (in bytes) from the pipedoutputstream of the Worker class
	 *
	 * ARGUMENTS :
	 *	- cookie associated to a game, used as a key in the maps
	 *
	 * RETURNS : The formatted guess as a String, where the 2-bit header has been removed
	 ********************************************************************************/
	private static String getResponse(int cookie) {
		//Geting the input stream associated with the cookie/game
		PipedInputStream gameIn = currentGamesInput.get(cookie);
		byte[] rawGuess = new byte[128];
		int length = 0;

		try{
			length = gameIn.read(rawGuess);

		}catch(IOException ioe){
			ioe.printStackTrace();
		}	
		//Formatting the guess to String and removing the header
		String formattedGuess = formatGuessToString(length, rawGuess);
		
		return formattedGuess;
	}


	/********************************************************************************
	 * Prepends a 2-bit header to the guess and formats it to a byte array 
	 *
	 * ARGUMENTS :
	 *	- the guess String to be formatted, consists of 4 colors
	 *
	 * RETURNS : formated guess as a byte array
	 ********************************************************************************/
	private static byte[] formatGuessToByte(String guess){
		StringBuilder builder = new StringBuilder("12");
		builder.append(guess);

		return builder.toString().getBytes();
	}


	/********************************************************************************
	 * Removes the 2-bit header from the guess byte array, which is a response from
	 * the worker Class 
	 *
	 * ARGUMENTS :
	 *	- the length of the guess byte array
	 * 	- the guess byte array
	 *
	 * RETURNS : guess without header as a string
	 ********************************************************************************/
	private static String formatGuessToString(int length, byte[] guess){

		String rawGuess = new String(guess);

		//Getting only the useful part and removing the header
		rawGuess = rawGuess.substring(0, length);
		String colors = rawGuess.substring(2);
		return new String(colors);
	}
}