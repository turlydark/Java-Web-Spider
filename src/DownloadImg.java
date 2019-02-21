import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;

public class DownloadImg{
    private static void Download(String urlList,String path) {
        URL url = null;
        try {
            url = new URL(urlList);
            DataInputStream dataInputStream = new DataInputStream(url.openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(new File(path));
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = dataInputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            fileOutputStream.write(output.toByteArray());
            dataInputStream.close();
            fileOutputStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // JDBC 驱动名及数据库 URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/doubanbooks";

    // 数据库的用户名与密码，需要根据自己的设置
    static final String USER = "root";
    static final String PASS = "123456";

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        String[] names = new String[1000];
        String[] imgurls = new String[1000];
        int kase = 0;
        try{
            // 注册 JDBC 驱动
            Class.forName("com.mysql.jdbc.Driver");

            // 打开链接
            System.out.println("连接数据库...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            // 执行查询
            System.out.println("实例化Statement对象...");
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT name, imgurl FROM books";
            ResultSet rs = stmt.executeQuery(sql);

            // 展开结果集数据库
            while(rs.next()){
                // 通过字段检索
                String name = rs.getString("name");
                String imgurl = rs.getString("imgurl");
                names[kase] = name;
                imgurls[kase] = imgurl;
                kase++;
                // 输出数据
//                System.out.println("电影名称: " + name);
//                System.out.println("海报URL: " + imgurl);
                String path = "C:\\Users\\98766\\Desktop\\Books2\\" + name + ".jpg";
                Download(imgurl,path);

            }
            // 完成后关闭
            rs.close();
            stmt.close();
            conn.close();



        }catch(SQLException se){
            // 处理 JDBC 错误
            se.printStackTrace();
        }catch(Exception e){
            // 处理 Class.forName 错误
            e.printStackTrace();
        }finally{
            // 关闭资源
            try{
                if(stmt!=null) stmt.close();
            }catch(SQLException se2){
            }// 什么都不做
            try{
                if(conn!=null) conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }
        }
        System.out.println("Goodbye!");
    }
}