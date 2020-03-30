package ros.integrate.pkg.xml.annotate;

import com.intellij.lang.annotation.AnnotationHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkg.xml.ExportTag;
import ros.integrate.pkg.xml.ROSPackageXml;

import java.util.List;
import java.util.Optional;

public class PackageExportAnnotator {
    @Nullable
    private final ExportTag export;

    @NotNull
    private final AnnotationHolder holder;

    public PackageExportAnnotator(@NotNull ROSPackageXml pkgXml, @NotNull AnnotationHolder holder) {
        this.export = pkgXml.getExport();
        this.holder = holder;
    }

    public void annEmptyMessageGenerator() {
        if (export == null) {
            return;
        }
        if (Optional.ofNullable(export.getMessageGenerator()).map(String::isEmpty).orElse(false)) {
            holder.createWarningAnnotation(export.getMessageGeneratorTextRange(),"Empty Message Generator");
        }
    }

    public void annTooManyMessageGenerators() {
        if (export == null) {
            return;
        }
        if (export.getRawTag().findSubTags("message_generator").length > 1) {
            holder.createWarningAnnotation(export.getMessageGeneratorTextRange(),
                    "Each package may only generate code for one language at most");
        }
    }
}
