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

public class GetProfileNamae extends HttpServlet{
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException{
        try{
            GetSFUsers sf = new GetSFUsers();
            List<JSONObject> profiles = sf.getprofilenames();
            response.setContentType("text/json");
            PrintWriter out = response.getWriter();
            response.addHeader("Access-Control-Allow-Origin", "*");
            out.println(profiles);
        }catch(Exception e){
            System.out.println(e);
        
        }
    }
}
