import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class insert{
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/doubanmovie";
    static final String USER = "root";
    static final String PASS = "123456";

    public static void main(String[] args){
    Connection conn = null;
    Statement stmt = null;
    try{
        Class.forName("com.mysql.jdbc.Driver");
        conn = DriverManager.getConnection(DB_URL,USER,PASS);
        stmt = conn.createStatement();
        String sql;
        sql = "\"INSERT INTO books2 (name, rating, quote, imgurl, infourl) VALUES ('\"+name+\"','\"+rating+\"','\"+quote+\"','\"+imgurl+\"','\"+infourl+\"');\";";
        int rowline = stmt.executeUpdate(sql);

        stmt.close();
        conn.close();
    }catch(SQLException se){
        se.printStackTrace();
    }catch(Exception e){
        e.printStackTrace();
    }finally{
        try{
            if(stmt!=null) stmt.close();
        }catch(SQLException se2){
        }
        try{
            if(conn!=null) conn.close();
        }catch(SQLException se){
            se.printStackTrace();
        }
    }

        return;
    }
}
