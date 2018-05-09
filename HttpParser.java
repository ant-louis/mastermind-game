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

		String buffer = "";
		char c;

		//Reading the first line of the header
		
		do{
			c = (char) parserIn.read();
			int count = 0;//temp
			count++;
			buffer += c +"";
			if(c == '\n'){
				break;
			}

			if(count == 100) {break;} //temp

		}while(c != -1);
		
		//Splitting into tokens, the first one being the request type
		//The second being the requested path
		String[] tokens = buffer.split(" ");
		//String[] tokens = colors.split(" "); //temp


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
	public void getRemainingHeader() throws IOException {

       	StringBuilder headerLine = new StringBuilder();
       	StringBuilder entireHeader = new StringBuilder();
       	char c;

       	do{
       		c = (char) parserIn.read();
       		//System.out.print(c);
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

	//Extracts the cookie from the header
	public int getCookie() {
		return Integer.parseInt(headerMap.get("Cookie"));
	}

	//Extract a color guess from the request
	public String getGuess(){
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




