package com.github.mikrop.quickfix;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;

import java.io.File;

public class NotificationWorker {
    private static final String GROUP_ID = "quickfix.notifications";

    public static void displaySuccessNotification(Project project, File zip) {
        Notification notification =
                new Notification(
                        NotificationWorker.GROUP_ID,
                        "QuickFix",
                        "Upraven\u00e9 class ulo\u017eeny do souboru:<br/><a href=\"#\">" + zip.getAbsolutePath() + "</a>",
                        NotificationType.INFORMATION);
        notification.notify(project);
    }
}
