package com.hcxc.jenkins.plugins.test;

import com.hcxc.jenkins.plugins.harbor.CredentialUtils;
import com.hcxc.jenkins.plugins.harbor.JenkinsUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.*;


public class CredentialUtilTest {

    @Test
    public void testGetHarborCredentialInfo() {
        CredentialUtils utils=new CredentialUtils();
        Map<String, String> harbor = utils.getHarborCredentialInfo();
        Assert.assertEquals(3, harbor.size());
    }


}
