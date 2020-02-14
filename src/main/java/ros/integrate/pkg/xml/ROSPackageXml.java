package ros.integrate.pkg.xml;

import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkg.psi.ROSPackage;
import ros.integrate.pkg.xml.impl.ROSPackageXmlImpl;

import java.util.List;

/**
 * A facade class that simplifies interactions with a package.xml file
 */
public interface ROSPackageXml {

    class Contributor {
        @NotNull
        final String name, email;

        public Contributor(@NotNull String name, @NotNull String email) {
            this.email = email;
            this.name = name;
        }

        @NotNull
        public String getEmail() {
            return email;
        }

        @NotNull
        public String getName() {
            return name;
        }
    }

    enum URLType {
        WEBSITE,
        BUGTRACKER,
        REPOSITORY
    }

    /**
     * @return the raw XML file this wrapper manages
     */
    @NotNull
    XmlFile getRawXml();

    /**
     * changes the raw xml file managed under this wrapper.
     *
     * @param newXml the new package.xml file this wrapper manages.
     */
    void setRawXml(@NotNull XmlFile newXml);

    /**
     * binds an xml file to a packageXml file.
     *
     * @param xmlToWrap the XML file to wrap with packageXml
     * @return a new instance of ROSPackageXml bound to the XML file.
     */
    @Contract("_,_ -> new")
    @NotNull
    static ROSPackageXml newInstance(@NotNull XmlFile xmlToWrap, @NotNull ROSPackage pkg) {
        return new ROSPackageXmlImpl(xmlToWrap, pkg);
    }

    int getFormat();

    @NotNull
    TextRange getFormatTextRange();

    ROSPackage getPackage();

    @Nullable
    String getPkgName();

    TextRange getRootTextRange();

    TextRange getNameTextRange();

    void setNewFormat();

    void setPkgName(String name);

    @Nullable
    String getVersion();

    @NotNull
    TextRange getVersionTextRange();

    void setVersion(String newVersion);

    @Nullable
    String getDescription();

    void setDescription(String newDescription);

    TextRange getDescriptionTextRange();

    @NotNull
    List<String> getLicences();

    @NotNull
    List<Pair<String, URLType>> getURLs();

    /**
     * @return a list with at least length 1 that points towards all text ranges of the license values,
     * or if no license is available, to the package tag.
     */
    @NotNull
    List<TextRange> getLicenceTextRanges();

    /**
     * @return a list with at least length 1 that points towards all text ranges of the license values,
     * or if no license is available, to the package tag.
     */
    @NotNull
    List<TextRange> getURLTextRanges();

    void addLicence(String licenseName);

    @NotNull
    List<Contributor> getMaintainers();

    @NotNull
    List<Contributor> getAuthors();

    /**
     * @return a list with at least length 1 that points towards all text ranges of the maintainer tags,
     * or if no maintainer is available, to the package tag.
     */
    @NotNull
    List<TextRange> getMaintainerTextRanges();

    /**
     * @return a list with at least length 1 that points towards all text ranges of the author tags,
     * or if no author is available, to the package tag.
     */
    @NotNull
    List<TextRange> getAuthorTextRanges();

    void addMaintainer(String name, String email);

    boolean setAuthor(int id, @NotNull Contributor contributor);

    boolean setMaintainer(int id, @NotNull Contributor contributor);

    void removeAuthor(int id);

    void removeMaintainer(int id);

    void removeURL(int id);

    void setURL(int id, @NotNull String url, @NotNull URLType type);

    @SuppressWarnings("unused") // I know, but it is not something obvious
    default void setURL(int id, @NotNull String url) { setURL(id, url, URLType.WEBSITE); }

    void removeLicense(int id);

    void setLicense(int id, @NotNull String licenseName);

    XmlTag[] findSubTags(String qName);

    List<ROSPackage> getDependencies(DependencyType dependencyType);
}
