package wallet;

public class Error {

	String error="";

	public Error(String input)
	{
		this.error = "Invalid "+input;
	}
	public String getError() {
		return error;
	}
	
}
