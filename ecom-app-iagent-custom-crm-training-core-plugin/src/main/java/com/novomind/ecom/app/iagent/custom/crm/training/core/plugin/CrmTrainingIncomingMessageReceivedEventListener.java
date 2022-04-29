package com.novomind.ecom.app.iagent.custom.crm.training.core.plugin;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.inject.Inject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.json.JSONObject;

import org.slf4j.Logger;

import com.novomind.ecom.api.iagent.exception.WrongTypeException;
import com.novomind.ecom.api.imail.core.CorePlugin;
import com.novomind.ecom.api.imail.core.IncomingMessageReceivedEventListener;
import com.novomind.ecom.api.imail.core.event.IncomingMessageReceivedEvent;
import com.novomind.ecom.api.imail.exception.PermanentMessagingException;
import com.novomind.ecom.api.imail.exception.TemporaryMessagingException;
import com.novomind.ecom.app.iagent.custom.crm.training.shared.CrmTrainingConstants;

@CorePlugin
public class CrmTrainingIncomingMessageReceivedEventListener implements IncomingMessageReceivedEventListener {

    @Inject
    private Logger log;

    @Override
    public void incomingMessageReceived(IncomingMessageReceivedEvent incomingMessageReceivedEvent) throws PermanentMessagingException, TemporaryMessagingException {
        if (incomingMessageReceivedEvent == null) {
            log.warn("The contact id could not be stored. Reason: incomingMessageReceivedEvent = null");
            return;
        }
        String logTicketId = String.valueOf(incomingMessageReceivedEvent.getTicketId());

        try {
            if (incomingMessageReceivedEvent.getTicketStorage() != null) {
                String phoneNumber = "(287) 415-3558";
                String contactId = getCiviCRMContactId(phoneNumber);
                incomingMessageReceivedEvent.getTicketStorage().setString(CrmTrainingConstants.ISSUE_PROPERTY_CONTACT_ID, contactId);
                log.info("[{}] Contact id = {} stored as ticket property.", logTicketId, contactId);
            } else {
                log.warn("The contact id could not be stored. Reason: TicketStorage = null");
            }
        } catch (WrongTypeException | UnsupportedEncodingException e) {
            log.error("[{}] Ticket property could not be stored.", logTicketId, e);
        }
    }

    /**
     * Gets a random contact id between 1 and 100 from the custom REST API.
     *
     * @param phoneNumber
     * @return a random contact id between 1 and 100 from the REST API.
     */
    private String getCiviCRMContactId(String phoneNumber) throws UnsupportedEncodingException {
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
