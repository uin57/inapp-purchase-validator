package core;

import com.sun.media.jfxmedia.logging.Logger;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.util.concurrent.TimeUnit;

@Getter
public abstract class AbstractValidator {
    protected Channel channel;
    protected String receipt;
    protected CloseableHttpClient httpClient;

    public abstract boolean validate();

    public AbstractValidator(){
        httpClient = buildHttpClient();
    }

    public CloseableHttpClient buildHttpClient() {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(45, TimeUnit.SECONDS);
        cm.setMaxTotal(12);
        cm.setDefaultMaxPerRoute(12);
        return HttpClientBuilder.create()
                .setConnectionManager(cm)
                .setRetryHandler(new StandardHttpRequestRetryHandler(3, true))
                .build();
    }

    public void close(CloseableHttpResponse response){
        if(response != null){
            try {
                response.close();
            }catch (Exception e){
                Logger.logMsg(Logger.ERROR, "Unable to close the HttpResponse");
            }
        }
    }
}
