import java.io.*;
import java.net.*; 


public class MastermindClient {
	
	//Variables
	private static Socket socket;
	
	
	//Main
	public static void main(String[] args) {
		
		try {
			socket = new Socket("localhost", 2140);
			MastermindClientManager client = new MastermindClientManager(socket);
			client.play();
		}
		catch(SocketTimeoutException e){
			System.err.println("Error the server doesn't response.Cause : "+e.getMessage());
		}
		catch(UnknownHostException e) {
			System.err.println("Can not connect the address " + socket.getLocalAddress());
		}
		catch(SocketException e){
			System.err.println("Error the server doesn't response.Cause : "+e.getMessage());
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
	}
}