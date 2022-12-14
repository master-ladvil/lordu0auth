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

public class Consent extends HttpServlet{
        public static String scope = null;
        public static String client = null;
        public static String redirect = null;  
    public void doGet(HttpServletRequest request,HttpServletResponse response)throws IOException,ServletException{
        scope = request.getParameter("scope");
        client = request.getParameter("clientid");
        redirect = request.getParameter("redirect");
        System.out.println("client id in consent -> "+client);
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        response.sendRedirect("http://localhost:4201/login");
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException,ServletException{
            response.addHeader("Access-Control-Allow-Origin","*");
			response.setContentType("text/plain");
			PrintWriter out = response.getWriter();
            out.println(scope); 
    }
}
