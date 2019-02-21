import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

public class httpRequest {
    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet("http://www.baidu.com");
        CloseableHttpResponse response = httpclient.execute(httpget);
        System.out.println(response);
//        HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1
//                , HttpStatus.SC_OK, "OK");
        System.out.println(httpget.getRequestLine());

    }
}
