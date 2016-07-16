package cucumber.runtime;

import cucumber.api.Scenario;
import gherkin.GherkinDialect;
import gherkin.pickles.PickleStep;

import java.util.Collections;

public class HookDefinitionMatch implements DefinitionMatch {
    private final HookDefinition hookDefinition;

    public HookDefinitionMatch(HookDefinition hookDefinition) {
        this.hookDefinition = hookDefinition;
    }

    @Override
    public void runStep(GherkinDialect i18n, Scenario scenario) throws Throwable {
        hookDefinition.execute(scenario);
    }

    @Override
    public void dryRunStep(GherkinDialect i18n, Scenario scenario) throws Throwable {
        // Do nothing
    }

    @Override
    public Match getMatch() {
        return new Match(Collections.<Argument>emptyList(), hookDefinition.getLocation(false));
    }

    @Override
    public boolean isHook() {
        return true;
    }

    @Override
    public PickleStep getStep() {
        throw new UnsupportedOperationException();
    }

}
