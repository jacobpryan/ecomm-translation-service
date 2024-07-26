package com.nm.translation.client;

import com.amazonaws.util.IOUtils;
import com.nm.translation.jpa.repository.EcommAttachmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.File;
import java.io.InputStream;

/**
 * Proxy for S3Client operations
 */
@Slf4j
@Service
public class OCS3Client {
    @Autowired
    private S3Client s3Client;

    @Autowired
    private EcommAttachmentRepository ecommAttachmentRepository;

    @Value("${ecomm.s3.bucket.name}")
    private String bucketName;

    public void uploadfile(final File file) throws Exception {
        try {
            final PutObjectResponse putObjectResponse = s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(file.getName())
                            .build(),
                    RequestBody.fromFile(file));
        }
        catch (Exception e){
            throw new Exception("Exception occurred due to connectivity issue with awsconfiguration",e);
        }
    }

    public byte[] getInputStreamFromBucket(String keyFolderName, String fileName) throws Exception {
        String fileKey = keyFolderName + fileName;
        try {
            InputStream attachStream = s3Client.getObject(GetObjectRequest.builder().bucket(bucketName)
                    .key(fileKey)
                    .build(), ResponseTransformer.toInputStream());

            byte[] attachmentBinary = IOUtils.toByteArray(attachStream);

            return attachmentBinary;
        } catch (Exception e) {
            throw new Exception("Issue reading from S3 ", e);
        }
    }

}
