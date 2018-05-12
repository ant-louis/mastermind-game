import java.io.*;
import java.net.*; 
import java.lang.StringBuilder.*;
import java.util.Map;
import java.util.HashMap;

public class HttpParser {

	InputStreamReader parserIn;
	private String requestType;
	private String path;
	private String httpVersion;
	private Map<String,String> headerMap = new HashMap<>();

	
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
			if(c == '\n'){
				break;
			}
			buffer += c +"";
			
		}while(c != -1);
		
		//Splitting into tokens, the first one being the request type
		//The second being the requested path, the third the http version
		String[] tokens = buffer.split(" ");

		this.requestType = tokens[0];
		this.path = tokens[1];
		tokens[2] =  tokens[2].substring(0, tokens[2].length() - 1);//Delete \n
		this.httpVersion = tokens[2];
	}

	//Returns the request type of the HTTP request
	public String getRequestType(){
		return this.requestType;
	}

	//Returns the path of the HTTP request
	public String getPath(){
		return this.path;
	}

	public String getHttpVersion(){
		return this.httpVersion;
	}

	//Check if there is Content-Length
	public boolean checkIfContentLength(){
		return (headerMap.get("Content-Length") != null);
	}


	//Gets the remaining header and stores each line in a map, 
	//where the key is the first keyword (before ':') and the 
	//value is the rest of the line
	private void getRemainingHeader() throws IOException {
       	StringBuilder headerLine = new StringBuilder();
       	StringBuilder entireHeader = new StringBuilder();
       	char c;

       	do{
       		c = (char) parserIn.read();
       		//Appending to the line buffering the current line we're parsing
   			headerLine.append(c);

   			System.out.print(c);

		    //End of line reached, storing as a map
       		if (c == '\n' && headerLine != null){
       			String line = headerLine.toString();
       			String[] tokens = line.split(":");
       			
       			//Putting into map
       			if(tokens != null && tokens.length == 2){
       				//Small manipulation because the stored key is "\nCookie" otherwise
       				tokens[0] = tokens[0].substring(1,tokens[0].length());
       				headerMap.put(tokens[0],tokens[1]);
       			}

       			//Append to the string containing the whole header
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
		char[] colors = new char[4];
		char c;
		int j = 0;

		//Reading the body and extracting the guess		
		do{
			c = (char) parserIn.read();
			//The value follows the '='
			if(c == '='){
				c = (char) parserIn.read();
				colors[j] = c;
				j++;

				//All the colors extracted
				if(j == 4) {
					break;
				}
			}

		}while(c != -1);

		return new String(colors);
	}




	//Extracts the cookie from the header
	public int getCookie() {
		String cookieField;
		if((cookieField = headerMap.get("Cookie")) == null){
			return -1;
		}
		else {
			//Map value of "Cookie" is stored as "SESSID=1"
			int index = cookieField.indexOf("=");
			char value = cookieField.charAt(index+1);
			return Character.getNumericValue(value);
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



