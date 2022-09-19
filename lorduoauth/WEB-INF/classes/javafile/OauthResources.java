import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.net.http.HttpResponse;
import java.sql.*;
import javax.security.auth.login.LoginContext;
import org.json.simple.JSONObject;
import java.util.*;
import org.json.simple.parser.JSONParser;
import java.text.*;

public class OauthResources {
    public static Connection oauthcon; 
    public OauthResources(){
        try {
            System.out.println("[+]inside authcodegen constructor..");
            Class.forName("org.postgresql.Driver");
            oauthcon = DriverManager.getConnection("jdbc:postgresql://localhost:5432/elloauth", "postgres", "pwd");
            if (oauthcon != null) {
                System.out.println("connection estabished");
            } else {
                System.out.println("Connection failed");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    } 
    public ResultSet checkat(String at){
        Statement stmt;
        ResultSet rs = null;
        try{
            String query = String.format("select * from accesstoken where accesstoken = '%s'",at);
            stmt = oauthcon.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            return rs;
        }catch(Exception e){
            System.out.println(e);
        }
        return rs;
    }   
}
