import java.io.*;
import java.net.*; 
import java.util.*;


public class MastermindClientManager {
	
	//Variables
	private Socket socket;
	private OutputStream out;
	private InputStream in;
	
	private enum Colors {red, blue, yellow, green, white, black};
	private boolean quit = false;
	private int nbGuesses = 0;
	private final int NB_GUESSES = 12;
	
	
	//Constructor
	public MastermindClientManager(Socket s) throws IOException{
		socket = s;
		socket.setSoTimeout(60000);
		
		in = socket.getInputStream();
		out = socket.getOutputStream();
		
	}
	
	
	/***********************************************************************************************
	 *  Play the game : display menu, send messages to the server and print answers from it
	 **********************************************************************************************/
	public void play() throws IOException, SocketException{
		
		//Local variables
		byte[] clientRequest = new byte[2]; //Header of 2 bytes
		byte[] clientMessage = new byte[4]; //Message of 4 bytes
		byte[] serverAnswer = new byte[2];//Header of 2 bytes
		byte[] serverMessage;
		
		Scanner sc = new Scanner(System.in);
		int choice = 0;
		
		
		//A new game starts
		clientRequest[0] = 1;
		clientRequest[1] = 0;
		System.out.println("Welcome to the game of Mastermind! Let's play...");
		System.out.println("Client requests a new game.");
		out.write(clientRequest,0,clientRequest.length);
		out.flush();
		
		while(true) {

			//Read the answer of the server
			int length = in.read(serverAnswer, 0, serverAnswer.length);
			
			if (length <= 0) {
				System.out.println("Connection lost with the server");
				sc.close();
				break;
			}
			
			switch(serverAnswer[1]) {
				
				//Print that a new game has started
				case 1:
					System.out.println("A new game has started.");
					break;
					
				//Print the result of the guess
				case 2:
					serverMessage = new byte[2];
					in.read(serverMessage);
					
					System.out.println(serverMessage[0] + " color(s) well placed, " + serverMessage[1] + " good color(s) at wrong place.");
					
					//Check the game over
					if(nbGuesses == NB_GUESSES || serverMessage[0] == 4) {
						System.out.println("Game over... Do you want to start a new game ? [y/n] \n");
						
						char response = '\0';
						do {
							try {
								response = sc.next().charAt(0);
							}
							catch(InputMismatchException e) {
							  System.out.println("Please enter y or n.");
							}
							finally {
								sc.nextLine();
							}
						}while(response != 'y' && response != 'n');
						
						
						if(response == 'y') {
							clientRequest[0] = 1;
							clientRequest[1] = 0;
							
							out.write(clientRequest,0,clientRequest.length);
							out.flush();
							
							System.out.println("Client requests a new game.");
							continue;
						}
						else {
							quit = true;
						}
					}
					break;
					
					
				//Print the list of previous tries and their results
				case 3:
					//Get the number of tries (first byte of the message)
					int tries = (byte) in.read();
					
					//Create the byte array of correct size
					int len = tries*6;
					serverMessage = new byte[len];
					
					//Get the list of previous combinations
					in.read(serverMessage, 0, serverMessage.length); 
					
					System.out.println("Number of tries: " + tries);
					
					//Print it
					int color;
					int nb;
					int index = 0;
					for(int t=0; t < tries; t++) {
						
						for(int i=0; i < 6; i++) {
							if(i < 4) {
								color = serverMessage[index];
								System.out.print(Colors.values()[color]);
								System.out.printf(" ");
							}	
							else {
								nb = serverMessage[index];
								if(i == 4)
									System.out.printf(" %d color(s) well placed, ", nb);
								else 
									System.out.printf(" %d color(s) at the wrong place.\n", nb);
							}
							index++;	
						}
					}
					break;

					
				//Bad request
				case 4:
					System.out.println("Request error.");
					break;	
					
			}if (quit) break;
			
			

			//Display Menu
			System.out.println("\nPlease select one of these operations by taping the corresponding number: \n");
			System.out.println("1) Propose a combination.");
			System.out.println("2) See the list of previous tries and their score.");
			System.out.println("3) Quit the game. \n");
			System.out.printf("Your choice: ");
			
			//Client sends a message
			do {
				
				boolean valid = false;
				do {
					try {
						choice = sc.nextInt();
						valid = true;
					}
					catch(InputMismatchException e){
					  System.out.println("This is not an integer. Please enter one of the three operations.");
					}
					finally {
						  sc.nextLine();//Empty the line
					}
				}while(!valid);
				

				switch(choice) {
				
					//Propose a combination
					case 1:
						clientRequest[1] = 1;
						
						boolean validColors = false;
						String line;
						
						do {
							System.out.println("\nColors are red, blue, yellow, green, white, black.");
							System.out.println("Enter your guess (4 colors): ");
							line = sc.nextLine();
							validColors = parse(line);
						}while(!validColors);
						
						clientMessage = convert(line);
						
						//Send it
						out.write(clientRequest,0,clientRequest.length);
						out.write(clientMessage, 0, clientMessage.length);
						out.flush();
						
						//Increment number of tries
						nbGuesses++;
						break;
					
						
					//See the list of previous tries and their score.
					case 2:
						clientRequest[1] = 2;
						out.write(clientRequest,0,clientRequest.length);
						out.flush();
						break;
					
						
					//Quit 
					case 3:
						quit = true;
						break;
						
		
					//Wrong number entered
					default:
						System.out.println("Wrong input... Please enter a valid operation number.");
						System.out.println("Your choice : ");
						break;
				}
						
			}while(choice > 4 || choice < 1);
		
			//Quit the game if the client has decided to
			if (quit) break;
			
		}
		
		//Close
		sc.close();
		socket.close();
		out.close();
		in.close();
	}

	
	
	/***********************************************************************************************
	 *  Parse the color combination entered by the client : check if he entered 4 colors and
	 *  if these colors are valid.
	 **********************************************************************************************/
	private boolean parse(String line) {
		
		boolean valid = false;
		char first;
		
		//Split the colors
		String[] tokens = line.split("\\s+");
		
		//Check if client entered 4 colors
		if (tokens.length == 4) {
			
			//Check if it's a valid color
			for(String s : tokens) {
				
				//Get the first char of the color
				first = s.charAt(0);
				
				switch(first) {
					
					case 'r':
						valid = s.equals("red");
						break;
						
					case 'b':
						valid = s.equals("blue");
						if(!valid)
							valid = s.equals("black");
						break;
					
					case 'y':
						valid = s.equals("yellow");
						break;
						
					case 'g':
						valid = s.equals("green");
						break;
					
					case 'w':
						valid = s.equals("white");
						break;
				}
			}
		}

		return valid;
	}
	
	
	
	/***********************************************************************************************
	 *  Convert the valid input entered by the client to a corresponding array of bytes
	 **********************************************************************************************/
	private byte[] convert(String line) {
		
		byte[] guess = new byte[4];
		int nb;
		int i = 0;
		
		//Split the colors
		String[] tokens = line.split("\\s+");
		
		//Convert each color into the corresponding int of the Colors enum
		for(String s : tokens) {
			nb = Colors.valueOf(s).ordinal();
			guess[i] = (byte) nb; //Append the int to the byte array
			i++;
			
		}
		return guess;
	}

}
