package ros.integrate.cmake.ref;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import ros.integrate.cmake.CMakeClasses;
import ros.integrate.cmake.adapter.CMakeArgumentAdapter;
import ros.integrate.pkg.psi.ROSPackage;

import java.util.Objects;

public class ROSCMakeReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(CMakeClasses.getUnquotedArgClass()),
                new PsiReferenceProvider() {
            @Override
            public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element,
                                                                   @NotNull ProcessingContext context) {
                // check if the file this element is part of is part of a package, and promptly check if the file
                // is a CMakeLists.txt file
                if (!element.getContainingFile().getName().equals("CMakeLists.txt")) {
                    return PsiReference.EMPTY_ARRAY;
                }
                var command = new CMakeArgumentAdapter(element).getCommand();
                PsiManager manager = PsiManager.getInstance(element.getProject());
                // check if this file can be a package's CMakeLists.txt
                var pkg = command.getPackage();
                if (pkg == null || !Objects.equals(pkg.getRoot(ROSPackage.RootType.SHARE),
                        element.getContainingFile().getParent())) {
                    return PsiReference.EMPTY_ARRAY;
                }
                switch (command.getName()) {
                    case "project" -> {
                        var argList = command.getArguments();
                        if (!argList.isEmpty() && manager.areElementsEquivalent(argList.get(0).raw(), element))
                            return new PsiReference[]{new ROSCMakeToPackageReference(argList.get(0))};
                    }
                    case "add_message_files", "add_service_files", "add_action_files" -> {
                        // collect directory information
                        // find the argument after "DIRECTORY"
                        String directory = getDefaultDirectoryName(command.getName());
                        int state = 0;
                        for (var arg : command.getArguments()) {
                            String text = arg.getText();
                            switch (state) {
                                case 0 -> {
                                    if (text.equals("DIRECTORY")) {
                                        state = 1;
                                    }
                                    if (text.equals("FILES")) {
                                        state = 2;
                                    }
                                }
                                case 1 -> {
                                    if (!text.equals("FILES"))
                                        directory = arg.getText();
                                    else {
                                        state = 2;
                                    }
                                }
                                case 2 -> {
                                    if (manager.areElementsEquivalent(element, arg.raw())) {
                                        return new PsiReference[]{new ROSCmakeToPktReference(arg, directory)};
                                    }
                                }
                            }
                        }
                    }
                }
                return PsiReference.EMPTY_ARRAY;
            }
        });
    }

    String getDefaultDirectoryName(@NotNull String commandName) {
        return switch (commandName) {
            case "add_service_files" -> "srv";
            case "add_action_files" -> "action";
            default -> "msg";
        };
    }
}
