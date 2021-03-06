/*
 * Copyright 2020, GeoSolutions Sas.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
*/
package it.geosolutions.mapstore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletContext;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import it.geosolutions.mapstore.ConfigController.ResourceNotAllowedException;

public class ConfigControllerTest {
    ConfigController controller;
    
    @Before
    public void setUp() {
        controller = new ConfigController();
    }
    
    @Test
    public void testLoadAllowedResource() throws IOException {
        ServletContext context = Mockito.mock(ServletContext.class);
        File tempResource = TestUtils.copyToTemp(ConfigControllerTest.class.getResourceAsStream("/localConfig.json"));
        Mockito.when(context.getRealPath(Mockito.anyString())).thenReturn(tempResource.getAbsolutePath());
        controller.setContext(context);
        String resource = controller.loadResource("localConfig", false);
        assertEquals("{}", resource.trim());
        tempResource.delete();
    }
    
    @Test
    public void testLoadNotAllowedResource() throws IOException {
        ServletContext context = Mockito.mock(ServletContext.class);
        File tempResource = TestUtils.copyToTemp(ConfigControllerTest.class.getResourceAsStream("/localConfig.json"));
        Mockito.when(context.getRealPath(Mockito.anyString())).thenReturn(tempResource.getAbsolutePath());
        controller.setContext(context);
        try {
            controller.loadResource("notAllowed", false);
            fail();
        } catch(ResourceNotAllowedException e) {
            assertNotNull(e);
        }
        tempResource.delete();
    }
    
    @Test
    public void testLoadFromDataDir() throws IOException {
        File dataDir = TestUtils.getDataDir();
        File tempResource = TestUtils.copyTo(ConfigControllerTest.class.getResourceAsStream("/localConfig.json"), dataDir, "localConfig.json");
        controller.setDataDir(dataDir.getAbsolutePath());
        ServletContext context = Mockito.mock(ServletContext.class);
        controller.setContext(context);
        String resource = controller.loadResource("localConfig", false);
        assertEquals("{}", resource.trim());
        tempResource.delete();
    }
    
    @Test
    public void testLoadAsset() throws IOException {
    	ServletContext context = Mockito.mock(ServletContext.class);
        File tempResource = TestUtils.copyToTemp(ConfigControllerTest.class.getResourceAsStream("/bundle.js"));
        Mockito.when(context.getRealPath(Mockito.anyString())).thenReturn(tempResource.getAbsolutePath());
        controller.setContext(context);
        String resource = controller.loadAsset("localConfig");
        assertEquals("console.log('hello')", resource.trim());
        tempResource.delete();
    }
    
    @Test
    public void testLoadAssetFromDataDir() throws IOException {
    	File dataDir = TestUtils.getDataDir();
        File tempResource = TestUtils.copyTo(ConfigControllerTest.class.getResourceAsStream("/bundle.js"), dataDir, "bundle.js");
        controller.setDataDir(dataDir.getAbsolutePath());
        ServletContext context = Mockito.mock(ServletContext.class);
        controller.setContext(context);
        String resource = controller.loadAsset("bundle.js");
        assertEquals("console.log('hello')", resource.trim());
        tempResource.delete();
    }
    
    @Test
    public void testOverrides() throws IOException {
        File tempResource = TestUtils.copyToTemp(ConfigControllerTest.class.getResourceAsStream("/localConfigFull.json"));
        File tempProperties = TestUtils.copyToTemp(ConfigControllerTest.class.getResourceAsStream("/mapstore.properties"));
        ServletContext context = Mockito.mock(ServletContext.class);
        Mockito.when(context.getRealPath(Mockito.anyString())).thenReturn(tempResource.getAbsolutePath());
        controller.setContext(context);
        controller.setOverrides(tempProperties.getAbsolutePath());
        controller.setMappings("header.height=headerHeight,header.url=headerUrl");
        String resource = controller.loadResource("localConfig", true);
        assertEquals("{\"header\":{\"height\":\"200\",\"url\":\"https://mapstore2.geo-solutions.it\"}}", resource.trim());
        tempResource.delete();
    }
}
