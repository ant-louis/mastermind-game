import java.io.*;
import java.util.*;
import java.net.Socket;


public class MastermindServerWorker implements Runnable {
	
	//Variables
	private Socket socket;
	private OutputStream out;
	private InputStream in;
		
	private final int NB_COLORS = 4;
	private int nbGuesses = 0;
	
	
	private enum Colors {red, blue, yellow, green, white, black};
	private int secretColors[] = new int[NB_COLORS];//Secret combination of the chosen colors
	private int colorsRepetitions[] = new int[Colors.values().length];//array of the occurrences of the colors (same order)
	private List<Integer> previousGuesses; //List of the previous guesses and their results
	
	
	//Constructor
	public MastermindServerWorker(Socket ss) {socket = ss;}
	
	
	/***********************************************************************************************
	 *  Override the run method : read client's messages, analyze them and answer
	 **********************************************************************************************/
	@Override
	public void run() {
		
		//Local variables
		byte[] clientRequest = new byte[2]; //Header of 2 bytes
		byte[] clientMessage = new byte[4]; //Message of 4 bytes
		byte[] serverAnswer = new byte[2];//Header of 2 bytes
		byte[] serverMessage = new byte[67]; //Maximum size of the guesses list (11*6 + 1)
		
		int len = 0;
		boolean quit = false;
		
		try {
			in = socket.getInputStream();
			out = socket.getOutputStream();
			
			while(!quit) {
				
				//Set a timeout of 1 minute
				socket.setSoTimeout(60000);
				
				//Read the message header of the client
				len = in.read(clientRequest, 0, clientRequest.length);
				
				//Check if client quit the game
				if (len <= 0) {
					System.out.println("Connection lost with the client");
					break;
				}

				//Version byte of the header
				serverAnswer[0] = 1;
				
				//Analyze the message
				switch(clientRequest[1]) {
					
					//Start a new game
					case 0:
						serverAnswer[1] = 1;
						System.out.println("Server accepts request, a new game starts.");
						new_game();
						out.write(serverAnswer,0,serverAnswer.length);
						out.flush();
						break;
						
					//Make a guess
					case 1:
						serverAnswer[1] = 2;
						len = in.read(clientMessage);
						if(len <= 0) {
							System.out.println("No message from the client while expected. Connection lost.");
							quit = true;
							break;
						}
						serverMessage = analyze_guess(clientMessage);//Analyze the combination guess
						out.write(serverAnswer,0,serverAnswer.length);
						out.write(serverMessage,0,serverMessage.length);
						out.flush();
						break;
						
					//Ask for previous guesses and their results
					case 2:
						serverAnswer[1] = 3;
						
						//Create the byte array of correct size
						len = (nbGuesses*6)+1;
						serverMessage = new byte[len];
						
						//First byte of the message is the number of guesses
						serverMessage[0] = (byte) nbGuesses;
						
						//Create the byte array of the previous guesses
						Iterator<Integer> it = previousGuesses.iterator();
						int j = 1;
						while(it.hasNext()) {
							Integer i = it.next();
							serverMessage[j] = i.byteValue();
							j++;
						}
						out.write(serverAnswer,0,serverAnswer.length);
						out.write(serverMessage,0,serverMessage.length);
						out.flush();
						break;
						
					//In all other cases, bad request
					default:
						serverAnswer[1] = 4;
						out.write(serverAnswer,0,serverAnswer.length);
						out.flush();
						break;
				}		
			}
		}
		
		catch(InterruptedIOException e){
        	System.out.println("The Client is not answering : The connection is being closed...");
        }
		catch(IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				socket.close();
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}	
	}
	
	
	
	/***********************************************************************************************
	 *  Reset a game : new secret color combination, reset the list of previous guesses
	 ************************************************************************************************/
	private void new_game() {
		
		int pick;
		Random random = new Random();
		
		//Create a new list of previous guesses
		previousGuesses = new ArrayList<Integer>();
		
		//Reset number of guesses
		nbGuesses = 0;
		
		System.out.printf("Secret color combination: ");
		
		for(int i=0; i < NB_COLORS; i++) {
			pick = random.nextInt(Colors.values().length); //Get a random color from the Colors enum
			secretColors[i] = pick; // Add it to the secret combination
			colorsRepetitions[pick]++; //Update the array of the occurrences
			
			System.out.printf("%s ", Colors.values()[pick]);
		}
		System.out.printf("\n");

	}
	
	
	/***********************************************************************************************
	 *  Analyze the guess combination of the client
	 **********************************************************************************************/
		private byte[] analyze_guess(byte[] guess) {
			
			int guessedColor;
			int wellPlacedColors = 0;
			int badlyPlacedGoodColors = 0;
			byte[] guessResult = new byte[2];
			
			//Temporary array containing the number of occurrences of each color for the current guess
			int[] colorsRep = Arrays.copyOf(colorsRepetitions, colorsRepetitions.length);
			
			//Check the well placed colors
			for(int i=0; i < NB_COLORS; i++) {
				guessedColor = guess[i];
		
				if(guessedColor == secretColors[i]) {
					wellPlacedColors++;
					colorsRep[guessedColor]--;
				}
			}
			
			//Check the badly placed good colors
			for(int i=0; i < NB_COLORS; i++) {
				guessedColor = guess[i];
				
				if(colorsRep[guessedColor] > 0) {
					badlyPlacedGoodColors++;
					colorsRep[guessedColor]--;
				}
				
				//Update the previous guesses list with the new combination
				previousGuesses.add(guessedColor);
			}
			
			//Make the guess result
			guessResult[0] = (byte) wellPlacedColors;
			guessResult[1] = (byte) badlyPlacedGoodColors;
		
			
			//Add it to the previous guesses list
			previousGuesses.add(wellPlacedColors);
			previousGuesses.add(badlyPlacedGoodColors);
			nbGuesses++;
			
			return guessResult;
		}
	
}
