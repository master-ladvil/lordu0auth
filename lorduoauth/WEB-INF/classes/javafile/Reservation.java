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
    
    public boolean checkscope(String accesstoken)throws Exception{
        OauthResources orob = new OauthResources();
        System.out.println("checking scope........." + accesstoken);
        boolean result = false;
        ResultSet rs = orob.checkat(accesstoken);
        if(rs != null){
            String scope = rs.getString("scope");
            System.out.println("\n\nscope assigned for at -> "+scope+"\n\n");
            String[] chunks = scope.split("\\.");
            for (int i =0 ;i<chunks.length;i++){
                if(chunks[i].equals("reservation")){
                    result = true;
                   
                    break;
                }
                System.out.println(chunks[i] + " -> " + result);
            }
        } 
        System.out.println("result -> "+result);       
        return result;
    }
    

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            ReservationDb rdb = new ReservationDb();
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
                System.out.println("payload -> "+ payload);
                JSONParser parser = new JSONParser();
                JSONObject jobj = (JSONObject) parser.parse(payload);
                String clientid = jobj.get("aud").toString();
                //String scope = jobj.get("scope").toString();
                List<JSONObject> reservation = rdb.getReservations();
                out.println(reservation); 
            } else {
                out.println("no access");
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }
}
