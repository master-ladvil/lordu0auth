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

public class SubmitUser extends HttpServlet {
    public static Connection dbcon;
    public SubmitUser(){
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
        String username = request.getParameter("username");
        String lastname = request.getParameter("lastname");
        String firstname = request.getParameter("firstname");
        String email = request.getParameter("email");
        String isactive = request.getParameter("isactive");
        String alias = request.getParameter("alias");
        String timezone = request.getParameter("timezone");
        String local = request.getParameter("local");
        String emailencode = request.getParameter("emailencode");
        String profileid = request.getParameter("profileid");
        String langkey = request.getParameter("langkey");
        String manager = "null";
        String roleid = "000000000000000";
        
        
        System.out.println("\n" + username + "\n" + lastname + "\n" + firstname + "\n" + email + "\n" + isactive + "\n"
                + alias + "\n" + timezone + "\n" + local + "\n" + emailencode + "\n" + profileid + "\n" + langkey);
        



        // creating jsonbody
        JSONObject job = new JSONObject();
        job.put("Username", username);
        job.put("LastName", lastname);
        job.put("FirstName", firstname);
        job.put("Email", email);
        job.put("isActive", isactive);
        job.put("Alias", alias);
        job.put("TimeZoneSidKey", timezone);
        job.put("LocaleSidKey", local);
        job.put("EmailEncodingKey", emailencode);
        job.put("ProfileId", profileid);
        job.put("LanguageLocaleKey", langkey);

       
       //addtodb
       
       
       
       
       
        // getting accesstoken
        Slaesforceconnect tokob = new Slaesforceconnect();
        String accesstoken = tokob.getAccessToken();
        System.out.println("\n\n Accesstoken -> " + accesstoken + "\n\n");

        // creating a post request
        String url = "https://zoho-c2-dev-ed.develop.my.salesforce.com/services/data/v54.0/sobjects/User";
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
        String id = result.get("id").toString();
        response.setContentType("text/plain");
        
        
        //add to db
        //add to db
        Statement stmt;
        try{
            String query = String.format("insert into sfusers(id,name,alias,email,username,title,companyname,department,division,timezone,local,language,profileid,isactive,manager,roleid) values('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s');",id,firstname+lastname,lastname,email,firstname+lastname+"@ladvil.com","lead","nazz","general","north",timezone,local,"english",profileid,isactive,manager,roleid);
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