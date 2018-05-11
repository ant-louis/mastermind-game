import java.io.*;
import java.net.*; 
import java.lang.StringBuilder.*;
import java.util.Map;
import java.util.HashMap;

public class HttpParser {

	InputStreamReader parserIn;
	private String requestType;
	private String path;
	private Map<String, String> headerMap = new HashMap<>();

	public HttpParser(InputStreamReader istream){
		parserIn = istream;

		try{

			getFirstHeaderLine();
			getRemainingHeader();

		}catch(IOException ioe){
			ioe.printStackTrace();
		}
	}	



	//A method that extract the first line of the header
	//Must always be called first before the rest of the header methods
	private void getFirstHeaderLine() throws IOException {
		// String colors = "GET /play.html?param1=1&param2=2&param3=3&param4=4";
		// System.out.println(colors);
		System.out.println("Getting first HEADER line");

		String buffer = "";
		char c;

		//Reading the first line of the header
		
		do{
			c = (char) parserIn.read();
			//System.out.print(c);
			buffer += c +"";
			if(c == '\n'){
				break;
			}

		}while(c != -1);
		
		//Splitting into tokens, the first one being the request type
		//The second being the requested path
		String[] tokens = buffer.split(" ");

		this.requestType = tokens[0];
		this.path = tokens[1];
	}

	//Returns the request type of the HTTP request
	public String getRequestType(){
		return this.requestType;
	}

	//Returns the path of the HTTP request
	public String getPath(){
		return this.path;
	}


	public Map<String,String> getMap(){
		return this.headerMap;
	}


	//Gets the remaining header and stores each line in a map, 
	//where the key is the first keywords (before ':') and the 
	//value is the rest
	private void getRemainingHeader() throws IOException {
		System.out.println("Getting remaining HEADER");

       	StringBuilder headerLine = new StringBuilder();
       	StringBuilder entireHeader = new StringBuilder();
       	char c;

       	do{

       		c = (char) parserIn.read();
       		System.out.print(c);
       		headerLine.append(c);

		    //End of line reached, storing as a map
       		if (c == '\n'){
       			String line = headerLine.toString();
       			String[] tokens = line.split(":");
       			
       			//Putting into map
       			if(tokens != null && tokens.length == 2){
       				headerMap.put(tokens[0],tokens[1]);
       			}
       			entireHeader.append(line);
       			//Delete the line once it's mapped
       			headerLine.delete(0,headerLine.length()-1);
       		}

       		int currentLength = entireHeader.length();
  			
		    //Check end of header
       		if (currentLength > 4 &&
   				entireHeader.charAt(currentLength - 1) == '\n'&&
				entireHeader.charAt(currentLength - 2) == '\r' &&
				entireHeader.charAt(currentLength - 3) == '\n'){
				break; 
			}
			
   		}while(c != -1);
	}

	public String getGuess_POST() throws IOException{

		System.out.println("Getting body of POST request");
		char[] colors = new char[4];
		char c;
		int j = 0;

		//Reading the body and extracting the guess		
		do{
			c = (char) parserIn.read();
			System.out.println(c);
			//The value follows the '='
			if(c == '='){
				System.out.println("Enter");
				c = (char) parserIn.read();
				colors[j++] = c;
			}
		}while(c != -1);
		return new String(colors);
	}




	//Extracts the cookie from the header
	public int getCookie() {
		if(headerMap.get("Cookie") == null){
			return -1;
		}
		else {
			return Integer.parseInt(headerMap.get("Cookie"));
		}
	}

	//Extract a color guess from the AJAX/GET request
	public String getGuess_GET(){
		String path = this.path;
		char[] colors = new char[4];
	 	
		for (int i = 0, j = 0; i < path.length(); i++){
			if(path.charAt(i) == '='){
				colors[j++] = path.charAt(i+1);
			}
		}

		return new String(colors);
	}
}	




