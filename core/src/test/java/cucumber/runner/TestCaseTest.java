package cucumber.runner;

import cucumber.api.Scenario;
import gherkin.GherkinDialect;
import gherkin.pickles.Pickle;
import gherkin.pickles.PickleTag;
import org.junit.Test;
import org.mockito.InOrder;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestCaseTest {

    @Test
    public void run_wraps_execute_in_test_case_started_and_finished_events() throws Throwable {
        EventBus bus = mock(EventBus.class);
        GherkinDialect i18n = mock(GherkinDialect.class);
        TestStep testStep = mock(TestStep.class);
        when(testStep.run(eq(bus), eq(i18n), isA(Scenario.class), eq(false))).thenReturn(Result.UNDEFINED);

        TestCase testCase = new TestCase(Arrays.asList(testStep), Collections.<PickleTag>emptyList(), mock(Pickle.class));
        testCase.run(bus, i18n);

        InOrder order = inOrder(bus, testStep);
        order.verify(bus).send(isA(TestCaseStarted.class));
        order.verify(testStep).run(eq(bus), eq(i18n), isA(Scenario.class), eq(false));
        order.verify(bus).send(isA(TestCaseFinished.class));
    }

}
