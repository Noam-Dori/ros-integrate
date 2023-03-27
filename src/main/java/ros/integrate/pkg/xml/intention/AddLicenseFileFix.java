package ros.integrate.pkg.xml.intention;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileTypes.PlainTextLanguage;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.ROSPackageXml;
import ros.integrate.pkg.xml.ui.NewLicenseDialogue;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * an intention that downloads the license file related to a specific license and links it to the tag that called it.
 * This will also bring up a dialogue where the user can fill in information so the license is specific for their
 * company/product.
 * @author Noam Dori
 */
public class AddLicenseFileFix implements LocalQuickFix {
    private static final Logger LOG = Logger.getLogger("#ros.integrate.pkg.xml.intention.AddLicenseFileFix");
    private static final String GET_LICENSE_MSG = "Downloading License";
    private static final String YEAR_REGEX = "\\[y+]|<year>",
            HOLDER_REGEX = "\\[.*name.*]|<<.*name=organization.*>>|<name of author.*>|<copyright holder.*>",
            FULL_REGEX = "<<.*name=copyright.*>>",
            REMOVE_REGEX = "<<.+>>|\\[.+]";

    @SafeFieldForPreview
    @NotNull
    private final ROSPackageXml pkgXml;
    private final int id;
    @NotNull
    private final String fileSourceUrl;

    /**
     * construct a new intention
     * @param pkgXml the relevant package.xml file
     * @param fileSourceUrl the URL of the raw license text
     * @param id the index of the tag in the package.xml
     */
    public AddLicenseFileFix(@NotNull ROSPackageXml pkgXml, int id, @NotNull String fileSourceUrl) {
        this.pkgXml = pkgXml;
        this.id = id;
        this.fileSourceUrl = fileSourceUrl;
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getName() {
        return "Add license file";
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "ROS XML";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        PsiDirectory xmlDir = pkgXml.getRawXml().getContainingDirectory();
        if (xmlDir == null) {
            return;
        }
        NewLicenseDialogue dialog = new NewLicenseDialogue(xmlDir);
        ApplicationManager.getApplication().invokeLater(() -> {
            dialog.show();
            if (dialog.getExitCode() != DialogWrapper.OK_EXIT_CODE) {
                return;
            }
            ProgressManager.getInstance().run(new Task.Backgroundable(project, GET_LICENSE_MSG, true) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    runFileDownload(project, dialog, xmlDir);
                }
            });
        });

    }

    void runFileDownload(Project project, NewLicenseDialogue dialog, PsiDirectory xmlDir) {
        try {
            StringBuilder fileContent = new StringBuilder();
            Scanner scanner = new Scanner(new URL(fileSourceUrl).openStream());
            if (scanner.hasNextLine()) { // it should.
                for (String line = scanner.nextLine(); scanner.hasNextLine(); line = scanner.nextLine()) {
                    fileContent.append(
                            line.replaceAll(YEAR_REGEX, dialog.getYear())
                                    .replaceAll(HOLDER_REGEX, dialog.getCopyrightHolder())
                                    .replaceAll(FULL_REGEX, dialog.getYear() + " " + dialog.getCopyrightHolder())
                                    .replaceAll(REMOVE_REGEX, "")
                    ).append('\n');
                }
            }

            ApplicationManager.getApplication().invokeLater(() ->
                    WriteCommandAction.runWriteCommandAction(project, () -> {
                PsiFile newFile = dialog.getDirectory().copyFileFrom(dialog.getFileName(),
                        PsiFileFactory.getInstance(project).createFileFromText("dummy",
                                PlainTextLanguage.INSTANCE, fileContent));
                pkgXml.setLicense(id, new ROSPackageXml.License(pkgXml.getLicences().get(id).getValue(),
                        VfsUtilCore.getRelativePath(newFile.getVirtualFile(), xmlDir.getVirtualFile())));
            }));
        }
        catch (IOException e) {
            LOG.warning("Could not download required license file from " + fileSourceUrl + ". Terminating fix.");
        }
    }
}
