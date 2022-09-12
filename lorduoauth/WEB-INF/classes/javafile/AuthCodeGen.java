import javax.security.auth.login.LoginContext;
import javax.servlet.*;
import java.io.*;
import javax.servlet.http.*;
import java.sql.*;
import org.json.simple.JSONObject;
import java.util.*;
import java.lang.*;
import java.text.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.*;
import org.json.simple.parser.JSONParser;
import java.math.BigInteger;  
import java.security.NoSuchAlgorithmException;  
import java.security.MessageDigest; 
import io.jsonwebtoken.Jwts;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;
public class AuthCodeGen {
    public Connection con;
    public AuthCodeGen() {
        try {
            System.out.println("[+]inside authcodegen constructor..");
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
    public boolean checkdb(String colname,String tablename,String value){
        Statement stmt;
        ResultSet rs = null;
        try{
            String query = String.format("select count(*) from %s where %s = '%s';",tablename,colname,value);
            System.out.println("SQLQuery-> "+query);
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            int count = Integer.valueOf(rs.getString(1));
            if(count == 1 ){
                return true;
            }
        }catch(Exception e){
            System.out.println(e);
        }
        return false;
    }
    public String checkdbtok(String colname,String tablename,String value,String tokenType){
        Statement stmt;
        ResultSet rs = null;
        try{
            String query = String.format("select count(*) from %s where %s = '%s';",tablename,colname,value);
            System.out.println("SQLQuery-> "+query);
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            int count = Integer.valueOf(rs.getString(1));
            if(count > 0 ){
                query = String.format("select %s from %s;",tokenType,tablename);
                ResultSet trs = stmt.executeQuery(query);
                trs.next();
                String token = trs.getString(tokenType);
                return token;
            }
        }catch(Exception e){
            System.out.println(e);
        }
        return null;
    }
    public String genHash(String key){
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
    public void delAuthCode(String clientid,String tokentype,String token){
        Statement stmt;
        Loggen logob = new Loggen();
        try{
            String query = String.format("delete from %s where clientid = '%s';",tokentype,clientid);
            System.out.println(query);
            stmt = con.createStatement();
            stmt.executeUpdate(query);
            logob.updateactionlog(token, "removed");
        }catch(Exception e){
            System.out.println(e);
        }
        System.out.println("code removed + " + token);
    }
    public String genAuthCode(String key,String clientid,String userkey){
        
        String tok = checkdbtok("clientid", "authcode", clientid, "authcode");
        
        if(tok != null){
            delAuthCode(clientid, "authcode", tok);
        }
        String authcode  = genHash(key);
        String time = String.valueOf(System.currentTimeMillis());
        Statement stmt;
        try{
            String query = String.format("insert into authcode(authcode,time,clientid,userkey) values('%s','%s','%s','%s');",authcode,time,clientid,userkey);
            stmt = con.createStatement();
            stmt.executeUpdate(query);
            return authcode;
        }catch(Exception e){
            System.out.println(e);
        }
        return null;
    }
}
