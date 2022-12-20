package ma.leet.ft_header;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "ma.leet.ft_header.AppSettingsState",
        storages = @Storage("42HeaderSettingsPlugin.xml")
)
public class AppSettingsState implements PersistentStateComponent<AppSettingsState> {

    public String fileNameRegex;
    public String login;
    public String email;

    public AppSettingsState() {
        this.fileNameRegex = "^.+\\.[ch]$";
        this.login = getLogin();
        this.email = getEmail();
    }

    public static AppSettingsState getInstance() {
        return ApplicationManager.getApplication().getService(AppSettingsState.class);
    }

    private static String getLogin() {
        if (System.getenv("LOGNAME") != null) {
            return System.getenv("LOGNAME");
        } else if (System.getenv("USER") != null) {
            return System.getenv("USER");
        } else {
            return "";
        }
    }

    private static String getEmail() {
        if (System.getenv("MAIL") != null) {
            return System.getenv("MAIL");
        } else if (System.getenv("EMAIL") != null) {
            return System.getenv("EMAIL");
        } else {
            return "";
        }
    }

    @Nullable
    @Override
    public AppSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull AppSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
