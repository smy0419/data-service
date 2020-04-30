package network.asimov.chainrpc.service;


import com.aliyun.oss.OSS;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.UUID;

/**
 * @author zhangjing
 * @date 2019-11-08
 */
@Builder
@Slf4j
public class OssService {
    private OSS ossClient;
    private String bucket;
    private String endPoint;

    public String uploadPicture(InputStream inputStream, String prefix) {
        String hash = getRandomUUID();
        String path = getFullPath(prefix, hash);
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentType("image/jpg");
        try {
            PutObjectResult result = ossClient.putObject(bucket, path, inputStream, meta);
            if (result.getETag() != null) {
                return hash;
            }
        } catch (Exception e) {
            log.error("OSS error", e);
        }
        return null;
    }

    public String getFullPath(String prefix, String hash) {
        return prefix + hash.substring(0, 2) + "/" + hash.substring(2, 4) + "/" + hash.substring(4, 6) + "/" + hash;
    }

    public String getFullPathUrl(String prefix, String hash) {
        return "https://" + bucket + "." + endPoint + "/" + getFullPath(prefix, hash);
    }

    public static String getRandomUUID() {
        UUID uuid = UUID.randomUUID();
        long mostSigBits = uuid.getMostSignificantBits();
        long leastSigBits = uuid.getLeastSignificantBits();
        StringBuilder sb = new StringBuilder();
        sb.append(digits(mostSigBits >> 32, 8));
        sb.append(digits(mostSigBits >> 16, 4));
        sb.append(digits(mostSigBits, 4));
        sb.append(digits(leastSigBits >> 48, 4));
        sb.append(digits(leastSigBits, 12));
        assert (sb.length() == 32);
        return sb.toString();
    }

    private static String digits(long val, int digits) {
        long hi = 1L << (digits * 4);
        return Long.toHexString(hi | (val & (hi - 1))).substring(1);
    }
}
