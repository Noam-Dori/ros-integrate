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

    /**
     * helper class describing contributors: authors or maintainers
     */
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

    /**
     * @return the format of this manifest:
     * <ul>
     *   <li>{@literal 0}: invalid root tag</li>
     *   <li>{@literal 1}: manifest format 1: https://www.ros.org/reps/rep-0127.html</li>
     *   <li>{@literal 2}: manifest format 2: https://www.ros.org/reps/rep-0140.html</li>
     *   <li>{@literal 3}: manifest format 3: https://www.ros.org/reps/rep-0149.html</li>
     * </ul>
     */
    int getFormat();

    /**
     * @return the name of the package as specified by the manifest.
     * This MIGHT be different from the parent directory name if something is wrong.
     */
    @Nullable
    String getPkgName();

    /**
     * @return the version string of this package, format NUMBER.NUMBER.NUMBER
     */
    @Nullable
    String getVersion();

    /**
     * @return gets the entire description string, which may be in HTML format
     */
    @Nullable
    String getDescription();

    /**
     * @return a list of all licenses in the package.
     */
    @NotNull
    List<String> getLicences();

    /**
     * @return a list of all URLs added to the package with their type (type is non-null)
     */
    @NotNull
    List<Pair<String, URLType>> getURLs();

    /**
     * @return a list of all active maintainers of this package. These are the people you refer to when there are bugs.
     */
    @NotNull
    List<Contributor> getMaintainers();

    /**
     * @return a list of the original creators of this package.
     */
    @NotNull
    List<Contributor> getAuthors();

    /**
     * @param dependencyType the type of dependency to check
     * @return a list of all packages this package depends on for the specific task described by the dependency type.
     */
    @NotNull
    List<ROSPackage> getDependencies(@NotNull DependencyType dependencyType);

    /**
     * @return the package this manifest describes.
     */
    @NotNull
    ROSPackage getPackage();

    /**
     * @return the text range of the format tag, or if it is not available, the root tag.
     */
    @NotNull
    TextRange getFormatTextRange();

    /**
     * @return the text range of the name tag, or if it is not available, the root tag.
     */
    @NotNull
    TextRange getNameTextRange();

    /**
     * @return the text range of the version tag, or if it is not available, the root tag.
     */
    @NotNull
    TextRange getVersionTextRange();

    /**
     * @return the text range of the description tag, or if it is not available, the root tag.
     */
    @NotNull
    TextRange getDescriptionTextRange();

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

    /**
     * updates the format of the package to the latest version (based on your ROS version).
     * This does NOT change other tags.
     */
    void setNewFormat();

    /**
     * changes the package name in the manifest, nowhere else.
     * @param name the new name to use.
     */
    void setPkgName(@NotNull String name);

    /**
     * changes the version of the package.
     * @param newVersion the new version string to use
     */
    void setVersion(@NotNull String newVersion);

    /**
     * changes the description of the package.
     * @param newDescription the new description to use, may be HTML and use CDATA
     */
    void setDescription(@NotNull String newDescription);

    /**
     * adds a new license to the package
     * @param licenseName the name of the new license, use licenses.properties for reference
     */
    void addLicence(@NotNull String licenseName);

//    void addURL(@NotNull String url, @NotNull URLType type);

//    @SuppressWarnings("unused") // I know, but it is not something obvious
//    default void addURL(@NotNull String url) { addURL(url, URLType.WEBSITE); }

    /**
     * adds a new maintainer to the package.
     * @param name the name of the maintainer
     * @param email the email of the maintainer
     */
    void addMaintainer(@NotNull String name, @NotNull String email);

//    void addAuthor(@NotNull String name, @Nullable String email);

    /**
     * changes a license
     * @param id the identifier of the resource - its index in the list of tags with the same name in the file.
     * @param licenseName the new license to use.
     */
    void setLicense(int id, @NotNull String licenseName);

    /**
     * changes a url
     * @param id the identifier of the resource - its index in the list of tags with the same name in the file.
     * @param url the new url to use.
     * @param type the type of the URL
     */
    void setURL(int id, @NotNull String url, @NotNull URLType type);

    /**
     * changes a url
     * @param id the identifier of the resource - its index in the list of tags with the same name in the file.
     * @param url the new "website" url to use.
     */
    @SuppressWarnings("unused") // I know, but it is not something obvious
    default void setURL(int id, @NotNull String url) { setURL(id, url, URLType.WEBSITE); }

    /**
     * changes a maintainer
     * @param id the identifier of the resource - its index in the list of tags with the same name in the file.
     * @param contributor the new contributor to use
     */
    boolean setMaintainer(int id, @NotNull Contributor contributor);

    /**
     * changes an author
     * @param id the identifier of the resource - its index in the list of tags with the same name in the file.
     * @param contributor the new contributor to use
     */
    boolean setAuthor(int id, @NotNull Contributor contributor);

    /**
     * removes a license
     * @param id the identifier of the resource - its index in the list of tags with the same name in the file.
     */
    void removeLicense(int id);

    /**
     * removes a url
     * @param id the identifier of the resource - its index in the list of tags with the same name in the file.
     */
    void removeURL(int id);

    /**
     * removes a maintainer
     * @param id the identifier of the resource - its index in the list of tags with the same name in the file.
     */
    void removeMaintainer(int id);

    /**
     * removes an author
     * @param id the identifier of the resource - its index in the list of tags with the same name in the file.
     */
    void removeAuthor(int id);

    /**
     * wrapper method for {@link XmlTag#findSubTags(String)} of {@link XmlFile#getRootTag()}
     */
    @NotNull
    XmlTag[] findSubTags(@NotNull String qName);
}
