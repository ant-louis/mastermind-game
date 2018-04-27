
import java.net.*;

public class MastermindServer {
	
	//private static ServerSocket serversock;
	//public static ArrayList<Socket> PlayersArray = new ArrayList<Socket>();
	
	public static void main (String[] args){

		Thread t = new Thread(new Worker());
		t.start();	


		/*
		try{
			serversock = new ServerSocket(2416);
			System.out.println("Server is running");
			
			while (true){
				Socket sock = serversock.accept();
				System.out.println("Connection accepted");

				Thread t1 = new Thread(new Worker(sock));
				t1.start();			
			}


		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				serversock.close();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		*/


	}
}


