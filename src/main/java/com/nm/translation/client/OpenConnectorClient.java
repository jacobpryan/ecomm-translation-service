package com.nm.translation.client;

import com.nm.translation.model.oc.OCRequestBody;

/**
 * Create Client Interface for Open Connector Endpoint Calls
 */
public interface OpenConnectorClient {
    String postConversationMessage(OCRequestBody requestBody, String clientId);

    boolean putConversationAttachment(byte[] attachmentBinary, String clientId, String fileKeyName);
}
