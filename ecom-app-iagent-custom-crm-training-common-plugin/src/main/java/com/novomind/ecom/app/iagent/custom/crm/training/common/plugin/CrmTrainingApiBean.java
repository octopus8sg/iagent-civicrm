package com.novomind.ecom.app.iagent.custom.crm.training.common.plugin;

import com.novomind.ecom.api.iagent.exception.PersistencyException;
import com.novomind.ecom.api.iagent.exception.WrongTypeException;
import com.novomind.ecom.api.iagent.model.App;
import com.novomind.ecom.api.iagent.persistence.storage.Storage;
import com.novomind.ecom.app.iagent.custom.crm.training.shared.CrmTrainingConstants;
import com.novomind.ecom.common.api.frontend.CustomBean;
import org.json.JSONObject;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

//@CustomManagedBean("CrmTrainingApiBean")
public class CrmTrainingApiBean implements CustomBean {

    @Inject
    private App app;

    @Inject
    private Logger log;

    static String SITE;
    static String USER_KEY;
    static String SITE_KEY;
    public static String CRM_LINK_FORMAT = "asdfdfdasasdf";

    public CrmTrainingApiBean(App app, Logger log) {
        this.app = app;
        this.log = log;
        CRM_LINK_FORMAT = "init";
        SITE = "init";
        if (app != null) {
            Storage config = null;
            try {
                config = app.getConfig();
            } catch (PersistencyException e) {
                SITE = "no config";
                log.warn("Failure getting config: ", e);
            }

            if (config != null) {
                try {
                    SITE = config.getString(CrmTrainingConstants.KEY_SITE);
                    USER_KEY = config.getString(CrmTrainingConstants.KEY_USER_KEY);
                    SITE_KEY = config.getString(CrmTrainingConstants.KEY_SITE_KEY);
                } catch (WrongTypeException e) {
                    SITE = "wrong type";
                    log.warn("wrong type: ", e);
                }
            } else {
                SITE = "no storage";
                log.warn("no storage");
            }
        } else {
            SITE = "no app";
            log.warn("no app");
        }
        CRM_LINK_FORMAT = "https://" +
                SITE +
                "/wp-admin/admin.php?page=CiviCRM&q=civicrm%%2Fcontact%%2Fview&reset=1&cid=%s";
    }


    public String getCrmApiLink() throws PersistencyException, WrongTypeException {
        if (this.app == null) {
            return "no app";
        }
        return CRM_LINK_FORMAT;
    }


    public String getCrmApiLinkFromPhone(String phoneNumber) throws UnsupportedEncodingException {
        String payload = String.format("{\"phone\": \"%s\", \"return\": [\"id\"]}",
                phoneNumber);
        String params = URLEncoder.encode(payload, StandardCharsets.UTF_8.toString());
        String customRestApiPath = "https://" + SITE + "/wp-json/civicrm/v3/rest";
        String customRestApiAuthorizationValue =
                String.format("key=%s&api_key=%s",
                        SITE_KEY,
                        USER_KEY);
        String customRestApiEntity = "entity=Contact"; //
        String customRestApiAction = "action=getsingle"; //
        String customRestApiJson = "json=1"; //
        String crmApiLinkFromPhone = customRestApiPath +
                '?' + customRestApiEntity +
                '&' + customRestApiAction +
                '&' + customRestApiJson +
                '&' + customRestApiAuthorizationValue +
                '&' + "json=" + params;
        return crmApiLinkFromPhone;
    }

    public String getCrmContactIdFromPhone(String inner_contact_phone) throws IOException {
        String urlString = this.getCrmApiLinkFromPhone(inner_contact_phone);
        URL url = new URL(urlString);

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(3000);
        String customRestApiAcceptHeaderValue = "application/json";
        con.setRequestProperty("Accept", customRestApiAcceptHeaderValue);

        int status = con.getResponseCode();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        String inner_contact_id;
        String strcontent = content.toString();
        var jsonObj = new JSONObject(strcontent);
        inner_contact_id = jsonObj.getString("contact_id");

        return inner_contact_id;
    }

}
