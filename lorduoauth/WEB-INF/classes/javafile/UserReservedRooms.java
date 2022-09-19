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

public class UserReservedRooms extends HttpServlet {

    public boolean checkscope(String accesstoken) throws Exception {
        OauthResources orob = new OauthResources();
        System.out.println("checking scope........." + accesstoken);
        boolean result = false;
        ResultSet rs = orob.checkat(accesstoken);
        if (rs != null) {
            String scope = rs.getString("scope");
            System.out.println("\n\nscope assigned for at -> " + scope + "\n\n");
            String[] chunks = scope.split("\\.");
            for (int i = 0; i < chunks.length; i++) {
                if (chunks[i].equals("userrooms")) {
                    result = true;

                    break;
                }
                System.out.println(chunks[i] + " -> " + result);
            }
        }
        System.out.println("result -> " + result);
        return result;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            UserReservedRoomsDb urob = new UserReservedRoomsDb();
            response.setContentType("text/json");
            response.addHeader("Access-Control-Allow-Origin", "*");
            PrintWriter out = response.getWriter();
            String accesstoken = request.getHeader("accesstoken");
            if (accesstoken == null) {
                out.println("no token");
            } else {
                AccessToken atob = new AccessToken();
                boolean isAccess = atob.verify(accesstoken);
                boolean isScope = checkscope(accesstoken);
                if (isAccess && isScope) {
                    List<JSONObject> myrooms = urob.getReservedRoom(accesstoken);
                    out.println(myrooms);
                    System.out.println(myrooms);
                } else {
                    out.println("noaccess");
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }
    
      
}