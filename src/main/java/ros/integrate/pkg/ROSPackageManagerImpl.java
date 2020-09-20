package ros.integrate.pkg;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.events.VFileContentChangeEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.containers.MultiMap;
import com.intellij.util.containers.SortedList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkg.psi.ROSPackage;
import ros.integrate.settings.BrowserOptions;
import ros.integrate.settings.ROSSettings;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class ROSPackageManagerImpl implements ROSPackageManager {
    private final ConcurrentMap<String, ROSPackage> pkgCache = new ConcurrentHashMap<>();
    private final Project project;

    private boolean loaded;

    public ROSPackageManagerImpl(@NotNull Project project) {
        this.project = project;
        loaded = false;
        ROSSettings.getInstance(project).addListener(settings -> reloadIndex(),
                BrowserOptions.HistoryKey.EXCLUDED_XMLS.get());
    }

    private void loadIndex() {
        if (!loaded && !DumbService.isDumb(project)) {
            loaded = true;
            findPackages();
        }
    }

    @Override
    public void reloadIndex() {
        if (loaded) {
            pkgCache.clear();
            findPackages();
        }
    }

    private void findPackages() {
        ROSPackageFinder.FINDERS.forEach(finder -> finder.findAndCache(project, pkgCache));
    }

    @Override
    public void filesChanged(@NotNull List<? extends VFileEvent> events) {
        if (loaded) {
            // 1. group by parent dir name (convert to package if possible)
            Set<ROSPackage> affectedPackages = new TreeSet<>();
            List<VFileEvent> affectedOrphans = new SortedList<>(Comparator.comparing(VFileEvent::getPath)),
                    affectedOrphansOld = new SortedList<>(Comparator.comparing(VFileEvent::getPath));
            affectedOrphansOld.addAll(events);
            boolean orphansRemainedTheSame = false;
            while (!orphansRemainedTheSame) {
                affectedOrphansOld.forEach(event -> sortToLists(event, affectedPackages, affectedOrphans));
                // 2. figure out what happened per package per file & react accordingly (create new package, delete new package, modify details)
                affectedPackages.forEach(this::applyChangesToPackage);
                if (affectedOrphans.containsAll(affectedOrphansOld)) {
                    orphansRemainedTheSame = true;
                } else {
                    affectedOrphansOld.retainAll(affectedOrphans);
                    affectedOrphans.clear();
                }
            }
            applyChangesToOrphans(affectedOrphans); // we now know that these files are not renamed packages.
        }
    }

    private void applyChangesToOrphans(@NotNull List<VFileEvent> events) {
        /* possible things that can happen:
         * new package
         */
        ApplicationManager.getApplication().invokeLater(() -> {
            for (ROSPackageFinder finder : ROSPackageFinder.FINDERS) {
                MultiMap<ROSPackage, VFileEvent> newPackages = finder.investigate(project, events);
                for (Map.Entry<ROSPackage, Collection<VFileEvent>> newPkg : newPackages.entrySet()) {
                    if (newPkg.getKey() != ROSPackage.ORPHAN) {
                        pkgCache.putIfAbsent(newPkg.getKey().getName(), newPkg.getKey());
                        events.removeAll(newPkg.getValue()); // removes associated events from collection.
                    }
                }
            }
        });
    }

    private void applyChangesToPackage(@NotNull ROSPackage pkg) {
        /* possible things that can happen:
         * details update
         * removed pkg
         * package moved entirely
         */
        String oldName = pkg.getName();
        for (ROSPackageFinder finder : ROSPackageFinder.FINDERS) {
            ROSPackageFinder.CacheCommand cmd = finder.investigateChanges(project, pkg);
            if (cmd == null) {
                continue;
            }
            switch (cmd) {
                case DELETE: {
                    pkgCache.remove(pkg.getName());
                    break;
                }
                case RENAME: {
                    pkgCache.remove(oldName);
                    pkgCache.putIfAbsent(pkg.getName(), pkg);
                    break;
                }
                case NONE:
                default:
                    break;
            }
        }
    }

    private void sortToLists(VFileEvent event, Set<ROSPackage> affectedPackages,
                             List<VFileEvent> affectedOrphans) {
        // try to see if it falls under a package, if not put it under the orphan list
        int successfulSorts = 0;
        for (ROSPackage pkg : getAllPackages()) {
            if (pkg == null) { // because this happens for some reason?
                continue;
            }
            for (PsiDirectory root : pkg.getRoots()) {
                if (ROSPackageUtil.belongsToRoot(root, event)) {
                    affectedPackages.add(pkg);
                    successfulSorts++;
                    if (ROSPackageUtil.getRequiredSorts(event, root) == successfulSorts) {
                        return;
                    }
                }
            }
        }
        // if no package was found
        affectedOrphans.add(event);
    }

    @Override
    public Collection<ROSPackage> getAllPackages() {
        loadIndex();
        return pkgCache.values();
    }

    @Nullable
    @Override
    public ROSPackage findPackage(String pkgName) {
        loadIndex();
        return pkgCache.get(pkgName);
    }

    @Nullable
    @Override
    public ROSPackage findPackage(PsiDirectory childDirectory) {
        loadIndex();
        for (ROSPackage pkg : pkgCache.values()) {
            if (ROSPackageUtil.belongToPackage(pkg, childDirectory)) {
                return pkg;
            }
        }
        return null;
    }

    @Override
    public void updatePackageName(@NotNull ROSPackage pkg, String newName) {
        loadIndex();
        pkgCache.remove(pkg.getName());
        pkgCache.put(newName, pkg);
    }

    @Override
    public void excludePkgXml(@NotNull XmlFile file) {
        ROSSettings.getInstance(project).addExcludedXml(file.getVirtualFile().getPath());
        applyChangesForFile(file);
    }

    @Override
    public void includeXml(@NotNull XmlFile file) {
        ROSSettings.getInstance(project).removeExcludedXml(file.getVirtualFile().getPath());
        applyChangesForFile(file);
    }

    private void applyChangesForFile(@NotNull PsiFile file) {
        project.getMessageBus().syncPublisher(VirtualFileManager.VFS_CHANGES)
                .after(Collections.singletonList(new VFileContentChangeEvent(this, file.getVirtualFile(),
                        file.getModificationStamp(), -1, false)));
    }

    @NotNull
    @Override
    public Collection<ROSPackage> findGroupMembers(@NotNull String groupName) {
        loadIndex();
        return getAllPackages().stream()
                .filter(pkg -> pkg.getPackageXml() != null && pkg.getPackageXml().getGroups().stream()
                        .anyMatch(group -> group.getGroup().equals(groupName)))
                .collect(Collectors.toList());
    }

    @NotNull
    @Override
    public Collection<ROSPackage> findGroupDependents(@NotNull String groupName) {
        loadIndex();
        return getAllPackages().stream()
                .filter(pkg -> pkg.getPackageXml() != null && pkg.getPackageXml().getGroupDepends().stream()
                        .anyMatch(group -> group.getGroup().equals(groupName)))
                .collect(Collectors.toList());
    }
}
