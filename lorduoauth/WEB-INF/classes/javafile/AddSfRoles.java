import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import javax.servlet.http.*;
import java.io.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;
import javax.security.auth.login.LoginContext;
import org.json.simple.JSONObject;
import java.util.*;
import org.json.simple.parser.JSONParser;
import java.text.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import javax.servlet.ServletException;

public class AddSfRoles extends HttpServlet {
    public static Connection dbcon;
    public AddSfRoles(){
        try {
            System.out.println("[+]inside getuser constructor..");
            Class.forName("org.postgresql.Driver");
            dbcon = DriverManager.getConnection("jdbc:postgresql://localhost:5432/elloauth", "postgres", "pwd");
            if (dbcon != null) {
                System.out.println("connection estabished");
            } else {
                System.out.println("Connection failed");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String role = request.getParameter("name");
        String id = null; 
        System.out.println("profilename-> "+role);
        // creating jsonbody
        JSONObject job = new JSONObject();
        job.put("Name", role);
        System.out.println("\n\n"+job+"\n\n");
        // getting accesstoken
        Slaesforceconnect tokob = new Slaesforceconnect();
        String accesstoken = tokob.getAccessToken();
        System.out.println("\n\n Accesstoken -> " + accesstoken + "\n\n");

        // creating a post request
        String url = "https://zoho-c2-dev-ed.develop.my.salesforce.com/services/data/v54.0/sobjects/UserRole";
        URL uri = new URL(url);
        HttpURLConnection con = (HttpURLConnection) uri.openConnection();
        con.setRequestProperty("Authorization", "Bearer " + accesstoken);
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        con.setDoOutput(true);
        con.setDoInput(true);
        con.setRequestMethod("POST");

        // request
        OutputStream os = con.getOutputStream();
        os.write(String.valueOf(job).getBytes("UTF-8"));
        os.close();

        // response
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer res = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            res.append(inputLine);
        }
        in.close();
        try{
        JSONParser parser = new JSONParser();
        JSONObject result = (JSONObject) parser.parse(res.toString());
        id = result.get("id").toString();
        response.setContentType("text/plain");
        
        //add to db
        Statement stmt;
        try{
            String query = String.format("insert into role(id,rolename) values('%s','%s');",id,role);
            System.out.println(query);
            stmt = dbcon.createStatement();
            stmt.executeUpdate(query);
        }catch(Exception e){
            System.out.println(e);
        }
        
        PrintWriter out = response.getWriter();

        response.addHeader("Access-Control-Allow-Origin", "*");
        out.println(id);
        }catch(Exception e){
            System.out.println(e);
        }
    }
}