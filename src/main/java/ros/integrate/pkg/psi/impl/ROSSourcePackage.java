package ros.integrate.pkg.psi.impl;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.ROSIcons;
import ros.integrate.cmake.adapter.CMakeFileAdapter;
import ros.integrate.pkt.psi.ROSPktFile;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * represents packages that are not compiled. There packages contain source code and can be quite messy.
 * These usually exist somewhere in the workspace (under src) or in additional places specified by the user.
 * These can be organised in extra folders.
 * @author Noam Dori
 */
public class ROSSourcePackage extends ROSPackageBase {

    @NotNull
    private final PsiDirectory root;
    @Nullable
    private CMakeFileAdapter cmakeFile;

    public ROSSourcePackage(@NotNull Project project,@NotNull String name,@NotNull PsiDirectory root,
                            @NotNull XmlFile pkgXml,@NotNull Collection<ROSPktFile> packets,
                            @Nullable CMakeFileAdapter cmakeFile) {
        super(project,name,pkgXml);
        this.root = root;
        addPackets(packets);
        this.cmakeFile = cmakeFile;
    }

    @Override
    public String toString() {
        return "ROSSourcePackage{\"" + getName() + "\"}";
    }

    @NotNull
    @Override
    public PsiDirectory[] getRoots() {
        return new PsiDirectory[]{root};
    }

    @Override
    public boolean isEditable() {
        return true;
    }

    /** @noinspection SpellCheckingInspection*/
    @Nullable
    @Override
    public PsiDirectory getMsgRoot() {
        List<String> foundPaths = new ArrayList<>();
        if (cmakeFile != null)
        {
            // for ROS1, add_message_files can inform the user using the "DIRECTORY" directive
            for (var command : cmakeFile.findCommandCalls("add_message_files")) {
                // find the argument after "DIRECTORY"
                boolean foundFlag = false;
                for (var arg : command.getArguments()) {
                    if (foundFlag) {
                        if (!arg.getText().equals("FILES"))
                            foundPaths.add(arg.getText());
                        break;
                    }
                    if (arg.getText().equals("DIRECTORY"))
                        foundFlag = true;
                }
            }
            // for ROS2, the rosidl_generate_interfaces contains lists of message paths.
            for (var command : cmakeFile.findCommandCalls("rosidl_generate_interfaces")) {
                boolean notFirst = false;
                for (var arg : command.getArguments()) {
                    if (notFirst) {
                        // if DEPENDENCIES is encountered, all messages were scanned.
                        if (arg.getText().equals("DEPENDENCIES"))
                            break;
                        // trim
                        if (arg.getText().endsWith(".msg"))
                            foundPaths.add(arg.getText().replaceFirst("/?[^/]*.msg$", ""));
                    } else {
                        notFirst = true;
                    }
                }
            }
        }
        if (foundPaths.isEmpty())
            foundPaths.add("msg"); // the default
        PsiDirectory msgDir = root.findSubdirectory(commonPath(foundPaths.toArray(String[]::new)));
        return msgDir == null ? root : msgDir;
    }

    @Override
    public void setCMakeLists(@Nullable PsiFile cmakeFile) {
        this.cmakeFile = Optional.ofNullable(cmakeFile).map(CMakeFileAdapter::new).orElse(null);
    }

    @Override
    public @Nullable CMakeFileAdapter getCMakeLists() {
        return cmakeFile;
    }

    public static String commonPath(String @NotNull ... paths){
        String commonPath = "";
        String[][] folders = new String[paths.length][];
        for(int i = 0; i < paths.length; i++){
            folders[i] = paths[i].split("/"); //split on file separator
        }
        for(int j = 0; j < folders[0].length; j++){
            String thisFolder = folders[0][j]; //grab the next folder name in the first path
            boolean allMatched = true; //assume all have matched in case there are no more paths
            for(int i = 1; i < folders.length && allMatched; i++){ //look at the other paths
                if(folders[i].length < j){ //if there is no folder here
                    allMatched = false; //no match
                    break; //stop looking because we've gone as far as we can
                }
                //otherwise
                allMatched = folders[i][j].equals(thisFolder); //check if it matched
            }
            if(allMatched){ //if they all matched this folder name
                commonPath += thisFolder + "/"; //add it to the answer
            }else{//otherwise
                break;//stop looking
            }
        }
        return commonPath;
    }

    @Override
    protected void extraSetName(@NotNull String name) throws IncorrectOperationException {
        if (cmakeFile != null) {
            for (var command : cmakeFile.findCommandCalls("project")) {
                command.getArguments().get(0).setText(name);
            }
        }
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return ROSIcons.SRC_PKG;
    }

    @Nullable
    @Override
    public PsiDirectory getRoot(RootType type) {
        return root; // update later once include and other join in the party.
    }
}
