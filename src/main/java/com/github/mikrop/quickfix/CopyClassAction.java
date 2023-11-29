package com.github.mikrop.quickfix;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.vcsUtil.VcsUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CopyClassAction extends AnAction {
    private static final Logger LOG = Logger.getInstance(CopyClassAction.class);

    private void copyClassFiles(Collection<Change> changes, VirtualFile root, Project project) {
        try {
            File zip = FilesWorker.getResultZip(project);
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zip));

            for (Change change : changes) {
                ContentRevision afterRevision = change.getAfterRevision();
                if (afterRevision != null) {

                    VirtualFile virtualFile = change.getVirtualFile();
                    if (virtualFile != null) {

                        FilePath file = afterRevision.getFile();
                        String relativePath = VfsUtil.getRelativePath(file.getVirtualFile(), root, '/');
                        String name = ModuleWorker.replaceSourceRoot(relativePath);

                        if (!virtualFile.isDirectory()) {
                            if ("java".equals(virtualFile.getExtension())) {

                                String path = virtualFile.getParent().getPath();
                                Matcher sourceMatcher = ModuleWorker.SOURCE_ROOT_PATTERN.matcher(path);
                                if (sourceMatcher.find()) {

                                    String pathname = sourceMatcher.replaceFirst("target/classes");
                                    File dir = new File(pathname);
                                    File[] files = dir.listFiles(new ClassnameFilter(virtualFile.getName()));
                                    if (files != null) {
                                        for (File clazz : files) {

                                            String clazzName = name.replace(file.getName(), clazz.getName());
                                            zos.putNextEntry(new ZipEntry(clazzName));
                                            Files.copy(clazz.toPath(), zos);

                                        }
                                    }
                                } else {
                                    LOG.info("Error during setting file as writable.");
                                }
                            } else {
                                zos.putNextEntry(new ZipEntry(name));
                                Files.copy(Paths.get(file.getPath()), zos);
                            }
                        }
                    }
                }
            }
            zos.close();
            NotificationWorker.displaySuccessNotification(project, zip);
        } catch (Exception e) {
            Messages.showErrorDialog(project, e.getMessage(), "Error");
        }
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
        if (project == null) return;

        Collection<Change> changes = ChangeListManager.getInstance(project).getDefaultChangeList().getChanges();

        try {
            Set<VirtualFile> vcsRoots = new HashSet<>();

            for (Change change : changes) {
                ContentRevision afterRevision = change.getAfterRevision();
                if (afterRevision != null) {
                    FilePath path = afterRevision.getFile();
                    VirtualFile vcsRoot = VcsUtil.getVcsRootFor(project, path);
                    vcsRoots.add(vcsRoot);
                }
            }
            if (!vcsRoots.isEmpty()) {
                Iterator<VirtualFile> fileIterator = vcsRoots.iterator();
                VirtualFile root = fileIterator.next();

                while (fileIterator.hasNext()) {
                    root = VfsUtil.getCommonAncestor(root, fileIterator.next());
                }

                if (root != null) {
                    this.copyClassFiles(changes, root, project);
                } else {
                    Messages.showErrorDialog(project, "No common ancestor found for changes.", "Error");
                }
            } else {
                Messages.showErrorDialog(project, "No VCS roots found.", "Error");
            }
        } catch (Exception e) {
            Messages.showErrorDialog(project, e.getMessage(), "Error");
        }
    }
}
