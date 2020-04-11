package ros.integrate.pkg.xml;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ROSLicenses {
    public static class LicenseEntity {
        @NotNull
        private final String link, tldr, file;
        private final boolean fileRequired;

        private LicenseEntity(@NotNull String link, @NotNull String tldr, @NotNull String file, boolean fileRequired) {
            this.link = link;
            this.tldr = tldr;
            this.file = file;
            this.fileRequired = fileRequired;
        }

        public String getLink(@NotNull String type) {
            switch (type) {
                default:
                case "Official": return link;
                case "Summary": return tldr.isEmpty() ? link : tldr;
            }
        }

        public boolean isFileRequired() {
            return fileRequired;
        }

        public String getFileSource() {
            return file;
        }

        @NotNull
        @Contract(value = " -> new", pure = true)
        public static String[] getLinkTypeOptions() {
            return new String[]{"Official", "Summary"};
        }
    }

    private static final Logger LOG = Logger.getLogger("ros.integrate.pkg.xml.ROSLicenses");

    @NotNull
    public static final Map<String, LicenseEntity> AVAILABLE_LICENSES = getLicenses();

    @NotNull
    private static Map<String, LicenseEntity> getLicenses() {
        Map<String, LicenseEntity> ret = new HashMap<>();
        try {
            InputStream in = ROSLicenses.class.getClassLoader().getResourceAsStream("licenses.xml");
            if (in == null) {
                throw new IOException("Could not find license resource file licenses.xml");
            }
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(in);
            doc.getDocumentElement().normalize();
            NodeList nodes = doc.getDocumentElement().getChildNodes();

            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    LicenseEntity license = new LicenseEntity(
                            ((Element)node).getAttribute("link"),
                            ((Element)node).getAttribute("tldr"),
                            ((Element)node).getAttribute("file"),
                            ((Element)node).getElementsByTagName("fileRequired").getLength() > 0
                    );
                    NodeList names = ((Element)node).getElementsByTagName("name");
                    for (int j = 0; j < names.getLength(); j++) {
                        ret.put(names.item(j).getTextContent(), license);
                    }
                }
            }
        } catch (ParserConfigurationException e) {
            LOG.severe("Configurations could not be found, error: " + e.getMessage());
        } catch (IOException e) {
            LOG.severe("Could not load configuration file, error: " + e.getMessage());
        } catch (SAXException e) {
            LOG.severe("Could not parse configuration file, error: " + e.getMessage());
        }
        return ret;
    }
}
