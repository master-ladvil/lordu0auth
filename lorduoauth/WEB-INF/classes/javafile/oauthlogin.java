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



public class oauthlogin extends HttpServlet{
    public static Connection con;
    public oauthlogin() {
        try {
            System.out.println("[+]inside login constructor..");
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
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,ServletException{
        
        String email = request.getParameter("email");
        String lname = request.getParameter("lname");
        Statement stmt;
        ResultSet rs = null;
        try{
            String query = String.format("select lname from userinfo where email = '%s';",email);
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            String dblname =  rs.getString("lname");
            response.addHeader("Access-Control-Allow-Origin","*");
			response.setContentType("text/plain");
			PrintWriter out = response.getWriter();
            if(dblname.equals(lname)){
                out.println(1);
            }else{
                out.println(0);
            }
        }catch(Exception e){
            System.out.println(e);
        }
    }
}
