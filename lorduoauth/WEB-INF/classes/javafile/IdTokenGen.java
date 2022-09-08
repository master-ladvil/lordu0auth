import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
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
import java.nio.charset.StandardCharsets;
import java.net.*;
import org.json.simple.parser.JSONParser;
import java.math.BigInteger;  
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.security.MessageDigest; 
import io.jsonwebtoken.Jwts;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.time.Instant;
import java.util.UUID;
import java.util.Base64;


public class IdTokenGen {
    public  Connection con;
    public IdTokenGen() {
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
    public String b64(String key){
        return Base64.getUrlEncoder().withoutPadding().encodeToString(key.getBytes());
    }
    public String hmacsha256(String data, String clientsecret){
        try{
            byte[] hash = b64(clientsecret).getBytes(StandardCharsets.UTF_8);
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(hash, "HmacSHA256");
            sha256Hmac.init(secretKey);
            byte[] signedBytes = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return b64(String.valueOf(signedBytes));
        }catch(NoSuchAlgorithmException | InvalidKeyException e){
            System.out.println(e);
            return null;
        }
    }
    public List<JSONObject> getscopevalue(String scope){
        Statement stmt;
        ResultSet rs = null;
        try{
            String query = String.format("select * from %s",scope);
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            List<JSONObject> resJsonList = Tojasonrs.getResultSet(rs);
            System.out.println(resJsonList);
            return resJsonList;
        }catch(Exception e){
            System.out.println(e);
        }
        return null;
    }
    public JSONObject generateJWT(String clientid, String scope,String clientsecret){
        JSONObject jobj = new JSONObject();
        String idtok = null;
        AuthCodeGen authob = new AuthCodeGen();
        //timer thread
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run(){
                System.out.println("--------------------deleting id tokens---------------------");
                Statement stmt;
                ResultSet rs = null;
                try{
                    String query = String.format("select idtoken from idtoken;");
                    stmt = con.createStatement();
                    rs = stmt.executeQuery(query);
                    rs.next();
                    String token = rs.getString("idtoken");
                    query = String.format("delete from idtoken;");
                    stmt.executeUpdate(query);
                    Loggen logob = new Loggen();
                    logob.updateactionlog(token, "expired");
                }catch(Exception e){
                    System.out.println(e);
                }
            }
        };

        
        String id = authob.checkdbtok("clientid", "idtoken", clientid, "idtoken");
        if(id != null){
            jobj.put("idtoken",id);
            jobj.put("state","exist");
            return jobj;
        }else{
        try{
        Instant inst = Instant.now();
        final String JWT_HEADER = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        
       JSONObject obj = new JSONObject();
       obj.put("iss","Elloauth");
       obj.put("aud",clientid);
       obj.put("iat",String.valueOf(System.currentTimeMillis()));
       obj.put("exp",String.valueOf(System.currentTimeMillis()+5000));
       obj.put("data",getscopevalue(scope));
       obj.put("scope",scope);
       
        String payload = b64(String.valueOf(obj));
        String header = b64(JWT_HEADER);
        String signature = hmacsha256(header+"."+payload, clientsecret);
        idtok = header+"."+payload+"."+signature;
        String time = String.valueOf(System.currentTimeMillis());
        Statement stmt;
        try{
            String query = String.format("insert into idtoken(idtoken,time,clientid) values('%s','%s','%s');",idtok,time,clientid);
            stmt = con.createStatement();
            stmt.executeUpdate(query);
            jobj.put("idtoken",idtok);
            jobj.put("state","new");
            timer.schedule(task,60000);
            
            return jobj;
        }catch(Exception e){
            System.out.println(e);
        }
        }catch(Exception e){
            System.out.println(e);
        }
        return null;
    }
    }
}
