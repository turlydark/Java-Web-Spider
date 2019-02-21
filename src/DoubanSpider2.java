import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

//解析http
public class DoubanSpider2{
    //     根据URL获得所有的html信息
    public static String getHtmlByUrl(String url){
        String html = null;
        //创建httpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(url);

        httpget.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64)" +
                " AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36");
        RequestConfig config=RequestConfig.custom()
                .setConnectTimeout(10000) //设置连接超时时间为10s
                .setSocketTimeout(10000) //设置读取超时时间为10s
                .build();
        httpget.setConfig(config);
        try {
            //得到responce对象
            HttpResponse responce = httpClient.execute(httpget);
            int ResStatu = responce.getStatusLine().getStatusCode();
            if (ResStatu==HttpStatus.SC_OK) {
                //获得输入流
                InputStream entity = responce.getEntity().getContent();
                if (entity!=null) {
                    html=getStreamString(entity);
//                    此处输出html
                    System.out.println(html);
                }
            }
        } catch (Exception e) {
            System.out.println("访问出现异常!");
            e.printStackTrace();
        } finally {
            //httpClient.getConnectionManager().shutdown();
        }
        return html;
    }

    //    将一个字节输入流转化为字符串
    public static String getStreamString(InputStream inputstream){
        if (inputstream != null){
            try{
                BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(inputstream));
                StringBuffer stringbuffer = new StringBuffer();
                String sTempOneLine = new String("");
                while ((sTempOneLine = bufferedreader.readLine()) != null){
                    stringbuffer.append(sTempOneLine+"\n");
                }
                return stringbuffer.toString();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        return null;
    }

    //    数据库插入信息
    public static void mysqlinsert(String name, float rating, String quote, String imgurl, String infourl) {
        final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        final String DB_URL = "jdbc:mysql://localhost:3306/doubanbooks";
        final String USER = "root";
        final String PASS = "123456";

        Connection conn = null;
        Statement stmt = null;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
            stmt = conn.createStatement();
            String sql;
            sql = "INSERT INTO books2 (name, rating, quote, imgurl, infourl) VALUES ('"+name+"','"+rating+"','"+quote+"','"+imgurl+"','"+infourl+"');";
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
    public static void main(String[] args) throws Exception {

        String html = getHtmlByUrl("https://book.douban.com/top250?start=225");
        if (html != null && !"".equals(html)) {
            Document doc = Jsoup.parse(html);


            String[] names = new String[100];
            String[] quotes = new String[100];
            String[] imgurls = new String[100];
            float[] ratings = new float[100];
            String[] infourls = new String[100];

            int kase = 0;
            kase = 0;
            Elements titles = doc.getElementsByTag("a");
            for(Element elem:titles){
                String title = elem.attr("title");
                if(title != null && title != ""){
//                    System.out.println(title);
                    names[kase++] = title;
                }
            }
            kase = 0;
            Elements rating_num = doc.getElementsByClass("rating_nums");
            for(Element rating:rating_num){
                float num = Float.parseFloat(rating.text());
//                System.out.println(num);
                ratings[kase++] = num;
            }
            kase = 0;
            Elements quos = doc.select("p.quote");
            for(Element quo:quos){
                String text = quo.text();
//                System.out.println(text);
                quotes[kase++] = text;
            }
            kase = 0;
            Elements links = doc.select("[width=90]");
            for(Element link:links){
                String linkHref = link.attr("src");
//                System.out.println(linkHref);
                imgurls[kase++] = linkHref;
            }
            kase = 0;
            Elements infolinks = doc.select("a.nbg");
            for(Element link:infolinks){
                String linkHref = link.attr("href");
//                System.out.println(linkHref);
                infourls[kase++] = linkHref;
            }

            for (int i = 0; i < 25; ++i) {
                mysqlinsert(names[i],ratings[i],quotes[i],imgurls[i],infourls[i]);
                System.out.println(i + 1);
                System.out.println("片名 " + names[i] + " ");
                System.out.println("评分为 " + ratings[i] + " ");
                System.out.println("一句话影评 " + quotes[i] + " ");
                System.out.println("海报链接 " + imgurls[i] + " ");
                System.out.println("图书信息链接 " + infourls[i] + " ");
            }
        }
    }
}
