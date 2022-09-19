import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.net.http.HttpResponse;
import java.sql.*;
import javax.security.auth.login.LoginContext;
import org.json.simple.JSONObject;
import java.util.*;
import org.json.simple.parser.JSONParser;
import java.text.*;

public class RoomsDb {
    public static Connection reseourcecon;
    public static Connection oauthcon;
    public RoomsDb(){
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
    public void showRooms(HttpServletResponse response) {
        Statement stmt;
        ResultSet rs = null;
        try {
            String query = String.format(
                    "SELECT room.id,capacity,rtype,price,isavailablle FROM room JOIN capacity ON room.cid=capacity.id JOIN rtype ON room.tid = rtype.id order by room.id asc;");
            stmt = reseourcecon.createStatement();
            rs = stmt.executeQuery(query);
            response.setContentType("text/json");
            response.addHeader("Access-Control-Allow-Origin", "*");
            PrintWriter out = response.getWriter();

            List<JSONObject> resJsonList = Tojasonrs.getResultSet(rs);
            for (int i = 0; i < resJsonList.size(); i++) {
                System.out.println(resJsonList.get(i));
            }
            out.println(resJsonList);

        } catch (Exception e) {
            System.out.println(e);
        }
    } 
}
