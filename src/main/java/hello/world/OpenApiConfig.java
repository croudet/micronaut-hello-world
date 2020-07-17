package hello.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.Resource;
import io.github.classgraph.ScanResult;
import io.micronaut.context.annotation.ConfigurationProperties;

/**
 * OpenApi configuration for Swagger-ui, ReDoc and RapiDoc.
 *
 * @see <a href="https://github.com/swagger-api/swagger-ui">Swagger-ui</a>
 * @see <a href="https://github.com/Rebilly/ReDoc">ReDoc</a>
 * @see <a href="https://github.com/mrin9/RapiDoc">RapiDoc</a>
 *
 * @author croudet
 */
@ConfigurationProperties(OpenApiConfig.PREFIX)
public class OpenApiConfig {

    public static final String PREFIX = "open-api";

    private List<URIConfig> urls = new ArrayList<>(2);
    private String path;
    private String rapiPdfVersion = "1.0.3";
    private SwaggerConfig swaggerConfig = new SwaggerConfig();
    private RedocConfig redocConfig = new RedocConfig();
    private RapidocConfig rapidocConfig = new RapidocConfig();

    public OpenApiConfig() {
    }

    public OpenApiConfig(SwaggerConfig swaggerConfig, RedocConfig redocConfig,
            RapidocConfig rapidocConfig) {
        this.swaggerConfig = swaggerConfig;
        this.redocConfig = redocConfig;
        this.rapidocConfig = rapidocConfig;
    }

    private void buildURIConfig(Resource resource) {
        final String openApi = resource.getPathRelativeToClasspathElement();
        final int index = openApi.lastIndexOf('/');
        if (index >= 0) {
            final String filename = openApi.substring(index + 1);
            final String apiname = filename.substring(0, filename.length() - 4);
            urls.add(new URIConfig(apiname, path + '/' + filename));
        }
        resource.close();
    }

    @PostConstruct
    void findOpenApiDefinitions() {
        // Micronaut puts generated Swagger YAML file into the META-INF/swagger
        // directory of the project's class output.
        try (ScanResult scanResult = new ClassGraph().acceptPathsNonRecursive("META-INF/swagger").scan()) {
            scanResult.getResourcesWithExtension("yml").forEach(this::buildURIConfig);
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRapiPdfVersion() {
        return rapiPdfVersion;
    }

    public void setRapiPdfVersion(String rapiPdfVersion) {
        this.rapiPdfVersion = rapiPdfVersion;
    }

    public RapidocConfig getRapidoc() {
        return rapidocConfig;
    }

    public void setRapidoc(RapidocConfig rapidocConfig) {
        this.rapidocConfig = rapidocConfig;
    }

    public RedocConfig getRedoc() {
        return redocConfig;
    }

    public void setRedoc(RedocConfig redocConfig) {
        this.redocConfig = redocConfig;
    }

    public List<URIConfig> getUrls() {
        return urls;
    }

    public void setUrls(List<URIConfig> urls) {
        this.urls = urls;
    }

    public SwaggerConfig getSwaggerUi() {
        return swaggerConfig;
    }

    public void setSwaggerUi(SwaggerConfig cfg) {
        swaggerConfig = cfg;
    }

    @Override
    public String toString() {
        return new StringBuilder(100).append("OpenApiConfig [urls=").append(urls).append(", swaggerConfig=")
                .append(swaggerConfig).append(", reDocConfig=").append(redocConfig).append(", rapiDocConfig=")
                .append(rapidocConfig).append(']').toString();
    }

    /**
     * RapiDoc configuration.
     *
     * Currently only the version and theme can be set.
     *
     * @author croudet
     */
    @ConfigurationProperties(RapidocConfig.PREFIX)
    public static class RapidocConfig {
        static final String PREFIX = "rapidoc";
        private String version = "5.0.5";
        private Theme theme = Theme.DARK;
        private Layout layout = Layout.ROW;

        enum Theme {
            LIGHT, DARK;

            @Override
            public String toString() {
                return this.name().toLowerCase(Locale.US);
            }
        }

        enum Layout {
            COLUMN, ROW;

            @Override
            public String toString() {
                return this.name().toLowerCase(Locale.US);
            }
        }

        public RapidocConfig() {
        }

        public RapidocConfig(String version, Theme theme, Layout layout) {
            this.version = version;
            this.theme = theme == null ? Theme.DARK : theme;
            this.layout = layout == null ? Layout.ROW : layout;
        }

        public Theme getTheme() {
            return theme;
        }

        public void setTheme(Theme theme) {
            if (theme != null) {
                this.theme = theme;
            }
        }

        public Layout getLayout() {
            return layout;
        }

        public void setLayout(Layout layout) {
            if (layout != null) {
                this.layout = layout;
            }
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        @Override
        public String toString() {
            return new StringBuilder(50).append("RapidocConfig [version=").append(version)
                    .append(", theme=").append(theme).append(", layout=").append(layout).append(']').toString();
        }

    }

    /**
     * ReDoc configuration.
     *
     * Currently only the version can be set.
     *
     * @author croudet
     */
    @ConfigurationProperties(RedocConfig.PREFIX)
    public static class RedocConfig {
        static final String PREFIX = "redoc";
        private String version = "next";

        public RedocConfig() {

        }

        public RedocConfig(String version) {
            this.version = version;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        @Override
        public String toString() {
            return new StringBuilder(30).append("[version=").append(version).append(']').toString();
        }

    }

    /**
     * Swagger-ui configuration.
     *
     * @author croudet
     */
    @ConfigurationProperties(SwaggerConfig.PREFIX)
    public static class SwaggerConfig {
        static final String PREFIX = "swagger-ui";
        private String version = "3.23.5";
        private String layout = "StandaloneLayout";
        private boolean deepLinking = true;
        private Theme theme = Theme.FLATTOP;

        enum Theme {
            DEFAULT(null), MATERIAL("theme-material"), FEELING_BLUE("theme-feeling-blue"), FLATTOP("theme-flattop"),
            MONOKAI("theme-monokai"), MUTED("theme-muted"), NEWSPAPER("theme-newspaper"), OUTLINE("theme-outline");
            private String css;

            Theme(String css) {
                this.css = css;
            }

            public String getCss() {
                return css;
            }
        }

        public SwaggerConfig() {
        }

        public SwaggerConfig(String version, String layout, boolean deepLinking, Theme theme) {
            this.version = version;
            this.layout = layout;
            this.deepLinking = deepLinking;
            this.theme = theme;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getLayout() {
            return layout;
        }

        public void setLayout(String layout) {
            this.layout = layout;
        }

        public boolean isDeepLinking() {
            return deepLinking;
        }

        public void setDeepLinking(boolean deepLinking) {
            this.deepLinking = deepLinking;
        }

        public void setTheme(Theme theme) {
            this.theme = theme;
        }

        public Theme getTheme() {
            return theme;
        }

        @Override
        public String toString() {
            return new StringBuilder(100).append("[version=").append(version).append(", layout=").append(layout)
                    .append(", theme=").append(theme).append(", deepLinking=").append(deepLinking).append(']').toString();
        }

    }

    /**
     * Named URIs to OpenApi definition files.
     *
     * @author croudet
     */
    public static class URIConfig {
        private String name;
        private String url;

        public URIConfig(String name, String url) {
            this.name = name;
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        @Override
        public String toString() {
            return "[name=" + name + ", url=" + url + ']';
        }

    }

}
