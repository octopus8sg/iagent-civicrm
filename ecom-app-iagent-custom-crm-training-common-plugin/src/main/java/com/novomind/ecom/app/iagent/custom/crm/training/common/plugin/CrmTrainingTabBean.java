package com.novomind.ecom.app.iagent.custom.crm.training.common.plugin;

import javax.annotation.PostConstruct;
import javax.inject.Inject;


import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.json.*;
import org.slf4j.Logger;

import com.novomind.ecom.api.iagent.exception.PersistencyException;
import com.novomind.ecom.api.iagent.exception.WrongTypeException;
import com.novomind.ecom.api.iagent.frontend.IssueViewContext;
import com.novomind.ecom.api.iagent.persistence.storage.Storage;
import com.novomind.ecom.app.iagent.custom.crm.training.shared.CrmTrainingConstants;
import com.novomind.ecom.common.api.frontend.CustomBean;
import com.novomind.ecom.common.api.frontend.CustomManagedBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@CustomManagedBean("CrmTrainingTabBean")
public class CrmTrainingTabBean implements CustomBean {

  @Inject
  private Logger           log;

  @Inject
  private IssueViewContext context;

  private String           logIssueId;

  private String           logUsername;

  private String           contactId;

  private String           contactPhone;

  @PostConstruct
  public void init() {
    loadLoggingData();
    loadContactId();
    loadContactPhone();
  }

  /**
   * Loads the issue id and the username for logging.
   */
  private void loadLoggingData() {
    if (context == null) {
      logIssueId = "null";
      logUsername = "null";
    } else {
      logIssueId = String.valueOf(context.getIssue().getId());
      logUsername = (context.getUser() != null) ? context.getUser().getUsername() : "null";
    }
  }

  /**
   * Loads the contact id from the issue storage.
   */
  private void loadContactId() {
    try {
      if (context != null && context.getIssue() != null && context.getIssue().getStorage() != null) {
        long startTime = System.currentTimeMillis();
        Storage issueStorage = context.getIssue().getStorage();
        contactId = issueStorage.getString(CrmTrainingConstants.ISSUE_PROPERTY_CONTACT_ID);
        log.info("[{}|{}] Contact id loaded in {} ms", logIssueId, logUsername, (System.currentTimeMillis() - startTime));
      } else {
        log.warn("[{}|{}] Contact id could not be loaded. Reason: context, issue or storage was null", logIssueId, logUsername);
      }
    } catch (PersistencyException | WrongTypeException e) {
      log.error("[{}|{}] Error occurred loading the contact id.", logIssueId, logUsername, e);
    }
  }

  /**
   * Saves the contact id to the issue storage.
   */
  public void saveContactId() {
    try {
      if (context != null && context.getIssue() != null && context.getIssue().getStorage() != null) {
        long startTime = System.currentTimeMillis();
        Storage issueStorage = context.getIssue().getStorage();
        issueStorage.setString(CrmTrainingConstants.ISSUE_PROPERTY_CONTACT_ID, contactId);
        issueStorage.store();
        log.info("[{}|{}] Contact id saved in {} ms", logIssueId, logUsername, (System.currentTimeMillis() - startTime));
      } else {
        log.warn("[{}|{}] Contact id could not be generated. Reason: context, issue or storage was null", logIssueId, logUsername);
      }
    } catch (PersistencyException | WrongTypeException e) {
      log.error("[{}|{}] Error occurred saving the contact id.", logIssueId, logUsername, e);
    }
  }

  /**
   * Loads the contact phone from the issue storage.
   */
  private void loadContactPhone() {
    try {
      if (context != null && context.getIssue() != null && context.getIssue().getStorage() != null) {
        long startTime = System.currentTimeMillis();
        Storage issueStorage = context.getIssue().getStorage();
        contactPhone = issueStorage.getString(CrmTrainingConstants.ISSUE_PROPERTY_CONTACT_PHONE);
        log.info("[{}|{}] Contact phone loaded in {} ms", logIssueId, logUsername, (System.currentTimeMillis() - startTime));
      } else {
        log.warn("[{}|{}] Contact phone could not be loaded. Reason: context, issue or storage was null", logIssueId, logUsername);
      }
    } catch (PersistencyException | WrongTypeException e) {
      log.error("[{}|{}] Error occurred loading the contact phone.", logIssueId, logUsername, e);
    }
  }

  /**
   * Saves the contact phone to the issue storage.
   */
  public void saveContactPhone() {
    try {
      if (context != null && context.getIssue() != null && context.getIssue().getStorage() != null) {
        long startTime = System.currentTimeMillis();
        Storage issueStorage = context.getIssue().getStorage();
        issueStorage.setString(CrmTrainingConstants.ISSUE_PROPERTY_CONTACT_PHONE, contactPhone);
        issueStorage.store();
        log.info("[{}|{}] Contact phone saved in {} ms", logIssueId, logUsername, (System.currentTimeMillis() - startTime));
      } else {
        log.warn("[{}|{}] Contact phone could not be generated. Reason: context, issue or storage was null", logIssueId, logUsername);
      }
    } catch (PersistencyException | WrongTypeException e) {
      log.error("[{}|{}] Error occurred saving the contact phone.", logIssueId, logUsername, e);
    }
  }

  /**
   * Gets the CRM link with the contact id.
   */
  public String getCrmLink() {
    return String.format(CrmTrainingConstants.CRM_LINK_FORMAT, contactId);
  }

  /**
   * Gets the CRM Api link with the contact id.
   */
  public String getCrmApiLink() throws UnsupportedEncodingException {
//    return String.format(CrmTrainingConstants.CRM_LINK_FORMAT, contactId);
    String phoneNumber = contactPhone;
    String payload = String.format("{\"phone\": \"%s\", \"return\": [\"id\"]}",
            phoneNumber);
    String params = URLEncoder.encode(payload, StandardCharsets.UTF_8.toString());
    return CrmTrainingConstants.CUSTOM_REST_API_GET_PATH +
            '?' + CrmTrainingConstants.CUSTOM_REST_API_ENTITY +
            '&' + CrmTrainingConstants.CUSTOM_REST_API_ACTION +
            '&' + CrmTrainingConstants.CUSTOM_REST_API_JSON +
            '&' + CrmTrainingConstants.CUSTOM_REST_API_AUTHORIZATION_VALUE +
            '&' + "json=" + params;
  }



  // Getters and Setters
  public String getContactId() {
    return contactId;
  }

  public String getContactPhone() {
    return contactPhone;
  }

  public void setContactId(String contactId) {
    this.contactId = contactId;
  }

  public void setContactPhone(String contactPhone) throws IOException {
    this.contactPhone = contactPhone;
    String urlString = getCrmApiLink();
    URL url = new URL(urlString);
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    con.setRequestMethod("GET");
    con.setRequestProperty(HttpHeaders.ACCEPT, CrmTrainingConstants.CUSTOM_REST_API_ACCEPT_HEADER_VALUE);
    int status = con.getResponseCode();
    BufferedReader in = new BufferedReader(
            new InputStreamReader(con.getInputStream()));
    String inputLine;
    StringBuffer content = new StringBuffer();
    while ((inputLine = in.readLine()) != null) {
      content.append(inputLine);
    }
    in.close();
//    if (content != null) {
    String strcontent = content.toString();
//    this.contactPhone = strcontent;
    try {
      var jsonObj = new JSONObject(strcontent);
      String contact_id = jsonObj.getString("contact_id");
      this.contactId = contact_id;
    } catch (JSONException e) {
      e.printStackTrace();
    }
/*
    var request = new HttpGet(urlString);
    request.addHeader(HttpHeaders.ACCEPT, CrmTrainingConstants.CUSTOM_REST_API_ACCEPT_HEADER_VALUE);
    try (CloseableHttpClient httpClient = HttpClients.createDefault();
         CloseableHttpResponse response = httpClient.execute(request)) {
      HttpEntity entity = response.getEntity();
      if (entity != null) {
        var result = new JSONObject(EntityUtils.toString(entity));
        String contact_id = result.getString("contact_id");
        this.contactId = contact_id;
      }
    } catch (IOException e) {
      log.error("[{}] The contact id could not be stored.", contactPhone, e);
    }
*/
  }

}