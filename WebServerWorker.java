import java.io.*;
import java.net.*; 


public class WebServerWorker implements Runnable {

	private Socket workerSock;

	public WebServerWorker(Socket clientSocket){
		workerSock = clientSocket;
		System.out.println("Thread started");
	}


	public void run() {
		try{
			InputStreamReader istream = new InputStreamReader(workerSock.getInputStream());

			PrintWriter workerOut = new PrintWriter(workerSock.getOutputStream());


			workerOut.println("HTTP/1.1 303 See Other");
			workerOut.println("Location: /play.html");
			workerOut.println("\r\n");
			workerOut.flush();

			readRequest(istream);
	
		}
		catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				workerSock.close();
			}
			catch(Exception e1){
				e1.printStackTrace();
			}
		}
	}




	private void readRequest(InputStreamReader in) throws IOException {
		
		String buffer = "";
		char c;

		do{
			c = (char) in.read();
			buffer += c + "";
			if(c == '\n'){
				System.out.print(buffer);
				buffer = "";
			}
		}while(c != -1);
	}
}