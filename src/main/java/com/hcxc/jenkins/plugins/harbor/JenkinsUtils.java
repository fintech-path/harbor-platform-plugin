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

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.UsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import hudson.EnvVars;
import hudson.security.ACL;
import hudson.slaves.EnvironmentVariablesNodeProperty;
import jenkins.model.Jenkins;

import java.util.List;

public class JenkinsUtils {
    public static String getJenkinsEnvironment(String key) {

        Jenkins jenkins = Jenkins.get();

        List<EnvironmentVariablesNodeProperty> list = jenkins.getGlobalNodeProperties().getAll(EnvironmentVariablesNodeProperty.class);
        for (EnvironmentVariablesNodeProperty e : list) {
            EnvVars envs = e.getEnvVars();
            if (envs.containsKey(key)) {
                return envs.get(key, "");
            }
        }
        return null;
    }

    public static List<UsernamePasswordCredentials> getUsernamePasswordCredentials() {
        return CredentialsProvider.lookupCredentials(UsernamePasswordCredentials.class, Jenkins.get(), ACL.SYSTEM, new DomainRequirement());
    }

}
