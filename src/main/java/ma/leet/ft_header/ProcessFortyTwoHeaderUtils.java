package ma.leet.ft_header;

import com.intellij.openapi.editor.*;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class ProcessFortyTwoHeaderUtils {

    private static final String DATE_REGEX = "([0-9]{4}/[0-9]{2}/[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2})";

    private static final String LOGIN_REGEX = "([\\w-.]{3,10})";

    private static final String EMAIL_REGEX = "([\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4})";

    private static final String[] HEADER_REGEX = {
        "^/\\* \\*{74} \\*/$",
        "^/\\* {76}\\*/$",
        "^/\\* {56}:{3} {6}:{8} {3}\\*/$",
        "^/\\* {3}([a-z_][a-z0-9_]{0,38}\\.([ch])) {10,48}:\\+: {6}:\\+: {4}:\\+: {3}\\*/$",
        "^/\\* {52}\\+:\\+ \\+:\\+ {9}\\+:\\+ {5}\\*/$",
        "^/\\* {3}By: " + LOGIN_REGEX + " <" + EMAIL_REGEX + "> {1,8}\\+#\\+ {2}\\+:\\+ {7}\\+#\\+ {8}\\*/$",
        "^/\\* {48}\\+#\\+#\\+#\\+#\\+#\\+ {3}\\+#\\+ {11}\\*/$",
        "^/\\* {3}Created: " + DATE_REGEX + " by " + LOGIN_REGEX + " {1,10}#\\+# {4}#\\+# {13}\\*/$",
        "^/\\* {3}Updated: " + DATE_REGEX + " by " + LOGIN_REGEX + " {1,9}### {3}#{8}\\.fr {7}\\*/$",
        "^/\\* {76}\\*/$",
        "^/\\* \\*{74} \\*/$"
    };

    private static String getUserName() {
        String userName;
        if (System.getenv("USER") != null) {
            userName = System.getenv("USER");
        } else if (System.getenv("LOGNAME") != null) {
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
        String email;
        if (System.getenv("MAIL") != null) {
            email = System.getenv("MAIL");
        } else if (System.getenv("EMAIL") != null) {
            email = System.getenv("EMAIL");
        } else {
            email = getUserName() + "@student.1337.ma";
        }
        if (email.length() > 25) {
            email = email.substring(0, 25);
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

    static boolean hasHeader(VirtualFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            int i = 0;
            while (reader.ready() && i < 11) {
                String line = reader.readLine();
                if (line.length() != 80 || !line.startsWith("/*") || !line.endsWith("*/")) {
                    return false;
                }
                if (!Pattern.matches(HEADER_REGEX[i], line)) {
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
        StringBuilder builder = new StringBuilder();
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
        builder.append("/*   Updated: ").append(updated).append(" by ").append(userName)
                .append("        ###   ########.fr       */");
        String updatedLine = builder.toString();
        if (document.getTextLength() > 648 + 80 && updatedLine.startsWith("/*   Updated: ")
                && updatedLine.endsWith("###   ########.fr       */")) {
            document.replaceString(648, 648 + 80, updatedLine);
        }
    }

    private static String generateHeader(VirtualFile file) {
        StringBuilder builder = new StringBuilder();
        String fileName = getFileName(file);
        String userName = getUserName();
        String owner = userName + " <" + getEmail() + ">";
        if (userName.length() < 9) {
            userName += " ".repeat(9 - userName.length());
        }
        if (owner.length() < 37) {
            owner += " ".repeat(37 - owner.length());
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

        builder.append("/* ************************************************************************** */\n");
        builder.append("/*                                                                            */\n");
        builder.append("/*                                                        :::      ::::::::   */\n");
        builder.append("/*   ").append(fileName).append("          :+:      :+:    :+:   */\n");
        builder.append("/*                                                    +:+ +:+         +:+     */\n");
        builder.append("/*   By: ").append(owner).append("      +#+  +:+       +#+        */\n");
        builder.append("/*                                                +#+#+#+#+#+   +#+           */\n");
        builder.append("/*   Created: ").append(created).append(" by ").append(userName)
                .append("         #+#    #+#             */\n");
        builder.append("/*   Updated: ").append(updated).append(" by ").append(userName)
                .append("        ###   ########.fr       */\n");
        builder.append("/*                                                                            */\n");
        builder.append("/* ************************************************************************** */\n");
        builder.append('\n');
        return builder.toString();
    }

    static void putHeader(Document document, VirtualFile file) {
        String header = generateHeader(file);
        document.insertString(0, header);
    }
}
