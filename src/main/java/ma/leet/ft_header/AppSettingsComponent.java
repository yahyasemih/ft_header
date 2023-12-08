package ma.leet.ft_header;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class AppSettingsComponent {

    private static final String LOGIN_REGEX = "^([a-zA-Z0-9-]{3,9})?$";

    private static final String EMAIL_REGEX = "^([\\w-.]+@([\\w-]+\\.)+[a-zA-Z]{2,4})?$";

    private final JPanel myMainPanel;
    private final JBTextField fileNameText = new JBTextField();
    private final JBTextField loginText = new JBTextField();
    private final JBTextField emailText = new JBTextField();

    public AppSettingsComponent() {
        myMainPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("Filename REGEX: "), fileNameText, 1, false)
                .addLabeledComponent(new JBLabel("Login: "), loginText, 1, false)
                .addLabeledComponent(new JBLabel("Email: "), emailText, 1, false)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    public JPanel getPanel() {
        return myMainPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return fileNameText;
    }

    @NotNull
    public String getFileNameText() {
        try {
            Pattern.compile(fileNameText.getText());
            fileNameText.setBorder(BorderFactory.createLineBorder(JBColor.BLUE));
        } catch (PatternSyntaxException exception) {
            fileNameText.setBorder(BorderFactory.createLineBorder(JBColor.RED));
        }
        return fileNameText.getText();
    }

    public void setFileNameText(@NotNull String newText) {
        try {
            Pattern.compile(newText);
            fileNameText.setBorder(BorderFactory.createLineBorder(JBColor.BLUE));
        } catch (PatternSyntaxException exception) {
            fileNameText.setBorder(BorderFactory.createLineBorder(JBColor.RED));
        }
        fileNameText.setText(newText);
    }

    @NotNull
    public String getLoginText() {
        if (!Pattern.matches(LOGIN_REGEX, loginText.getText())) {
            loginText.setBorder(BorderFactory.createLineBorder(JBColor.RED));
        } else {
            loginText.setBorder(BorderFactory.createLineBorder(JBColor.BLUE));
        }
        return loginText.getText();
    }

    public void setLoginText(@NotNull String newText) {
        if (!Pattern.matches(LOGIN_REGEX, newText)) {
            loginText.setBorder(BorderFactory.createLineBorder(JBColor.RED));
        } else {
            loginText.setBorder(BorderFactory.createLineBorder(JBColor.BLUE));
        }
        loginText.setText(newText);
    }

    @NotNull
    public String getEmailText() {
        if (!Pattern.matches(EMAIL_REGEX, emailText.getText())) {
            emailText.setBorder(BorderFactory.createLineBorder(JBColor.RED));
        } else {
            emailText.setBorder(BorderFactory.createLineBorder(JBColor.BLUE));
        }
        return emailText.getText();
    }

    public void setEmailText(@NotNull String newText) {
        if (!Pattern.matches(EMAIL_REGEX, newText)) {
            emailText.setBorder(BorderFactory.createLineBorder(JBColor.RED));
        } else {
            emailText.setBorder(BorderFactory.createLineBorder(JBColor.BLUE));
        }
        emailText.setText(newText);
    }

    public boolean isValid() {
        try {
            Pattern.compile(fileNameText.getText());
        } catch (PatternSyntaxException exception) {
            return false;
        }
        return Pattern.matches(EMAIL_REGEX, emailText.getText()) && Pattern.matches(LOGIN_REGEX, loginText.getText());
    }
}
