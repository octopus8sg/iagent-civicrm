package com.novomind.ecom.app.iagent.custom.crm.training.common.plugin;

import java.util.Objects;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.novomind.ecom.api.iagent.frontend.chatinfo.ChatInfoTab;
import com.novomind.ecom.api.iagent.frontend.chatinfo.ChatInfoTabProvider;
import com.novomind.ecom.api.iagent.frontend.chatinfo.ChatInfoViewContext;
import com.novomind.ecom.api.iagent.plugin.ChatAgentPlugin;
import com.novomind.ecom.api.imail.agent.MailAgentPlugin;
import com.novomind.ecom.api.imail.common.frontend.mailinfo.MailInfoTab;
import com.novomind.ecom.api.imail.common.frontend.mailinfo.MailInfoTabProvider;
import com.novomind.ecom.api.imail.common.frontend.mailinfo.MailInfoViewContext;
import com.novomind.ecom.api.imail.routing.RoutingPlugin;
import com.novomind.ecom.app.iagent.custom.crm.training.shared.CrmTrainingConstants;

@RoutingPlugin
@MailAgentPlugin
@ChatAgentPlugin
public class CrmTrainingTab implements MailInfoTabProvider, ChatInfoTabProvider {

  @Inject
  private Logger log;

  @Override
  public MailInfoTab getMailInfoTab(MailInfoViewContext context) {
    if (Objects.nonNull(context)) {
      return new MailInfoTab(CrmTrainingConstants.ISSUE_INFO_TAB_NAME, CrmTrainingConstants.ISSUE_INFO_TAB_DISPLAY_NAME, context.getViewUrl(CrmTrainingConstants.ISSUE_INFO_TAB_VIEW_URL));
    } else {
      log.warn("MailInfoTab could not be displayed. Reason: context=null");
    }
    return null;
  }

  @Override
  public ChatInfoTab getChatInfoTab(ChatInfoViewContext context) {
    if (Objects.nonNull(context)) {
      return new ChatInfoTab(CrmTrainingConstants.ISSUE_INFO_TAB_NAME, CrmTrainingConstants.ISSUE_INFO_TAB_DISPLAY_NAME, context.getViewUrl(CrmTrainingConstants.ISSUE_INFO_TAB_VIEW_URL));
    } else {
      log.warn("ChatInfoTab could not be displayed. Reason: context=null");
    }
    return null;
  }

}