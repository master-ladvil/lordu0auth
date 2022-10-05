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

public class Map extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            GetSFUsers sf = new GetSFUsers();
            List<JSONObject> perm = sf.getmaps();
            response.setContentType("text/json");
            PrintWriter out = response.getWriter();
            response.addHeader("Access-Control-Allow-Origin", "*");
            out.println(perm);
        } catch (Exception e) {
            System.out.println(e);

        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            GetSFUsers sf = new GetSFUsers();
            String map1 = request.getParameter("m1");
            String map2 = request.getParameter("m2");
            //JSONParser parser = new JSONParser();
            //JSONObject job = (JSONObject) parser.parse(map);
            String[] m1 = map1.split("\\,");
            String[] m2 = map2.split("\\,");
            sf.delmap();
            for(int i =0 ;i<m1.length;i++){
                System.out.println(m1[i] + " -> " + m2[i]);
                sf.addmap(m1[i], m2[i]);
            }
            response.setContentType("text/plain");
            PrintWriter out = response.getWriter();
            response.addHeader("Access-Control-Allow-Origin", "*");
            out.println("success");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
