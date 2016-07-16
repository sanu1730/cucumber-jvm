package cucumber.runtime;

import cucumber.api.Scenario;
import gherkin.GherkinDialect;
import gherkin.pickles.PickleStep;;

public class UndefinedStepDefinitionMatch extends StepDefinitionMatch {

    public UndefinedStepDefinitionMatch(PickleStep step) {
        super(null, new NoStepDefinition(), null, step, null);
    }

    @Override
    public void runStep(GherkinDialect i18n, Scenario scenario) throws Throwable {
        throw new UndefinedStepDefinitionException();
    }

    @Override
    public void dryRunStep(GherkinDialect i18n, Scenario scenario) throws Throwable {
        runStep(i18n, scenario);
    }

    @Override
    public Match getMatch() {
        return Match.UNDEFINED;
    }
}
