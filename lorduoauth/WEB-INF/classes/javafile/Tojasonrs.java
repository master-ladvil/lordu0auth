import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONObject;

public class Tojasonrs {
    public static List<JSONObject> getResultSet(ResultSet rs){
        List<JSONObject> resJsonList = new ArrayList<JSONObject>();
        try{
            ResultSetMetaData rsMeta = rs.getMetaData();
            int columnCnt = rsMeta.getColumnCount();
            List<String> columnNames = new ArrayList<String>();
            for(int i = 1;i<=columnCnt;i++){
                columnNames.add(rsMeta.getColumnName(i));
            }
            while(rs.next()){
                JSONObject jobj = new JSONObject();
                for(int i =1;i<=columnCnt;i++){
                    String key = columnNames.get(i-1);
                    String value = rs.getString(i);
                    jobj.put(key,value);
                }
                resJsonList.add(jobj);
            }
        }catch(Exception e){
            System.out.println(e);
        }
        return resJsonList;
    }
    
}
