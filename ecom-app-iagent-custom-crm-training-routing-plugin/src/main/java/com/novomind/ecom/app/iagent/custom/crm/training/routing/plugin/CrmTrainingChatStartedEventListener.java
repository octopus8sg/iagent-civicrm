package com.novomind.ecom.app.iagent.custom.crm.training.routing.plugin;

import java.io.IOException;

import javax.inject.Inject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;

import com.novomind.ecom.api.iagent.exception.PersistencyException;
import com.novomind.ecom.api.iagent.exception.WrongTypeException;
import com.novomind.ecom.api.iagent.routing.event.ChatStartedEvent;
import com.novomind.ecom.api.iagent.routing.workflow.ChatStartedEventListener;
import com.novomind.ecom.api.imail.routing.RoutingPlugin;
import com.novomind.ecom.app.iagent.custom.crm.training.shared.CrmTrainingConstants;

@RoutingPlugin
public class CrmTrainingChatStartedEventListener implements ChatStartedEventListener {

    @Inject
    private Logger log;

    @Override
    public void chatStarted(ChatStartedEvent chatStartedEvent) {
        if (chatStartedEvent == null) {
            log.warn("The contact id could not be stored. Reason: chatStartedEvent = null");
            return;
        }
        String logChatId = String.valueOf(chatStartedEvent.getChat().getId());

        try {
            if (chatStartedEvent.getChat().getStorage() != null) {
                String phoneNumber = "(287) 415-3558";
                String contactId = getContactId(phoneNumber);
                chatStartedEvent.getChat().getStorage().setString(CrmTrainingConstants.ISSUE_PROPERTY_CONTACT_ID, contactId);
                chatStartedEvent.getChat().getStorage().store();
                log.info("[{}] Contact id = {} stored as chat property.", logChatId, contactId);
            } else {
                log.warn("The contact id could not be stored. Reason: ChatStorage = null");
            }
        } catch (PersistencyException | WrongTypeException | UnsupportedEncodingException e) {
            log.error("[{}] Chat property could not be stored.", logChatId, e);
        }
    }

    /**
     * Gets a random contact id between 1 and 100 from the custom REST API.
     *
     * @param phoneNumber
     * @return a random contact id between 1 and 100 from the REST API.
     */
    private String getContactId(String phoneNumber) throws UnsupportedEncodingException {
        String payload = String.format("{\"phone\": \"%s\", \"return\": [\"id\"]}",
                phoneNumber);
        String params = URLEncoder.encode(payload, StandardCharsets.UTF_8.toString());
        String urlString = CrmTrainingConstants.CUSTOM_REST_API_GET_PATH +
                '?' + CrmTrainingConstants.CUSTOM_REST_API_ENTITY +
                '&' + CrmTrainingConstants.CUSTOM_REST_API_ACTION +
                '&' + CrmTrainingConstants.CUSTOM_REST_API_JSON +
                '&' + CrmTrainingConstants.CUSTOM_REST_API_AUTHORIZATION_VALUE +
                '&' + "json=" + params;
        HttpGet request = new HttpGet(urlString);
        request.addHeader(HttpHeaders.ACCEPT, CrmTrainingConstants.CUSTOM_REST_API_ACCEPT_HEADER_VALUE);
        String myAnswer = new String("");
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                JSONObject result = new JSONObject(EntityUtils.toString(entity));
                String contact_id = result.getString("contact_id");
                return contact_id;
            }
        } catch (IOException e) {
            log.error("[{}] The contact id could not be stored.", phoneNumber, e);
        }

        return null;
    }

}
