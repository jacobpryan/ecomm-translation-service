package com.nm.translation.service;

import com.nm.translation.client.OpenConnectorClient;
import com.nm.translation.jpa.entity.*;
import com.nm.translation.jpa.repository.*;
import com.nm.translation.client.OCS3Client;
import com.nm.translation.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class ContentDataService {
    private final OpenConnectorClient ocClient;

    @Autowired
    EcommContentRepository ecommContentRepository;


    @Autowired
    EcommAttachmentRepository ecommAttachmentRepository;

    @Autowired
    EcommErrorsRepository ecommErrorsRepository;

    @Autowired
    OCS3Client OCS3Client;

    public ContentDataService(OpenConnectorClient ocClient) {
        this.ocClient = ocClient;
    }

    /**
     * fetch attachment details from the service
     */
    public List<String> processAttachments(Long ecommContentId, String clientId) {
        List<String> attachmentFileKeys = new ArrayList<>();
        List<EcommAttachmentEntity> attachmentEntityList = ecommAttachmentRepository.findByEcommContentId(ecommContentId);
        if (attachmentEntityList != null && !attachmentEntityList.isEmpty()) {
            for (EcommAttachmentEntity attachmentEntity : attachmentEntityList) {
                if (attachmentEntity != null) {
                    /**
                     * fetch the attachment from s3 bucket
                     */
                    try {
                        byte[] attachmentBinary = OCS3Client.getInputStreamFromBucket(attachmentEntity.getFolder(), attachmentEntity.getStoredFileName());
                        if (attachmentBinary != null && attachmentBinary.length > 0) {
                            String fileKeyName = Constants.OC_FILE_KEY_ECOMM + attachmentEntity.getStoredFileName().replaceAll(" ", "");
                            if (ocClient.putConversationAttachment(attachmentBinary, clientId, fileKeyName)) {
                                attachmentFileKeys.add(fileKeyName);
                            } else {
                                throw new Exception("Error occurred convert attachment file to Binary.");
                            }
                        }
                    } catch(Exception ex) {
                        log.info("Error occurred while processing attachments:", ex);
                        EcommContentEntity ecommContentEntity = findContentData(ecommContentId);
                        EcommGrErrorsEntity grErrorsEntity = new EcommGrErrorsEntity();
                        grErrorsEntity.setEcommConetentId(ecommContentId);
                        grErrorsEntity.setSource(ecommContentEntity.getSource());
                        grErrorsEntity.setErrorMessage(Arrays.toString(ex.getStackTrace()));
                    }
                }
            }
        }

        return attachmentFileKeys;
    }

    public EcommContentEntity findContentData(Long ecommContentId) {
        Optional<EcommContentEntity> contentData = ecommContentRepository.findById(ecommContentId);
        if (contentData.isPresent()) {
            return contentData.get();

        } else {
            log.info("Content Id is not found in the content table" + ecommContentId);
            /**
             * to get the data from error table
             */
            Optional<EcommErrorsEntity> errorContentData = ecommErrorsRepository.findById(ecommContentId);
            if (errorContentData.isPresent()) {
                EcommErrorsEntity ecommErrorsEntity = errorContentData.get();
                EcommContentEntity ecommContentEntity = new EcommContentEntity();
                ecommContentEntity.setId(ecommContentId);
                ecommContentEntity.setSource(ecommErrorsEntity.getSource());
                ecommContentEntity.setCommunicationType(ecommErrorsEntity.getCommunicationType());
                ecommContentEntity.setContent(ecommErrorsEntity.getContent());

                return ecommContentEntity;
            }
        }

        return null;
    }
}

