package org.safehaus.kiskis.mgmt.ui.monitor.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

// TODO refactor
class HttpPost {

    private final static Logger LOG = LoggerFactory.getLogger(HttpPost.class);

    static String execute(String params) throws Exception {

        String url = "http://127.0.0.1:9200/_all/logs/_search";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("POST");

        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(params);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        LOG.info("responseCode: {}", responseCode);

        if (responseCode != 200) {
            return "";
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuffer buffer = new StringBuffer();
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            buffer.append(inputLine);
        }

        in.close();

        return buffer.toString();
    }

}
