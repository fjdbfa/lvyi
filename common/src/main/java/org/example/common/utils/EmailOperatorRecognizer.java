package org.example.common.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 邮箱运营商识别工具类
 * 支持：QQ邮箱、移动139邮箱、电信189邮箱、联通沃邮箱、其他主流邮箱
 */
public class EmailOperatorRecognizer {

    // 邮箱运营商匹配规则：key=运营商名称，value=对应的正则表达式
    private static final Map<String, Pattern> EMAIL_OPERATOR_PATTERNS;

    // 静态初始化：定义各运营商邮箱的正则规则
    static {
        EMAIL_OPERATOR_PATTERNS = new HashMap<>();

        // 1. QQ邮箱：域名包含 qq.com（含企业邮 qq.cn）
        // 匹配规则：@后为 qq.com 或 qq.cn，且用户名可包含数字/字母/下划线（符合QQ邮箱命名规范）
        EMAIL_OPERATOR_PATTERNS.put("QQ",
                Pattern.compile("^[a-zA-Z0-9_.-]+@(qq\\.com|qq\\.cn)$", Pattern.CASE_INSENSITIVE));

        // 2. 移动邮箱（139邮箱）：域名包含 139.com
        EMAIL_OPERATOR_PATTERNS.put("139",
                Pattern.compile("^[a-zA-Z0-9_.-]+@139\\.com$", Pattern.CASE_INSENSITIVE));

        // 3. 电信邮箱（189邮箱）：域名包含 189.cn
        EMAIL_OPERATOR_PATTERNS.put("189",
                Pattern.compile("^[a-zA-Z0-9_.-]+@189\\.cn$", Pattern.CASE_INSENSITIVE));

        // 4. 联通邮箱（沃邮箱）：域名包含 wo.cn 或 10010.com
        EMAIL_OPERATOR_PATTERNS.put("联通沃邮箱",
                Pattern.compile("^[a-zA-Z0-9_.-]+@(wo\\.cn|10010\\.com)$", Pattern.CASE_INSENSITIVE));

        // 5. 其他常见邮箱（可根据需求扩展）
        EMAIL_OPERATOR_PATTERNS.put("网易邮箱（163/126）",
                Pattern.compile("^[a-zA-Z0-9_.-]+@(163\\.com|126\\.com)$", Pattern.CASE_INSENSITIVE));
        EMAIL_OPERATOR_PATTERNS.put("新浪邮箱",
                Pattern.compile("^[a-zA-Z0-9_.-]+@sina\\.(com|cn)$", Pattern.CASE_INSENSITIVE));
        EMAIL_OPERATOR_PATTERNS.put("阿里云邮箱",
                Pattern.compile("^[a-zA-Z0-9_.-]+@(aliyun\\.com|alibaba\\.com)$", Pattern.CASE_INSENSITIVE));
    }

    /**
     * 验证邮箱格式是否合法（基础校验，先于运营商识别）
     * @param email 待验证的邮箱地址
     * @return true=格式合法，false=格式非法
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        // 基础邮箱格式正则：包含 @ 和 域名后缀（如 .com/.cn）
        String baseEmailRegex = "^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,}$";
        return Pattern.matches(baseEmailRegex, email.trim());
    }

    /**
     * 识别邮箱所属运营商
     * @param email 待识别的邮箱地址（需先通过 isValidEmail 校验）
     * @return 运营商名称（如 "QQ邮箱"），若未匹配则返回 "未知邮箱运营商"
     */
    public static String recognizeOperator(String email) {
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("邮箱格式非法：" + email);
        }
        String trimmedEmail = email.trim().toLowerCase(); // 转为小写，忽略大小写匹配

        // 遍历所有运营商规则，匹配则返回对应名称
        for (Map.Entry<String, Pattern> entry : EMAIL_OPERATOR_PATTERNS.entrySet()) {
            Matcher matcher = entry.getValue().matcher(trimmedEmail);
            if (matcher.matches()) {
                return entry.getKey();
            }
        }

        // 未匹配到已知运营商
        return "未知邮箱运营商";
    }

//    /**
//     * 测试方法：验证不同类型邮箱的识别结果
//     */
//    public static void main(String[] args) {
//        // 测试用例：覆盖主流邮箱类型
//        String[] testEmails = {
//                "123456@qq.com",          // QQ邮箱（标准）
//                "user_abc@qq.cn",         // QQ企业邮
//                "13800138000@139.com",    // 移动139邮箱
//                "test123@189.cn",         // 电信189邮箱
//                "wo_user@wo.cn",          // 联通沃邮箱（wo.cn）
//                "10010_user@10010.com",   // 联通沃邮箱（10010.com）
//                "user@163.com",           // 网易163邮箱
//                "abc@aliyun.com",         // 阿里云邮箱
//                "test@gmail.com",         // 谷歌邮箱（未知）
//                "invalid-email",          // 非法格式（无@）
//                "user@.com"               // 非法格式（域名无效）
//        };
//
//        // 执行测试并打印结果
//        for (String email : testEmails) {
//            System.out.printf("邮箱：%20s | 格式合法：%5b | 运营商：%s%n",
//                    email,
//                    isValidEmail(email),
//                    isValidEmail(email) ? recognizeOperator(email) : "——");
//        }
//    }
}