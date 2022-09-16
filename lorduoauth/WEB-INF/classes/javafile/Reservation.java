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
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.time.Instant;
import java.util.UUID;
import java.util.Base64;

public class Reservation extends HttpServlet {
    public static Connection oauthcon;
    public static Connection reseourcecon;

    public Reservation() {
        try {
            System.out.println("[+]inside my constructor..");
            Class.forName("org.postgresql.Driver");
            reseourcecon = DriverManager.getConnection("jdbc:postgresql://localhost:5432/hotelreserve1_0", "postgres",
                    "pwd");
            if (reseourcecon != null) {
                System.out.println("connection estabished");
            } else {
                System.out.println("Connection failed");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        try {
            System.out.println("[+]inside authcodegen constructor..");
            Class.forName("org.postgresql.Driver");
            oauthcon = DriverManager.getConnection("jdbc:postgresql://localhost:5432/elloauth", "postgres", "pwd");
            if (oauthcon != null) {
                System.out.println("connection estabished");
            } else {
                System.out.println("Connection failed");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public boolean checkscope(String accesstoken)throws Exception{
        System.out.println("checking scope........." + accesstoken);
        boolean result = false;
        String[] chunks = accesstoken.split("\\.");
        System.out.println(chunks[1]);
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]));
        System.out.println("payload -> "+payload);
        JSONParser parser = new JSONParser();
        JSONObject job = (JSONObject) parser.parse(payload);
        String scopes = job.get("scope").toString();
        System.out.println("scopes->"+scopes);
        String[] scopechunks = scopes.split("\\.");
        for(int i=0;i<scopechunks.length;i++){
            if(scopechunks[i].equals("reservation")){
                
                result = true;
            }else{
                result = false;
            }
            System.out.println(scopechunks[i] +" "+ result);
        }
        return result;
    }
    public List<JSONObject> getReservations(){
        
		try{
			System.out.println("inside Reservation...");
			Statement stmt = null;
			ResultSet rs = null;
			String query = String.format("select reservation.id,fullname,sdate,edate,rid from reservation join client on reservation.clid = client.id;");
			stmt = reseourcecon.createStatement();
			rs  = stmt.executeQuery(query);
			List<JSONObject> resJsonList = Tojasonrs.getResultSet(rs);
			for(int i =0;i<resJsonList.size();i++){
				System.out.println(resJsonList.get(i));
			}
			return resJsonList;
		}catch(Exception e){System.out.println(e);}
        return null;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String accesstoken = request.getHeader("accesstoken");
            AccessToken atob = new AccessToken();
            boolean isAccess = atob.verify(accesstoken);
            boolean isScope = checkscope(accesstoken);
            response.setContentType("text/plain");
            PrintWriter out = response.getWriter();
            if (isAccess && isScope) {

                String[] chunks = accesstoken.split("\\.");
                Base64.Decoder decoder = Base64.getUrlDecoder();
                String payload = new String(decoder.decode(chunks[1]));
                JSONParser parser = new JSONParser();
                JSONObject jobj = (JSONObject) parser.parse(payload);
                String clientid = jobj.get("aud").toString();
                String scope = jobj.get("scope").toString();
                List<JSONObject> reservation = getReservations();
                out.println(reservation); 
            } else {
                out.println("no access");
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }
}
