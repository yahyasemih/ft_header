package ma.leet.ft_header;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class AppSettingsConfigurable implements Configurable {

    private AppSettingsComponent mySettingsComponent;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "42 Header Settings";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return mySettingsComponent.getPreferredFocusedComponent();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        mySettingsComponent = new AppSettingsComponent();
        return mySettingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        AppSettingsState settings = AppSettingsState.getInstance();

        boolean modified = !mySettingsComponent.getFileNameText().equals(settings.fileNameRegex);
        modified |= !mySettingsComponent.getLoginText().equals(settings.login);
        modified |= !mySettingsComponent.getEmailText().equals(settings.email);

        return modified && mySettingsComponent.isValid();
    }

    @Override
    public void apply() {
        AppSettingsState settings = AppSettingsState.getInstance();
        settings.fileNameRegex = mySettingsComponent.getFileNameText();
        settings.login = mySettingsComponent.getLoginText();
        settings.email = mySettingsComponent.getEmailText();
    }

    @Override
    public void reset() {
        AppSettingsState settings = AppSettingsState.getInstance();

        mySettingsComponent.setFileNameText(settings.fileNameRegex);
        mySettingsComponent.setLoginText(settings.login);
        mySettingsComponent.setEmailText(settings.email);
    }

    @Override
    public void disposeUIResources() {
        mySettingsComponent = null;
    }

}
