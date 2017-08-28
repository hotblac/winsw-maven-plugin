package org.dontpanic.plugins.winsw;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.zip.ZipArchiver;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static junit.framework.TestCase.assertFalse;
import static org.dontpanic.plugins.winsw.PackageMojo.WINSW_EXE_FILENAME;
import static org.dontpanic.plugins.winsw.PackageMojo.ZIP_TYPE;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    @Mock private ArchiverManager archiverManager;

    @Before
    public void setUp() throws Exception {
        assertFalse(DEFAULT_OUTPUT_DIR.exists());
        when(archiverManager.getArchiver(ZIP_TYPE)).thenReturn(new ZipArchiver());
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
    public void zipFile_isInOutputDirectory() throws Exception {
        File outputDir = new File(DEFAULT_OUTPUT_DIR, "subdir");
        PackageMojo mojo = getMojoWithDefaultConfig();
        mojoRule.setVariableValueToObject(mojo, "outputDirectory", outputDir);
        mojo.execute();

        File expectedNewFile = new File(outputDir, DEFAULT_FILE_NAME + ".zip");
        assertTrue(expectedNewFile.exists());
    }

    @Test
    public void zipFileName_isPackageName() throws Exception {
        final String packageName = "packageName";
        PackageMojo mojo = getMojoWithDefaultConfig();
        mojoRule.setVariableValueToObject(mojo, "packageName", packageName);
        mojo.execute();

        File expectedNewFile = new File(DEFAULT_OUTPUT_DIR, packageName + ".zip");
        assertTrue(expectedNewFile.exists());
    }

    public void zipFile_containsWinSwExe() throws Exception {
        PackageMojo mojo = getMojoWithDefaultConfig();
        mojo.execute();
        File createdFile = new File(DEFAULT_OUTPUT_DIR, DEFAULT_FILE_NAME + ".zip");
        ZipFile zipFile = new ZipFile(createdFile);

        ZipEntry winswEntry = zipFile.getEntry(WINSW_EXE_FILENAME);
        assertNotNull(winswEntry);
    }

    @Test
    public void mojoExecute_createsArtifact() throws Exception {
        PackageMojo mojo = getMojoWithDefaultConfig();
        mojo.execute();
        verify(projectHelper).attachArtifact(eq(mavenProject), anyString(), anyString(), any(File.class));
    }

    @Test
    public void createdArtifactType_isZip() throws Exception {
        PackageMojo mojo = getMojoWithDefaultConfig();
        mojo.execute();
        verify(projectHelper).attachArtifact(eq(mavenProject), eq(ZIP_TYPE), anyString(), any(File.class));
    }

    @Test
    public void createdArtifactClassifier_isGivenClassifier() throws Exception {
        final String expectedClassifer = "expectedClassifier";
        PackageMojo mojo = getMojoWithDefaultConfig();
        mojoRule.setVariableValueToObject(mojo, "classifier", expectedClassifer);
        mojo.execute();
        verify(projectHelper).attachArtifact(eq(mavenProject), anyString(), eq(expectedClassifer), any(File.class));
    }

    @Test
    public void createdArtifactFile_isBuiltZip() throws Exception {
        File expectedNewFile = new File(DEFAULT_OUTPUT_DIR, DEFAULT_FILE_NAME + ".zip");
        PackageMojo mojo = getMojoWithDefaultConfig();
        mojo.execute();
        verify(projectHelper).attachArtifact(eq(mavenProject), anyString(), anyString(), eq(expectedNewFile));
    }

    private PackageMojo getMojoWithDefaultConfig() throws Exception {
        PackageMojo mojo = lookupMojo();
        mojoRule.setVariableValueToObject(mojo, "projectHelper", projectHelper);
        mojoRule.setVariableValueToObject(mojo, "archiverManager", archiverManager);
        mojoRule.setVariableValueToObject(mojo, "project", mavenProject);
        mojoRule.setVariableValueToObject(mojo, "packageName", DEFAULT_FILE_NAME);
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