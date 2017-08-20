package org.dontpanic.plugins.winsw;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * winsw package test case
 * Created by stevie on 20/08/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class PackageMojoTest {

    private static final String DEFAULT_FILE_NAME = "defaultFileName";
    private static final File DEFAULT_OUTPUT_DIR = new File("target/test-output/");
    private static final String DEFAULT_POM_PATH = "src/test/resources/unit/winsw-maven-plugin-test/pom.xml";

    @Rule
    public MojoRule mojoRule = new MojoRule();

    private MavenProject mavenProject = new MavenProjectStub();
    @Mock private MavenProjectHelper projectHelper;

    @Before
    public void setUp() throws Exception {
        assertFalse(DEFAULT_OUTPUT_DIR.exists());
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(DEFAULT_OUTPUT_DIR);
    }

    @Test
    public void mojo_isPresentInTestEnvironment() throws Exception {
        // Just check that the mojo is present
        lookupMojo();
    }

    @Test
    public void mojoExecute_createsZipFile() throws Exception {
        PackageMojo mojo = getMojoWithDefaultConfig();
        mojo.execute();

        File expectedNewFile = new File(DEFAULT_OUTPUT_DIR, DEFAULT_FILE_NAME + ".zip");
        assertTrue(expectedNewFile.exists());
    }

    @Test
    public void mojoExecute_createsArtifact() throws Exception {

        PackageMojo mojo = getMojoWithDefaultConfig();
        mojo.execute();

        verify(projectHelper).attachArtifact(eq(mavenProject), eq("zip"), anyString(), any(File.class));
    }

    private PackageMojo getMojoWithDefaultConfig() throws Exception {
        PackageMojo mojo = lookupMojo();
        mojoRule.setVariableValueToObject(mojo, "projectHelper", projectHelper);
        mojoRule.setVariableValueToObject(mojo, "project", mavenProject);
        mojoRule.setVariableValueToObject(mojo, "zipFile", DEFAULT_FILE_NAME);
        mojoRule.setVariableValueToObject(mojo, "outputDirectory", DEFAULT_OUTPUT_DIR);
        return mojo;
    }

    protected PackageMojo lookupMojo() throws Exception {
        File pomFile = new File(DEFAULT_POM_PATH);
        PackageMojo mojo = (PackageMojo) mojoRule.lookupMojo( "package", pomFile );
        assertNotNull( mojo );
        return mojo;
    }
}