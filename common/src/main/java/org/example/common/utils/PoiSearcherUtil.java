package org.example.common.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 百度地图 POI 检索工具类
 * 功能：通过小区名和区域检索精准经纬度
 */
@Component
public class PoiSearcherUtil {

    // 百度地图开发者 AK（从配置文件注入）
    @Value("${baidu.map.ak}")
    private String ak;

    // OKHttp 客户端（复用连接，提升性能）
    private final OkHttpClient client = new OkHttpClient();

    /**
     * 检索小区 POI 并返回经纬度
     * @param community 小区名称（如“华隆首府”）
     * @param district 所在区县（如“宁都县”，用于缩小范围）
     * @return 经纬度数组 [经度, 纬度]，失败返回 null
     */
    public double[] searchCommunity(String community, String district) {
        // 1. 校验参数（避免空值导致的异常）
        if (community == null || community.trim().isEmpty() ||
                district == null || district.trim().isEmpty()) {
            throw new IllegalArgumentException("小区名和区域不能为空");
        }

        try {
            // 2. 构建编码后的请求 URL（处理中文和特殊字符）
            String encodedCommunity = URLEncoder.encode(community, StandardCharsets.UTF_8.name());
            String encodedDistrict = URLEncoder.encode(district, StandardCharsets.UTF_8.name());
            String poiUrl = buildPoiUrl(encodedCommunity, encodedDistrict);

            // 3. 发送请求并解析结果
            String responseBody = executeRequest(poiUrl);
            return parsePoiResponse(responseBody);

        } catch (Exception e) {
            // 日志记录异常（建议使用日志框架如 SLF4J）
            System.err.println("POI检索失败：" + e.getMessage());
            return null;
        }
    }

    /**
     * 构建 POI 检索接口 URL
     */
    private String buildPoiUrl(String encodedCommunity, String encodedDistrict) {
        return "http://api.map.baidu.com/place/v2/search" +
                "?query=" + encodedCommunity +  // 编码后的小区名
                "&region=" + encodedDistrict +  // 编码后的区域
                "&output=json" +                // 返回格式
                "&scope=2" +                    // 详细信息（包含经纬度）
                "&filter=category:房地产|小区" +  // 过滤仅返回小区类型
                "&ak=" + ak;                    // 开发者 AK
    }

    /**
     * 执行 HTTP 请求并返回响应体
     */
    private String executeRequest(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("请求失败，状态码：" + response.code());
            }
            // 确保响应体非空
            ResponseBody body = response.body();
            if (body == null) {
                throw new IOException("响应体为空");
            }
            return body.string();
        }
    }

    /**
     * 解析 POI 响应结果，提取经纬度
     */
    private double[] parsePoiResponse(String responseBody) {
        JSONObject json = JSONObject.parseObject(responseBody);
        // 检查百度 API 状态码（0 表示成功）
        if (json.getInteger("status") != 0) {
            throw new RuntimeException("API 错误：" + json.getString("msg"));
        }

        JSONArray results = json.getJSONArray("results");
        if (results == null || results.isEmpty()) {
            throw new RuntimeException("未找到匹配的小区");
        }

        // 优先选择精确匹配的小区
        for (int i = 0; i < results.size(); i++) {
            JSONObject poi = results.getJSONObject(i);
            int precision = poi.getInteger("precision"); // 1=精确匹配
            String type = poi.getString("type");         // POI 类型

            if (precision == 1 && type.contains("小区")) {
                JSONObject location = poi.getJSONObject("location");
                return new double[]{
                        location.getDoubleValue("lng"),  // 经度
                        location.getDoubleValue("lat")   // 纬度
                };
            }
        }

        // 无精确匹配时返回第一个结果
        JSONObject firstPoi = results.getJSONObject(0);
        JSONObject location = firstPoi.getJSONObject("location");
        return new double[]{
                location.getDoubleValue("lng"),
                location.getDoubleValue("lat")
        };
    }
}
