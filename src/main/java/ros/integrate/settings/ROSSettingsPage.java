package ros.integrate.settings;

import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkg.ROSDepKeyCache;
import ros.integrate.pkg.xml.ROSLicenses;
import ros.integrate.ui.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * the user interface that allows the user to fill persistent, important configurations about the ROS plugin and the
 * user's ROS environment
 * @author Noam Dori
 */
public class ROSSettingsPage implements SearchableConfigurable {
    private static final String GET_SOURCES_PROGRESS = "Fetching ROSDep Source Lists";

    @NotNull
    @Override
    public String getId() {
        return "ROS";
    }

    private final Project project;
    private final ROSSettings data;

    private final JBLabel rosSettingsLabel = new JBLabel();

    private final PathTextFieldWithHistory rosRoot = new PathTextFieldWithHistory();
    private final JBLabel rosRootLabel = new JBLabel();

    private final PathTextFieldWithHistory workspace = new PathTextFieldWithHistory();
    private final JBLabel workspaceLabel = new JBLabel();

    private final PathListTextField additionalSources = new PathListTextField();
    private final JBLabel additionalSourcesLabel = new JBLabel();

    private final JButton resetSourcesButton = new JButton();

    private final PathListTextField excludedXmls = new PathListTextField();
    private final JBLabel excludedXmlsLabel = new JBLabel();

    private final ComboBox<String> licenseLinkType = new ComboBox<>(ROSLicenses.LicenseEntity.getLinkTypeOptions());
    private final JBLabel licenseLinkTypeLabel = new JBLabel();

    private final PathListTextField knownRosdepKeys = new PathListTextField();
    private final JBLabel knownRosdepKeysLabel = new JBLabel();

    private final PathListTextField rosdepSources = new PathListTextField();
    private final JBLabel rosdepSourcesLabel = new JBLabel();

    private final JButton fetchSourceListsButton = new JButton();

    /**
     * construct a new settings page
     * @param project the project this settings page belongs to
     */
    public ROSSettingsPage(Project project) {
        this.project = project;
        data = ROSSettings.getInstance(project);
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "ROS";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        reset();

        String envVariables = "Environment";
        String pluginSpecific = "Plugin Specific";
        String rosdep = "Dependencies";

        rosSettingsLabel.setText("In here, you can configure your interactions with ROS in the IDE");
        rosRootLabel.setText("ROS path:");
        workspaceLabel.setText("Workspace:");
        additionalSourcesLabel.setText("Additional package paths:");
        resetSourcesButton.setText("Set to $ROS_PACKAGE_PATH");
        excludedXmlsLabel.setText("Excluded XML files:");
        licenseLinkTypeLabel.setText("License details preference:");
        rosdepSourcesLabel.setText("Online ROSDep source lists:");
        knownRosdepKeysLabel.setText("Known ROSDep Keys:");
        fetchSourceListsButton.setText("Fetch ROSDep Source Lists");

        rosRoot.installHistory(project, HistoryKey.ROS_ROOT);
        rosRoot.installBrowser("Choose Target Directory", "This Directory is the Root ROS Library.");
        workspace.installHistory(project, HistoryKey.WORKSPACE);
        workspace.installBrowser("Choose Target Workspace", "This is the root directory of this project's workspace");
        additionalSources.installHistory(project, HistoryKey.EXTRA_SOURCES);
        additionalSources.installBrowser("Modify source path",
                "This is the a root directory to additional sources outside of the workspace.");
                additionalSources.installListExpansion("Configure Paths to Source");
        excludedXmls.installHistory(project, HistoryKey.EXCLUDED_XMLS);
        excludedXmls.installBrowser("Modify Excluded XMLs",
                "These XML files will not be processed by the ROS plugin, and will not get extra context.");
        excludedXmls.installListExpansion("Configure excluded XMLs");
        rosdepSources.installHistory(project, HistoryKey.ROSDEP_SOURCES);
        rosdepSources.installListExpansion("Configure ROSDep Lists", '"');
        knownRosdepKeys.installHistory(project, HistoryKey.KNOWN_ROSDEP_KEYS);
        knownRosdepKeys.installListExpansion("Configure Saved ROSDep Keys");
        additionalSources.getTextEditor().getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                resetSourcesButton.setEnabled(!rosPackagePathEnv().equals(additionalSources.getText()));
            }
        });
        resetSourcesButton.addActionListener(action -> additionalSources.setText(rosPackagePathEnv()));
        resetSourcesButton.setEnabled(!rosPackagePathEnv().equals(additionalSources.getText()));
        ROSDepKeyCache keyCache = project.getService(ROSDepKeyCache.class);
        fetchSourceListsButton.addActionListener(action -> ProgressManager.getInstance().run(new Task.Backgroundable(
                project, GET_SOURCES_PROGRESS, true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                keyCache.forceFetch();
                fetchSourceListsButton.setEnabled(keyCache.inOfflineMode());
            }
        }));
        fetchSourceListsButton.setEnabled(keyCache.inOfflineMode());

        return SectionedFormBuilder.createFormBuilder()
                .addComponent(rosSettingsLabel)
                .addSection(envVariables)
                .addLabeledComponent(rosRootLabel, rosRoot)
                .addLabeledComponent(workspaceLabel, workspace)
                .addLabeledComponent(additionalSourcesLabel, additionalSources)
                .addComponent(resetSourcesButton)
                .closeSection()
                .addSection(rosdep)
                .addLabeledComponent(knownRosdepKeysLabel, knownRosdepKeys)
                .addLabeledComponent(rosdepSourcesLabel, rosdepSources)
                .addComponent(fetchSourceListsButton)
                .closeSection()
                .addSection(pluginSpecific)
                .addLabeledComponent(excludedXmlsLabel, excludedXmls)
                .addLabeledComponent(licenseLinkTypeLabel, licenseLinkType)
                .getPanel();
    }

    @Override
    public boolean isModified() {
        return isModified(rosRoot.getTextEditor(), data.getROSPath())
                || isModified(workspace.getTextEditor(), data.getWorkspacePath())
                || isModified(additionalSources.getTextEditor(), data.getRawAdditionalSources())
                || isModified(excludedXmls.getTextEditor(), data.getRawExcludedXmls())
                || isModified(licenseLinkType, data.getLicenseLinkType())
                || isModified(rosdepSources.getTextEditor(), data.getRawROSDepSources())
                || isModified(knownRosdepKeys.getTextEditor(), data.getRawKnownROSDepKeys());
    }

    @NotNull
    @Contract(pure = true)
    private BiConsumer<String, String> withTrigger(Consumer<String> function) {
        return (fieldName, topic) -> {function.consume(fieldName); data.triggerListeners(topic);};
    }

    @Override
    public void apply() {
        rosRoot.addToHistory(project, withTrigger(data::setRosPath));
        workspace.addToHistory(project, withTrigger(data::setWorkspacePath));
        additionalSources.addToHistory(project, withTrigger(data::setAdditionalSources));
        excludedXmls.addToHistory(project, withTrigger(data::setExcludedXmls));
        knownRosdepKeys.addToHistory(project, withTrigger(data::setKnownROSDepKeys));
        rosdepSources.addToHistory(project, withTrigger(data::setROSDepSources));
        data.setLicenseLinkType((String) licenseLinkType.getSelectedItem());
        data.triggerListeners(HistoryKey.LICENSE_LINK_TYPE.get());
    }

    @Override
    public void reset() {
        rosRoot.setText(data.getROSPath());
        workspace.setText(data.getWorkspacePath());
        additionalSources.setText(data.getRawAdditionalSources());
        excludedXmls.setText(data.getRawExcludedXmls());
        licenseLinkType.setSelectedItem(data.getLicenseLinkType());
        knownRosdepKeys.setText(data.getRawKnownROSDepKeys());
        rosdepSources.setText(data.getRawROSDepSources());
    }

    private String rosPackagePathEnv() {
        return PathListUtil.serializePathList(PathListUtil
                .parsePathList(Optional.ofNullable(System.getenv("ROS_PACKAGE_PATH")).orElse(""))
                .stream().filter(path -> notChildOf(workspace.getText(), path))
                .filter(path -> notChildOf(rosRoot.getText(), path))
                .collect(Collectors.toList()));
    }

    private static boolean notChildOf(@NotNull String parent, @NotNull String child) {
        if (!child.startsWith(parent)) {
            return true;
        }
        String diff = child.substring(parent.length());
        return !diff.isEmpty() && !diff.startsWith("/");
    }
}
