package cucumber.runtime;

import cucumber.api.Scenario;
import gherkin.GherkinDialect;
import gherkin.pickles.PickleStep;;

public interface DefinitionMatch {
    public void runStep(GherkinDialect i18n, Scenario scenario) throws Throwable;

    public void dryRunStep(GherkinDialect i18n, Scenario scenario) throws Throwable;

    public boolean isHook();

    public Match getMatch();

    public PickleStep getStep();
}
