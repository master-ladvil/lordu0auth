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

public class AddDbUser extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        String firstname = request.getParameter("firstname");
        String lastname = request.getParameter("lastname");
        String email = request.getParameter("email");
        String timezone = request.getParameter("timezone");
        String locale = request.getParameter("locale");
        String language = request.getParameter("language");
        String dpt = request.getParameter("dpt");
        String div = request.getParameter("div");
        String tit = request.getParameter("tit");
        String mobile = request.getParameter("mobile");
        String semail = request.getParameter("semail");
        try {
            GetSFUsers userob = new GetSFUsers();
            userob.adddbuser(firstname + " " + lastname, email, timezone, locale, language,dpt,div,tit,mobile,semail);
            response.addHeader("Access-Control-Allow-Origin", "*");
            out.println("added");
        } catch (Exception e) {
            System.out.println(e);
            response.addHeader("Access-Control-Allow-Origin", "*");
            out.println("e");
        }

    }
}