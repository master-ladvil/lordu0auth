
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
    public void addperm(String name){
        Statement stmt;
        try{
            String query = String.format("insert into permissions(perm) values('%s');",name);
            System.out.println("Query -> "+ query);
            stmt = con.createStatement();
            stmt.executeUpdate(query);

        }catch(Exception e){
            System.out.println(e);
        }
    }
    public List<JSONObject> getperm(){
        Statement stmt;
        ResultSet rs = null;
        try{
            String query = String.format("select * from permissions;");
            System.out.println("Query -> " + query);
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            List<JSONObject> permissions = Tojasonrs.getResultSet(rs);
            return permissions;
        }catch(Exception e){
            System.out.println(e);
        }
        return null;
    }
    public List<JSONObject> getRole(){
        Statement stmt;
        ResultSet rs = null;
        try{
            String query = String.format("select * from role;");
            System.out.println("Query -> " + query);
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            List<JSONObject> role = Tojasonrs.getResultSet(rs);
            return role;
        }catch(Exception e){
            System.out.println(e);
        }
        return null;
    }
    public void adddbuser(String name,String email,String timezone,String locale,String languange,String dpt,String div,String tit,String mobile,String semail){
        Statement stmt;
        try{
            String query = String.format("insert into sfcloneuser(name,email,timezone,locale,language,department,division,title,mobile,semail) values( '%s','%s','%s','%s','%s','%s','%s','%s','%s','%s' );",name,email,timezone,locale,languange,dpt,div,tit,mobile,semail);
            System.out.println("Query -> "+ query);
            stmt = con.createStatement();
            stmt.executeUpdate(query);

        }catch(Exception e){
            System.out.println(e);
        }
    }
    public List<JSONObject> getclone(){
        Statement stmt;
        ResultSet rs = null;
        try{
            String query = String.format("select * from sfcloneuser;");
            System.out.println("Query -> " + query);
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            List<JSONObject> role = Tojasonrs.getResultSet(rs);
            return role;
        }catch(Exception e){
            System.out.println(e);
        }
        return null;
    }
    public List<JSONObject> getsfattr(){
        Statement stmt;
        ResultSet rs = null;
        try{
            String query = String.format("select * from sfattr;");
            System.out.println("Query -> " + query);
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            List<JSONObject> role = Tojasonrs.getResultSet(rs);
            return role;
        }catch(Exception e){
            System.out.println(e);
        }
        return null;
    }
    public List<JSONObject> getdbattr(){
        Statement stmt;
        ResultSet rs = null;
        try{
            String query = String.format("select * from dbattr;");
            System.out.println("Query -> " + query);
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            List<JSONObject> role = Tojasonrs.getResultSet(rs);
            return role;
        }catch(Exception e){
            System.out.println(e);
        }
        return null;
    }

    public List<JSONObject> getmaps(){
        Statement stmt;
        ResultSet rs = null;
        try{
            String query = String.format("select * from maps;");
            System.out.println("Query -> " + query);
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            List<JSONObject> role = Tojasonrs.getResultSet(rs);
            return role;
        }catch(Exception e){
            System.out.println(e);
        }
        return null;
    }
    public void delmap(){
        Statement stmt;
        try{
            String query = String.format("delete from maps;");
            System.out.println("Query -> "+ query);
            stmt = con.createStatement();
            stmt.executeUpdate(query);

        }catch(Exception e){
            System.out.println(e);
        }
    }
    public void addmap(String m1,String m2){
        Statement stmt;
        try{
            String query = String.format("insert into maps(map1,map2) values('%s','%s');",m1,m2);
            System.out.println("Query -> "+ query);
            stmt = con.createStatement();
            stmt.executeUpdate(query);

        }catch(Exception e){
            System.out.println(e);
        }
    }
}
