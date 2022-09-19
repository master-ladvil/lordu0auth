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

public class ReservationDb {
    public static Connection oauthcon;
    public static Connection reseourcecon;
    public ReservationDb(){
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
}
