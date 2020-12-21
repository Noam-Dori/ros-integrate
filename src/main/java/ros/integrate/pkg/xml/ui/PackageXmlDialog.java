package ros.integrate.pkg.xml.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.*;
import com.intellij.ui.components.fields.IntegerField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkg.psi.ROSPackage;
import ros.integrate.pkg.xml.ROSPackageXml;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Optional;

import static java.awt.GridBagConstraints.*;
import static ros.integrate.pkg.xml.ROSPackageXml.getLatestFormat;

public class PackageXmlDialog extends DialogWrapper {
    private final ROSPackageXml pkgXml;

    private final IntegerField format = new IntegerField();
    private final JBLabel formatLabel = new JBLabel();
    private final JBCheckBox latestFormat = new JBCheckBox();
    private final JBLabel latestFormatLabel = new JBLabel();

    private final JBTextField name = new JBTextField();
    private final JBLabel nameLabel = new JBLabel();

    private final VersionField version = new VersionField();
    private final JBLabel versionLabel = new JBLabel();

    private final JBTextArea description = new JBTextArea();
    private final JBLabel descriptionLabel = new JBLabel();

    private final LicenseTable licenses = new LicenseTable();
    private final JBLabel licenseLabel = new JBLabel();

    public PackageXmlDialog(@NotNull Project project, @Nullable ROSPackageXml pkgXml) {
        super(project);
        this.pkgXml = pkgXml;

        setTitle("Complete package.xml");
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        nameLabel.setText("name:");
        descriptionLabel.setText("description:");
        formatLabel.setText("format:");
        latestFormatLabel.setText("use latest format");
        versionLabel.setText("version:");
        version.setCompatibilityLabel("compatibility:");
        licenseLabel.setText("licenses:");

        Optional<ROSPackageXml> oPkgXml = Optional.ofNullable(pkgXml);
        format.setValue(oPkgXml.map(ROSPackageXml::getFormat).filter(i -> i != 0).orElse(getLatestFormat()));
        if (format.getValue() == getLatestFormat()) {
            format.setEnabled(false);
            latestFormat.setSelected(true);
        }
        oPkgXml.map(ROSPackageXml::getVersion).ifPresent(version::setVersion);
        oPkgXml.map(ROSPackageXml::getLicences).ifPresent(licenses::setLicenses);
        description.setText(oPkgXml.map(ROSPackageXml::getDescription).orElse("\nPackage description here\n"));
        name.setText(oPkgXml.map(ROSPackageXml::getPackage).map(ROSPackage::getName).orElse(""));
        name.setEnabled(!oPkgXml.isPresent());

        format.setMinValue(1);
        latestFormat.addChangeListener(status -> {
            format.setEnabled(!latestFormat.isSelected());
            if (latestFormat.isSelected()) {
                format.setValue(getLatestFormat());
            }
        });

        version.installKeyEvents();

        return FormBuilder.createFormBuilder()
                .addLabeledComponent(formatLabel, getFormatPanel())
                .addLabeledComponent(nameLabel, name)
                .addLabeledComponent(versionLabel, version)
                .addLabeledComponent(descriptionLabel, new JBScrollPane(description))
                .addLabeledComponent(licenseLabel, licenses.getComponent())
                .getPanel();
    }

    @NotNull
    private JPanel getFormatPanel() {
        GridBagLayout formatLayout = new GridBagLayout();
        JPanel formatPanel = new JPanel(formatLayout);

        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        c.weighty = 0;
        c.fill = NONE;
        c.anchor = WEST;
        c.insets = JBUI.insets(0, 0, 0, 5);
        formatPanel.add(latestFormat, c);

        c.gridx = 1;
        c.weightx = 1;
        c.insets = JBUI.insets(0, 0, 0, 0);
        formatPanel.add(latestFormatLabel, c);

        c.gridx = 2;
        c.weightx = 1;
        c.fill = HORIZONTAL;
        formatPanel.add(format, c);

        return formatPanel;
    }

    @NotNull
    public String getDescription() {
        return description.getText();
    }

    @NotNull
    public String getName() {
        return name.getText();
    }

    public int getFormat() {
        return format.getValue();
    }

    @NotNull
    public ROSPackageXml.Version getVersion() {
        return version.getVersion();
    }

    @NotNull
    public List<ROSPackageXml.License> getLicenses() {
        return licenses.getLicenses();
    }

    @NotNull
    public List<ROSPackageXml.Contributor> getMaintainers() {
        return null;
    }

    @NotNull
    public List<ROSPackageXml.Dependency> getDependencies() {
        return null;
    }
}
