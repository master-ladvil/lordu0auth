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

public class Token extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        IdTokenGen idob = new IdTokenGen();
        String idtok = null;
        String authcode = request.getParameter("authcode");
        String clientid = request.getParameter("clientid");
        System.out.println(clientid);
        String clientsecret = request.getParameter("clientsecret");
        String scope = request.getParameter("scope");
        String time = String.valueOf(System.currentTimeMillis());
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("text/json");
        JSONObject jobj = new JSONObject();
        PrintWriter out = response.getWriter();
        AuthCodeGen authob = new AuthCodeGen();
        if (authob.checkdb("clientid", "idsec", clientid)) {
            if (authob.checkdb("scopename", "scopes", scope)) {
                if (authob.checkdb("authcode", "authcode", authcode)) {
                    if (authob.checkdb("clientsecret", "idsec", clientsecret)) {
                        JSONObject idtokob = new JSONObject();
                        idtokob = idob.generateJWT(clientid, scope, clientsecret, authcode);
                        String status = idtokob.get("state").toString();
                        idtok = idtokob.get("idtoken").toString();
                        if (status.equals("new")) {
                            authob.delAuthCode(clientid, "authcode", authcode);
                            Loggen logob = new Loggen();
                            logob.addToLog("idtoken", idtok, String.valueOf(System.currentTimeMillis()), clientid,
                                    "granted");
                        }
                        jobj.put("idtoken", idtok);
                        jobj.put("clientid", clientid);
                        jobj.put("scope", scope);

                    } else {
                        jobj.put("error", "invalid request");
                        jobj.put("desc", "invalid clientsecret");
                    }
                } else {
                    jobj.put("error", "invalid request");
                    jobj.put("desc", "invalid  authcode");
                }
            } else {
                jobj.put("error", "invalid request");
                jobj.put("desc", "invalid  scope");
            }
        } else {
            jobj.put("error", "invalid request");
            jobj.put("desc", "invalid clientid");
        }

        out.println(jobj);
    }
}