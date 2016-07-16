package cucumber.runtime.model;

import cucumber.runtime.CucumberException;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.Resource;
import cucumber.runtime.io.ResourceLoader;
import cucumber.util.Encoding;
import gherkin.AstBuilder;
import gherkin.GherkinDialect;
import gherkin.GherkinDialectProvider;
import gherkin.Parser;
import gherkin.ParserException;
import gherkin.TokenMatcher;
import gherkin.ast.GherkinDocument;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CucumberFeature implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String path;
    private GherkinDialect i18n;
    private GherkinDocument gherkinDocument;

    public static List<CucumberFeature> load(ResourceLoader resourceLoader, List<String> featurePaths, final List<Object> filters, PrintStream out) {
        final List<CucumberFeature> cucumberFeatures = load(resourceLoader, featurePaths, filters);
        if (cucumberFeatures.isEmpty()) {
            if (featurePaths.isEmpty()) {
                out.println(String.format("Got no path to feature directory or feature file"));
            } else if (filters.isEmpty()) {
                out.println(String.format("No features found at %s", featurePaths));
            } else {
                out.println(String.format("None of the features at %s matched the filters: %s", featurePaths, filters));
            }
        }
        return cucumberFeatures;
    }

    public static List<CucumberFeature> load(ResourceLoader resourceLoader, List<String> featurePaths, final List<Object> filters) {
        final List<CucumberFeature> cucumberFeatures = new ArrayList<CucumberFeature>();
        for (String featurePath : featurePaths) {
            if (featurePath.startsWith("@")) {
                loadFromRerunFile(cucumberFeatures, resourceLoader, featurePath.substring(1), filters);
            } else {
                loadFromFeaturePath(cucumberFeatures, resourceLoader, featurePath, filters, false);
            }
        }
        Collections.sort(cucumberFeatures, new CucumberFeatureUriComparator());
        return cucumberFeatures;
    }

    private static void loadFromRerunFile(List<CucumberFeature> cucumberFeatures, ResourceLoader resourceLoader, String rerunPath, final List<Object> filters) {
        Iterable<Resource> resources = resourceLoader.resources(rerunPath, null);
        for (Resource resource : resources) {
            String source = read(resource);
            if (!source.isEmpty()) {
                for (String featurePath : source.split(" ")) {
                    loadFromFileSystemOrClasspath(cucumberFeatures, resourceLoader, featurePath, filters);
                }
            }
        }
    }

    static private String read(Resource resource) {
        try {
            String source = Encoding.readFile(resource);
            return source;
        } catch (IOException e) {
            throw new CucumberException("Failed to read resource:" + resource.getPath(), e);
        }
    }

    private static void loadFromFileSystemOrClasspath(List<CucumberFeature> cucumberFeatures, ResourceLoader resourceLoader, String featurePath, final List<Object> filters) {
        try {
            loadFromFeaturePath(cucumberFeatures, resourceLoader, featurePath, filters, false);
        } catch (IllegalArgumentException originalException) {
            if (!featurePath.startsWith(MultiLoader.CLASSPATH_SCHEME) &&
                    originalException.getMessage().contains("Not a file or directory")) {
                try {
                    loadFromFeaturePath(cucumberFeatures, resourceLoader, MultiLoader.CLASSPATH_SCHEME + featurePath, filters, true);
                } catch (IllegalArgumentException secondException) {
                    if (secondException.getMessage().contains("No resource found for")) {
                        throw new IllegalArgumentException("Neither found on file system or on classpath: " +
                                originalException.getMessage() + ", " + secondException.getMessage());
                    } else {
                        throw secondException;
                    }
                }
            } else {
                throw originalException;
            }
        }
    }

    private static void loadFromFeaturePath(List<CucumberFeature> cucumberFeatures, ResourceLoader resourceLoader, String featurePath, final List<Object> filters, boolean failOnNoResource) {
        PathWithLines pathWithLines = new PathWithLines(featurePath);
        ArrayList<Object> filtersForPath = new ArrayList<Object>(filters);
        filtersForPath.addAll(pathWithLines.lines);
        Iterable<Resource> resources = resourceLoader.resources(pathWithLines.path, ".feature");
        if (failOnNoResource && !resources.iterator().hasNext()) {
            throw new IllegalArgumentException("No resource found for: " + pathWithLines.path);
        }
        for (Resource resource : resources) {
            Parser<GherkinDocument> parser = new Parser<GherkinDocument>(new AstBuilder());
            TokenMatcher matcher = new TokenMatcher();

            String source = read(resource);
            try {
                GherkinDocument gherkinDocument = parser.parse(source, matcher);
                CucumberFeature feature = new CucumberFeature(gherkinDocument, pathWithLines.path);
                cucumberFeatures.add(feature);
            } catch (ParserException e) {
                throw new CucumberException(e);
            }
        }
    }

    public CucumberFeature(GherkinDocument gherkinDocument, String path) {
        this.gherkinDocument = gherkinDocument;
        this.path = path;
        if (gherkinDocument.getFeature() != null) {
            setI18n(new GherkinDialectProvider(gherkinDocument.getFeature().getLanguage()).getDefaultDialect());
        }
    }

    public GherkinDocument getGherkinFeature() {
        return gherkinDocument;
    }

    public void setI18n(GherkinDialect i18n) {
        this.i18n = i18n;
    }

    public GherkinDialect getI18n() {
        return i18n;
    }

    public String getPath() {
        return path;
    }

    private static class CucumberFeatureUriComparator implements Comparator<CucumberFeature> {
        @Override
        public int compare(CucumberFeature a, CucumberFeature b) {
            return a.getPath().compareTo(b.getPath());
        }
    }
}
