package ros.integrate.settings;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.refactoring.copy.CopyFilesOrDirectoriesDialog;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.RecentsManager;
import com.intellij.ui.TextFieldWithHistoryWithBrowseButton;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkg.ROSDepKeyCache;
import ros.integrate.pkg.xml.ROSLicenses;
import ros.integrate.settings.BrowserOptions.HistoryKey;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ROSSettingsPage implements SearchableConfigurable {
    private static final String GET_SOURCES_PROGRESS = "Fetching ROSDep Source Lists";


    @NotNull
    @Override
    public String getId() {
        return "ROS";
    }

    private final Project project;
    private final RecentsManager recentsManager;
    private final ROSSettings data;

    private final JBLabel rosSettingsLabel = new JBLabel();

    private final TextFieldWithHistoryWithBrowseButton rosRoot = new TextFieldWithHistoryWithBrowseButton();
    private final JBLabel rosRootLabel = new JBLabel();

    private final TextFieldWithHistoryWithBrowseButton workspace = new TextFieldWithHistoryWithBrowseButton();
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

    public ROSSettingsPage(Project project) {
        this.project = project;
        recentsManager = RecentsManager.getInstance(project);
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
        rosRootLabel.setText("ROS Path:");
        workspaceLabel.setText("Workspace:");
        additionalSourcesLabel.setText("Additional Package Paths:");
        resetSourcesButton.setText("Set to $ROS_PACKAGE_PATH");
        excludedXmlsLabel.setText("Excluded XML files:");
        licenseLinkTypeLabel.setText("License Details Preference:");
        rosdepSourcesLabel.setText("Online ROSDep Source Lists:");
        knownRosdepKeysLabel.setText("Known ROSDep Keys:");
        fetchSourceListsButton.setText("Fetch ROSDep Source Lists");

        installBrowserHistory(rosRoot, new BrowserOptions(project)
                .withTitle("Choose Target Directory")
                .withDescription("This Directory is the Root ROS Library."));
        installBrowserHistory(workspace, new BrowserOptions(project, HistoryKey.WORKSPACE)
                .withTitle("Choose Target Workspace")
                .withDialogTitle("Configure Path to Workspace")
                .withDescription("This is the root directory of this project's workspace"));
        additionalSources.installHistoryAndDialog(recentsManager, new BrowserOptions(project, HistoryKey.EXTRA_SOURCES)
                .withTitle("Modify source path")
                .withDialogTitle("Configure Paths to Source")
                .withDescription("This is the a root directory to additional sources outside of the workspace."));
        excludedXmls.installHistoryAndDialog(recentsManager, new BrowserOptions(project, HistoryKey.EXCLUDED_XMLS)
                .withTitle("Modify Excluded XMLs")
                .withDialogTitle("Configure excluded XMLs")
                .withDescription("These XML files will not be processed by the ROS plugin, and will not get extra context."));
        rosdepSources.installHistoryAndDialog(recentsManager, new BrowserOptions(project, HistoryKey.ROSDEP_SOURCES)
                .withDialogTitle("Configure ROSDep Lists")
                .withDelimiter('"')
                .noFilePaths());
        knownRosdepKeys.installHistoryAndDialog(recentsManager, new BrowserOptions(project, HistoryKey.KNOWN_ROSDEP_KEYS)
                .withDialogTitle("Configure Saved ROSDep Keys")
                .noFilePaths());
        additionalSources.getChildComponent().getTextEditor().getDocument().addDocumentListener(new DocumentAdapter() {
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

    private void installBrowserHistory(@NotNull TextFieldWithHistoryWithBrowseButton field, @NotNull BrowserOptions options) {
        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        field.addBrowseFolderListener(options.title,
                options.description,
                project, descriptor, TextComponentAccessor.TEXT_FIELD_WITH_HISTORY_WHOLE_TEXT);

        List<String> recentEntries = Optional.ofNullable(recentsManager.getRecentEntries(options.getKey()))
                .orElse(new LinkedList<>());
        recentEntries.remove(field.getText()); // doing this and the line below will move curDir to the top regardless if it exists or not
        recentEntries.add(0, field.getText());
        field.getChildComponent().setHistory(recentEntries);

        // folder text field
        final JTextField textField = field.getChildComponent().getTextEditor();
        FileChooserFactory.getInstance().installFileCompletion(textField, descriptor, true, null);
        field.setTextFieldPreferredWidth(CopyFilesOrDirectoriesDialog.MAX_PATH_LENGTH);
    }

    @Override
    public boolean isModified() {
        return isModified(rosRoot.getChildComponent().getTextEditor(), data.getROSPath())
                || isModified(workspace.getChildComponent().getTextEditor(), data.getWorkspacePath())
                || isModified(additionalSources.getChildComponent().getTextEditor(), data.getRawAdditionalSources())
                || isModified(excludedXmls.getChildComponent().getTextEditor(), data.getRawExcludedXmls())
                || isModified(licenseLinkType, data.getLicenseLinkType())
                || isModified(rosdepSources.getChildComponent().getTextEditor(), data.getRawROSDepSources())
                || isModified(knownRosdepKeys.getChildComponent().getTextEditor(), data.getRawKnownROSDepKeys());
    }

    private void addToHistory(@NotNull TextFieldWithHistoryWithBrowseButton field, @NotNull HistoryKey historyKey,
                              @NotNull Consumer<String> updateAction) {
        recentsManager.registerRecentEntry(historyKey.get(), field.getChildComponent().getText());
        updateAction.consume(field.getText());
        data.triggerListeners(historyKey.get());
    }

    @Override
    public void apply() {
        addToHistory(rosRoot, HistoryKey.DEFAULT, data::setRosPath);
        addToHistory(workspace, HistoryKey.WORKSPACE, data::setWorkspacePath);
        addToHistory(additionalSources, HistoryKey.EXTRA_SOURCES, data::setAdditionalSources);
        addToHistory(excludedXmls, HistoryKey.EXCLUDED_XMLS, data::setExcludedXmls);
        addToHistory(knownRosdepKeys, HistoryKey.KNOWN_ROSDEP_KEYS, data::setKnownROSDepKeys);
        addToHistory(rosdepSources, HistoryKey.ROSDEP_SOURCES, data::setROSDepSources);
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
