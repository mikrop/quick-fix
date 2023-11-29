package com.github.mikrop.quickfix;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.shelf.ShelveChangesManager;

import java.io.File;

public class FilesWorker {
    private static final Logger LOG = Logger.getInstance(FilesWorker.class);

    private FilesWorker() {
    }

    public static File getResultZip(Project project) {
        String quickFixPath = project.getBaseDir().getPresentableUrl();
        File file = ShelveChangesManager.suggestPatchName(project, "hotfix", new File(quickFixPath), null);
        if (!file.getName().endsWith(".zip")) {
            String path = file.getPath();
            if (path.lastIndexOf('.') >= 0) {
                path = path.substring(0, path.lastIndexOf('.'));
            }
            file = new File(path + ".zip");
        }
        return file;
    }
}
