package ma.leet.ft_header;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.AnActionResult;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.ex.AnActionListener;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import static ma.leet.ft_header.ProcessFortyTwoHeaderUtils.*;

public class SaveHook implements AnActionListener {

    @Override
    public void afterActionPerformed(@NotNull AnAction action, @NotNull AnActionEvent event, @NotNull AnActionResult result) {
        AnActionListener.super.afterActionPerformed(action, event, result);
        if (action.getTemplatePresentation().getText().equals("Save All")) {
            VirtualFile file = event.getData(CommonDataKeys.VIRTUAL_FILE);
            Editor editor = event.getData(CommonDataKeys.EDITOR);
            Project project = event.getProject();

            if (file == null || !file.exists() || !file.isInLocalFileSystem() || file.isDirectory()
                    || editor == null || project == null) {
                return;
            }
            String extension = file.getExtension();
            // TODO: make it regex if possible (by plugin settings)
            if (extension == null || (!extension.equals("c") && !extension.equals("h"))) {
                return;
            }
            Document document = editor.getDocument();
            Runnable runnable = ()-> {
                if (hasHeader(file)) {
                    System.out.println("Updating header in file: " + file);
                    updateHeader(document, file);
                } else {
                    System.out.println("Putting header in file: " + file);
                    putHeader(document, file);
                }
            };
            WriteCommandAction.runWriteCommandAction(project, runnable);
        }
    }
}
