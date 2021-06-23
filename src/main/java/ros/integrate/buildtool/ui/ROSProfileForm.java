package ros.integrate.buildtool.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.TextFieldWithAutoCompletionListProvider;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.components.fields.ExpandableTextField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.buildtool.ROSBuildTool;
import ros.integrate.buildtool.ROSProfile;
import ros.integrate.pkg.ROSPackageManager;
import ros.integrate.ui.HistoryKey;
import ros.integrate.ui.PathListTextField;
import ros.integrate.ui.PathTextFieldWithHistory;
import ros.integrate.ui.SectionedFormBuilder;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * the GUI component that allows editing details about a specific profile while remaining
 * detached from the data component (until the user specifically requests so)
 * @author Noam Dori
 */
public class ROSProfileForm {
    @NotNull
    private final JPanel panel;
    private final JBLabel buildtoolArgsLabel = new JBLabel();
    private final JBTextField name = new FocusTextField();
    private final ComboBox<ROSBuildTool> buildtool = new ComboBox<>(ROSBuildTool.values());
    private final JBCheckBox doInstall = new JBCheckBox("Run install step");
    private final ComboBox<Boolean> doIsolation = new ComboBox<>(new Boolean[]{true, false});
    private final ExpandableTextField makeArgs = new ExpandableTextField(), cmakeArgs = new ExpandableTextField(),
            buildtoolArgs = new ExpandableTextField();

    private final PathTextFieldWithHistory sourceDir = new PathTextFieldWithHistory(),
            buildDir = new PathTextFieldWithHistory(),
            develDir = new PathTextFieldWithHistory(),
            installDir = new PathTextFieldWithHistory();

    private final PathListTextField allowList = new PathListTextField(), denyList = new PathListTextField();
    private final Map<String, Icon> packages = new HashMap<>();
    private ROSProfile profile;

    /**
     * create a new profile form
     * @param project the project this form is used to customize
     */
    public ROSProfileForm(@NotNull Project project) {
        JBLabel nameLabel = new JBLabel("Name:");
        JBLabel buildtoolLabel = new JBLabel("Build tool:");
        JBLabel isolationLabel = new JBLabel("Build layout:");
        JBLabel makeArgsLabel = new JBLabel("<html><code>Make</code> args:</html>");
        JBLabel cmakeArgsLabel = new JBLabel("<html><code>CMake</code> args:</html>");
        JBLabel sourceDirLabel = new JBLabel("Source directory:");
        JBLabel buildDirLabel = new JBLabel("Build directory:");
        JBLabel develDirLabel = new JBLabel("Devel directory:");
        JBLabel installDirLabel = new JBLabel("Install directory:");
        JBLabel allowListLabel = new JBLabel("Allowed packages");
        JBLabel denyListLabel = new JBLabel("Denied packages");
        buildtoolArgsLabel.setText("<html><code>Buildtool</code> args:</html>");

        project.getService(ROSPackageManager.class).getAllPackages()
                .forEach(pkg -> packages.put(pkg.getName(), pkg.getIcon(0)));
        TextFieldWithAutoCompletionListProvider<String> provider =
                new TextFieldWithAutoCompletionListProvider<String>(packages.keySet()){
                    @NotNull
                    @Override
                    protected String getLookupString(@NotNull String item) {
                        return item;
                    }

                    @Override
                    protected @Nullable Icon getIcon(@NotNull String item) {
                        return packages.get(item);
                    }
                };

        sourceDir.installHistory(project, HistoryKey.PROFILE_SOURCE);
        sourceDir.installBrowser("Choose source directory", "The directory containing all sources");
        buildDir.installHistory(project, HistoryKey.PROFILE_BUILD);
        buildDir.installBrowser("Choose build directory",
                "The directory containing all files generated by CMake, the compiler, and linker");
        develDir.installHistory(project, HistoryKey.PROFILE_DEVEL);
        buildDir.installBrowser("Choose devel directory",
                "The directory containing the result files created by the build process");
        installDir.installHistory(project, HistoryKey.PROFILE_INSTALL);
        installDir.installBrowser("Choose build directory",
                "The directory containing the final output exposed to the client");
        allowList.installHistory(project, HistoryKey.PACKAGE_LISTS);
        allowList.installListExpansion("Change allowed packages", ',');
        allowList.installAutoCompletion(provider);
        denyList.installHistory(project, HistoryKey.PACKAGE_LISTS);
        denyList.installListExpansion("Change denied packages ", ',');
        denyList.installAutoCompletion(provider);

        buildtool.setEditable(false);
        buildtool.setRenderer(new DefaultListCellRenderer() {
            @Override
            public JLabel getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                JLabel ret = (JLabel) super.getListCellRendererComponent(list, value.toString().toLowerCase(),
                        index, isSelected, cellHasFocus);
                ret.setIcon(ROSBuildTool.valueOf(value.toString()).getIcon());
                return ret;
            }
        });

        doIsolation.setEditable(false);
        doIsolation.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                String toRender = (Boolean) value ? "Isolated" : "Merged";
                return super.getListCellRendererComponent(list, toRender,
                        index, isSelected, cellHasFocus);
            }
        });
        buildtool.addItemListener(event -> {
            if (buildtool.getItem() == ROSBuildTool.CATKIN_MAKE) {
                doIsolation.setEnabled(true);
            }
            else {
                doIsolation.setEnabled(false);
                doIsolation.setSelectedItem(true);
            }
            String buildtoolName = buildtool.getItem().name().toLowerCase();
            buildtoolArgsLabel.setText(String.format("<html><code>%s</code> args:</html>", buildtoolName));
        });

        panel = SectionedFormBuilder.createFormBuilder()
                .addLabeledComponent(nameLabel, name)
                .addSection(null)
                .addLabeledComponent(buildtoolLabel, buildtool)
                .addComponent(doInstall)
                .addLabeledComponent(isolationLabel, doIsolation)
                .closeSection().addSection("Build arguments")
                .addLabeledComponent(cmakeArgsLabel, cmakeArgs)
                .addLabeledComponent(buildtoolArgsLabel, buildtoolArgs)
                .addLabeledComponent(makeArgsLabel, makeArgs)
                .closeSection().addSection("Target directories")
                .addLabeledComponent(sourceDirLabel, sourceDir)
                .addLabeledComponent(buildDirLabel, buildDir)
                .addLabeledComponent(develDirLabel, develDir)
                .addLabeledComponent(installDirLabel, installDir)
                .closeSection().addSection("Specific packages")
                .addLabeledComponent(allowListLabel, allowList)
                .addLabeledComponent(denyListLabel, denyList)
                .getPanel();
    }

    /**
     * @return the form component generated by this object
     */
    @NotNull
    public JPanel getPanel() {
        return panel;
    }

    /**
     * loads a ROS profile into the existing data of this form.
     * @param profile the profile data to load into the GUI
     * @param update an update method to run when the name or buildtool change.
     */
    public void loadData(@NotNull ROSProfile profile, Runnable update) {
        name.setText(profile.getName());
        buildtool.setItem(profile.getBuildtool());
        doInstall.setSelected(profile.isInstall());
        doIsolation.setSelectedItem(profile.getIsolation());
        makeArgs.setText(profile.getMakeArgs());
        cmakeArgs.setText(profile.getCmakeArgs());
        buildtoolArgs.setText(profile.getBuildtoolArgs());
        sourceDir.setText(profile.getSourceDirectory());
        buildDir.setText(profile.getBuildDirectory());
        develDir.setText(profile.getDevelDirectory());
        installDir.setText(profile.getInstallDirectory());
        allowList.setText(profile.getAllowList());
        denyList.setText(profile.getDenyList());

        name.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                profile.setGuiName(name.getText());
                update.run();
            }
        });

        buildtool.addItemListener(event -> {
            profile.setGuiBuildtool(buildtool.getItem());
            update.run();
        });

        this.profile = profile;
    }

    /**
     * @return true if the user changed any value in the form, false otherwise.
     */
    public boolean isModified() {
        boolean sumFlags = name.getText().equals(profile.getName())
                && buildtool.getItem().equals(profile.getBuildtool())
                && doInstall.isSelected() == profile.isInstall()
                && doIsolation.getItem().equals(profile.getIsolation())
                && makeArgs.getText().equals(profile.getMakeArgs())
                && cmakeArgs.getText().equals(profile.getCmakeArgs())
                && buildtoolArgs.getText().equals(profile.getBuildtoolArgs())
                && sourceDir.getText().equals(profile.getSourceDirectory())
                && buildDir.getText().equals(profile.getBuildDirectory())
                && develDir.getText().equals(profile.getDevelDirectory())
                && installDir.getText().equals(profile.getInstallDirectory())
                && allowList.getText().equals(profile.getAllowList())
                && denyList.getText().equals(profile.getDenyList());
        return !sumFlags;
    }

    /**
     * saves changed data into the linked profile
     * @return the linked profile that was just modified.
     */
    public ROSProfile saveProfile() {
        profile.setGuiName(name.getText());
        profile.setGuiBuildtool(buildtool.getItem());
        profile.setInstall(doInstall.isSelected());
        profile.setIsolation(doIsolation.getItem());
        profile.setMakeArgs(makeArgs.getText());
        profile.setCmakeArgs(cmakeArgs.getText());
        profile.setBuildtoolArgs(buildtoolArgs.getText());
        profile.setSourceDirectory(sourceDir.getText());
        profile.setBuildDirectory(buildDir.getText());
        profile.setDevelDirectory(develDir.getText());
        profile.setInstallDirectory(installDir.getText());
        profile.setAllowList(allowList.getText());
        profile.setDenyList(denyList.getText());
        return profile;
    }
}
