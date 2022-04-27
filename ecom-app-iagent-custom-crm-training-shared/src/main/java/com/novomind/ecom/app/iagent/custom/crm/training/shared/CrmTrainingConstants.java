package com.novomind.ecom.app.iagent.custom.crm.training.shared;

import java.util.Base64;

public class CrmTrainingConstants {

  // Issue info tab
  public static final String ISSUE_INFO_TAB_NAME                       = "custom.crm.training.issueinfotab";
  public static final String ISSUE_INFO_TAB_DISPLAY_NAME               = "CRM Training";
  public static final String ISSUE_INFO_TAB_VIEW_URL                   = "/apps/crm/training/trainingCommon.xhtml";

  // Issue property
  public static final String ISSUE_PROPERTY_CONTACT_ID                 = "crmTrainingContactId";

  // Custom REST API
  public static final String CUSTOM_REST_API_GET_CONTACT_ID_PATH       = "http://localhost:9009/rest/contacts/0123456789"; // NOSONAR
  public static final String CUSTOM_REST_API_ACCEPT_HEADR_VALUE        = "text/plain";
  public static final String CUSTOM_REST_API_AUTHORIZATION_HEADR_VALUE = String.format("%s %s", "Basic", Base64.getEncoder().encodeToString(("admin" + ":" + "admin").getBytes()));

  // CRM link
  public static final String CRM_LINK_FORMAT                           = "https://aces.socialservicesconnect.com/wp-admin/admin.php?page=CiviCRM&q=civicrm%%2Fcontact%%2Fview&reset=1&cid=%s";

  private CrmTrainingConstants() {
  }

}
