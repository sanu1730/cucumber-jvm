package cucumber.runtime;

import cucumber.api.Scenario;
import cucumber.api.Step;
import gherkin.formatter.model.Tag;

import java.util.Collection;

public interface HookDefinition {
    /**
     * The source line where the step definition is defined.
     * Example: foo/bar/Zap.brainfuck:42
     *
     * @param detail true if extra detailed location information should be included.
     */
    String getLocation(boolean detail);

    void execute(Scenario scenario) throws Throwable;

    void executeStepHook(Step step) throws Throwable;

    boolean matches(Collection<Tag> tags);

    int getOrder();

    /**
     * @return true if this instance is scoped to a single scenario, or false if it can be reused across scenarios.
     */
    boolean isScenarioScoped();

    boolean reportingEnabled();
}
