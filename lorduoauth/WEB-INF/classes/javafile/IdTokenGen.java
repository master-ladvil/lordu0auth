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
import java.nio.file.attribute.UserPrincipalLookupService;
import java.net.*;
import org.json.simple.parser.JSONParser;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import io.jsonwebtoken.Jwts;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.time.Instant;
import java.util.UUID;
import java.util.Base64;

public class IdTokenGen {
    public Connection con;

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


    public String b64(String key) {
        return Base64.getUrlEncoder().encodeToString(key.getBytes(StandardCharsets.UTF_8));
    }
    public byte[] b64decode(String code){
        return Base64.getUrlDecoder().decode(code);
    }

    public String hmacsha256(String data, String clientsecret) {
        try {
            byte[] hash = b64(clientsecret).getBytes(StandardCharsets.UTF_8);
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(hash, "HmacSHA256");
            sha256Hmac.init(secretKey);
            byte[] signedBytes = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return b64(String.valueOf(signedBytes));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            System.out.println(e);
            return null;
        }
    }
    
    
    public String sign(String plaintext, PrivateKey privateKey) throws Exception {
        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(privateKey);
        privateSignature.update(plaintext.getBytes(StandardCharsets.UTF_8));
        byte[] signature = privateSignature.sign();
        return Base64.getEncoder().encodeToString(signature);
    }

    public boolean verify(String plaintext, String signature, PublicKey publicKey) throws Exception {
        Signature publicSignature = Signature.getInstance("SHA256withRSA");
        publicSignature.initVerify(publicKey);
        publicSignature.update(plaintext.getBytes(StandardCharsets.UTF_8));

        byte[] signatureBytes = Base64.getDecoder().decode(signature);

        return publicSignature.verify(signatureBytes);
    }
    public List<JSONObject> getscopevalue(String scope, String userkey) {
        System.out.println("getiing scope values");
        Statement stmt;
        ResultSet rs = null;
        try {
            String query = String.format("select * from %s where userkey = '%s'", scope, userkey);
            System.out.println("query->"+query);
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            List<JSONObject> resJsonList = Tojasonrs.getResultSet(rs);
            System.out.println(resJsonList);
            return resJsonList;
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }
    public String getUserKey(String authcode){
        System.out.println("Getting userkey");
        String uskey = null;
        Statement stmt;
        ResultSet rs = null;
        try{
            String query = String.format("select * from authcode where authcode = '%s';",authcode);
            System.out.println("Quer -> "+query);
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            System.out.println("uskey -> " + rs.getString(1));
            uskey = rs.getString("userkey");
            System.out.println("Userkey -> " + uskey);
        }catch(Exception e){
            System.out.println(uskey);
        }
        return uskey;
    }

    public JSONObject generateJWT(String clientid, String scope, String clientsecret, String authcode) {
        System.out.println("Generating idtok...");
        String userkey = null;
        userkey = getUserKey(authcode);
        JSONObject jobj = new JSONObject();
        String idtok = null;
        AuthCodeGen authob = new AuthCodeGen();
        // timer thread
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                System.out.println("--------------------deleting id tokens---------------------");
                Statement stmt;
                ResultSet rs = null;
                try {
                    String query = String.format("select idtoken from idtoken;");
                    stmt = con.createStatement();
                    rs = stmt.executeQuery(query);
                    rs.next();
                    String token = rs.getString("idtoken");
                    query = String.format("delete from idtoken;");
                    stmt.executeUpdate(query);
                    Loggen logob = new Loggen();
                    logob.updateactionlog(token, "expired");
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        };

        try {
            Instant inst = Instant.now();
            final String JWT_HEADER = "{\"alg\":\"RS256\",\"type\":\"JWT\"}";
            Key sign = new Key();
            JSONObject obj = new JSONObject();
            obj.put("iss", "Elloauth");
            obj.put("aud", clientid);
            obj.put("iat", String.valueOf(System.currentTimeMillis()/1000));
            obj.put("exp", String.valueOf(System.currentTimeMillis()/1000 + 5000));
            obj.put("data", getscopevalue(scope, userkey));
            obj.put("scope", scope);

            String payload = b64(String.valueOf(obj));
            String header = b64(JWT_HEADER);
            String tata = header+"."+payload;
            System.out.println("plaintext -> "+tata);
            String signature = b64(sign.sign(tata,Key.privatekey));
            System.out.println("sign -> " + signature);
            //boolean resull = verify(tata,signature,Key.publicKey);
            //System.out.println(resull);
            idtok = header + "." + payload + "." + signature;
            String time = String.valueOf(System.currentTimeMillis());
            Statement stmt;
            try {
                String query = String.format("insert into idtoken(idtoken,time,clientid) values('%s','%s','%s');",
                        idtok, time, clientid);
                stmt = con.createStatement();
                stmt.executeUpdate(query);
                jobj.put("idtoken", idtok);
                jobj.put("state", "new");
                timer.schedule(task, 60000);

                return jobj;
            } catch (Exception e) {
                System.out.println(e);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
        // }
    }
}
