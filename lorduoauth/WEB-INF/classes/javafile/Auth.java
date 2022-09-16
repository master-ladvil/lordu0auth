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

public class Auth extends HttpServlet{
        public static Connection con = null;
        public static String scope = null;
        public static String client = null;
        public static String redirect = null;
        public Auth() {
            try {
                System.out.println("[+]inside Auth constructor..");
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
    public boolean checkcreds(String scope,String clientid){
        Statement stmt;
        ResultSet rs = null;
        ResultSet rs2 = null;
        try{
            String clientquery = String.format("select count(*) from keys where clientid = '%s';",clientid);
            stmt = con.createStatement();
            rs = stmt.executeQuery(clientquery);
            rs.next();
            if(rs.getString("count").equals("1")){
                String query = String.format("select count(*) from scopes where scopename = '%s';",scope);
                rs2 = stmt.executeQuery(query);
                rs2.next();
                if(rs2.getString("count").equals("1")){
                    return true;
                }else{
                    return false;
                }
            }else{
                return false;
            }
        }catch(Exception e){
            System.out.println(e);
        }
        return false;
    }   
    public void doGet(HttpServletRequest request,HttpServletResponse response)throws IOException,ServletException{
        scope = request.getParameter("scope");
        client = request.getParameter("clientid");
        redirect = request.getParameter("redirect");
        boolean auth = checkcreds(scope, client);
        if(auth){
            System.out.println("client id in consent -> "+client);
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            response.sendRedirect("http://localhost:4201/auth");
        }else{
            response.sendRedirect("http://localhost:4201/error");
        }
        
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException,ServletException{
            response.addHeader("Access-Control-Allow-Origin","*");
			response.setContentType("text/plain");
			PrintWriter out = response.getWriter();
            out.println(scope); 
    }
}
