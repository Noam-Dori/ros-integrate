package ros.integrate.pkg.xml;

import com.intellij.ide.browsers.OpenInBrowserRequest;
import com.intellij.ide.browsers.WebBrowserUrlProvider;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.Url;
import com.intellij.util.Urls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ROSLicenseLinkProvider extends WebBrowserUrlProvider {
    @Nullable
    @Override
    protected Url getUrl(@NotNull OpenInBrowserRequest request, @NotNull VirtualFile file) throws BrowserException {
        if (PackageXmlUtil.getWrapper(request.getFile()) == null) {
            return super.getUrl(request, file);
        }
        XmlTag tag;
        if (request.getElement().getParent() instanceof XmlTag) {
            tag = (XmlTag) request.getElement().getParent();
        } else if (request.getElement().getParent().getParent() instanceof XmlTag) {
            tag = (XmlTag) request.getElement().getParent().getParent();
        } else {
            tag = null;
        }
        if (tag == null) {
            return super.getUrl(request, file);
        }
        String url = ROSLicenses.AVAILABLE_LICENSES.get(tag.getValue().getText());
        return url != null && !url.isEmpty() ? Urls.newUnparsable(url) : null;
    }

    @Override
    public boolean canHandleElement(@NotNull OpenInBrowserRequest request) {
        if (PackageXmlUtil.getWrapper(request.getFile()) == null) {
            return super.canHandleElement(request);
        }
        XmlTag tag;
        if (request.getElement().getParent() instanceof XmlTag) {
            tag = (XmlTag) request.getElement().getParent();
        } else if (request.getElement().getParent().getParent() instanceof  XmlTag) {
            tag = (XmlTag) request.getElement().getParent().getParent();
        } else {
            tag = null;
        }
        return tag != null && tag.getName().equals("license") || super.canHandleElement(request);
    }
}
