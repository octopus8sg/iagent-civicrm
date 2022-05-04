package com.novomind.ecom.app.iagent.custom.crm.training.core.plugin;

import java.io.IOException;

import javax.inject.Inject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
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
        String contactId = getContactId(logTicketId);
        incomingMessageReceivedEvent.getTicketStorage().setString(CrmTrainingConstants.ISSUE_PROPERTY_CONTACT_ID, contactId);
        log.info("[{}] Contact id = {} stored as ticket property.", logTicketId, contactId);
      } else {
        log.warn("The contact id could not be stored. Reason: TicketStorage = null");
      }
    } catch (WrongTypeException e) {
      log.error("[{}] Ticket property could not be stored.", logTicketId, e);
    }
  }

  /**
   * Gets a random contact id between 1 and 100 from the custom REST API.
   * 
   * @param logTicketId
   * @return a random contact id between 1 and 100 from the custom REST API.
   */
  private String getContactId(String logTicketId) {
    HttpGet request = new HttpGet(CrmTrainingConstants.CUSTOM_REST_API_GET_CONTACT_ID_PATH);
    request.addHeader(HttpHeaders.ACCEPT, CrmTrainingConstants.CUSTOM_REST_API_ACCEPT_HEADR_VALUE);
    request.addHeader(HttpHeaders.AUTHORIZATION, CrmTrainingConstants.CUSTOM_REST_API_AUTHORIZATION_HEADR_VALUE);

    try (CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = httpClient.execute(request)) {
      HttpEntity entity = response.getEntity();
      if (entity != null) {
        return EntityUtils.toString(entity);
      }
    } catch (IOException e) {
      log.error("[{}] The contact id could not be stored.", logTicketId, e);
    }

    return null;
  }

}
