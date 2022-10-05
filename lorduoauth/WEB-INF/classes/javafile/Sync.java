import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.*;
import java.io.*;
import java.net.http.HttpResponse;
import java.sql.*;
import javax.security.auth.login.LoginContext;
import org.json.simple.JSONObject;
import java.util.*;
import java.util.Date;

import org.json.simple.parser.JSONParser;
import java.text.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import javax.servlet.ServletException;

public class Sync extends HttpServlet{
    Profiledb prob = new Profiledb();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException{
        try{
            Timer timer = new Timer();
            TimerTask task = new TimerTask(){
                @Override
                public void run(){
                    try{
                        System.out.println(".........syncing from salesforce......");
                        prob.sync();
                    }catch(Exception e){
                        System.out.println(e);
                    }
                }
            };
            timer.schedule(task,new Date(), 360000);
        }catch(Exception e){
            System.out.println(e);
        
        }
    }
}
