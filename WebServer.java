import java.io.*;
import java.net.*; 


public class WebServer {
	
	public static void main ( String argv[] ) {
		
		try {

			URL url = new URL("http://localhost/Mastermind/test.php");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");

			/******Usefull methods
			String method = connection.getRequestMethod();
			int status = connection.getResponseCode();

			System.out.println("Status :" + status);
			System.out.println("Method : " + method);
			*/

			String headerField;
			int i = 0;
			while((headerField = connection.getHeaderField(i)) != null){
				System.out.println(headerField);
				i++;
			}

			/****** Getting the whole HTTP page (not useful)


			BufferedReader testBR = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuffer buf = new StringBuffer();
			String line;

			while((line = testBR.readLine()) != null){
				buf.append(line);
			}
			
			testBR.close();
			connection.disconnect();

			System.out.println(buf.toString());
			*/

		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
