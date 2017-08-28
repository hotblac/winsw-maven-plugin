package org.dontpanic.plugins.winsw;

import org.apache.maven.artifact.Artifact;
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
import java.util.Set;

/**
 * Build a winsw release package from the current project
 */
@Mojo( name = "package", defaultPhase = LifecyclePhase.PACKAGE )
public class PackageMojo extends AbstractMojo {

    static final String ZIP_TYPE = "zip";
    static final String DEFAULT_CLASSIFIER = "winsw";
    static final String WINSW_EXE_FILENAME = "winsw.exe";
    static final String WINSW_GROUPID = "com.sun.winsw";
    static final String WINSW_ARTIFACTID = "winsw";
    static final String WINSW_CLASSIFIER = "bin";
    static final String WINSW_TYPE = "exe";

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
            throw new MojoExecutionException("Cannot create archiver for type " + ZIP_TYPE, e);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to create file '" + file + "'", e);
        }
    }


    private void addWinsw(Archiver archiver) throws MojoExecutionException, IOException {

        File winswFile = null;
        Set<Artifact> dependencies = project.getDependencyArtifacts();
        for (Artifact dependency : dependencies) {
            if (isWinSw(dependency)) {
               winswFile = dependency.getFile();
               break;
            }
        }

        if (winswFile == null) {
            throw new MojoExecutionException("Could not find winsw artifact");
        }
        archiver.addFile(winswFile, WINSW_EXE_FILENAME);
    }

    private boolean isWinSw(Artifact artifact) {
        return WINSW_GROUPID.equals(artifact.getGroupId()) &&
                WINSW_ARTIFACTID.equals(artifact.getArtifactId()) &&
                WINSW_CLASSIFIER.equals(artifact.getClassifier()) &&
                WINSW_TYPE.equals(artifact.getType());
    }
}
