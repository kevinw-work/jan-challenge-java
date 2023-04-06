package com.noisy.wobbler.formatter;

import java.io.InputStream;

import org.apache.commons.compress.utils.IOUtils;
import org.springframework.stereotype.Component;

import com.noisy.wobbler.formatter.FormatRequest.ComponentSection;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;

@Component
public class S3Operations {

    private MinioClient minioClient;
    
    public S3Operations() throws Exception {
        minioClient = MinioClient.builder()
                        .endpoint("http://minio-service.kevin-w.svc.cluster.local:9000")
                        // .endpoint("http://minio-kevin-w.flows-dev-cluster-7c309b11fc78649918d0c8b91bcb5925-0000.eu-gb.containers.appdomain.cloud")
                        .credentials("b6J4Q7OXiGQZrCvp", "CfQDpLK0xaKqZAsp2j1SAjKyBVEoal0F")
                        .build();
    }

    public String read(ComponentSection objectDetails) {
        String object = null;

        String bucketName = objectDetails.getBucket();
        String objectKey = objectDetails.getKey();
        System.out.println("Bucket " + bucketName + ", object " + objectKey);

        byte[] dataBytes = null;
        try (InputStream stream = minioClient.getObject(
            GetObjectArgs.builder()
            .bucket(bucketName)
            .object(objectKey)
            .build())) {
           
            dataBytes = IOUtils.toByteArray(stream);
            object = new String(dataBytes);
        } catch (Exception e) {
            System.out.println("Failed to retrieve object from bucket");
        }
    
        return object;
    }

    public void write(InputStream inputStream, String bucketName, String objectKey) {
        try {
            minioClient.putObject(
                PutObjectArgs.builder().bucket(bucketName).object(objectKey).stream(
                        inputStream, -1, 10485760)
                    .contentType("video/mp4")
                    .build());
        } catch (Exception e) {
            System.out.println("Failed to write object to bucket");
        }
    }
}
