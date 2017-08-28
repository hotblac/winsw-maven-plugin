package org.dontpanic.plugins.winsw;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Build a winsw release package from the current project
 */
@Mojo( name = "package", defaultPhase = LifecyclePhase.PACKAGE )
public class PackageMojo extends AbstractMojo {

    static final String ZIP_TYPE = "zip";
    static final String DEFAULT_CLASSIFIER = "winsw";
    static final String WINSW_EXE_FILENAME = "winsw.exe";

    /**
     * The directory location for the generated zip.
     */
    @Parameter( defaultValue = "${project.build.directory}", required = true, readonly = true )
    private File outputDirectory;

    /**
     * Classifier to add to the artifact generated.
     */
    @Parameter( defaultValue = DEFAULT_CLASSIFIER, required = true, readonly = true)
    private String classifier;

    /**
     * Name of zip package file to generate
     */
    @Parameter (defaultValue = "${project.build.finalName}", required = true, readonly = true)
    private String packageName;

    /**
     * The Maven project.
     */
    @Parameter( defaultValue = "${project}", readonly = true, required = true )
    private MavenProject project;

    /**
     * The Maven project's helper.
     */
    @Component
    private MavenProjectHelper projectHelper;

    @Component
    private ArchiverManager archiverManager;

    public void execute() throws MojoExecutionException, MojoFailureException {
        File assemblyFile = new File(outputDirectory, packageName + ".zip");
        createAssemblyFile(assemblyFile);
        projectHelper.attachArtifact(project, ZIP_TYPE, classifier, assemblyFile);
    }

    private void createAssemblyFile(File file) throws MojoExecutionException {
        try {
            Archiver archiver = archiverManager.getArchiver(ZIP_TYPE);
            archiver.setDestFile(file);
            addWinsw(archiver);
            archiver.createArchive();
        } catch (NoSuchArchiverException e) {
            String message = "Cannot create archiver for type " + ZIP_TYPE;
            getLog().error(message);
            throw new MojoExecutionException(message, e);
        } catch (IOException e) {
            String message = "Failed to create file '" + file + "'";
            getLog().error(message);
            throw new MojoExecutionException(message, e);
        }
    }


    private void addWinsw(Archiver archiver) throws MojoExecutionException, IOException {

        // TODO: Get real file from plugin dependencies
        File winswFile = new File(outputDirectory, WINSW_EXE_FILENAME);
        winswFile.createNewFile();
        archiver.addFile(winswFile, WINSW_EXE_FILENAME);
    }
}
