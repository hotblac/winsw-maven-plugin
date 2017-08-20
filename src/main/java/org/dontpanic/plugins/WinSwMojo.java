package org.dontpanic.plugins;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

import java.io.File;
import java.io.IOException;

/**
 * Build a winsw release package from the current project
 * Created by stevie on 20/08/17.
 */
@Mojo( name = "package", defaultPhase = LifecyclePhase.PACKAGE )
public class WinSwMojo extends AbstractMojo {

    private static final String ZIP_TYPE = "zip";
    private static final String DEFAULT_CLASSIFIER = "winsw";

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
     * The Maven project.
     */
    @Parameter( defaultValue = "${project}", readonly = true, required = true )
    private MavenProject project;

    /**
     * The Maven project's helper.
     */
    @Component
    private MavenProjectHelper projectHelper;

    public void execute() throws MojoExecutionException, MojoFailureException {
        File assemblyFile = new File(outputDirectory, "stubWinSwAssembly.zip");
        try {
            assemblyFile.createNewFile();
            projectHelper.attachArtifact(project, ZIP_TYPE, classifier, assemblyFile);
        } catch (IOException e) {
            String message = "Failed to create file '" + assemblyFile + "'";
            getLog().error(message);
            throw new MojoExecutionException(message, e);
        }
    }
}