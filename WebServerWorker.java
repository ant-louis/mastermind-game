import java.io.*;
import java.net.*; 


public class WebServerWorker implements Runnable {

	private Socket workerSock;
	private static int i;

	public WebServerWorker(Socket clientSocket){
		workerSock = clientSocket;
		System.out.println("Thread "+ i +"started");
		i++; 
	}


	public void run() {
		try{

			InputStreamReader istream = new InputStreamReader(workerSock.getInputStream());
			PrintWriter workerOut = new PrintWriter(workerSock.getOutputStream());

			/*
			System.out.println("Redirecting...");
			workerOut.print("HTTP/1.1 303 See Other\r\n");
			workerOut.print("Location: /play.html\r\n");
		    workerOut.print("Connection: close\r\n");

			workerOut.print("\r\n");
			workerOut.flush();
			*/

	    	workerOut.print("HTTP/1.1 200 OK\r\n");
		    workerOut.print("Content-Type: text/html\r\n");
		    workerOut.print("Connection: close\r\n");
		    workerOut.print("Set-Cookie: SESSID=rk64vvmhlbt6rsdfv4f02kc5g0; path=/\r\n");
		    workerOut.print("\r\n");
		    workerOut.print("<p> Hello world" + i + " </p>\r\n");
		    workerOut.flush();

			readRequest(istream);
			istream.close();
			workerOut.close();

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
		/*
		String buffer = "";
		String prevbuf = "";
		char c;

		do{
			c = (char) in.read();
			buffer += c + "";
			if(c == '\r'){System.out.print("_r");}
			if(c == '\n'){
				System.out.print("_n");
				System.out.print(buffer);
				if(buffer =="\r\n" && prevbuf =="\r\n"){
					break;
				}
				prevbuf = buffer;
				buffer = "";
			}
		}while(c != -1);

		System.out.print(buffer);
		*/

		String httpresponse = "";
		char[] buffer = new char[2];
		char[] prevbuf = new char[2];
		char c;

		while(in.read(buffer,0,2) != -1) {
			httpresponse += String.valueOf(buffer);
			
			System.out.print(buffer);

			if(buffer[0] == '\r' && buffer[1] ==  '\n'){
				if(prevbuf[0] == '\r' && prevbuf[1] ==  '\n'){
					break;
				}

				prevbuf[0] = buffer[0];
				prevbuf[1] = buffer[1];
			}
		}

		System.out.println(httpresponse);
	}
}