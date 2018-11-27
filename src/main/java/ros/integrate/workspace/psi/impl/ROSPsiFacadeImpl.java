package ros.integrate.workspace.psi.impl;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.ExtensionPoint;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.extensions.SimpleSmartExtensionPoint;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Conditions;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiModificationTracker;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.ConcurrencyUtil;
import com.intellij.util.Processor;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.messages.MessageBus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.elementFinder.ROSPsiElementFinder;
import ros.integrate.pkt.psi.ROSPktFile;
import ros.integrate.workspace.psi.ROSPackage;
import ros.integrate.workspace.psi.ROSPsiFacade;

import java.util.*;
import java.util.concurrent.ConcurrentMap;

public class ROSPsiFacadeImpl extends ROSPsiFacade {
    private static final Logger LOG = Logger.getInstance(ROSPsiFacadeImpl.class);

    private final SimpleSmartExtensionPoint<ROSPsiElementFinder> myElementFinders;
    private final ConcurrentMap<String, ROSPackage> myPackageCache = ContainerUtil.createConcurrentSoftValueMap();
    private final Project myProject;

//    private final ConcurrentMap<GlobalSearchScope, Map<String, PsiClass>> myClassCache = ContainerUtil.createConcurrentWeakKeySoftValueMap();
    // note: class caches are used to easily find classes in the java facade. you may want to use this for packets and bundle files.

    public ROSPsiFacadeImpl(Project project,
                            @NotNull PsiManager psiManager,
                            MessageBus bus) {
        myProject = project;

        final PsiModificationTracker modificationTracker = psiManager.getModificationTracker();

        if (bus != null) {
            bus.connect().subscribe(PsiModificationTracker.TOPIC, new PsiModificationTracker.Listener() {
                private long lastTimeSeen = -1L;

                @Override
                public void modificationCountChanged() {
                    //myClassCache.clear();
                    final long now = modificationTracker.getJavaStructureModificationCount();
                    if (lastTimeSeen != now) {
                        lastTimeSeen = now;
                        myPackageCache.clear();
                    }
                }
            });
        }

        myElementFinders = new SimpleSmartExtensionPoint<ROSPsiElementFinder>() {
            @NotNull
            @Override
            protected ExtensionPoint<ROSPsiElementFinder> getExtensionPoint() {
                return Extensions.getArea(myProject).getExtensionPoint(ROSPsiElementFinder.EP_NAME);
            }
        };
    }

    @NotNull
    private List<ROSPsiElementFinder> finders() {
        return myElementFinders.getExtensions();
    }

    @NotNull
    private List<ROSPsiElementFinder> filteredFinders() {
        return DumbService.getInstance(getProject()).filterByDumbAwareness(finders());
    }

    @Override
    public boolean processPackageDirectories(ROSPackage pkg, GlobalSearchScope scope, Processor<PsiDirectory> processor, boolean includeLibrarySources) {
        for (ROSPsiElementFinder finder : filteredFinders()) {
            if (!finder.processPackageDirectories(pkg, scope, processor, includeLibrarySources)) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected Project getProject() {
        return myProject;
    }

    @Nullable
    @Override
    public ROSPackage findPackage(@NotNull String qualifiedName) {
        ROSPackage aPackage = myPackageCache.get(qualifiedName);
        if (aPackage != null) {
            return aPackage;
        }

        for (ROSPsiElementFinder finder : filteredFinders()) {
            aPackage = finder.findPackage(qualifiedName);
            if (aPackage != null) {
                return ConcurrencyUtil.cacheOrGet(myPackageCache, qualifiedName, aPackage);
            }
        }

        return null;
    }

    @Override
    public PsiFile[] getFilesInPackage(ROSPackage pkg, GlobalSearchScope scope) {
        Condition<PsiFile> filter = null;

        for (ROSPsiElementFinder finder : filteredFinders()) {
            Condition<PsiFile> finderFilter = finder.getPackageFilesFilter(pkg, scope);
            if (finderFilter != null) {
                if (filter == null) {
                    filter = finderFilter;
                }
                else {
                    filter = Conditions.and(filter, finderFilter);
                }
            }
        }

        Set<PsiFile> result = new LinkedHashSet<>();
        PsiDirectory[] directories = pkg.getDirectories(scope);
        for (PsiDirectory directory : directories) {
            for (PsiFile file : directory.getFiles()) {
                if (filter == null || filter.value(file)) {
                    result.add(file);
                }
            }
        }

        for (ROSPsiElementFinder finder : filteredFinders()) {
            Collections.addAll(result, finder.getPackageFiles(pkg, scope));
        }
        return result.toArray(PsiFile.EMPTY_ARRAY);
    }

    private static Condition<ROSPktFile> getPacketFilterFromFinders(@NotNull GlobalSearchScope scope, @NotNull List<ROSPsiElementFinder> finders) {
        Condition<ROSPktFile> filter = null;
        for (ROSPsiElementFinder finder : finders) {
            Condition<ROSPktFile> finderFilter = finder.getPacketsFilter(scope);
            if (finderFilter != null) {
                filter = filter == null ? finderFilter : Conditions.and(filter, finderFilter);
            }
        }
        return filter;
    }

    private static void filterPacketsAndAppend(ROSPsiElementFinder finder,
                                               @Nullable Condition<? super ROSPktFile> filter,
                                               @NotNull ROSPktFile[] packets,
                                               @NotNull List<? super ROSPktFile> result) {
        for (ROSPktFile packet : packets) {
            if (packet == null) {
                LOG.error("Finder " + finder + " returned null ROSPktFile");
                continue;
            }
            if (filter == null || filter.value(packet)) {
                result.add(packet);
            }
        }
    }

    @Override
    public ROSPktFile[] getPackets(ROSPackage pkg, GlobalSearchScope scope) {
        List<ROSPsiElementFinder> finders = filteredFinders();
        Condition<ROSPktFile> filter = getPacketFilterFromFinders(scope, finders);

        List<ROSPktFile> result = null;
        for (ROSPsiElementFinder finder : finders) {
            ROSPktFile[] packets = finder.getPackets(pkg, scope);
            if (packets.length == 0) continue;
            if (result == null) result = new ArrayList<>(packets.length);
            filterPacketsAndAppend(finder, filter, packets, result);
        }

        return result == null ? ROSPktFile.EMPTY_ARRAY : result.toArray(ROSPktFile.EMPTY_ARRAY);
    }

    private static Condition<PsiFile> getSourcesFilterFromFinders(@NotNull GlobalSearchScope scope, @NotNull List<ROSPsiElementFinder> finders) {
        Condition<PsiFile> filter = null;
        for (ROSPsiElementFinder finder : finders) {
            Condition<PsiFile> finderFilter = finder.getSourcesFilter(scope);
            if (finderFilter != null) {
                filter = filter == null ? finderFilter : Conditions.and(filter, finderFilter);
            }
        }
        return filter;
    }

    private static void filterSourcesAndAppend(ROSPsiElementFinder finder,
                                               @Nullable Condition<? super PsiFile> filter,
                                               @NotNull PsiFile[] sources,
                                               @NotNull List<? super PsiFile> result) {
        for (PsiFile file : sources) {
            if (file == null) {
                LOG.error("Finder " + finder + " returned null ROSPktFile");
                continue;
            }
            if (filter == null || filter.value(file)) {
                result.add(file);
            }
        }
    }

    @Override
    public PsiFile[] getSources(ROSPackage pkg, GlobalSearchScope scope) {
        List<ROSPsiElementFinder> finders = filteredFinders();
        Condition<PsiFile> filter = getSourcesFilterFromFinders(scope, finders);

        List<PsiFile> result = null;
        for (ROSPsiElementFinder finder : finders) {
            PsiFile[] sources = finder.getSources(pkg, scope);
            if (sources.length == 0) continue;
            if (result == null) result = new ArrayList<>(sources.length);
            filterSourcesAndAppend(finder, filter, sources, result);
        }

        return result == null ? PsiFile.EMPTY_ARRAY : result.toArray(PsiFile.EMPTY_ARRAY);
    }

    @Override
    public PsiFile getCmakeLists(ROSPackage pkg) {
        List<ROSPsiElementFinder> finders = filteredFinders();

        for (ROSPsiElementFinder finder : finders) {
            PsiFile file = finder.getCMakeLists(pkg);
            if(file != null) return file;
        }
        return null;
    }

    @Override
    public XmlFile getPackageXml(ROSPackage pkg) {
        List<ROSPsiElementFinder> finders = filteredFinders();

        for (ROSPsiElementFinder finder : finders) {
            XmlFile file = finder.getPackageXml(pkg);
            if(file != null) return file;
        }
        return null;
    }
}
