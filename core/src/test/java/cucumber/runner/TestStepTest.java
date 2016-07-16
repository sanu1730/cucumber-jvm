package cucumber.runner;

import cucumber.api.Scenario;
import cucumber.runtime.DefinitionMatch;
import cucumber.runtime.StopWatch;
import gherkin.GherkinDialect;
import org.junit.Test;
import org.mockito.InOrder;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

public class TestStepTest {

    @Test
    public void run_wraps_execute_in_test_step_started_and_finished_events() throws Throwable {
        DefinitionMatch definitionMatch = mock(DefinitionMatch.class);
        EventBus bus = mock(EventBus.class);
        GherkinDialect i18n = mock(GherkinDialect.class);
        Scenario scenario = mock(Scenario.class);

        TestStep step = new TestStep(definitionMatch, StopWatch.SYSTEM);
        step.run(bus, i18n, scenario, false);

        InOrder order = inOrder(bus, definitionMatch);
        order.verify(bus).send(isA(TestStepStarted.class));
        order.verify(definitionMatch).runStep(i18n, scenario);
        order.verify(bus).send(isA(TestStepFinished.class));
    }

    @Test
    public void run_does_not_execute_when_skip_steps_is_true() throws Throwable {
        DefinitionMatch definitionMatch = mock(DefinitionMatch.class);
        EventBus bus = mock(EventBus.class);
        GherkinDialect i18n = mock(GherkinDialect.class);
        Scenario scenario = mock(Scenario.class);

        TestStep step = new TestStep(definitionMatch, StopWatch.SYSTEM);
        step.run(bus, i18n, scenario, true);

        InOrder order = inOrder(bus, definitionMatch);
        order.verify(bus).send(isA(TestStepStarted.class));
        order.verify(bus).send(isA(TestStepFinished.class));
    }

}
