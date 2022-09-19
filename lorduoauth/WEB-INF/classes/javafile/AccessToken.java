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
import java.security.PublicKey;
import java.security.KeyPair;
import java.security.MessageDigest;
import io.jsonwebtoken.Jwts;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

public class AccessToken extends HttpServlet {
    public static Connection con = null;

    public AccessToken() {
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

    public void addtodb(PublicKey publickey, String accesstoken, String clientid, String userkey,String scope) throws Exception {
        // convert publickey to string
        String pubkey = Base64.getUrlEncoder().encodeToString(publickey.getEncoded());
        Statement stmt;
        String query = String.format(
                "insert into accesstoken(accesstoken,clientid,uid,publickey,scope) values('%s','%s','%s','%s','%s');",
                accesstoken, clientid, userkey, pubkey,scope);
        System.out.println("Query -> " + query);
        stmt = con.createStatement();
        stmt.executeUpdate(query);
    }

    public String generateAccessToken(String clientid, String scope, String authcode) {
        System.out.println("[+]generating access token..");
        IdTokenGen idob = new IdTokenGen();
        InitAuth keyob = new InitAuth();
        String userkey = idob.getUserKey(authcode);
        JSONObject jobj = new JSONObject();
        String accesstoken = null;
        KeyPair pair;
        try {
            // getting keys
            pair = keyob.getkeypair();
            // header
            final String JWT_HEADER = "{\"alg\":\"RS256\",\"type\":\"JWT\"}";
            String header = Base64.getUrlEncoder().encodeToString(JWT_HEADER.getBytes());
            // payload
            jobj.put("iss", "elloauth");
            jobj.put("aud", clientid);
            jobj.put("iat", System.currentTimeMillis() / 10000);
            //jobj.put("scope", scope);
            jobj.put("exp", System.currentTimeMillis() + 60000 / 10000);
            jobj.put("uid", userkey);
            // encoding payload
            String payload = Base64.getUrlEncoder().encodeToString((String.valueOf(jobj)).getBytes());
            String signature = Base64.getUrlEncoder()
                    .encodeToString(keyob.sign(header + "." + payload, pair.getPrivate()));
            accesstoken = header + "." + payload + "." + signature;
            addtodb(pair.getPublic(), accesstoken, clientid, userkey,scope);
            return accesstoken;
        } catch (Exception e) {
            System.out.println(e);
        }
        return accesstoken;
    }

    public PublicKey checkauthtodb(String accesstoken) throws Exception {
        Statement stmt;
        ResultSet rs = null;
        String query = String.format("select publickey from accesstoken where accesstoken = '%s'", accesstoken);
        System.out.println("Query -> " + query + "\n\n");
        stmt = con.createStatement();
        rs = stmt.executeQuery(query);
        rs.next();
        String publickey = rs.getString("publickey");
        System.out.println("pubkey -> " + publickey + "\n\n");
        InitAuth keyob = new InitAuth();
        PublicKey pubkey = keyob.getpubob(publickey);
        return pubkey;
    }

    public boolean verify(String accesstoken) throws Exception {
        PublicKey publickey = checkauthtodb(accesstoken);
        InitAuth keyob = new InitAuth();
        String[] chunks = accesstoken.split("\\.");
        boolean verify = keyob.verify(chunks[0] + "." + chunks[1], chunks[2], publickey);
        return verify;

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String clientid = request.getParameter("clientid");
        String scope = request.getParameter("scope");
        String authcode = request.getParameter("authcode");
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        try {

            System.out.println("[+]generating access token..");
            IdTokenGen idob = new IdTokenGen();
            InitAuth keyob = new InitAuth();
            String userkey = idob.getUserKey(authcode);
            JSONObject jobj = new JSONObject();
            String accesstoken = null;
            KeyPair pair;
            try {
                // getting keys
                pair = keyob.getkeypair();
                // header
                final String JWT_HEADER = "{\"alg\":\"RS256\",\"type\":\"JWT\"}";
                String header = Base64.getUrlEncoder().encodeToString(JWT_HEADER.getBytes());
                // payload
                jobj.put("iss", "elloauth");
                jobj.put("aud", clientid);
                jobj.put("iat", System.currentTimeMillis() / 10000);
                //jobj.put("scope", scope);
                jobj.put("exp", System.currentTimeMillis() + 60000 / 10000);
                jobj.put("uid", userkey);
                // encoding payload
                String payload = Base64.getUrlEncoder().encodeToString((String.valueOf(jobj)).getBytes());
                String signature = Base64.getUrlEncoder()
                        .encodeToString(keyob.sign(header + "." + payload, pair.getPrivate()));
                accesstoken = header + "." + payload + "." + signature;
                System.out.println("scope -> "+ scope);
                addtodb(pair.getPublic(), accesstoken, clientid, userkey,scope);
                // verification
                boolean verify = keyob.verify(header + "." + payload, signature, pair.getPublic());
                // building reponse
                JSONObject res = new JSONObject();
                res.put("at", accesstoken);
                res.put("Verified?", verify);
                out.println(res);
            } catch (Exception e) {
                System.out.println(e);
                out.println(e);
            }
        } catch (Exception e) {
            System.out.println(e);
            out.println(e);
        }

    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        try {
            String accesstoken = request.getParameter("accesstoken");
            PublicKey publickey = checkauthtodb(accesstoken);
            InitAuth keyob = new InitAuth();
            String[] chunks = accesstoken.split("\\.");
            boolean verify = keyob.verify(chunks[0] + "." + chunks[1], chunks[2], publickey);

            out.println(verify);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
