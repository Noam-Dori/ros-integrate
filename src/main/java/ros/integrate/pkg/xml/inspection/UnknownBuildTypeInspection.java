package ros.integrate.pkg.xml.inspection;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkg.xml.ExportTag;
import ros.integrate.pkg.xml.PackageXmlUtil;
import ros.integrate.pkg.xml.ROSPackageXml;
import ros.integrate.settings.ROSSettings;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Logger;

public class UnknownBuildTypeInspection extends LocalInspectionTool {
    private static final Logger LOG = Logger.getLogger("#ros.integrate.pkg.xml.completion.PackageXmlCompletionContributor");
    private static final String[] BUILD_TYPES = loadBuildTypes();

    @NotNull
    private static String[] loadBuildTypes() {
        Properties ret = new Properties();
        try {
            ret.load(ROSSettings.class.getClassLoader().getResourceAsStream("defaults.properties"));
            return ret.getProperty("buildTypes").split(":");
        } catch (IOException e) {
            LOG.warning("could not load configuration file, default values will not be loaded. error: " +
                    e.getMessage());
        }
        return new String[0];
    }

    @Nullable
    @Override
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
        ExportTag export = Optional.ofNullable(PackageXmlUtil.getWrapper(file))
                .map(ROSPackageXml::getExport).orElse(null);
        if (export == null) {
            return null;
        }
        return Arrays.stream(export.getRawTag().findSubTags("build_type"))
                .filter(build_type -> !build_type.getValue().getText().isEmpty())
                .filter(build_type -> !Arrays.asList(BUILD_TYPES).contains(build_type.getValue().getText()))
                .map(build_type -> manager.createProblemDescriptor(file, build_type.getValue().getTextRange(),
                                getDisplayName() + " " + build_type.getText() + ".",
                                ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly)
                ).toArray(ProblemDescriptor[]::new);
    }
}
