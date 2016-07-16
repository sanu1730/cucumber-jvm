package cucumber.runner;

import cucumber.runtime.DefinitionMatch;

public class TestStepStarted implements Event {
    public final DefinitionMatch definitionMatch;

    public TestStepStarted(DefinitionMatch definitionMatch) {
        this.definitionMatch = definitionMatch;
    }

}
