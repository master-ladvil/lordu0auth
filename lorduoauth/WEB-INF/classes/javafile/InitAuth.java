import javax.security.auth.login.LoginContext;
import javax.servlet.*;
import java.io.*;
import javax.servlet.http.*;
import java.sql.*;
import org.json.simple.JSONObject;
import java.math.BigInteger;  
import java.security.NoSuchAlgorithmException;  
import java.security.MessageDigest; 

import netscape.javascript.JSObject;

public class InitAuth extends HttpServlet{
    public static Connection con;
    
    public InitAuth() {
        try {
            System.out.println("[+]inside init constructor..");
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/elloauth", "postgres", "pwd");
            if (con != null) {
                System.out.println("connection estabished");
            } else {
                System.out.println("Connection failed");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public String getHash(String key){
        try{
           MessageDigest dst = MessageDigest.getInstance("MD5");
           byte[] msgArr = dst.digest(key.getBytes());
           BigInteger bi = new BigInteger(1,msgArr);
           String hshtxt = bi.toString(16);
           while(hshtxt.length()<32){
               hshtxt = "0"+hshtxt;
           }
           return hshtxt;
        }catch(Exception e){
            System.out.println(e);
        }
        return null;
    }
    public final String sign  = "Ellouth";
    //generate client id
    public String clientIdGen(String clientinfo,String time){
        String value = clientinfo+time+sign;
        return getHash(value);
    }
    //generate client secret
    public String clientSecretGen(String id){
        String time = String.valueOf(System.currentTimeMillis());
        String hash = id+time+sign;
        return getHash(hash);
    }
    
    public int addtodb(String clientid,String clientsecret,String email){
        ResultSet rs = null;
        Statement stmt;
        try{
            String query = String.format("insert into idsec(clientid,clientsecret,email) values('%s','%s','%s');",clientid,clientsecret,email);
            stmt = con.createStatement();
            stmt.executeUpdate(query);
            return 1;
        }catch(Exception e){
            System.out.println(e);
        }
        return 0;
    }

	public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{
		response.addHeader("Access-Control-Allow-Origin","*"); 
		response.setContentType("text/json");
		PrintWriter out = response.getWriter();
        String clientinfo = request.getParameter("clientinfo");
        String time = String.valueOf(System.currentTimeMillis());
        String clientId = clientIdGen(clientinfo, time);
        String clientSecret = clientSecretGen(clientId);
        int flag = addtodb(clientId, clientSecret,clientinfo);
        System.out.println(flag);
        JSONObject jobj = new JSONObject();
        jobj.put("client_id",clientId);
        jobj.put("client_secret",clientSecret);
        System.out.println(jobj);
        response.addHeader("Access-Control-Allow-Origin","*");
        out.println(jobj);
		
	}
}	
