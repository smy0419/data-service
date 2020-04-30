package network.asimov.chainrpc.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import network.asimov.error.BusinessException;
import network.asimov.error.ErrorCode;
import okhttp3.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author zhangjing
 * @date 2019-09-21
 */
@Service("okHttpService")
@Slf4j
public class OkHttpService {
    @Resource(name = "okHttpClient")
    private OkHttpClient okHttpClient;

    private static final MediaType MEDIA_TYPE_JSON = MediaType.get("application/json; charset=utf-8");

    public Object post(String url, Object param, String resultKey) {
        byte[] paramBytes = JSON.toJSONBytes(param);
        RequestBody body = RequestBody.create(paramBytes, MEDIA_TYPE_JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                JSONObject responseObj = JSONObject.parseObject(responseBody);
                if (responseObj.containsKey("error") || (responseObj.containsKey("code")) && responseObj.getInteger("code") != 0) {
                    log.error("response error, url = {}, param = {}, err = {}", url, param, responseBody);
                    throw BusinessException.builder().message("third party response error").errorCode(ErrorCode.THIRD_PARTY_RESPONSE_ERROR).build();
                }
                return responseObj.get(resultKey);
            } else {
                log.error("request failed, url = {}, param = {}, http_code = {}", url, param, response.code());
                throw BusinessException.builder().message("third party request failed").errorCode(ErrorCode.THIRD_PARTY_REQUEST_FAILED).build();
            }
        } catch (IOException e) {
            log.error("request IO exception, url = {}, param = {}, err = {}", url, param, e.toString());
            throw BusinessException.builder().message("third party request IO exception").errorCode(ErrorCode.THIRD_PARTY_REQUEST_FAILED).build();
        }
    }
}
