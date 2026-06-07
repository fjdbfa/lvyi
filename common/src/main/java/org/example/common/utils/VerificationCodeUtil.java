package org.example.common.utils;

import java.security.SecureRandom;

public class VerificationCodeUtil {


    /**
     * 验证码生成工具类，用于生成6位包含数字和字母的随机验证码
     */


        // 验证码字符集，去掉了容易混淆的字符如0和O，1和I等
        private static final String CODE_CHARACTERS = "23456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz";

        private static final int CODE_LENGTH = 6; // 验证码长度

        private static final SecureRandom random = new SecureRandom(); // 安全的随机数生成器

        /**
         * 生成6位随机验证码
         * @return 6位随机验证码字符串
         */
        public static String generateVerificationCode() {
            StringBuilder codeBuilder = new StringBuilder(CODE_LENGTH);

            for (int i = 0; i < CODE_LENGTH; i++) {
                // 从字符集中随机选取一个字符
                int index = random.nextInt(CODE_CHARACTERS.length());
                codeBuilder.append(CODE_CHARACTERS.charAt(index));
            }

            return codeBuilder.toString();
        }

        /**
         * 生成指定长度的随机验证码
         * @param length 验证码长度
         * @return 指定长度的随机验证码字符串
         */
        public static String generateVerificationCode(int length) {
            if (length <= 0) {
                throw new IllegalArgumentException("验证码长度必须大于0");
            }

            StringBuilder codeBuilder = new StringBuilder(length);

            for (int i = 0; i < length; i++) {
                int index = random.nextInt(CODE_CHARACTERS.length());
                codeBuilder.append(CODE_CHARACTERS.charAt(index));
            }

            return codeBuilder.toString();
        }

        /**
         * 生成仅包含数字的6位验证码
         * @return 6位数字验证码
         */
        public static String generateNumericCode() {
            return generateNumericCode(6);
        }

        /**
         * 生成指定长度的数字验证码
         * @param length 验证码长度
         * @return 指定长度的数字验证码
         */
        public static String generateNumericCode(int length) {
            if (length <= 0) {
                throw new IllegalArgumentException("验证码长度必须大于0");
            }

            StringBuilder codeBuilder = new StringBuilder(length);

            for (int i = 0; i < length; i++) {
                // 生成0-9的随机数字
                int num = random.nextInt(10);
                codeBuilder.append(num);
            }

            return codeBuilder.toString();
        }


}

