package com.wztc.demo.util;

import io.minio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.InputStream;

@Component
public class MinioUtil {

    @Autowired
    private MinioClient minioClient;

    /**
     * 访问地址
     */
    @Value("${minio.url}")
    private String endpoint;

    /**
     * 默认存储桶
     */
    @Value("${minio.bucketName}")
    private String bucketName;

    // 上传文件
    public String uploadFile(String objectName, InputStream inputStream, String contentType) throws Exception {
        ObjectWriteResponse objectWriteResponse =
                minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .contentType(contentType)
                            .stream(inputStream, inputStream.available(), -1)
                            .build());
        return endpoint + "/" + bucketName + "/" + objectWriteResponse.object();
    }

    // 下载文件
    public InputStream downloadFile(String objectName) throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build());
    }

    // 删除文件
    public void deleteFile(String objectName) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build());
    }




}
