package org.example.common.utils;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 地址结构化解析工具类
 * 功能：将原始始地址字符串拆分为省、市、区、小区等结构化字段
 */
@Component
public class AddressParserUtil {

    /**
     * 解析原始地址为结构化字段
     * @param rawAddress 原始地址字符串（如："江西省赣州市市宁都县华隆首府十栋一单元2202"）
     * @return 结构化地址Map，包含 province/city/district/community 等字段
     */
    public Map<String, String> parse(String rawAddress) {
        if (rawAddress == null || rawAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("原始地址不能为空");
        }

        Map<String, String> addressStruct = new HashMap<>();
        String address = rawAddress.trim();

        // 解析省份（包含"省"字的前缀）
        parseProvince(address, addressStruct);

        // 解析城市（包含"市"字，且在省份之后）
        parseCity(address, addressStruct);

        // 解析区县（包含"县"或"区"，且在城市之后）
        parseDistrict(address, addressStruct);

        // 解析小区名（区县之后，门牌号之前）
        parseCommunity(address, addressStruct);

        return addressStruct;
    }

    /**
     * 解析省份（如："江西省"）
     */
    private void parseProvince(String address, Map<String, String> struct) {
        int provinceIndex = address.indexOf("省");
        if (provinceIndex > 0) {
            String province = address.substring(0, provinceIndex + 1);
            struct.put("province", province);
        }
    }

    /**
     * 解析城市（如："赣州市"）
     */
    private void parseCity(String address, Map<String, String> struct) {
        String province = struct.getOrDefault("province", "");
        int startIndex = province.isEmpty() ? 0 : address.indexOf(province) + province.length();

        int cityIndex = address.indexOf("市", startIndex);
        if (cityIndex > startIndex) {
            String city = address.substring(startIndex, cityIndex + 1);
            struct.put("city", city);
        }
    }

    /**
     * 解析区县（如："宁都县"、"西湖区"）
     */
    private void parseDistrict(String address, Map<String, String> struct) {
        String city = struct.getOrDefault("city", "");
        int startIndex = city.isEmpty() ? 0 : address.indexOf(city) + city.length();

        // 优先匹配"县"，再匹配"区"
        int districtIndex = address.indexOf("县", startIndex);
        String suffix = "县";
        if (districtIndex == -1) {
            districtIndex = address.indexOf("区", startIndex);
            suffix = "区";
        }

        if (districtIndex > startIndex) {
            String district = address.substring(startIndex, districtIndex + 1);
            struct.put("district", district);
        }
    }

    /**
     * 解析小区名（如："华隆首府"）
     * 规则：区县之后，第一个"栋"或"单元"之前的内容
     */
    private void parseCommunity(String address, Map<String, String> struct) {
        String district = struct.getOrDefault("district", "");
        if (district.isEmpty()) {
            return; // 区县解析失败，无法提取小区名
        }

        int districtEndIndex = address.indexOf(district) + district.length();
        if (districtEndIndex >= address.length()) {
            return; // 地址已结束，无小区信息
        }

        // 提取区县之后的部分
        String afterDistrict = address.substring(districtEndIndex).trim();

        // 查找"栋"或"单元"的位置，取之前的内容作为小区名
        int buildingIndex = afterDistrict.indexOf("栋");
        int unitIndex = afterDistrict.indexOf("单元");
        int endIndex = -1;

        if (buildingIndex != -1 && unitIndex != -1) {
            endIndex = Math.min(buildingIndex, unitIndex); // 取靠前的分隔符
        } else if (buildingIndex != -1) {
            endIndex = buildingIndex;
        } else if (unitIndex != -1) {
            endIndex = unitIndex;
        }

        if (endIndex != -1) {
            String community = afterDistrict.substring(0, endIndex).trim();
            if (!community.isEmpty()) {
                struct.put("community", community);
            }
        }
    }
}
