package wallet;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class CreateWeblogin {

	String login_id="";
	String url="";
	String login="";
	String password="";
	
	public CreateWeblogin()
	{
		this.setLogin_id();
	}
		
	public String getLogin_id() {
		return login_id;
	}
	public void setLogin_id() {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		df.setTimeZone(tz);
		this.login_id =  "l-"+df.format(new Date());
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

}
