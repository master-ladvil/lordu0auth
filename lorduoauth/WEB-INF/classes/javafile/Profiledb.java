
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

public class Profiledb {
    public static Connection con;

    public Profiledb() {
        try {
            System.out.println("[+]inside profile constructor..");
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

    public static void main(String args[]) throws Exception {
        Profiledb pro = new Profiledb();
        Slaesforceconnect tokob = new Slaesforceconnect();
        String accesstoken = tokob.getAccessToken();
        System.out.println("\n\naccesstoken -> "+accesstoken+"\n\n");
        // building request to fetch profile
        String uri = "https://zoho-c2-dev-ed.develop.my.salesforce.com/services/data/v54.0/query?q=select+Id,Name,UserName,Alias,Email,Title,ProfileId,TimeZoneSidKey,UserRoleId,LocaleSidKey,isActive,ManagerId,CompanyName,Division,Department,Phone+from+User";
        //String uri = "https://zoho-c2-dev-ed.develop.my.salesforce.com/services/data/v54.0/query?q=select+fields(all)+from+UserRole+limit+200";
        URL url = new URL(uri);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + accesstoken);
        conn.setRequestProperty("Content-Type", "application/json");
        int responseCode = conn.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) {
            // respose
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer res = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                res.append(inputLine);
            }
            in.close();
            try {
                JSONParser parser = new JSONParser();
                JSONObject job = (JSONObject) parser.parse(res.toString());
                List<JSONObject> profile = (List<JSONObject>) job.get("records");
                for (int i = 0; i < profile.size(); i++) {
                    pro.addprofile(profile.get(i));
                    //System.out.println(profile.get(i));
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        } else {
            System.out.println("not worked");
        }
    }

    public void addprofile(JSONObject profile) {
        Statement stmt;
        //String id = profile.get("Id").toString();
        //String role = profile.get("Name").toString();
        String id = profile.get("Id").toString();
        String name = profile.get("Name").toString();
        String alias = profile.get("Alias").toString();
        String email = profile.get("Email").toString();
        String username = profile.get("Username").toString();
        String title = "mts";
        String companyname = "nazz";
        String department = "general";
        String division = "division";
        String timezone = profile.get("TimeZoneSidKey").toString();
        String local = profile.get("LocaleSidKey"). toString();
        String language = "english";
        String manager = null;
        String roleid = "000000000000000";
        if(profile.get("UserRoleId")!=null){
            roleid = profile.get("UserRoleId").toString();
        }
        String profileid = profile.get("ProfileId").toString();
        String isactive = profile.get("IsActive").toString();
        //System.out.println(id+name+alias+email+username+title+companyname+department+division+timezone+local+language+manager+roleid+profileid+isactive);

        //getting the data
        try {
            String query = String.format("insert into sfusers(id,name,alias,email,username,title,companyname,department,division,timezone,local,language,manager,roleid,profileid,isactive) values('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s');",id,name,alias,email,username,title,companyname,department,division,timezone,local,language,manager,roleid,profileid,isactive);
            // query = String.format("insert into role(id,rolename) values('%s','%s');",id,role);
            System.out.println("Query -> " + query);
            stmt = con.createStatement();
            stmt.executeUpdate(query);
        } catch (Exception e) {
            System.out.println("\n\n\n\n"+e+"\n\n\n\n");
        }
    }
}
