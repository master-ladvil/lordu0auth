
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

public class GetPermissions {
    public static void main(String args[]) throws Exception {
        GetPermissions getp = new GetPermissions();
        getp.Getperm();
    }

    public void Getperm() {
        String accesstoken = null;
        Slaesforceconnect aob = new Slaesforceconnect();
        accesstoken = aob.getAccessToken();
        GetSFUsers sfob = new GetSFUsers();
        try {
            String url = "https://zoho-c2-dev-ed.develop.my.salesforce.com/services/data/v54.0/sobjects/Profile/describe/";
            URL obj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + accesstoken);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
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
                List<JSONObject> ob = (List<JSONObject>) jobj.get("fields");
                for(int i =0;i<ob.size();i++){
                    JSONObject od = ob.get(i);
                    //System.out.println(od.get("name"));
                    sfob.addperm(od.get("name").toString());
                }
            } else {
                System.out.println("GET request not worked");
            }

        } catch (Exception e) {
            System.out.println(e);
        }

    }
    
}
