import java.io.*;
import java.net.*;
import java.util.*;


public class Worker implements Runnable {
	//Length of the secret combination
	
	//All possible colors in the game
	private  enum colors {
		  RED,
		  BLUE,
		  YELLOW,
		  GREEN,
		  WHITE,
		  BLACK;
	}
	/*
	private Socket workersock;
	*/
	private OutputStream serverostream;
	private InputStream serveristream;
	


	// Number of occurrence of a color in the secret combination
	//Example : "1545" --> [0 1 0 0 1 2]
	private int[] coloroccurrence;
	private int[] secretcombination;
	private ArrayList<String> previousexchanges;

	private int NBGUESS;
	private int nbexchanges;

	
	//Constructor
	Worker(OutputStream out,InputStream in){

		this.serverostream = out;
		this.serveristream = in;
		/*workersock = sock;*/
	}
	
	
	public void run(){
		
		try {
			/*serverostream = workersock.getOutputStream();
			serveristream = workersock.getInputStream();
			*/
			boolean playGame = true;
			
			while(playGame){
				
				//Read message from inputStream
				byte[] incomingmessage = new byte[64];
				//workersock.setSoTimeout(60000);//Timeout of 1 minute
				int length = serveristream.read(incomingmessage);
				
				if(length <= 0){
					playGame = false;
					break;
				}
				
				//Convert it to string and print it to console
				String clientMessage = new String(incomingmessage);
				System.out.println("Client " + workersock.getPort() + ": " + clientMessage);
				
				
				//Starting new game ("10")
				if(clientMessage.startsWith("10")){
					
					sendMessage("11");//Tell the client the game started
				
					startGame();
				
				
				}
				
				/*

				//List previous exchanges ("12")
				else if(clientMessage.startsWith("12") && length == 2){
					
					StringBuilder builder = new StringBuilder("13");
					
					//Add the number of previous exchanges
					builder.append(nbexchanges);
					
					//Iterate through previous guesses and append them to string
					Iterator<String> iter = previousexchanges.iterator();
					while (iter.hasNext()){
						builder.append(iter.next());
					}
					
					//Send the previous exchanges to the client
					sendMessage(builder.toString());
				}

				*/

				//Guess a combination (ex: "121345")
				else if(clientMessage.startsWith("12") && length == (2 + 4)){
					String guessedcombination = clientMessage.substring(2, length);
					guessCombination(guessedcombination);
				}

				/*
				//Unknown message
				else{
					
					sendMessage("14");	
				}
				*/
			}
		
		/*	
		}catch(SocketTimeoutException e){
			System.err.println("The connection timed out");	
		}catch(SocketException e){
			System.err.println("Connection with Client " + workersock.getPort() + " lost");
		*/
		}catch(Exception e){
			e.printStackTrace();
		}
		/*
		finally{
			try{
				workersock.close();
			}catch(SocketException e){
				System.err.println("The connection timed out");		
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		*/
	}
	
	/*
	 * Starts a new game of Mastermind, initializes all variables 
	 * Creates the random secret combination
	 * 
	 * ARGUMENTS : /
	 * RETURNS: /
	 */
	private void startGame() throws Exception{
		NBGUESS = 12;
		nbexchanges = 0;
		coloroccurrence = new int[6];
		secretcombination = new int[4];
		previousexchanges = new ArrayList<String>(NBGUESS);
		
		//Choose a given amount of colors for the secret combination
		Random rand = new Random();
		
		System.out.println("Secret combination: ");
		for(int i =0; i < 4 ;i++) {
			int randomcolor = rand.nextInt(colors.values().length);
			secretcombination[i] = randomcolor;
			coloroccurrence[randomcolor]++;
			//Output the secret combination to the server console
			System.out.print(colors.values()[randomcolor]+ " ");
		}
		
		System.out.println("");
	}
	
	/*
	 * Analyse the combination send by the user and compare it to the secretcombination
	 * 
	 * ARGUMENTS : The combination guessed by the user
	 * RETURNS: /
	 */
	private void guessCombination(String guessedcombination) throws Exception{
		int length = guessedcombination.length();
		int isPresent = 0;
		int isRightplace = 0;
		
		NBGUESS--;
			

		//Copy the colorOccurence array
		int[] temp_coloroccurence = Arrays.copyOf(coloroccurrence,coloroccurrence.length);
		
		//Check each color at a time
		for(int i = 0; i < length; i++){
			
			//Extract the color and convert it to int
			int guessedcolor = Character.getNumericValue(guessedcombination.charAt(i));
			
			//If color is at the right place
			if(guessedcolor == secretcombination[i]){
				isRightplace++;
				
			//If the color is present somewhere in the secret combination
			}else if(temp_coloroccurence[guessedcolor] > 0){
				
				isPresent++;
				temp_coloroccurence[guessedcolor]--;
			}
		}
		
		//Create a string for the client-server exchange and add it to a list
		String exchange = String.format("%s%d%d",guessedcombination,isRightplace,isPresent);
		previousexchanges.add(exchange);
		nbexchanges++;
		
		//Send the result of the guess to the client
		String guessresult = String.format("12%d%d",isRightplace,isPresent);
		sendMessage(guessresult);
				
	}
	
	
	
	/*
	 * Sends a message to the client through a TCP connection, in bytes
	 * 
	 * ARGUMENTS : The string to send to the client 
	 * RETURNS: /
	 */
	private void sendMessage(String message) throws Exception{
		
			byte[] sendingmessage = new byte[64];
			sendingmessage = message.getBytes();
			serverostream.write(sendingmessage);
			serverostream.flush();
		}
	}
