package ma.leet.ft_header;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class Settings {
    private String fileNameRegex;
    private String login;
    private String email;

    public Settings(String optionsFile) {
        File settingsFile = new File(optionsFile + "/42HeaderSettingsPlugin.xml");
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(settingsFile);
            doc.getDocumentElement().normalize();

            NodeList options = doc.getElementsByTagName("option");
            for (int index = 0; index < options.getLength(); ++index) {
                Node node = options.item(index);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String name = element.getAttribute("name");
                    String value = element.getAttribute("value");
                    switch (name) {
                        case "fileNameRegex" -> this.fileNameRegex = value;
                        case "login" -> this.login = value;
                        case "email" -> this.email = value;
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    public String getFileNameRegex() {
        return fileNameRegex;
    }

    public String getLogin() {
        return login;
    }

    public String getEmail() {
        return email;
    }
}
