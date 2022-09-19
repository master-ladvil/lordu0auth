
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import javax.security.auth.login.LoginContext;
import org.json.simple.JSONObject;
import java.util.*;
import java.text.*;

public class UserReservedRoomsDb {
    public Connection oauthcon = null;
    public static Connection con = null;

    public UserReservedRoomsDb() {

        try {
            System.out.println("[+]inside myroom constructor..");
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
        try {
            System.out.println("[+]inside myroom constructor..");
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/hotelreserve1_0", "postgres", "pwd");
            if (con != null) {
                System.out.println("connection estabished");
            } else {
                System.out.println("Connection failed");
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public String getname(String accesstoken) {
        Statement stmt;
        ResultSet rs = null;
        try {
            String query = String.format(
                    "select fname from userinfo where userkey = (select uid from accesstoken where accesstoken = '%s');",
                    accesstoken);
            System.out.println("Query ->> " + query);
            stmt = oauthcon.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            String fname = rs.getString("fname");
            return fname;
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }
    public String getClid(String name){
        String clid = null;
        Statement stmt;
        ResultSet rs = null;
        try{
            String query = String.format("select id from client where fullname = '%s';", name);
            System.out.println("Query -> " + query);
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            clid = rs.getString("id");

        }catch(Exception e){System.out.println(e);}
        return clid;
    }
    public List<JSONObject> getReservedRoom(String accesstoken) {
        String name = null;
        String clid = null;
        try {
            System.out.println("inside MyRoom...");
            name = getname(accesstoken);
            System.out.println("name ->  " + name);
            Statement stmt;
            ResultSet rs = null;
            clid = getClid(name);          
            System.out.println("clid -> " + clid);
            String query = String.format("select id,sdate,edate,rid from reservation where clid = '%s';", clid);
            System.out.println(query);
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            List<JSONObject> resJsonList = Tojasonrs.getResultSet(rs);
            for (int i = 0; i < resJsonList.size(); i++) {
                System.out.println(resJsonList.get(i));
            }
            return resJsonList;
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }
}
