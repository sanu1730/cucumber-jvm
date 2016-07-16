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

public class UnskipableTestStepTest {

    @Test
    public void run_execute_even_when_skip_steps_is_true() throws Throwable {
        DefinitionMatch definitionMatch = mock(DefinitionMatch.class);
        EventBus bus = mock(EventBus.class);
        GherkinDialect i18n = mock(GherkinDialect.class);
        Scenario scenario = mock(Scenario.class);

        UnskipableStep step = new UnskipableStep(definitionMatch, StopWatch.SYSTEM);
        step.run(bus, i18n, scenario, true);

        InOrder order = inOrder(bus, definitionMatch);
        order.verify(bus).send(isA(TestStepStarted.class));
        order.verify(definitionMatch).runStep(i18n, scenario);
        order.verify(bus).send(isA(TestStepFinished.class));
    }


}
