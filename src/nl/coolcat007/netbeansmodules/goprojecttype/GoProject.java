/* 
 * The MIT License
 *
 * Copyright 2015 Rik Schaaf aka CC007 (http://coolcat007.nl/).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package nl.coolcat007.netbeansmodules.goprojecttype;

import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.openide.nodes.Node;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.CopyOperationImplementation;
import org.netbeans.spi.project.DeleteOperationImplementation;
import org.netbeans.spi.project.MoveOrRenameOperationImplementation;
import org.netbeans.spi.project.ProjectState;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.netbeans.spi.project.ui.support.DefaultProjectOperations;
import org.netbeans.spi.project.ui.support.NodeFactorySupport;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

public class GoProject implements Project {

    private final FileObject projectDir;
    private final ProjectState state;
    private Lookup lookup;

    GoProject(FileObject dir, ProjectState state) {
        this.projectDir = dir;
        this.state = state;
    }

    @Override
    public FileObject getProjectDirectory() {
        return projectDir;
    }

    @Override
    public Lookup getLookup() {
        if (lookup == null) {
            lookup = Lookups.fixed(new Object[]{
                this,
                new GoProjectInformation(),
                new GoProjectLogicalView(this),
                new GoActionProvider(),
                new GoProjectMoveOrRenameOperation(),
                new GoProjectCopyOperation(),
                new GoProjectDeleteOperation(this)
            });
        }
        return lookup;
    }

    private final class GoProjectInformation implements ProjectInformation {

        @StaticResource()
        public static final String GO_ICON = "nl/coolcat007/netbeansmodules/goprojecttype/icon.png";

        @Override
        public Icon getIcon() {
            return new ImageIcon(ImageUtilities.loadImage(GO_ICON));
        }

        @Override
        public String getName() {
            return getProjectDirectory().getName();
        }

        @Override
        public String getDisplayName() {
            return getName();
        }

        @Override
        public void addPropertyChangeListener(PropertyChangeListener pcl) {
            //do nothing, won't change
        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener pcl) {
            //do nothing, won't change
        }

        @Override
        public Project getProject() {
            return GoProject.this;
        }

    }

    class GoProjectLogicalView implements LogicalViewProvider {

        @StaticResource()
        public static final String GO_ICON = "nl/coolcat007/netbeansmodules/goprojecttype/icon.png";

        private final GoProject project;

        public GoProjectLogicalView(GoProject project) {
            this.project = project;
        }

        @Override
        public Node createLogicalView() {
            try {
                //Obtain the project directory's node:
                FileObject projectDirectory = project.getProjectDirectory();
                DataFolder projectFolder = DataFolder.findFolder(projectDirectory);
                Node nodeOfProjectFolder = projectFolder.getNodeDelegate();
                //Decorate the project directory's node:
                return new ProjectNode(nodeOfProjectFolder, project);
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
                //Fallback-the directory couldn't be created -
                //read-only filesystem or something evil happened
                return new AbstractNode(Children.LEAF);
            }
        }

        private final class ProjectNode extends FilterNode {

            final GoProject project;

            public ProjectNode(Node node, GoProject project) throws DataObjectNotFoundException {
                super(node,
                        NodeFactorySupport.createCompositeChildren(
                                project,
                                "Projects/nl-coolccat007-netbeansmodules-goprojecttype/Nodes"),
                        new ProxyLookup(
                                new Lookup[]{
                                    Lookups.singleton(project),
                                    node.getLookup()
                                }));
                this.project = project;
            }

            @Override
            public Action[] getActions(boolean arg0) {
                return new Action[]{
                    CommonProjectActions.newFileAction(),
                    CommonProjectActions.closeProjectAction(),
                    CommonProjectActions.copyProjectAction(),
                    CommonProjectActions.moveProjectAction(),
                    CommonProjectActions.renameProjectAction(),
                    CommonProjectActions.deleteProjectAction()
                };
            }

            @Override
            public Image getIcon(int type) {
                return ImageUtilities.loadImage(GO_ICON);
            }

            @Override
            public Image getOpenedIcon(int type) {
                return getIcon(type);
            }

            @Override
            public String getDisplayName() {
                return project.getProjectDirectory().getName();
            }

        }

        @Override
        public Node findPath(Node root, Object target) {
            //leave unimplemented for now
            return null;
        }

    }

    class GoActionProvider implements ActionProvider {

        @Override
        public String[] getSupportedActions() {
            return new String[]{
                ActionProvider.COMMAND_RENAME,
                ActionProvider.COMMAND_MOVE,
                ActionProvider.COMMAND_COPY,
                ActionProvider.COMMAND_DELETE,
                ActionProvider.COMMAND_RUN,
                ActionProvider.COMMAND_BUILD,
                ActionProvider.COMMAND_TEST
            };
        }

        @Override
        public void invokeAction(String string, Lookup lkp) throws IllegalArgumentException {
            if (string.equalsIgnoreCase(ActionProvider.COMMAND_RENAME)) {
                DefaultProjectOperations.performDefaultRenameOperation(GoProject.this, "");
            }
            if (string.equalsIgnoreCase(ActionProvider.COMMAND_MOVE)) {
                DefaultProjectOperations.performDefaultMoveOperation(
                        GoProject.this);
            }
            if (string.equalsIgnoreCase(ActionProvider.COMMAND_COPY)) {
                DefaultProjectOperations.performDefaultCopyOperation(
                        GoProject.this);
            }
            if (string.equalsIgnoreCase(ActionProvider.COMMAND_DELETE)) {
                DefaultProjectOperations.performDefaultDeleteOperation(
                        GoProject.this);
            }
            if (string.equalsIgnoreCase(ActionProvider.COMMAND_RUN)) {
                String projectName = GoProject.this.getProjectDirectory().getName();
                String goPath = System.getenv("GOPATH");
                runCommand("\"" + goPath + File.separator + "bin" + File.separator + projectName + "\"", projectName + " (run)");
            }
            if (string.equalsIgnoreCase(ActionProvider.COMMAND_BUILD)) {
                Path buildPath = Paths.get(GoProject.this.getProjectDirectory().getPath());
                String projectName = buildPath.getFileName().toString();
                String author = buildPath.getParent().getFileName().toString();
                String repository = buildPath.getParent().getParent().getFileName().toString();
                String outputName = repository + File.separator + author + File.separator + projectName + " (build)";
                String command = "go install " + repository + File.separator + author + File.separator + projectName;
                runCommand(command, outputName);
            }
            if (string.equalsIgnoreCase(ActionProvider.COMMAND_TEST)) {
                Path buildPath = Paths.get(GoProject.this.getProjectDirectory().getPath());
                String projectName = buildPath.getFileName().toString();
                String author = buildPath.getParent().getFileName().toString();
                String repository = buildPath.getParent().getParent().getFileName().toString();
                String outputName = repository + File.separator + author + File.separator + projectName + " (test)";
                String command = "go test " + repository + File.separator + author + File.separator + projectName;
                runCommand(command, outputName);
            }
        }

        @Override
        public boolean isActionEnabled(String command, Lookup lookup) throws IllegalArgumentException {
            switch (command) {
                case COMMAND_RENAME:
                    return true;
                case COMMAND_MOVE:
                    return true;
                case COMMAND_COPY:
                    return true;
                case COMMAND_DELETE:
                    return true;
                case COMMAND_RUN:
                    String projectName = GoProject.this.getProjectDirectory().getName();
                    String goPath = System.getenv("GOPATH");
                    File executable = new File(goPath + File.separator + "bin" + File.separator + projectName + ".exe");
                    return executable.exists();
                case COMMAND_BUILD:
                    return true;
                case COMMAND_TEST:
                    return true;
            }
            return false;
        }

        private void runCommand(final String command, final String outputName) {
            Thread t = new Thread() {

                @Override
                public void run() {
                    try {
                        InputOutput io = IOProvider.getDefault().getIO(outputName, false);
                        io.closeInputOutput();
                        io = IOProvider.getDefault().getIO(outputName, true);
                        Runtime rt = Runtime.getRuntime();
                        Process proc = rt.exec(command);

                        // any error message?
                        StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), OutputType.ERROR, io);

                        // any output?
                        StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), OutputType.OUTPUT, io);

                        // kick them off
                        errorGobbler.start();
                        outputGobbler.start();

                        // any error???
                        int exitVal = proc.waitFor();
                        io.getOut().println("ExitValue: " + exitVal);
                        io.getOut().close();
                        io.getErr().close();
                        io.select();
                    } catch (IOException | InterruptedException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            };
            t.start();
        }
    }

    private final class GoProjectMoveOrRenameOperation implements MoveOrRenameOperationImplementation {

        @Override
        public List<FileObject> getMetadataFiles() {
            return new ArrayList<>();
        }

        @Override
        public List<FileObject> getDataFiles() {
            return new ArrayList<>();
        }

        @Override
        public void notifyMoving() throws IOException {
        }

        @Override
        public void notifyMoved(Project prjct, File file, String string) throws IOException {
        }

        @Override
        public void notifyRenaming() throws IOException {
        }

        @Override
        public void notifyRenamed(String string) throws IOException {
        }

    }

    private final class GoProjectCopyOperation implements CopyOperationImplementation {

        @Override
        public List<FileObject> getMetadataFiles() {
            return new ArrayList<>();
        }

        @Override
        public List<FileObject> getDataFiles() {
            return new ArrayList<>();
        }

        @Override
        public void notifyCopying() throws IOException {
        }

        @Override
        public void notifyCopied(Project prjct, File file, String string) throws IOException {
        }
    }

    private final class GoProjectDeleteOperation implements DeleteOperationImplementation {

        private final GoProject project;

        private GoProjectDeleteOperation(GoProject project) {
            this.project = project;
        }

        @Override
        public List<FileObject> getDataFiles() {
            List<FileObject> files = new ArrayList<>();
            FileObject[] projectChildren = project.getProjectDirectory().getChildren();
            for (FileObject fileObject : projectChildren) {
                addFile(project.getProjectDirectory(), fileObject.getNameExt(), files);
            }
            return files;
        }

        private void addFile(FileObject projectDirectory, String fileName, List<FileObject> result) {
            FileObject file = projectDirectory.getFileObject(fileName);
            if (file != null) {
                result.add(file);
            }
        }

        @Override
        public List<FileObject> getMetadataFiles() {
            return new ArrayList<>();
        }

        @Override
        public void notifyDeleting() throws IOException {
        }

        @Override
        public void notifyDeleted() throws IOException {
        }
    }
}
