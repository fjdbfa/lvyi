package org.example.common.utils;

import com.alibaba.fastjson.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Map;

@Component
public class BaiduMapUtils {

    @Value("${baidu.map.ak}")
    private String ak;

    @Value("${baidu.map.geocoding-url}")
    private String geocodingUrl;

    private final OkHttpClient client = new OkHttpClient();

    @Autowired
    private AddressParserUtil addressParserUtil;

    @Autowired
    private PoiSearcherUtil poiSearcherUtil;

    /**
     * 地址转经纬度
     * @return 经纬度数组 [longitude, latitude]
     */
    public double[] getLngLat(String address, String city) {
        // 构建请求URL（同前）
        StringBuilder urlBuilder = new StringBuilder(geocodingUrl);
        urlBuilder.append("?address=").append(address)
                .append("&output=json")
                .append("&ak=").append(ak)
                .append("&ret_coordtype=wgs84ll");

        if (city != null && !city.isEmpty()) {
            urlBuilder.append("&city=").append(city);
        }

        Request request = new Request.Builder().url(urlBuilder.toString()).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("百度API请求失败: " + response.code());
            }

            String responseBody = response.body().string();
            JSONObject json = JSONObject.parseObject(responseBody);

            if (json.getInteger("status") != 0) {
                throw new RuntimeException("地址解析失败: " + json.getString("msg"));
            }

            JSONObject location = json.getJSONObject("result").getJSONObject("location");
            double longitude = location.getDoubleValue("lng");
            double latitude = location.getDoubleValue("lat");

            return new double[]{longitude, latitude};

        } catch (IOException e) {
            throw new RuntimeException("调用百度API异常", e);
        }
    }
//    /**
//     * 精准解析地址到小区级（结合POI检索+地理编码）
//     * @param address 原始地址（如“江西省赣州市宁都县华隆首府十栋一单元2202”）
//     * @return 经纬度数组 [longitude, latitude]
//     */
    public double[] getPreciseLngLat(String address) {
        // 步骤1：地址结构化拆分
        Map<String, String> struct = addressParserUtil.parse(address);
        String district = struct.getOrDefault("district", ""); // 区县（如“宁都县”）
        String community = struct.getOrDefault("community", ""); // 小区名（如“华隆首府”）

        // 步骤2：优先调用POI检索（精准定位小区）
        if (!community.isEmpty() && !district.isEmpty()) {
            double[] poiLngLat = poiSearcherUtil.searchCommunity(community, district);
            if (poiLngLat != null) {
                // 步骤3：POI检索成功，用逆编码校验
                String reverseAddress = getAddressByLngLat(poiLngLat[1], poiLngLat[0]); // lat, lng
                if (reverseAddress.contains(community) && reverseAddress.contains(district)) {
                    return poiLngLat; // 校验通过，返回POI经纬度
                }
            }
        }

        // 步骤4：POI检索失败，fallback到基础地理编码（带结构化参数）
        return getLngLat(address, district); // 用区县作为city参数，提升精度
    }

    /**
     * 逆地理编码：经纬度转地址（用于校验）
     */
    public String getAddressByLngLat(double lat, double lng) {
        String reverseUrl = "http://api.map.baidu.com/reverse_geocoding/v3/" +
                "?location=" + lat + "," + lng +
                "&output=json" +
                "&ak=" + ak;
        // 发送请求并解析返回结果（代码类似getLngLat，略）
        // ...
        return reverseUrl;
    }
}