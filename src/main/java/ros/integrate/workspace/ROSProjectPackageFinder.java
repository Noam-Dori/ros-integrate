package ros.integrate.workspace;

import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlFile;
import ros.integrate.workspace.psi.ROSPackage;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * a default finder used for finding packages within the project
 */
public class ROSProjectPackageFinder implements ROSPackageFinder {
    @Override
    public void findAndCache(Project project, ConcurrentMap<String, ROSPackage> pkgCache) {
        /*
         * the files that actually determine whether or not a directory is a package is the package.xml file.
         * the following condition is true:
         * A directory is a ROS package iff one of its roots directly contains a package.xml file
         */
        List<VirtualFile> packageXmls = FileTypeIndex.getFiles(XmlFileType.INSTANCE, GlobalSearchScope.allScope(project))
                .stream().filter(xml -> xml.getName().equals("package.xml")).collect(Collectors.toList());
        for (VirtualFile vXml : packageXmls) {
            // 1. get package name
            // FIXME for now, since we don't want to read into files just yet, we will use the directory to name packages.
            String pkgName = vXml.getParent().getName();
            // 2. search for the package in the cache. If it exists, move on to next xml.
            if(pkgCache.getOrDefault(pkgName,ROSPackage.ORPHAN) == ROSPackage.ORPHAN) continue;
            // 3. get package.xml file in XML PSI form
            XmlFile pkgXml = (XmlFile) Objects.requireNonNull(PsiManager.getInstance(project).findFile(vXml));
            // 4. get package root dir
            PsiDirectory pkgRoot = pkgXml.getContainingDirectory();
            // 5. TODO try getting CMakeLists.txt since this is a project package
            // 6. find all packet files
            // FIXME for now they will just be searched in the project regardless of CMakeLists.txt
            // Last. make package and cache it. MAKE SURE IT IS NOT ROSPackage.ORPHAN
        }
    }
}
