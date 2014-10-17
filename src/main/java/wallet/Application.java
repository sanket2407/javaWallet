package wallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.Console;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@RestController
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class Application {

	DBCollection coll; 
	BasicDBObject doc;
    public static void main(String[] args) {
    	MongoDBConnection mongoCon = new MongoDBConnection();
        SpringApplication.run(Application.class, args);
    }
    
    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public String welcomeMessage() {
    	return "Welcome to Digital-Wallet";    	
    }
     
    @RequestMapping(value = "/users", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> createUser(@RequestBody CreateUser user) throws UnknownHostException {
    	
    	coll =  MongoDBConnection.getConnection();
    	coll.getReadPreference();
    	BasicDBObject query = new BasicDBObject("email", user.getEmail());
    	DBCursor cursor = coll.find(query);
    	try {
    	if(cursor.hasNext())
    	{
    		return new ResponseEntity<Object>(new Error(user.getEmail()),HttpStatus.BAD_REQUEST);
    	}else
    	{
    		
    		doc = new BasicDBObject("userid", user.getUserid()).append("email", user.getEmail()).append("password", user.getPassword()).append("created_at", user.getCreated_at()).append("updated_at", user.getUpdated_at());
        	coll.insert(doc);
        	return new ResponseEntity<Object>(user, HttpStatus.CREATED);
    	}
    	}finally {
     	   cursor.close();
     	}  	
    	
    }
    
    @RequestMapping(value = "/users/{userid}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getUser(@PathVariable String userid) throws UnknownHostException
    {
    	coll =  MongoDBConnection.getConnection();
    	BasicDBObject query = new BasicDBObject("userid", userid);
    	DBCursor cursor = coll.find(query);
    	try {
    	if(cursor.hasNext())
    	{
    		GetUser getUser = new GetUser(cursor);
    		return new ResponseEntity<Object>(getUser, HttpStatus.OK);
    	}
    	else{
    		return new ResponseEntity<Object>(new Error(userid), HttpStatus.BAD_REQUEST);
    		}
    	}finally{
    		cursor.close();
    	}
     }
    
    @RequestMapping(value = "/users/{userid}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<Object> updateUser(@PathVariable String userid, @RequestBody UpdateUser update) throws UnknownHostException
    {
    	coll = MongoDBConnection.getConnection();
    	BasicDBObject query = new BasicDBObject("userid", userid);
    	DBCursor cursor = coll.find(query);
    	try {
    	if(cursor.hasNext())
    	{
    		DBObject obj = cursor.next();
    		BasicDBObject newDocument = new BasicDBObject();
    		update.setUpdated_at();
    		if(update.getEmail().equals(""))
    		{
    			newDocument.append("$set", new BasicDBObject().append("password", update.getPassword()).append("updated_at", update.getUpdated_at()));
    			update.setEmail(obj.get("email").toString());
    			
    		}
    		else if(update.getPassword().equals(""))
    		{
    			newDocument.append("$set", new BasicDBObject().append("email", update.getEmail()).append("updated_at", update.getUpdated_at()));
    			update.setPassword(obj.get("password").toString());
    		}
    		else if(!(update.getPassword().equals("")) && !(update.getEmail().equals("")))
    		{
    			newDocument.append("$set", new BasicDBObject().append("email", update.getEmail()).append("password", update.getPassword()).append("updated_at", update.getUpdated_at()));
    		}
    		update.setUserid(obj.get("userid").toString());
    		update.setCreated_at(obj.get("created_at").toString());
    		BasicDBObject searchQuery = new BasicDBObject().append("userid", userid);
    		coll.update(searchQuery, newDocument);
    		
    		return new ResponseEntity<Object>(update, HttpStatus.CREATED);
    	}
    	else{
    		return new ResponseEntity<Object>(new Error(userid), HttpStatus.BAD_REQUEST);
    		}
    	}finally{
    		cursor.close();
    	}
    }
    
    
    @RequestMapping(value = "/users/{userid}/idcards", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> createIDCard(@PathVariable String userid, @RequestBody CreateIDCard idcard) throws UnknownHostException {
    	
    	coll =  MongoDBConnection.getConnection();
    	coll.getReadPreference();
    	BasicDBObject query = new BasicDBObject("userid", userid);
    	DBCursor cursor = coll.find(query);
    	try {
    	if(cursor.hasNext())
    	{
    		
    		HashMap<String, String> idcard_details = new HashMap<String, String>();
    		idcard_details.put("card_id", idcard.getCard_id());
    		idcard_details.put("card_name", idcard.getCard_name());
    		idcard_details.put("card_number", idcard.getCard_number());
    		idcard_details.put("expiration_date", idcard.getExpiration_date());
        	
        	BasicDBObject doc1=new BasicDBObject();
        	doc1.put("IDCards",idcard_details);
        		
        	coll.update(query,new BasicDBObject("$push",doc1));
        	return new ResponseEntity<Object>(idcard,HttpStatus.CREATED);
        	
    	}else
    	{
        	return new ResponseEntity<Object>(new Error(userid), HttpStatus.BAD_REQUEST);
    	}
    	}finally {
     	   cursor.close();
     	}  		
    }
    
    @RequestMapping(value = "/users/{userid}/idcards", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getIDcard(@PathVariable String userid) throws UnknownHostException
    {
    	coll =  MongoDBConnection.getConnection();
    	BasicDBObject query = new BasicDBObject("userid", userid);
    	DBCursor cursor = coll.find(query);
    	try {
    	if(cursor.hasNext())
    	{    		
    		BasicDBObject query1 = (BasicDBObject) cursor.next();
    		BasicDBList dblist = (BasicDBList) query1.get("IDCards");
    		//DBObject temp = (DBObject)dblist.get(i);
    		return new ResponseEntity<Object>(dblist, HttpStatus.OK);
    	}
    	else{
    		return new ResponseEntity<Object>(new Error(userid), HttpStatus.BAD_REQUEST);
    		}
    	}finally{
    		cursor.close();
    	}
     }
    
    @RequestMapping(value = "/users/{userid}/idcards/{card_id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Object> deleteIDcard(@PathVariable String userid, @PathVariable String card_id) throws UnknownHostException
    {
    	coll =  MongoDBConnection.getConnection();
    	BasicDBObject query_main = new BasicDBObject("userid", userid);
    	DBCursor cursor = coll.find(query_main);
    	try {
    	if(cursor.hasNext())
    	{
    		Boolean isCard_id_valid = false;
    		BasicDBObject query1 = (BasicDBObject) cursor.next();
    		//System.out.println(query1.get("IDCards"));
    		BasicDBList dblist = (BasicDBList) query1.get("IDCards"); 
    		for(int i=0; i<dblist.size(); i++)
    		{
    			DBObject o = (DBObject) dblist.get(i);
    			if(o.get("card_id").equals(card_id))
    			{
    				dblist.remove(i);
    				BasicDBObject doc1=new BasicDBObject();
    				doc1.put("IDCards",dblist);
    	        	coll.update(query_main,new BasicDBObject("$set",doc1));
    	        	isCard_id_valid = true;
    	        	break;
    			}
    		}
    		if(isCard_id_valid == true)
    		{
    			return new ResponseEntity<Object>(null, HttpStatus.NO_CONTENT);}
    		else{return new ResponseEntity<Object>(new Error(card_id), HttpStatus.BAD_REQUEST);}	
    	  }
    	else{return new ResponseEntity<Object>(new Error(userid), HttpStatus.BAD_REQUEST);}
    	}
    	finally{cursor.close();}
     }   
    
    /* Creating web login $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ */
    
    @RequestMapping(value = "/users/{userid}/weblogins", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> createWeblogin(@PathVariable String userid, @RequestBody CreateWeblogin weblogin) throws UnknownHostException {
    	
    	coll =  MongoDBConnection.getConnection();
    	coll.getReadPreference();
    	BasicDBObject query = new BasicDBObject("userid", userid);
    	DBCursor cursor = coll.find(query);
    	try {
    	if(cursor.hasNext())
    	{
    		
    		HashMap<String, String> weblogin_details = new HashMap<String, String>();
    		weblogin_details.put("login_id", weblogin.getLogin_id());
    		weblogin_details.put("url", weblogin.getUrl());
    		weblogin_details.put("login", weblogin.getLogin());
    		weblogin_details.put("password", weblogin.getPassword());
        	BasicDBObject doc1=new BasicDBObject();
        	doc1.put("WebLogins",weblogin_details);	
        	coll.update(query,new BasicDBObject("$push",doc1));
        	return new ResponseEntity<Object>(weblogin,HttpStatus.CREATED);
    	}else
    	{
        	return new ResponseEntity<Object>(new Error(userid), HttpStatus.BAD_REQUEST);
    	}
    	}finally {
     	    cursor.close();
     	}  		
    }
    
    @RequestMapping(value = "/users/{userid}/weblogins", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getWeblogin(@PathVariable String userid) throws UnknownHostException
    {
    	coll =  MongoDBConnection.getConnection();
    	BasicDBObject query = new BasicDBObject("userid", userid);
    	DBCursor cursor = coll.find(query);
    	try {
    	if(cursor.hasNext())
    	{    		
    		BasicDBObject query1 = (BasicDBObject) cursor.next();
    		BasicDBList dblist = (BasicDBList) query1.get("WebLogins");
    		return new ResponseEntity<Object>(dblist, HttpStatus.OK);
    	}
    	else{
    		return new ResponseEntity<Object>(new Error(userid), HttpStatus.BAD_REQUEST);
    		}
    	}finally{
    		cursor.close();
    	}
     }
    
    @RequestMapping(value = "/users/{userid}/weblogins/{login_id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Object> deleteWeblogin(@PathVariable String userid, @PathVariable String login_id) throws UnknownHostException
    {
    	coll =  MongoDBConnection.getConnection();
    	BasicDBObject query_main = new BasicDBObject("userid", userid);
    	DBCursor cursor = coll.find(query_main);
    	try {
    	if(cursor.hasNext())
    	{
    		Boolean isLogin_id_valid = false;
    		BasicDBObject query1 = (BasicDBObject) cursor.next();
    		BasicDBList dblist = (BasicDBList) query1.get("WebLogins"); 
    		for(int i=0; i<dblist.size(); i++)
    		{
    			DBObject o = (DBObject) dblist.get(i);
    			if(o.get("login_id").equals(login_id))
    			{
    				dblist.remove(i);
    				BasicDBObject doc1=new BasicDBObject();
    				doc1.put("WebLogins",dblist);
    	        	coll.update(query_main,new BasicDBObject("$set",doc1));
    	        	isLogin_id_valid = true;
    	        	break;
    			}
    		}
    		if(isLogin_id_valid == true)
    		{
    			return new ResponseEntity<Object>(null, HttpStatus.NO_CONTENT);}
    		else{return new ResponseEntity<Object>(new Error(login_id), HttpStatus.BAD_REQUEST);}	
    	  }
    	else{return new ResponseEntity<Object>(new Error(userid), HttpStatus.BAD_REQUEST);}
    	}
    	finally{cursor.close();}
     }  
    
    /* Creating bank account $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ */
    
    @RequestMapping(value = "/users/{userid}/bankaccounts", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> createBankaccount(@PathVariable String userid, @RequestBody CreateBankaccount bankaccount) throws UnknownHostException {
    	
    	coll =  MongoDBConnection.getConnection();
    	coll.getReadPreference();
    	BasicDBObject query = new BasicDBObject("userid", userid);
    	DBCursor cursor = coll.find(query);
    	try {
    	if(cursor.hasNext())
    	{
    		HashMap<String, String> bankaccount_details = new HashMap<String, String>();
    		bankaccount_details.put("ba_id", bankaccount.getBa_id());
    		bankaccount_details.put("account_name", bankaccount.getAccount_name());
    		bankaccount_details.put("routing_number", bankaccount.getRouting_number());
    		bankaccount_details.put("account_number", bankaccount.getAccount_number());
        	BasicDBObject doc1=new BasicDBObject();
        	doc1.put("BankAccounts", bankaccount_details);	
        	coll.update(query,new BasicDBObject("$push",doc1));
        	return new ResponseEntity<Object>(bankaccount,HttpStatus.CREATED);
    	}else
    	{
        	return new ResponseEntity<Object>(new Error(userid), HttpStatus.BAD_REQUEST);
    	}
    	}finally {
     	    cursor.close();
     	}  		
    }
    
    @RequestMapping(value = "/users/{userid}/bankaccounts", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getBankaccount(@PathVariable String userid) throws UnknownHostException
    {
    	coll =  MongoDBConnection.getConnection();
    	BasicDBObject query = new BasicDBObject("userid", userid);
    	DBCursor cursor = coll.find(query);
    	try {
    	if(cursor.hasNext())
    	{    		
    		BasicDBObject query1 = (BasicDBObject) cursor.next();
    		BasicDBList dblist = (BasicDBList) query1.get("BankAccounts");
    		return new ResponseEntity<Object>(dblist, HttpStatus.OK);
    	}
    	else{
    		return new ResponseEntity<Object>(new Error(userid), HttpStatus.BAD_REQUEST);
    		}
    	}finally{
    		cursor.close();
    	}
     }
    
    @RequestMapping(value = "/users/{userid}/bankaccounts/{ba_id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Object> deleteBankaccount(@PathVariable String userid, @PathVariable String ba_id) throws UnknownHostException
    {
    	coll =  MongoDBConnection.getConnection();
    	BasicDBObject query_main = new BasicDBObject("userid", userid);
    	DBCursor cursor = coll.find(query_main);
    	try {
    	if(cursor.hasNext())
    	{
    		Boolean isBank_id_valid = false;
    		BasicDBObject query1 = (BasicDBObject) cursor.next();
    		BasicDBList dblist = (BasicDBList) query1.get("BankAccounts"); 
    		for(int i=0; i<dblist.size(); i++)
    		{
    			DBObject o = (DBObject) dblist.get(i);
    			if(o.get("ba_id").equals(ba_id))
    			{
    				dblist.remove(i);
    				BasicDBObject doc1=new BasicDBObject();
    				doc1.put("BankAccounts",dblist);
    	        	coll.update(query_main,new BasicDBObject("$set",doc1));
    	        	isBank_id_valid = true;
    	        	break;
    			}
    		}
    		if(isBank_id_valid == true)
    		{
    			return new ResponseEntity<Object>(null, HttpStatus.NO_CONTENT);}
    		else{return new ResponseEntity<Object>(new Error(ba_id), HttpStatus.BAD_REQUEST);}	
    	  }
    	else{return new ResponseEntity<Object>(new Error(userid), HttpStatus.BAD_REQUEST);}
    	}
    	finally{cursor.close();}
     }
}