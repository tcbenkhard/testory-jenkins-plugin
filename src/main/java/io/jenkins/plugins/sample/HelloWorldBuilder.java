package io.jenkins.plugins.sample;

import hudson.Launcher;
import hudson.Extension;
import hudson.FilePath;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.util.FormValidation;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.servlet.ServletException;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;

import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundSetter;

public class HelloWorldBuilder extends Recorder implements SimpleBuildStep {

    private static final String DEFAULT_PATH = "target\\surefire-reports";
    private final String host;
    private final String applicationId;
    private String path;

    @DataBoundConstructor
    public HelloWorldBuilder(String host, String applicationId) {
        this.host = host;
        this.applicationId = applicationId;
    }

    public String getHost() {
        return host;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getPath() {
        return path;
    }

    @DataBoundSetter
    public void setPath(String path) {
        if(path == null || path.equals("")) {
            this.path = DEFAULT_PATH;
        } else {
            this.path = path;
        }
    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {
        listener.getLogger().println(String.format("TESTORY: Uploading testresults from '%s' to '%s/applications/%s/runs'", path, host, applicationId));

        Path resultsDirectory = Paths.get(path);
        if(resultsDirectory.toFile().exists()) {
            listener.getLogger().println("Reports exist!");
        } else {
            listener.getLogger().println("Reports dont exist!");
        }

        Path currentDirectory = Paths.get(workspace.getName());
        listener.getLogger().println("Workspace directory is "+currentDirectory.toFile().getAbsolutePath());
    }

    @Symbol("greet")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        public FormValidation doCheckHost(@QueryParameter String host)
                throws IOException, ServletException {
            System.out.println(String.format("doCheckFields: { host: %s }", host));

            try {
                URL url = new URL(host);
            } catch (MalformedURLException e) {
                System.out.println("Invalid URL: " + e.getMessage());
                return FormValidation.error(e.getMessage());
            }

            return FormValidation.ok();
        }

        public FormValidation doCheckApplicationId(@QueryParameter String applicationId)
                throws IOException, ServletException {
            System.out.println(String.format("doCheckApplicationId: { applicationId: %s }", applicationId));

            try {
                Integer.parseInt(applicationId);
            } catch (NumberFormatException e) {
                System.out.println("NumberFormatException: " + e.getMessage());
                return FormValidation.error("Must be a number");
            }

            return FormValidation.ok();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Testory upload";
        }

    }

    class TestoryFileFilter implements FileFilter, Serializable {

        @Override
        public boolean accept(File file) {
            return file.getPath().startsWith(path) && file.getName().endsWith(".xml");
        }
    }

}
