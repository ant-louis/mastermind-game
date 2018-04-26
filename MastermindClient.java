import java.net.*;

class MastermindClient {
		
	public static void main (String args[]){
			
		try{
			Socket clientsock = new Socket("localhost",2416);
			new Game(clientsock);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
