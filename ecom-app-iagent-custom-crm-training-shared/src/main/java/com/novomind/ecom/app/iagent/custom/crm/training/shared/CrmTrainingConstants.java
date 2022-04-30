package com.novomind.ecom.app.iagent.custom.crm.training.shared;

// import java.util.Base64;

public class CrmTrainingConstants {

    // Issue info tab
    public static final String ISSUE_INFO_TAB_NAME = "custom.crm.training.issueinfotab";
    public static final String ISSUE_INFO_TAB_DISPLAY_NAME = "CRM Training";
    public static final String ISSUE_INFO_TAB_VIEW_URL = "/apps/crm/training/trainingCommon.xhtml";

    // Issue property
    public static final String ISSUE_PROPERTY_CONTACT_ID = "crmTrainingContactId";
    public static final String ISSUE_PROPERTY_CONTACT_PHONE = "crmTrainingContactPhone";

    // Custom REST API
//  public static final String CUSTOM_REST_API_GET_CONTACT_ID_PATH       = "https://demo.socialservicesconnect.com/wp-json/civicrm/v3/rest?key=JdzMQkg3QkglPG79BPdvTxwyoF9jgpV4Q9LMgWGozmU&entity=Contact&action=getsingle&json=1&api_key=8dfe562bd4d16598117c"; // NOSONAR
//  public static final String CUSTOM_REST_API_ACCEPT_HEADR_VALUE        = "application/json";
//  public static final String CUSTOM_REST_API_AUTHORIZATION_HEADR_VALUE = String.format("%s %s", "Basic", Base64.getEncoder().encodeToString(("admin" + ":" + "admin").getBytes()));
    // Custom REST API
    static final String SITE_PREFIX = "demo";
    public static final String CUSTOM_REST_API_GET_PATH =
            String.format("https://%s.socialservicesconnect.com/wp-json/civicrm/v3/rest",
                    SITE_PREFIX); //
    public static final String CUSTOM_REST_API_ENTITY = "entity=Contact"; //
    public static final String CUSTOM_REST_API_ACTION = "action=getsingle"; //
    public static final String CUSTOM_REST_API_JSON = "json=1"; //
    public static final String CUSTOM_REST_API_ACCEPT_HEADER_VALUE = "application/json";
    static final String SITE_KEY = "JdzMQkg3QkglPG79BPdvTxwyoF9jgpV4Q9LMgWGozmU";
    static final String USER_KEY = "8dfe562bd4d16598117c";
    public static final String CUSTOM_REST_API_AUTHORIZATION_VALUE =
            String.format("key=%s&api_key=%s",
                    SITE_KEY,
                    USER_KEY);

    // CRM link
    public static final String CRM_LINK_FORMAT = "https://" +
            SITE_PREFIX +
            ".socialservicesconnect.com/wp-admin/admin.php?page=CiviCRM&q=civicrm%%2Fcontact%%2Fview&reset=1&cid=%s";

    private CrmTrainingConstants() {
    }

}
