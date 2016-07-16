package cucumber.runtime.android;

import gherkin.GherkinDialect;
import gherkin.pickles.Pickle;

/**
 * Data class to hold the compiled pickles together with their {@link GherkinDialect}.
 */
public class PickleStruct {
    public final Pickle pickle;
    public final GherkinDialect dialect;

    public PickleStruct(Pickle pickle, GherkinDialect dialect) {
        this.pickle = pickle;
        this.dialect = dialect;
    }
}
