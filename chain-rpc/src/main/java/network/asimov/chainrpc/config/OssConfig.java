package network.asimov.chainrpc.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import network.asimov.chainrpc.service.OssService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The OSSClient is used for upload picture.
 *
 * @author zhangjing
 * @date 2019-11-08
 */
@Configuration
public class OssConfig {
    @Value("${oss.endpoint:oss-cn-qingdao.aliyuncs.com}")
    private String endpoint;

    @Value("${oss.access_key_id}")
    private String accessKeyId;

    @Value("${oss.access_key_secret}")
    private String accessKeySecret;

    @Value("${oss.bucket_name}")
    private String bucketName;

    /**
     * OSS has the shutdown method, which is automatically called when application gracefully shutdown.
     * But, if initMethod is used, need to be called manually, such as initMethod = "init".
     *
     * @return OSSClient
     */
    @Bean(value = "ossClient")
    public OSS ossClient() {
        return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    @Bean(value = "daoOssService")
    public OssService daoOssService() {
        return OssService.builder().bucket(bucketName).endPoint(endpoint).ossClient(ossClient()).build();
    }
}
