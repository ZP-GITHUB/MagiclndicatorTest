package com.zpguet.connect;

import com.baidu.aip.bodyanalysis.AipBodyAnalysis;
import com.baidu.aip.http.AipRequest;
import com.baidu.aip.util.Base64Util;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Android Studio.
 * User: ZP
 * Date: 2019/6/20
 * Time: 18:53
 */
public class AipBodyAnalysisEx extends AipBodyAnalysis {
    public AipBodyAnalysisEx(String appId, String apiKey, String secretKey) {
        super(appId, apiKey, secretKey);
    }

    /*
    人流量动态分析（邀测）
    */
    public JSONObject bodyTracking(byte[] image, HashMap<String, String> options) {
        AipRequest request = new AipRequest();
        this.preOperation(request);
        String base64Content = Base64Util.encode(image);
        request.addBody("image", base64Content);
        if (options != null) {
            request.addBody(options);
        }

        request.setUri("https://aip.baidubce.com/rest/2.0/image-classify/v1/body_tracking");
        this.postOperation(request);
        return this.requestServer(request);
    }
}

