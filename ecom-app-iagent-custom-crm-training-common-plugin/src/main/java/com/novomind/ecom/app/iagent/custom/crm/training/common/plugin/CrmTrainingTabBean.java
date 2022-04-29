package com.novomind.ecom.app.iagent.custom.crm.training.common.plugin;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.novomind.ecom.api.iagent.exception.PersistencyException;
import com.novomind.ecom.api.iagent.exception.WrongTypeException;
import com.novomind.ecom.api.iagent.frontend.IssueViewContext;
import com.novomind.ecom.api.iagent.persistence.storage.Storage;
import com.novomind.ecom.app.iagent.custom.crm.training.shared.CrmTrainingConstants;
import com.novomind.ecom.common.api.frontend.CustomBean;
import com.novomind.ecom.common.api.frontend.CustomManagedBean;

import java.io.UnsupportedEncodingException;
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

  @PostConstruct
  public void init() {
    loadLoggingData();
    loadContactId();
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
    String phoneNumber = "(287) 415-3558";
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

  public void setContactId(String contactId) {
    this.contactId = contactId;
  }

}