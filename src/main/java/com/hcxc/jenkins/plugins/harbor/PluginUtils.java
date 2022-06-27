/*
 * Copyright 2007-2022 Home Credit Xinchi Consulting Co. Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2. *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hcxc.jenkins.plugins.harbor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cloudbees.plugins.credentials.common.UsernamePasswordCredentials;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class PluginUtils {

    private static final CredentialUtils credentialUtils = CredentialUtils.getCredentialUtils();

    public static String readFileContent(File file) {
        BufferedReader reader = null;
        StringBuffer buffer = new StringBuffer();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                buffer.append(tempStr);
            }
            reader.close();
            return buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return buffer.toString();
    }

    public static void writeFile(String path, String content) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(path));
            out.write(content);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getImageList(String project, String repository) {
        UsernamePasswordCredentials usernamePasswordCredentials = credentialUtils.findCredentialsByCredentialsId();
        String harborUserName = usernamePasswordCredentials.getUsername();
        String harborPassword = usernamePasswordCredentials.getPassword().getPlainText();

        String url = credentialUtils.getHarborDnsName() + "/api/v2.0/projects/" + project + "/repositories/" + repository + "/artifacts?page_size=100";
        String result = HttpClientUtils.getResultByHttpGet(url, harborUserName, harborPassword);
        JSONArray tagsArray = JSONObject.parseArray(result);
        List<String> resultArray = new ArrayList<>(tagsArray.size());
        for (int i = 0; i < tagsArray.size(); i++) {
            resultArray.add(tagsArray.getJSONObject(i).getJSONArray("tags").getJSONObject(0).getString("name"));
        }
        return resultArray;
    }


}
