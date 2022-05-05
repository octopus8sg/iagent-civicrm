package com.novomind.ecom.app.iagent.custom.crm.training.routing.plugin;


import javax.inject.Inject;


import com.novomind.ecom.api.iagent.model.App;
import org.slf4j.Logger;

import com.novomind.ecom.api.iagent.exception.PersistencyException;
import com.novomind.ecom.api.iagent.exception.WrongTypeException;
import com.novomind.ecom.api.iagent.routing.event.ChatStartedEvent;
import com.novomind.ecom.api.iagent.routing.workflow.ChatStartedEventListener;
import com.novomind.ecom.api.imail.routing.RoutingPlugin;
import com.novomind.ecom.app.iagent.custom.crm.training.shared.CrmTrainingConstants;
import com.novomind.ecom.app.iagent.custom.crm.training.common.plugin.CrmTrainingApiBean;

import java.io.IOException;

@RoutingPlugin
public class CrmTrainingChatStartedEventListener implements ChatStartedEventListener {

    @Inject
    private Logger log;

    @Inject
    private App app;

    @Override
    public void chatStarted(ChatStartedEvent chatStartedEvent) {
        if (chatStartedEvent == null) {
            log.warn("The contact id could not be stored. Reason: chatStartedEvent = null");
            return;
        }
        String logChatId = String.valueOf(chatStartedEvent.getChat().getId());

        try {

            if (chatStartedEvent.getChat().getStorage() != null) {
                String callingNumber = chatStartedEvent.getChat().getStorage().getString(CrmTrainingConstants.ISSUE_PROPERTY_CALLING_NUMBER);
                chatStartedEvent.getChat().getStorage().setString(CrmTrainingConstants.ISSUE_PROPERTY_CONTACT_PHONE, callingNumber);
                if (callingNumber != null) {
                    if (app != null) {
                        CrmTrainingApiBean crmTrainingApiBean = new CrmTrainingApiBean(app, log);
                        String contactId = crmTrainingApiBean.getCrmContactIdFromPhone(callingNumber);
                        if (contactId != null) {
                            chatStartedEvent.getChat().getStorage().setString(CrmTrainingConstants.ISSUE_PROPERTY_CONTACT_ID, contactId);
                        } else {
                            log.info("[{}] Contact Phone {} has no contact id .", logChatId, callingNumber);
                        }
                        log.info("[{}] Contact id = {} stored as ticket property.", logChatId, contactId);
                    } else {
                        log.info("[{}] No App in Chat.", logChatId);
                    }
                } else {
                    log.info("[{}] No Contact Phone in ticket.", logChatId);
                }
            } else {
                log.warn("The contact id could not be stored. Reason: ChatStorage = null");
            }
        } catch (PersistencyException | WrongTypeException | IOException e) {
            log.error("[{}] Chat property could not be stored.", logChatId, e);
        }
    }


}
