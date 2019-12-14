package ros.integrate.pkg.xml;

import com.google.common.collect.Maps;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class ROSLicenses {
    private static final Logger LOG = Logger.getLogger("ros.integrate.pkg.xml.ROSLicenses");

    private static final Supplier<Properties> f = () -> {
        Properties ret = new Properties();
        try {
            ret.load(ROSLicenses.class.getClassLoader().getResourceAsStream("licenses.properties"));
        } catch (IOException e) {
            LOG.severe("could not load configuration file, error: " + e.getMessage());
        }
        return ret;
    };
    public static final Map<String, String> AVAILABLE_LICENSES = Maps.fromProperties(f.get());
}
