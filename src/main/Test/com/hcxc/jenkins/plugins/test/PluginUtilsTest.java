package com.hcxc.jenkins.plugins.test;

import com.hcxc.jenkins.plugins.harbor.PluginUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class PluginUtilsTest {

    @Before
    public void setUp() {

    }

    @Test
    public void testReadFileContent() {
        File file = new File("test.json");
        Assert.assertEquals("{\"name\": \"hah\"}", PluginUtils.readFileContent(file));
    }

    @Test
    public void testWriteFile() {
        File file = new File("test.json");
        String content = "{\"name\": \"harbor\"}";
        PluginUtils.writeFile("test.json", content);
        Assert.assertEquals(content, PluginUtils.readFileContent(file));
    }

}
