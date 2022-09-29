
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.http.HttpResponse;
import java.sql.*;
import javax.security.auth.login.LoginContext;
import org.json.simple.JSONObject;
import java.util.*;
import org.json.simple.parser.JSONParser;
import java.text.*;

public class GetSFUsers {
    public static Connection con;
    public GetSFUsers(){
        try {
            System.out.println("[+]inside getuser constructor..");
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
    public List<JSONObject> getsfusers(){
        Tojasonrs jsonify = new Tojasonrs();
        Statement stmt;
        ResultSet rs = null;
        List<JSONObject> userlist = null;
        try{
            String query = String.format("select sfusers.id,name,alias,email,username,title,companyname,department,division,timezone,local,language,manager,rolename,profilename,isactive from sfusers join role on sfusers.roleid = role.id join profile on sfusers.profileid = profile.id;");
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            userlist = Tojasonrs.getResultSet(rs);
        }catch(Exception e){
            System.out.println(e);
        }
        return userlist;
    }
    public List<JSONObject> getprofilenames(){
        Statement stmt;
        ResultSet rs =null;
        List<JSONObject> profilelist = null;
        try{    
            String query = String.format("select * from profile");
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            profilelist = Tojasonrs.getResultSet(rs);

        }catch(Exception e){
            System.out.println(e);
        }
        return profilelist;
    }
}
