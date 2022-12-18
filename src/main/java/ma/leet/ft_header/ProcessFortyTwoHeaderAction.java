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

public class ProcessFortyTwoHeaderAction {
    private static String getUserName() {
        String userName;
        if (System.getenv("USER") != null) {
            userName = System.getenv("USER");
        } else if (System.getenv("LOGNAME") != null) {
            userName = System.getenv("LOGNAME");
        } else {
            userName = "no user!";
        }
        return userName;
    }

    static boolean hasHeader(VirtualFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            int i = 0;
            while (reader.ready() && i < 11) {
                String line = reader.readLine();
                if (line.length() != 80 || !line.startsWith("/*") || !line.endsWith("*/")) {
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
        String userName;
        userName = getUserName();
        Path path = Paths.get(file.getPath());
        BasicFileAttributes view;
        try {
            view = Files.getFileAttributeView(path, BasicFileAttributeView.class).readAttributes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        DateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date modificationDate = new Date(view == null ? file.getTimeStamp() : view.lastModifiedTime().toMillis());
        String updated = f.format(modificationDate);
        builder.append("/*   Updated: ").append(updated).append(" by ").append(userName)
                .append("         ###   ########.fr       */");
        String updatedLine = builder.toString();
        if (document.getTextLength() > 648 + 80 && updatedLine.startsWith("/*   Updated: ")
                && updatedLine.endsWith("###   ########.fr       */")) {
            document.replaceString(648, 648 + 80, updatedLine);
        }
    }

    private static String generateHeader(VirtualFile file) {
        StringBuilder builder = new StringBuilder();
        String fileName = file.getName().length() <= 41 ? file.getName() : file.getName().substring(0, 41);
        String userName = getUserName();
        Path path = Paths.get(file.getPath());
        BasicFileAttributes view = null;
        try {
            view = Files.getFileAttributeView(path, BasicFileAttributeView.class).readAttributes();
        } catch (IOException e) {
            return "";
        }
        DateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date creationDate = new Date(view == null ? file.getTimeStamp() : view.creationTime().toMillis());
        Date modificationDate = new Date(view == null ? file.getTimeStamp() : view.lastModifiedTime().toMillis());
        String created = f.format(creationDate);
        String updated = f.format(modificationDate);

        builder.append("/* ************************************************************************** */\n");
        builder.append("/*                                                                            */\n");
        builder.append("/*                                                        :::      ::::::::   */\n");
        builder.append("/*   ").append(fileName)
                .append("                                         ", 0, 41 - fileName.length())
                .append("          :+:      :+:    :+:   */\n");
        builder.append("/*                                                    +:+ +:+         +:+     */\n");
        builder.append("/*   By: ").append(userName).append(" <").append(userName)
                .append("@student.1337.ma>        +#+  +:+       +#+        */\n");
        builder.append("/*                                                +#+#+#+#+#+   +#+           */\n");
        builder.append("/*   Created: ").append(created).append(" by ").append(userName)
                .append("          #+#    #+#             */\n");
        builder.append("/*   Updated: ").append(updated).append(" by ").append(userName)
                .append("         ###   ########.fr       */\n");
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
