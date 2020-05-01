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
import ros.integrate.pkg.xml.TagTextRange;
import ros.integrate.settings.ROSSettings;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class UnknownBuildTypeInspection extends LocalInspectionTool {
    private static final Logger LOG = Logger.getLogger("#ros.integrate.pkg.xml.completion.PackageXmlCompletionContributor");
    private static final List<String> BUILD_TYPES = loadBuildTypes();

    @NotNull
    private static List<String> loadBuildTypes() {
        Properties ret = new Properties();
        try {
            ret.load(ROSSettings.class.getClassLoader().getResourceAsStream("defaults.properties"));
            return Arrays.asList(ret.getProperty("buildTypes").split(":"));
        } catch (IOException e) {
            LOG.warning("could not load configuration file, default values will not be loaded. error: " +
                    e.getMessage());
        }
        return Collections.emptyList();
    }

    @Nullable
    @Override
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
        ExportTag export = Optional.ofNullable(PackageXmlUtil.getWrapper(file))
                .map(ROSPackageXml::getExport).orElse(null);
        if (export == null) {
            return null;
        }
        int format = export.getParent().getFormat();
        List<ExportTag.BuildType> buildTypes = export.getBuildTypes();
        List<TagTextRange> buildTypeTrs = export.getBuildTypeTextRanges();
        List<ProblemDescriptor> ret = new ArrayList<>();
        for (int i = 0; i < buildTypes.size(); i++) {
            ExportTag.BuildType buildType = buildTypes.get(i);
            if (!PackageXmlUtil.conditionEvaluatesToFalse(buildType.getCondition(), format) &&
                    !BUILD_TYPES.contains(buildType.getType())) {
                ret.add(manager.createProblemDescriptor(file, buildTypeTrs.get(i).value(),
                        getDisplayName() + " " + buildType.getType() + ".",
                        ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly));
            }
        }
        return ret.toArray(ProblemDescriptor.EMPTY_ARRAY);
    }
}
