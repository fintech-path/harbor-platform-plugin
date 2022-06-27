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

import com.cloudbees.plugins.credentials.CredentialsMatcher;
import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.common.UsernamePasswordCredentials;

import java.util.HashMap;
import java.util.Map;

public class CredentialUtils {

    private static final CredentialUtils credentialUtils = new CredentialUtils();

    public CredentialUtils() {
    }

    public static CredentialUtils getCredentialUtils() {
        return credentialUtils;
    }

    public Map<String, String> getHarborCredentialInfo() {
        Map<String, String> harbor = new HashMap<>();
        harbor.put("HARBOR_DNS_NAME", JenkinsUtils.getJenkinsEnvironment("HARBOR_DNS_NAME"));
        harbor.put("HARBOR_DNS", JenkinsUtils.getJenkinsEnvironment("HARBOR_DNS"));
        harbor.put("HARBOR_CREDENTIAL_ID", JenkinsUtils.getJenkinsEnvironment("HARBOR_CREDENTIAL_ID"));
        return harbor;
    }

    public UsernamePasswordCredentials findCredentialsByCredentialsId() {
        CredentialsMatcher credentialsIdMatcher = CredentialsMatchers.withId(getHarborCredentialId());
        return CredentialsMatchers.firstOrNull(JenkinsUtils.getUsernamePasswordCredentials(), credentialsIdMatcher);
    }

    public String getHarborDnsName() {
        return getHarborCredentialInfo().get("HARBOR_DNS_NAME");
    }

    public String getHarborDns() {
        return getHarborCredentialInfo().get("HARBOR_DNS");
    }

    public String getHarborCredentialId() {
        return getHarborCredentialInfo().get("HARBOR_CREDENTIAL_ID");
    }
}
