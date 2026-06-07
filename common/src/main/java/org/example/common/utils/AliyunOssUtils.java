package org.example.common.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import org.example.common.properties.AliyunOssProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Component
public class AliyunOssUtils {

    @Autowired
    private AliyunOssProperties ossProperties;

    /**
     * 上传图片到阿里云OSS
     * @param file 前端传来的图片文件（MultipartFile）
     * @return 上传后的图片在OSS的访问URL
     */
    public String uploadImage(MultipartFile file) {
        // 1. 获取OSS配置信息
        String endpoint = ossProperties.getEndpoint();
        String accessKeyId = ossProperties.getAccessKeyId();
        String accessKeySecret = ossProperties.getAccessKeySecret();
        String bucketName = ossProperties.getBucketName();
        String fileHost = ossProperties.getFileHost();

        // 2. 生成OSS中的文件名（避免重复，使用UUID+原文件名后缀）
        String originalFilename = file.getOriginalFilename(); // 原文件名（如"test.jpg"）
        String fileName = UUID.randomUUID().toString() + originalFilename.substring(originalFilename.lastIndexOf("."));
        String key = fileHost + fileName; // 最终在OSS中的路径（如"images/xxx.jpg"）

        // 3. 初始化OSS客户端并上传
        OSS ossClient = null;
        try {
            // 创建OSS客户端
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

            // 获取文件输入流
            InputStream inputStream = file.getInputStream();

            // 设置文件元数据（可选，如Content-Type）
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(getContentType(originalFilename.substring(originalFilename.lastIndexOf("."))));

            // 上传文件到OSS
            ossClient.putObject(bucketName, key, inputStream, metadata);

            // 4. 生成访问URL（格式：https://bucketName.endpoint/key）
            return "https://" + bucketName + "." + endpoint + "/" + key;

        } catch (IOException e) {
            throw new RuntimeException("图片上传失败：" + e.getMessage());
        } finally {
            // 关闭OSS客户端（必须关闭，避免资源泄露）
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
    /**
     * 根据图片URL删除阿里云OSS中的图片
     * @param imageUrl 完整的图片访问URL（格式：https://bucketName.endpoint/key）
     * @return 删除成功返回true，否则返回false
     */
    public boolean deleteImage(String imageUrl) {
        // 1. 获取OSS配置信息
        String endpoint = ossProperties.getEndpoint();
        String accessKeyId = ossProperties.getAccessKeyId();
        String accessKeySecret = ossProperties.getAccessKeySecret();
        String bucketName = ossProperties.getBucketName();

        OSS ossClient = null;
        try {
            // 2. 从完整URL中提取key（OSS中的文件路径）
            String key = extractKeyFromUrl(imageUrl, bucketName, endpoint);

            // 3. 初始化OSS客户端并删除文件
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

            // 检查文件是否存在
            boolean exists = ossClient.doesObjectExist(bucketName, key);
            if (!exists) {
                // 文件不存在，可以记录日志或直接返回true（视业务需求）
                // logger.warn("文件不存在：" + imageUrl);
                return true; // 或者返回false
            }

            // 删除文件
            ossClient.deleteObject(bucketName, key);
            return true;

        } catch (Exception e) {
            // 记录异常日志
            // logger.error("删除图片失败：" + imageUrl, e);
            return false;
        } finally {
            // 关闭OSS客户端
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
    /**
     * 从完整的URL中提取OSS key
     * @param imageUrl 完整图片URL（格式：https://bucketName.endpoint/key）
     * @param bucketName 存储桶名称
     * @param endpoint 端点地址
     * @return OSS中的文件key
     */
    private String extractKeyFromUrl(String imageUrl, String bucketName, String endpoint) {
        try {
            // 验证URL格式
            if (imageUrl == null || imageUrl.isEmpty()) {
                throw new IllegalArgumentException("图片URL不能为空");
            }

            // 预期的URL格式：https://bucketName.endpoint/key
            String expectedPrefix = "https://" + bucketName + "." + endpoint + "/";

            if (!imageUrl.startsWith(expectedPrefix)) {
                throw new IllegalArgumentException("图片URL格式不正确，预期格式：" + expectedPrefix + "...");
            }

            // 提取key部分（去除协议、bucket和endpoint）
            return imageUrl.substring(expectedPrefix.length());
        } catch (Exception e) {
            // 如果URL格式有问题，可能需要其他解析方式
            // 尝试通用解析：获取最后一个"/"之后的部分
            String[] parts = imageUrl.split("/");
            if (parts.length > 0) {
                // 假设最后一部分是文件名，加上文件路径前缀
                String fileName = parts[parts.length - 1];
                // 这里需要根据你的实际文件路径结构来构造
                // 例如：如果文件都存储在images/目录下
                return ossProperties.getFileHost() + fileName;
            }
            throw new RuntimeException("无法从URL中提取文件key: " + imageUrl, e);
        }
    }

    /**
     * 根据文件后缀获取Content-Type（避免OSS默认下载文件）
     */
    private String getContentType(String fileSuffix) {
        if (fileSuffix.equalsIgnoreCase(".jpg") || fileSuffix.equalsIgnoreCase(".jpeg")) {
            return "image/jpeg";
        }
        if (fileSuffix.equalsIgnoreCase(".png")) {
            return "image/png";
        }
        if (fileSuffix.equalsIgnoreCase(".gif")) {
            return "image/gif";
        }
        return "application/octet-stream"; // 默认二进制流
    }
}