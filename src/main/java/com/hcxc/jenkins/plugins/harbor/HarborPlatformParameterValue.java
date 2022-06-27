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

import hudson.EnvVars;
import hudson.model.Run;
import org.kohsuke.stapler.DataBoundConstructor;

import hudson.model.AbstractBuild;
import hudson.model.StringParameterValue;
import hudson.util.VariableResolver;
import org.kohsuke.stapler.export.Exported;


public class HarborPlatformParameterValue extends StringParameterValue {
    private static final long serialVersionUID = 7993744779892775177L;

    @Exported
    private String imageProject;

    @DataBoundConstructor
    public HarborPlatformParameterValue(String name, String value, String imageProject) {
        super(name, value, "");
        this.imageProject = imageProject;
    }

    @Override
    public VariableResolver<String> createVariableResolver(final AbstractBuild<?, ?> build) {
        return new VariableResolver<String>() {
            @Override
            public String resolve(String name) {
                String result = null;
                if (HarborPlatformParameterValue.this.getName().equals(name)) {
                    result = value;
                }
                return result;
            }
        };
    }

    @Override
    public void buildEnvironment(Run<?, ?> build, EnvVars env) {
        super.buildEnvironment(build, env);
        env.put("IMAGE", CredentialUtils.getCredentialUtils().getHarborDns() + "/" + getImageProject() + ":" + getValue());
        env.put(getName() + "_PROJECT", getImageProject());
        env.put("IMAGE_TAG", getValue());
    }

    public String getImageProject() {
        return imageProject;
    }
}
