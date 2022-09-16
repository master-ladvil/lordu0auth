import javax.security.auth.login.LoginContext;
import javax.servlet.*;
import java.io.*;
import javax.servlet.http.*;
import java.sql.*;
import java.util.Base64;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import org.bouncycastle.jce.provider.*;

import netscape.javascript.JSObject;

public class InitAuth extends HttpServlet {
    
    public static Connection con;
    //public static String publickey = null;
    public static String arg1 = null;
    public static String arg2 = null;
    public static String arg3 = null;

    public InitAuth() {
        try {
            java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            System.out.println("[+]inside init constructor..");
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

    public String getHash(String key) {
        try {
            MessageDigest dst = MessageDigest.getInstance("MD5");
            byte[] msgArr = dst.digest(key.getBytes());
            BigInteger bi = new BigInteger(1, msgArr);
            String hshtxt = bi.toString(16);
            while (hshtxt.length() < 32) {
                hshtxt = "0" + hshtxt;
            }
            return hshtxt;
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    public final String sign = "Ellouth";

    // generate client id
    public String clientIdGen(String clientinfo, String time) {
        String value = clientinfo + time + sign;
        return getHash(value);
    }

    // jwt
    public KeyPair getkeypair() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048, new SecureRandom());
        KeyPair pair = generator.generateKeyPair();
        return pair;
    }

    // sign jwt
    public byte[] sign(String plaintext, PrivateKey privateKey) throws Exception {
        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(privateKey);
        privateSignature.update(plaintext.getBytes(StandardCharsets.UTF_8));
        byte[] signature = privateSignature.sign();
        return signature;
        //return Base64.getEncoder().encodeToString(signature);
    }

    // generate client secret
    public String[] clientSecretGen(String clientid, PrivateKey privatekey,String publickey) {
        String secrettoken = null, header = null, payload = null, signature = null;
        try {// header
            String JWT_HEADER = "{\"alg\":\"RS256\",\"type\":\"JWT\"}";
            header = Base64.getUrlEncoder().encodeToString(JWT_HEADER.getBytes());
            // payload
            JSONObject secret = new JSONObject();
            secret.put("iss", "elloauth");
            secret.put("aud", clientid);
            secret.put("iat", System.currentTimeMillis() / 1000);
            //secret.put("pk",publickey);
            payload = Base64.getUrlEncoder().encodeToString((String.valueOf(secret)).getBytes());
            signature = Base64.getUrlEncoder().encodeToString(sign(header + "." + payload, privatekey));
            secrettoken = header + "." + payload + "." + signature;
            //System.out.println("SecretToken ->> " + secrettoken);
        } catch (Exception e) {
            System.out.println(e);
        }
        //System.out.println("-----------"+signature+"--------------");
        String[] secrets = { secrettoken, header + "." + payload, signature };
        return secrets;
    }

    public void addtokeys(String clientid, String signedsecret, String privatekey, String publickey) {
        Statement stmt;
        try {
            String query = String.format(
                    "insert into keys(clientid,clientsecret,publickey,privatekey) values('%s','%s','%s','%s');",
                    clientid, signedsecret, privatekey, publickey);
            stmt = con.createStatement();
            stmt.executeUpdate(query);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public boolean verify(String plaintext, String signature, PublicKey publicKey) throws Exception {
        Signature publicSignature = Signature.getInstance("SHA256withRSA");
        publicSignature.initVerify(publicKey);
        publicSignature.update(plaintext.getBytes(StandardCharsets.UTF_8));

        byte[] signatureBytes = Base64.getUrlDecoder().decode(signature);

        return publicSignature.verify(signatureBytes);
    }

    public int addtodb(String clientid, String clientsecret, String email) {
        ResultSet rs = null;
        Statement stmt;
        try {
            String query = String.format("insert into idsec(clientid,clientsecret,email) values('%s','%s','%s');",
                    clientid, clientsecret, email);
            stmt = con.createStatement();
            stmt.executeUpdate(query);
            return 1;
        } catch (Exception e) {
            System.out.println(e);
        }
        return 0;
    }

    public PublicKey getpubob(String publickey) {
        System.out.println("[+] inside getpubob");
        //System.out.println("pubb-----> " + publickey);
        PublicKey pubkey = null;
        try {
            byte[] publicBytes = Base64.getUrlDecoder().decode(publickey);
            X509EncodedKeySpec keyspec = new X509EncodedKeySpec(publicBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            pubkey = keyFactory.generatePublic(keyspec);
        } catch (Exception e) {
            System.out.println(e);
        }
        return pubkey;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("text/json");
        PrintWriter out = response.getWriter();
        String signedclientsecret = null;
        KeyPair pair;
        String clientinfo = request.getParameter("clientinfo");
        String time = String.valueOf(System.currentTimeMillis());
        String clientId = clientIdGen(clientinfo, time);

        // add signed seccret with private and public key
        boolean verify = false;
        try {
            IdTokenGen aob = new IdTokenGen();
            pair = getkeypair();
            PrivateKey privatekey = pair.getPrivate();
            PublicKey pubkey = pair.getPublic();
            String privkey = Base64.getUrlEncoder().encodeToString(privatekey.getEncoded());
            String publickey = Base64.getUrlEncoder().encodeToString(pubkey.getEncoded());
            System.out.println("\n\n publickey gen -> "+ publickey+"\n\n");
            String[] secrets = clientSecretGen(clientId, pair.getPrivate(),publickey);
            signedclientsecret = secrets[0];
            addtokeys(clientId, signedclientsecret, privkey, publickey);
            Statement stmt = con.createStatement();
            String query = "update keys set publickey = '"+publickey+"' where clientid = '"+clientId+"';";
            stmt.executeUpdate(query);
            System.out.println("\n\n"+query+"\n\n");
            //System.out.println("-----------"+secrets[1]+"..............");
            verify = verify(secrets[1], secrets[2], getpubob(publickey));
            arg1 = secrets[1];
            arg2 = secrets[2];
            
            System.out.println(verify);
            // verify
        } catch (Exception e) {
            System.out.println(e);
        }

        // put the signed secret for response
        JSONObject jobj = new JSONObject();
        jobj.put("client_id", clientId);
        jobj.put("client_secret", signedclientsecret);
        jobj.put("Verified?:", verify);
        System.out.println(jobj);
        response.addHeader("Access-Control-Allow-Origin", "*");
        out.println(jobj);

    }

    // check for ids and secret
    public String checkClient(String clientid, String clientSecret) throws Exception {
        Statement stmt;
        ResultSet rs = null;
        String publickey = null;
        // System.out.println("client id insde fun -> "+clientid);
        try {
            String query = String.format("select * from keys where clientid = '%s';", clientid);
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            String dbsecret = rs.getString("clientsecret");
            // System.out.println("db -> "+dbsecret);
            if (dbsecret.equals(clientSecret)) {
                publickey = rs.getString("publickey");

            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return publickey;
    }

    public String[] decomposeSecret(String secret) {
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String[] chunks = secret.split("\\.");
        String header = chunks[0];
        String payload1 = chunks[1];
        String payload2 = new String (decoder.decode(chunks[1]));
        String signature = (chunks[2]);
        String[] decomposedSecret = { header+"."+payload1,payload2, signature };
        return decomposedSecret;
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        System.out.println("[+] inside post............");
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        String clientid = request.getParameter("clientid");
        String clientsecret = request.getParameter("clientsecret");
        try{
            out.println(verifysecret(clientid,clientsecret));
        } catch (Exception e) {
            System.out.println(e);
            out.println(e);
        }
    }

    public boolean verifysecret(String clientid, String clientsecret)throws Exception{
        System.out.println("[+] inside verification.. ");
        // System.out.println("clientid -> "+ clientid + "\nclientsecret -> "+
        // clientsecret);
        try {
            String publickey = checkClient(clientid, clientsecret);
            if (publickey == null) {
                return false;
            } else {
                System.out.println("publickey -> "+publickey);
                String[] decomposedSecret = decomposeSecret(clientsecret);
                String header = decomposedSecret[0];
                String payload = decomposedSecret[1];
                String signature = decomposedSecret[2];
                String[] chunks = clientsecret.split("\\.");
                /*JSONParser parser = new JSONParser();
                JSONObject jobj = (JSONObject) parser.parse(payload);
                String pk = jobj.get("pk").toString();
                System.out.println("publicactual -0> "+pk);*/
                PublicKey pukkey = getpubob(publickey);
                //System.out.println("pauload -> "+ chunks[2]);
                //PublicKey pussyk = getpubob(pussykey);
                //System.out.println("-----------"+decomposedSecret[0]+"----------");
                boolean verify = verify(decomposedSecret[0], chunks[2],pukkey);
                return verify;
            }
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }
}
