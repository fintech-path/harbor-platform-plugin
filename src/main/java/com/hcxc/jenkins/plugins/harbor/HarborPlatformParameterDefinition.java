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

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import hudson.Extension;
import hudson.cli.CLICommand;
import hudson.model.*;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;


public class HarborPlatformParameterDefinition extends ParameterDefinition implements Comparable<HarborPlatformParameterDefinition> {

    private static final long serialVersionUID = -2946187268529865645L;

    @Override
    public int compareTo(HarborPlatformParameterDefinition o) {
        if (this == o) {
            return 0;
        }
        return 1;
    }

    @Extension
    public static class DescriptorImpl extends ParameterDescriptor {


        @Override
        public String getDisplayName() {
            return "Harbor Platform Parameter";
        }


        @Override
        public HarborPlatformParameterDefinition newInstance(StaplerRequest req, JSONObject formData)
                throws FormException {
            String name = formData.getString("name");
            String harborImageProject = formData.getString("harborImageProject");

            return new HarborPlatformParameterDefinition(name, harborImageProject);
        }
    }

    private String cacheFilePath;

    private String cache;

    private List<String> harborImages;

    private String harborImageProject;

    @DataBoundConstructor
    public HarborPlatformParameterDefinition(String name, String harborImageProject) {
        super(name);
        this.harborImageProject = harborImageProject;
    }

    public Job getParentJob() {
        Job context = null;
        List<Job> jobs = Jenkins.get().getAllItems(Job.class);

        for (Job job : jobs) {
            if (!(job instanceof TopLevelItem)) continue;

            ParametersDefinitionProperty property = (ParametersDefinitionProperty) job.getProperty(ParametersDefinitionProperty.class);

            if (property != null) {
                List<ParameterDefinition> parameterDefinitions = property.getParameterDefinitions();

                if (parameterDefinitions != null) {
                    for (ParameterDefinition pd : parameterDefinitions) {
                        if (pd instanceof HarborPlatformParameterDefinition && ((HarborPlatformParameterDefinition) pd).compareTo(this) == 0) {
                            context = job;
                            break;
                        }
                    }
                }
            }
        }

        return context;
    }

    @Override
    public ParameterValue createValue(StaplerRequest request) {
        String requestValues = request.getParameter(getName());
        if (requestValues == null) {
            return getDefaultParameterValue();
        }
        return createValue(requestValues);
    }

    @Override
    public ParameterValue createValue(CLICommand command, String value) throws IOException, InterruptedException {
        return createValue(value);
    }

    @Override
    public ParameterValue createValue(StaplerRequest request, JSONObject jO) {
        PluginUtils.writeFile(this.cacheFilePath, jO.get("value") == null ? "" : String.valueOf(jO.get("value")));
        return new HarborPlatformParameterValue(getName(),
                jO.get("value") == null ? null : String.valueOf(jO.get("value")),
                jO.get("harborImageProject") == null ? null : String.valueOf(jO.get("harborImageProject")));
    }

    public ParameterValue createValue(String requestValue) {
        return new HarborPlatformParameterValue(getName(), StringUtils.isEmpty(requestValue) ? null : requestValue, getHarborImageProject());
    }

    public String getCacheHarborImage() {
        Job job = getParentJob();
        this.cacheFilePath = job.getConfigFile().getFile().getParent() + "\\image_cache.json";
        File cacheFile = new File(this.cacheFilePath);
        if (cacheFile.exists()) {
            this.cache = PluginUtils.readFileContent(cacheFile);
            if (this.cache.length() == 0) {
                this.cache = "";
                PluginUtils.writeFile(this.cacheFilePath, this.cache);
            }
        } else {
            try {
                cacheFile.createNewFile();
                this.cache = "";
                PluginUtils.writeFile(this.cacheFilePath, this.cache);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return cache;
    }

    public String getHarborImageProject() {
        return harborImageProject;
    }

    public String getHarborImages() {
        String[] arr = harborImageProject.split("/");
        this.harborImages = PluginUtils.getImageList(arr[0], arr[1]);
        Collections.sort(this.harborImages);
        return String.join(",", this.harborImages);
    }
}

