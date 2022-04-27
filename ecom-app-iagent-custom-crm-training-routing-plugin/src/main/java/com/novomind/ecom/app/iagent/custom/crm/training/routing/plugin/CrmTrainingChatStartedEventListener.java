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
        String contactId = getContactId(logChatId);
        chatStartedEvent.getChat().getStorage().setString(CrmTrainingConstants.ISSUE_PROPERTY_CONTACT_ID, contactId);
        chatStartedEvent.getChat().getStorage().store();
        log.info("[{}] Contact id = {} stored as chat property.", logChatId, contactId);
      } else {
        log.warn("The contact id could not be stored. Reason: ChatStorage = null");
      }
    } catch (PersistencyException | WrongTypeException e) {
      log.error("[{}] Chat property could not be stored.", logChatId, e);
    }
  }

  /**
   * Gets a random contact id between 1 and 100 from the custom REST API.
   * 
   * @param logChatId
   * @return a random contact id between 1 and 100 from the REST API.
   */
  private String getContactId(String logChatId) {
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
      log.error("[{}] The contact id could not be stored.", logChatId, e);
    }

    return null;
  }

}
