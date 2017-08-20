package org.dontpanic.plugins.winsw;

import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertNotNull;

/**
 * winsw package test case
 * Created by stevie on 20/08/17.
 */
public class PackageMojoTest {

    static final String DEFAULT_POM_PATH = "src/test/resources/unit/winsw-maven-plugin-test/pom.xml";

    @Rule
    public MojoRule mojoRule = new MojoRule();

    @Test
    public void testTestEnvironment() throws Exception {
        // Just check that the mojo is present
        lookupMojo();
    }

    protected PackageMojo lookupMojo() throws Exception {
        File pomFile = new File(DEFAULT_POM_PATH);
        PackageMojo mojo = (PackageMojo) mojoRule.lookupMojo( "package", pomFile );

        assertNotNull( mojo );

        return mojo;
    }
}