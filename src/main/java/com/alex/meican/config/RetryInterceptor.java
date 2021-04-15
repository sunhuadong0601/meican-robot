package com.alex.meican.config;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

/**
 * @author sunhuadong
 * @date 2020/5/18 11:22 下午
 */
@Component
public class RetryInterceptor implements Interceptor {
    public int maxRetryCount = 3;
    private int count = 0;

    @Override
    public Response intercept(Chain chain) {
        return retry(chain);
    }

    private Response retry(Chain chain) {
        Response response = null;
        Request request = chain.request();
        try {
            response = chain.proceed(request);
            while (!response.isSuccessful() && count < maxRetryCount) {
                count++;
                response = retry(chain);
            }
        } catch (Exception e) {
            while (count < maxRetryCount) {
                count++;
                response = retry(chain);
            }
        }
        return response;
    }
}
