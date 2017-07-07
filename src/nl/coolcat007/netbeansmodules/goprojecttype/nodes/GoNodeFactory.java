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
package nl.coolcat007.netbeansmodules.goprojecttype.nodes;

import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeListener;
import nl.coolcat007.netbeansmodules.goprojecttype.FakeFolderFileObject;
import nl.coolcat007.netbeansmodules.goprojecttype.GoProject;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

@NodeFactory.Registration(projectType = "nl-coolccat007-netbeansmodules-goprojecttype", position = 10)
public class GoNodeFactory implements NodeFactory {

    @Override
    public NodeList<?> createNodes(Project project) {
        GoProject p = project.getLookup().lookup(GoProject.class);
        assert p != null;
        return new GoNodeList(p);
    }

    private class GoNodeList implements NodeList<Node> {

        GoProject project;

        public GoNodeList(GoProject project) {
            this.project = project;
        }

        @Override
        public List<Node> keys() {
            FileObject binFolder = project.getProjectDirectory().getParent().getParent().getParent().getParent().getFileObject("bin");
            FakeFolderFileObject newBinFolder = new FakeFolderFileObject(project, "Build", binFolder);
            FileObject pkgFolder = project.getProjectDirectory().getParent().getParent().getParent().getParent().getFileObject("pkg");
            FakeFolderFileObject newPkgFolder = new FakeFolderFileObject(project, "Packages", pkgFolder);
            FakeFolderFileObject newSourcesFolder = new FakeFolderFileObject(project, "Sources", project.getProjectDirectory());
            List<Node> result = new ArrayList<Node>();
            if (binFolder != null && project.getProjectDirectory() != null) {
                try {
                    result.add(DataObject.find(newBinFolder).getNodeDelegate());
                    result.add(DataObject.find(newSourcesFolder).getNodeDelegate());
                    result.add(DataObject.find(newPkgFolder).getNodeDelegate());
                } catch (DataObjectNotFoundException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            return result;
        }

        @Override
        public Node node(Node node) {
            return new FilterNode(node);
        }

        @Override
        public void addNotify() {
        }

        @Override
        public void removeNotify() {
        }

        @Override
        public void addChangeListener(ChangeListener cl) {
        }

        @Override
        public void removeChangeListener(ChangeListener cl) {
        }

    }

}
