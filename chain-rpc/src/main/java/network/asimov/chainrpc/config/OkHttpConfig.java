package network.asimov.chainrpc.config;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * The OkHttp configuration is used for request chain.
 *
 * @author zhangjing
 * @date 2019-09-21
 */
@Configuration
public class OkHttpConfig {
    @Value("${ok.http.connect-timeout}")
    private Integer connectTimeout;

    @Value("${ok.http.read-timeout}")
    private Integer readTimeout;

    @Value("${ok.http.write-timeout}")
    private Integer writeTimeout;

    @Value("${ok.http.max-idle-connections}")
    private Integer maxIdleConnections;

    @Value("${ok.http.keep-alive-duration}")
    private Long keepAliveDuration;

    @Bean("okHttpClient")
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .retryOnConnectionFailure(false)
                .connectionPool(pool())
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .build();
    }

    @Bean
    public ConnectionPool pool() {
        return new ConnectionPool(maxIdleConnections, keepAliveDuration, TimeUnit.SECONDS);
    }
}
