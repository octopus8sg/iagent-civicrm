package com.novomind.ecom.app.iagent.custom.crm.training.common.plugin;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.novomind.ecom.api.iagent.model.App;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

import com.novomind.ecom.api.iagent.exception.PersistencyException;
import com.novomind.ecom.api.iagent.exception.WrongTypeException;
import com.novomind.ecom.api.iagent.frontend.IssueViewContext;
import com.novomind.ecom.api.iagent.persistence.storage.Storage;
import com.novomind.ecom.app.iagent.custom.crm.training.shared.CrmTrainingConstants;
import com.novomind.ecom.common.api.frontend.CustomBean;
import com.novomind.ecom.common.api.frontend.CustomManagedBean;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@CustomManagedBean("CrmTrainingTabBean")
public class CrmTrainingTabBean implements CustomBean {

    @Inject
    private Logger log;

    @Inject
    private IssueViewContext context;

    @Inject
    private App app;

    private String logIssueId;

    private String logUsername;

    private String contactId;

    private String contactPhone;

    private String callingNumber;

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
                callingNumber = getFromStorage(issueStorage, CrmTrainingConstants.ISSUE_PROPERTY_CALLING_NUMBER);
                contactPhone =  getFromStorage(issueStorage, CrmTrainingConstants.ISSUE_PROPERTY_CONTACT_PHONE);
                if (contactPhone == null || contactPhone == "0") {
                    contactPhone = callingNumber;
                    if (contactPhone != null && contactPhone != "0") {
                        saveContactId();
                    }
                }
                contactId = getFromStorage(issueStorage, CrmTrainingConstants.ISSUE_PROPERTY_CONTACT_ID);
                log.info("[{}|{}] Contact id loaded in {} ms", logIssueId, logUsername, (System.currentTimeMillis() - startTime));
                if (contactId == null || contactId == "0") {
                    if (contactPhone != "0") {
//                        CrmTrainingApiBean bean  = new CrmTrainingApiBean(this.app, this.log);
//                        contactId = bean.getCrmContactIdFromPhone(contactPhone);
                        CrmTrainingApiBean bean = new CrmTrainingApiBean(this.app, this.log);
                        contactId = bean.getCrmContactIdFromPhone(contactPhone);
                        saveContactId();
                    }
                }
            } else {
                log.warn("[{}|{}] Contact id could not be loaded. Reason: context, issue or storage was null", logIssueId, logUsername);
            }
        } catch (PersistencyException |  IOException e) {
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
                issueStorage.setString(CrmTrainingConstants.ISSUE_PROPERTY_CALLING_NUMBER, callingNumber);
                issueStorage.setString(CrmTrainingConstants.ISSUE_PROPERTY_CONTACT_PHONE, contactPhone);
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
    public String getCrmLink() throws WrongTypeException, PersistencyException {
        CrmTrainingApiBean bean = new CrmTrainingApiBean(this.app, this.log);
        if (contactId.equals("") || contactId.equals("0")) {
            return bean.getCrmNewLinkFormat();
        }
        return String.format(bean.getCrmApiLink(), contactId);
    }

    // Getters and Setters
    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getContactPhone() throws PersistencyException {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {

        this.contactPhone = contactPhone;
    }

    public String getCallingNumber() throws PersistencyException {
        return callingNumber;
    }

    public void setCallingNumber(String callingNumber) {

        this.callingNumber = callingNumber;
    }

    public String getFromStorage(Storage storage, String text) {
        String myString;
        try {
            myString = storage.getString(text);
        } catch (WrongTypeException ex) {
            return "0";
        }
        return myString;
    }

}