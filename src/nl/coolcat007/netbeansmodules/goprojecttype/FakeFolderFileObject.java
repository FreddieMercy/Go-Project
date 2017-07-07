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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileSystem;

/**
 *
 * @author Rik Schaaf aka CC007 (http://coolcat007.nl/)
 */
public class FakeFolderFileObject extends FileObject {

    private final GoProject project;
    private final String folderName;
    private final FileObject underlyingFileObject;

    public FakeFolderFileObject(GoProject project, String folderName, FileObject underlyingFileObject) {
        this.project = project;
        this.folderName = folderName;
        this.underlyingFileObject = underlyingFileObject;
    }

    @Override
    public String getName() {
        return folderName;
    }

    @Override
    public String getExt() {
        return "";
    }

    @Override
    public void rename(FileLock fl, String string, String string1) throws IOException {
        throw new UnsupportedOperationException("You can't rename this");
    }

    @Override
    public FileSystem getFileSystem() throws FileStateInvalidException {
        return underlyingFileObject.getFileSystem();
    }

    @Override
    public FileObject getParent() {
        return underlyingFileObject.getParent();
    }

    @Override
    public boolean isFolder() {
        return underlyingFileObject.isFolder();
    }

    @Override
    public Date lastModified() {
        return underlyingFileObject.lastModified();
    }

    @Override
    public boolean isRoot() {
        return underlyingFileObject.isRoot();
    }

    @Override
    public boolean isData() {
        return underlyingFileObject.isData();
    }

    @Override
    public boolean isValid() {
        return underlyingFileObject.isValid();
    }

    @Override
    public void delete(FileLock fl) throws IOException {
        throw new UnsupportedOperationException("You cant delete this.");
    }

    @Override
    public Object getAttribute(String string) {
        return underlyingFileObject.getAttribute(string);
    }

    @Override
    public void setAttribute(String string, Object o) throws IOException {
        throw new UnsupportedOperationException("You can't set attributes.");
    }

    @Override
    public Enumeration<String> getAttributes() {
        return underlyingFileObject.getAttributes();
    }

    @Override
    public void addFileChangeListener(FileChangeListener fl) {
        underlyingFileObject.addFileChangeListener(fl);
    }

    @Override
    public void removeFileChangeListener(FileChangeListener fl) {
        underlyingFileObject.removeFileChangeListener(fl);
    }

    @Override
    public long getSize() {
        return underlyingFileObject.getSize();
    }

    @Override
    public InputStream getInputStream() throws FileNotFoundException {
        return underlyingFileObject.getInputStream();
    }

    @Override
    public OutputStream getOutputStream(FileLock fl) throws IOException {
        return underlyingFileObject.getOutputStream(fl);
    }

    @Override
    public FileLock lock() throws IOException {
        return underlyingFileObject.lock();
    }

    @Override
    public void setImportant(boolean bln) {
        throw new UnsupportedOperationException("You can not change the importance state.");
    }

    @Override
    public FileObject[] getChildren() {
        return underlyingFileObject.getChildren();
    }

    @Override
    public FileObject getFileObject(String string, String string1) {
        return underlyingFileObject.getFileObject(string, string1);
    }

    @Override
    public FileObject createFolder(String string) throws IOException {
        return underlyingFileObject.createFolder(string);
    }

    @Override
    public FileObject createData(String string, String string1) throws IOException {
        return underlyingFileObject.createData(string, string1);
    }

    @Override
    public boolean isReadOnly() {
        return !underlyingFileObject.canWrite();
    }

}
