import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import javax.servlet.http.*;
import java.io.*;
import java.net.http.HttpResponse;
import java.sql.*;
import javax.security.auth.login.LoginContext;
import org.json.simple.JSONObject;
import java.util.*;
import org.json.simple.parser.JSONParser;
import java.text.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import javax.servlet.ServletException;

public class ConnectSalesForce extends HttpServlet {

    static final String USERNAME = "ladvil@ladvil.com";
    static final String PASSWORD = "14mDk0r10NWCYQ8RGwWubcBJN3baWwI9gof";
    static final String LOGINURL = "https://login.salesforce.com";
    static final String GRANTSERVICE = "/services/oauth2/token?grant_type=password";
    static final String CLIENTID = "3MVG9fe4g9fhX0E64jBI8eK6ijF3vMhbiWQdW.qsT096F7OZ3dVI7If3uRGSIWhtXpHGukZqBwi9m5.FhYuTq";
    static final String CLIENTSECRET = "29AC3659875FBB59D88189C96471FBE97F579DDCC8F723091B5B58B7224CC9EB";
    private static String REST_ENDPOINT = "/services/data";
    private static String API_VERSION = "/v54.0";

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String accesstoken = null, instanceurl = null;
            response.setContentType("text/plain");
            PrintWriter out = response.getWriter();
            // Assemble the login request URL
            String loginURL = LOGINURL +
                    GRANTSERVICE +
                    "&client_id=" + CLIENTID +
                    "&client_secret=" + CLIENTSECRET +
                    "&username=" + USERNAME +
                    "&password=" + PASSWORD;

            System.out.println(loginURL);
            URL obj = new URL(loginURL);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestMethod("POST");
            int responseCode = conn.getResponseCode();
            System.out.println("GET Response Code :: " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                String inputLine;
                StringBuffer res = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    res.append(inputLine);
                }
                in.close();

                // print result
                JSONParser parser = new JSONParser();
                JSONObject jobj = (JSONObject) parser.parse(res.toString());
                accesstoken = jobj.get("access_token").toString();
                instanceurl = jobj.get("instance_url").toString();
                String signature = jobj.get("signature").toString();
            } else {
                System.out.println("GET request not worked");
            }
            GetSFUsers get = new GetSFUsers();
            List<JSONObject> resJsonList = get.getsfusers();
            System.out.println(resJsonList);
            response.addHeader("Access-Control-Allow-Origin", "*");
            out.println(resJsonList);

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException,IOException {
        GetSFUsers profob = new GetSFUsers();
        List<JSONObject> profiles = profob.getprofilenames();
        System.out.println(profiles);
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        response.addHeader("Access-Control-Allow-Origin", "*");
        out.println(profiles);
    }
}