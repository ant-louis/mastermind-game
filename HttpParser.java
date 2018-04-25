import java.io.*;
import java.net.*; 
import java.lang.StringBuilder.*;
import java.util.regex.*;
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

		String buffer = "";
		char c;

		//Reading the first line of the header
		do{
			c = (char) parserIn.read();
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
	public void getRemainingHeader() throws IOException {

       	StringBuilder headerLine = new StringBuilder();
       	StringBuilder entireHeader = new StringBuilder();
       	char c;

       	do{
       		c = (char) parserIn.read();
       		headerLine.append(c);

		    //End of line reached, storing as a map
       		if (c == '\n'){
       			String line = headerLine.toString();
       			String[] tokens = line.split(":");
       			if(tokens != null && tokens.length == 2){
       				headerMap.put(tokens[0],tokens[1]);
       			}
       			entireHeader.append(line);
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
}	




