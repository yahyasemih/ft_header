package ma.leet.ft_header;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ProcessFortyTwoHeaderUtils {

    private static final String DATE_REGEX = "([0-9]{4}/[0-9]{2}/[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2})";

    private static final String LOGIN_REGEX = "([a-z-]{3,9})";

    private static final String EMAIL_REGEX = "([\\w-.]+@[a-zA-Z0-9-.]*)";

    private static final String[] C_LIKE_HEADER_REGEX = {
            "^/\\* \\*{74} \\*/$",
            "^/\\* {76}\\*/$",
            "^/\\* {56}:{3} {6}:{8} {3}\\*/$",
            "^/\\* {3}([a-zA-Z_][a-zA-Z0-9_.]{0,40}) {10,48}:\\+: {6}:\\+: {4}:\\+: {3}\\*/$",
            "^/\\* {52}\\+:\\+ \\+:\\+ {9}\\+:\\+ {5}\\*/$",
            "^/\\* {3}By: " + LOGIN_REGEX + " <" + EMAIL_REGEX + "> {1,33}\\+#\\+ {2}\\+:\\+ {7}\\+#\\+ {8}\\*/$",
            "^/\\* {48}\\+#\\+#\\+#\\+#\\+#\\+ {3}\\+#\\+ {11}\\*/$",
            "^/\\* {3}Created: " + DATE_REGEX + " by " + LOGIN_REGEX + " {1,15}#\\+# {4}#\\+# {13}\\*/$",
            "^/\\* {3}Updated: " + DATE_REGEX + " by " + LOGIN_REGEX + " {1,14}### {3}#{8}\\.fr {7}\\*/$",
            "^/\\* {76}\\*/$",
            "^/\\* \\*{74} \\*/$"
    };

    private static final String[] MAKEFILE_HEADER_REGEX = {
            "^# \\*{76} #$",
            "^# {78}#$",
            "^# {57}:{3} {6}:{8} {4}#$",
            "^# {4}([a-zA-Z_][a-zA-Z0-9_.]{0,40}) {10,48}:\\+: {6}:\\+: {4}:\\+: {4}#$",
            "^# {53}\\+:\\+ \\+:\\+ {9}\\+:\\+ {6}#$",
            "^# {4}By: " + LOGIN_REGEX + " <" + EMAIL_REGEX + "> {1,33}\\+#\\+ {2}\\+:\\+ {7}\\+#\\+ {9}#$",
            "^# {49}\\+#\\+#\\+#\\+#\\+#\\+ {3}\\+#\\+ {12}#$",
            "^# {4}Created: " + DATE_REGEX + " by " + LOGIN_REGEX + " {1,15}#\\+# {4}#\\+# {14}#$",
            "^# {4}Updated: " + DATE_REGEX + " by " + LOGIN_REGEX + " {1,14}### {3}#{8}\\.fr {8}#$",
            "^# {78}#$",
            "^# \\*{76} #$"
    };

    static final Set<String> SUPPORTED_TYPES = Set.of("c", "cpp", "h", "hpp", "makefile", "java");

    static String getFileExtension(VirtualFile file) {
        if (file.getExtension() != null) {
            return file.getExtension().toLowerCase();
        } else {
            return file.getName().toLowerCase();
        }
    }

    private static String getUserName() {
        Settings settings = new Settings(PathManager.getOptionsPath());
        String userName;
        if (settings.getLogin() != null && !settings.getLogin().isEmpty()) {
            userName = settings.getLogin();
        } else if (System.getenv("USER") != null && !System.getenv("USER").isEmpty()) {
            userName = System.getenv("USER");
        } else if (System.getenv("LOGNAME") != null && !System.getenv("LOGNAME").isEmpty()) {
            userName = System.getenv("LOGNAME");
        } else {
            userName = "logname";
        }
        if (userName.length() > 9) {
            userName = userName.substring(0, 9);
        }
        return userName;
    }

    private static String getEmail() {
        Settings settings = new Settings(PathManager.getOptionsPath());
        String email;
        if (settings.getEmail() != null && !settings.getEmail().isEmpty()) {
            email = settings.getEmail();
        } else if (System.getenv("MAIL") != null && !System.getenv("MAIL").isEmpty()) {
            email = System.getenv("MAIL");
        } else if (System.getenv("EMAIL") != null && !System.getenv("EMAIL").isEmpty()) {
            email = System.getenv("EMAIL");
        } else {
            email = getUserName() + "@student.1337.ma";
        }
        if (email.length() > 30) {
            email = email.substring(0, 30);
        }
        return email;
    }

    private static String getFileName(VirtualFile file) {
        String filename = file.getName();

        if (filename.length() > 41) {
            filename = filename.substring(0, 41);
        } else {
            filename += " ".repeat(41 - filename.length());
        }
        return filename;
    }

    static String getFileNameRegex() {
        Settings settings = new Settings(PathManager.getOptionsPath());

        if (settings.getFileNameRegex() == null || settings.getFileNameRegex().isEmpty()) {
            return "([a-z_][a-z0-9_]*\\.([ch]))";
        }
        try {
            Pattern.compile(settings.getFileNameRegex());
        } catch (PatternSyntaxException e) {
            return "([a-z_][a-z0-9_]*\\.([ch]))";
        }
        return settings.getFileNameRegex();
    }

    static boolean hasHeader(VirtualFile file) {
        String type = getFileExtension(file);
        if (!SUPPORTED_TYPES.contains(type)) {
            return false;
        }
        final String[] headerRegex = type.equals("makefile") ? MAKEFILE_HEADER_REGEX : C_LIKE_HEADER_REGEX;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            int i = 0;
            while (reader.ready() && i < 11) {
                String line = reader.readLine();
                if (line.length() != 80) {
                    return false;
                }
                if (!Pattern.matches(headerRegex[i], line)) {
                    System.out.println("Error in line " + (i + 1) + ": " + line);
                    return false;
                }
                ++i;
            }
            if (i < 11) {
                return false;
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    static void updateHeader(Document document, VirtualFile file) {
        String type = getFileExtension(file);
        if (!SUPPORTED_TYPES.contains(type)) {
            return;
        }
        String userName = getUserName();
        if (userName.length() < 9) {
            userName += " ".repeat(9 - userName.length());
        }
        Path path = Paths.get(file.getPath());
        BasicFileAttributes view;
        try {
            view = Files.getFileAttributeView(path, BasicFileAttributeView.class).readAttributes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        DateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date modificationDate = new Date(view == null ? file.getTimeStamp() : view.lastModifiedTime().toMillis());
        String updated = f.format(modificationDate);
        String updatedLine;
        if (type.equals("makefile")) {
            updatedLine = "#    Updated: " + updated + " by " + userName + "        ###   ########.fr        #";
        } else {
            updatedLine = "/*   Updated: " + updated + " by " + userName + "        ###   ########.fr       */";
        }
        if (document.getTextLength() > 648 + 80) {
            document.replaceString(648, 648 + 80, updatedLine);
        }
    }

    private static String generateHeader(VirtualFile file) {
        String type = getFileExtension(file);
        if (!SUPPORTED_TYPES.contains(type)) {
            return "";
        }
        String fileName = getFileName(file);
        String userName = getUserName();
        String owner = userName + " <" + getEmail() + ">";
        if (userName.length() < 9) {
            userName += " ".repeat(9 - userName.length());
        }
        if (owner.length() < 42) {
            owner += " ".repeat(42 - owner.length());
        }
        Path path = Paths.get(file.getPath());
        BasicFileAttributes view;

        try {
            view = Files.getFileAttributeView(path, BasicFileAttributeView.class).readAttributes();
        } catch (IOException e) {
            return "";
        }
        DateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date creationDate = new Date(view == null ? file.getTimeStamp() : view.creationTime().toMillis());
        Date modificationDate = new Date(view == null ? file.getTimeStamp() : view.lastModifiedTime().toMillis());
        String created = f.format(creationDate);
        String updated = f.format(modificationDate);

        if (type.equals("makefile")) {
            return generateMakefileHeader(fileName, userName, owner, created, updated);
        } else {
            return generateCLikeHeader(fileName, userName, owner, created, updated);
        }
    }

    private static String generateCLikeHeader(String fileName, String userName, String owner, String created,
                                              String updated) {
        return "/* ************************************************************************** */\n" +
                "/*                                                                            */\n" +
                "/*                                                        :::      ::::::::   */\n" +
                "/*   " + fileName + "          :+:      :+:    :+:   */\n" +
                "/*                                                    +:+ +:+         +:+     */\n" +
                "/*   By: " + owner + " +#+  +:+       +#+        */\n" +
                "/*                                                +#+#+#+#+#+   +#+           */\n" +
                "/*   Created: " + created + " by " + userName + "         #+#    #+#             */\n" +
                "/*   Updated: " + updated + " by " + userName + "        ###   ########.fr       */\n" +
                "/*                                                                            */\n" +
                "/* ************************************************************************** */\n\n";
    }

    private static String generateMakefileHeader(String fileName, String userName, String owner, String created,
                                                 String updated) {
        return "# **************************************************************************** #\n" +
                "#                                                                              #\n" +
                "#                                                         :::      ::::::::    #\n" +
                "#    " + fileName + "          :+:      :+:    :+:    #\n" +
                "#                                                     +:+ +:+         +:+      #\n" +
                "#    By: " + owner + " +#+  +:+       +#+         #\n" +
                "#                                                 +#+#+#+#+#+   +#+            #\n" +
                "#    Created: " + created + " by " + userName + "         #+#    #+#              #\n" +
                "#    Updated: " + updated + " by " + userName + "        ###   ########.fr        #\n" +
                "#                                                                              #\n" +
                "# **************************************************************************** #\n\n";
    }

    static void putHeader(Document document, VirtualFile file) {
        String header = generateHeader(file);
        document.insertString(0, header);
    }
}
