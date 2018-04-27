import java.io.*;
import java.net.*;

public class Game {
	
	private static enum colors {
		  RED,
		  BLUE,
		  YELLOW,
		  GREEN,
		  WHITE,
		  BLACK;
	}
	
	private Socket gamesocket;
	private OutputStream clientOstream;
	private InputStream clientIstream;
	private BufferedReader userBR;
	private int NBGUESS; //Number of guesses allowed
	private boolean WON;
	private boolean playGame;

	//Constructor 
	Game(Socket socket) {
		gamesocket = socket;
		play();
	}
	
	/*
	 * Plays a game of Mastermind with a remote server using a TCP connection
	 * 
	 * @param : /
	 * @return: /
	 */
	public void play() {
		
		try{
			clientOstream = gamesocket.getOutputStream();
			clientIstream = gamesocket.getInputStream();
			
			//First message, prompt user to play the game
			System.out.println("Do you want to play a round of Mastermind [Y/N] ?");
			userBR = new BufferedReader(new InputStreamReader(System.in));
			String userinput = userBR.readLine();
			
			//Even though I as for Y/N, I only continue on Y,else I quit
			//I do this to make the code cleaner, to not add another while loop
			if(userinput.equals("Y")){
				
				//Start the game
				startGame();
				
				while(playGame){
					
					analyseMessage();
					
					//Restart a game if player lost/won
					if(NBGUESS == 0 || WON == true){
						System.out.println("Would you like to start another game [Y/N] ?");
						userinput = userBR.readLine();
						if(userinput.equals("Y")){
							startGame();
							continue;
						}else{
							playGame = false;
							break;
						}
					}
					
					
					//User chooses what to do
					System.out.println("What do you want to do ?\n");
					System.out.println("1) Choose a color combination");
					System.out.println("2) See already played combination");
					System.out.println("3) Quit\n");
						
					boolean correctinput = false;
					do{
						System.out.print("Your choice: ");

						userinput = userBR.readLine();
						
						switch(userinput){
							case "1": //The user guesses the combination
								String colorinput = chooseColors();
								sendMessage(colorinput);
								correctinput = true;
								break;
							case "2": //The users wants to know the previous guesses
								sendMessage("12");
								correctinput = true;
								break;
							case "3": //The user wants to quit
								playGame = false;
								System.out.println("Quitting the game");
								correctinput = true;
								break;			
							default: //Invalid input
								System.out.println("Invalid input. Resubmit request");
								correctinput = false;
								break;
						}
					}while(correctinput == false);				
				}
			}else{//The users doesn't want to play the game
				System.out.println("That's fine.");
			}
			


		}catch(SocketTimeoutException e){
			System.err.println("The connection timed out");	
		}catch(SocketException e){
			System.err.println("Connection with the server lost");
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			//Client disconnected or quit the game
			System.out.println("Client shutting down.");
			
			if(userBR != null){
				try{
					gamesocket.close();
					userBR.close();
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
	/*
	 * Starts a new game of Mastermind, initializes all variables 
	 * 
	 * @param : /
	 * @return: /
	 */
	private void startGame() throws Exception{
		playGame = true;
		WON = false;
		NBGUESS = 12;
		sendMessage("10");
	}
	
	/*
 	 * Prompts the user to choose a color combination
	 * 
	 * @param : /
	 * @return: The message that is going to be send to the server,
	 * 			containing the header and the combination
	 */
	private String chooseColors() throws Exception {
		
		System.out.println("Input a combination consisting of 4 of the " +
							"following colors");
	
		for(colors color: colors.values()){
			System.out.println(color.name());
		}
		
		System.out.println("Separate each of them with spaces");
		
		String message = "12"; //Header
		
		//Outer loop to resubmit color request to player when he gives a 
		//wrong input
		boolean correctinput = false;
		do{
			String userinput = userBR.readLine();
			String[] inputcolors = userinput.split(" ");
			
			//Adding each chosen color to the message
			colorCheck: for(String color: inputcolors){
				switch(color){
				case "RED":
					message += colors.RED.ordinal();
					correctinput = true;
					break;
					
				case "BLUE":
					message += colors.BLUE.ordinal();
					correctinput = true;
					break;
					
				case "YELLOW":
					message += colors.YELLOW.ordinal();
					correctinput = true;
					break;
					
				case "GREEN":
					message += colors.GREEN.ordinal();
					correctinput = true;
					break;
					
				case "WHITE":
					message += colors.WHITE.ordinal();					
					correctinput = true;
					break;
					
				case "BLACK":
					message += colors.BLACK.ordinal();
					correctinput = true;
					break;
					
				default:
					System.out.println("Unknown color " + "\"" + color + "\"");
					System.out.println("Please input your complete guess again");
					message = "12"; //Deleting incorrect choice
					correctinput = false;
					break colorCheck;
				}
			}
		}while(correctinput == false);
		
		NBGUESS--;
		
		return message;
	}
	

	/*
	 * Sends a message to the server through a TCP connection, in bytes
	 * 
	 * @param : The string to send to the server 
	 * @return: /
	 */
	private void sendMessage(String userinput) throws Exception {		
		//Sending to server
		clientOstream.write(userinput.getBytes());
		clientOstream.flush();
	}
	
	
	
	/*
	 * Receives and analyzes the message received from the server, and selects
	 * a given action to take with regards to the header of the received message
	 * 
	 * @param : /
	 * @return: /
	 */
	private void analyseMessage() throws Exception{
		
		//Getting response from server
		byte serverBytes[] = new byte[64];
		gamesocket.setSoTimeout(60000);//Timeout of 1 minute
		int length = clientIstream.read(serverBytes);
		if(length <= 0){
			playGame = false;
			return;
		}
		String servermessage = new String(serverBytes);
		servermessage = servermessage.substring(0, length);
		
		//Separating the message in two parts
		String header = servermessage.substring(0, 2);
		String information = servermessage.substring(2,servermessage.length());
		
		String winningresult = "40";
		
		
		//Choosing the correct action to take based on the header
		switch(header){
		case "11": //Start the game
			System.out.println("The game has started !");
			break;
			
		case "12": //Analyse the combination and the result
			if(information.equals(winningresult)){ //The user won the game
				System.out.println("Congratulations, you won !");
				WON = true;
			}
			else if (NBGUESS == 0){//The user lost the game
				System.out.println("No guesses left, GAME OVER !");

			}
			else{//The guess is analysed
				String result = String.format("You placed %c color(s) right and %c " +
					"other(s)  is(are) not at the correct place",
					information.charAt(0),
					information.charAt(1));
			
				System.out.println(result);
			}
			
			break;
			
		case "13":
			//Getting and printing the number of guesses
			char nbguesses = information.charAt(0);
			System.out.println("You have made " + nbguesses + " guess(es)");
			if(Character.getNumericValue(nbguesses) > 0){
				System.out.println("These are :");	
			}
			
			//Listing the guesses and their result
			for(int i = 1; i < information.length(); i += 6){
				//Dividing into substrings
				String guess = information.substring(i, i + 6);
				String combination = guess.substring(0,4);
				
				char placedright = guess.charAt(4);
				char ispresent= guess.charAt(5);
				
				System.out.print("Guess " + ((i-1)/6 +1) + ": ");
				
				for(int j = 0; j < combination.length(); j++) {
					colors color = colors.values()[Character.getNumericValue(combination.charAt(j))];
					System.out.print(color +" ");
				}
				
				System.out.println(" Result: " + placedright + " " + ispresent);
			}
			
			break;
			
		case "14":
			System.out.println("Wrong request");	
			break;
		}
	}
}
