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

public class AvailableRooms {
    public static Connection con;

    public AvailableRooms() {
        try {
            System.out.println("[+]inside my constructor..");
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/hotelreserve1_0", "postgres", "pwd");
            if (con != null) {
                System.out.println("connection estabished");
            } else {
                System.out.println("Connection failed");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public List<JSONObject> getJsonObject(ResultSet rs) {
        List<JSONObject> resJsonList = Tojasonrs.getResultSet(rs);
        return resJsonList;
    }

    public List<JSONObject> showrooms() {
        Statement stmt;
        ResultSet rs = null;
        try {
            System.out.println("[+]inside showrooms..");
            String query = String.format(
                    "SELECT room.id,capacity,rtype,price FROM room JOIN capacity ON room.cid=capacity.id JOIN rtype ON room.tid = rtype.id WHERE isavailablle = true;");
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            // showing json
            List<JSONObject> jobjlist = getJsonObject(rs);

            for (int i = 0; i < jobjlist.size(); i++) {
                System.out.println(jobjlist.get(i));
            }
            return jobjlist;

        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    public void reserveroom(String sdate, String edate, String rid, String clid) {
        Statement stmt;

        try {
            String query = String.format(
                    "insert into reservation(clid,rid,sdate,edate) values('%s','%s','%s','%s');",
                    clid, rid, sdate, edate);
            stmt = con.createStatement();
            stmt.executeUpdate(query);
            System.out.println("Query -> " + query);
            query = String.format("update room set isavailablle = false where id = '%s';",rid);
            System.out.println("Query -> "+query);
            stmt.executeUpdate(query);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
