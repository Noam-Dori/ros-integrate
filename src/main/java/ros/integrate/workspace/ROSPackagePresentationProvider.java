package ros.integrate.workspace;

import com.intellij.navigation.ColoredItemPresentation;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.ItemPresentationProvider;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.util.PlatformIcons;
import org.jetbrains.annotations.NotNull;
import ros.integrate.workspace.psi.ROSPackage;

import javax.swing.*;

public class ROSPackagePresentationProvider implements ItemPresentationProvider<ROSPackage> {
    @Override
    public ItemPresentation getPresentation(@NotNull final ROSPackage aPackage) {
        return new ColoredItemPresentation() {
            @Override
            public TextAttributesKey getTextAttributesKey() {
                return null;
            }

            @Override
            public String getPresentableText() {
                return aPackage.getName();
            }

            @Override
            public String getLocationString() {
                return aPackage.getQualifiedName();
            }

            @Override
            public Icon getIcon(boolean open) {
                return PlatformIcons.SOURCE_FOLDERS_ICON;
            }
        };
    }
}
