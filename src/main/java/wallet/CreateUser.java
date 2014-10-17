package wallet;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class CreateUser {

	public CreateUser()
	{
		this.setUserid();
		this.setUpdated_at();
		this.setCreated_at();
	}
	String userid = "";
	String email="";
	String password="";
	String created_at ="";
	String updated_at ="";
	
	public String getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at() {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T':HH:mm:ss'Z'");
		df.setTimeZone(tz);
		this.updated_at = df.format(new Date());
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at() {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T':HH:mm:ss'Z'");
		df.setTimeZone(tz);
		this.created_at = df.format(new Date());		
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid() {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		df.setTimeZone(tz);
		this.userid =  "U-"+df.format(new Date());
	}
	
}
