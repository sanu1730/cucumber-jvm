package cucumber.runner;

import cucumber.runtime.DefinitionMatch;

public class TestStepFinished implements Event {
    public final DefinitionMatch definitionMatch;
    public final Result result;

    public TestStepFinished(DefinitionMatch definitionMatch, Result result) {
        this.definitionMatch = definitionMatch;
        this.result = result;
    }

}
