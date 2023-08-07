package ma.leet.ft_header;

import com.intellij.ide.actions.SaveAllAction;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.AnActionResult;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.ex.AnActionListener;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

import static ma.leet.ft_header.ProcessFortyTwoHeaderUtils.*;

public class SaveHook implements AnActionListener {

    @Override
    public void afterActionPerformed(@NotNull AnAction action, @NotNull AnActionEvent event,
                                     @NotNull AnActionResult result) {
        AnActionListener.super.afterActionPerformed(action, event, result);
        var Log = Logger.getInstance("afterActionPerformed");
        if (action instanceof SaveAllAction) {
            VirtualFile file = event.getData(CommonDataKeys.VIRTUAL_FILE);
            Editor editor = event.getData(CommonDataKeys.EDITOR);
            Project project = event.getProject();
            String fileNameRegex = ProcessFortyTwoHeaderUtils.getFileNameRegex();

            if (file == null || !file.exists() || !file.isInLocalFileSystem() || file.isDirectory()
                    || editor == null || project == null || !Pattern.matches(fileNameRegex, file.getName())) {
                Log.info("File is not valid, either null or does not respect regex.");
                return;
            }
            Document document = editor.getDocument();
            String type = getFileExtension(file);
            if (!SUPPORTED_TYPES.contains(type)) {
                String content = "No header support for language " + type;
                Log.info(content);
                Notification notification = NotificationGroupManager.getInstance()
                        .getNotificationGroup("Unsupported File Headers")
                        .createNotification(content, NotificationType.ERROR);
                notification.notify(project);
                return;
            }
            Runnable runnable = () -> {
                if (hasHeader(file)) {
                    Log.info("Updating header in file: " + file);
                    updateHeader(document, file);
                } else {
                    Log.info("Putting header in file: " + file);
                    putHeader(document, file);
                }
                Log.info("Header successfully created/updated");
            };
            WriteCommandAction.runWriteCommandAction(project, runnable);
        }
    }
}
