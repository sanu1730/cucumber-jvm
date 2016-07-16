package cucumber.runtime.formatter;

import cucumber.runner.EventBus;
import cucumber.runner.EventHandler;
import cucumber.runner.TestCaseFinished;
import cucumber.runner.TestCaseStarted;
import cucumber.runner.TestStepFinished;
import cucumber.runner.TestStepStarted;
import cucumber.runtime.formatter.Formatter;

public class FormatterSpy implements Formatter{
    StringBuilder calls = new StringBuilder();
    private final EventHandler<TestCaseStarted> testCaseStartedHandler = new EventHandler<TestCaseStarted>() {
        @Override
        public void receive(TestCaseStarted event) {
            calls.append("TestCase started\n");
        }
    };
    private final EventHandler<TestCaseFinished> testCaseFinishedHandler = new EventHandler<TestCaseFinished>() {
        @Override
        public void receive(TestCaseFinished event) {
            calls.append("TestCase finished\n");
        }
    };
    private final EventHandler<TestStepStarted> testStepStartedHandler = new EventHandler<TestStepStarted>() {
        @Override
        public void receive(TestStepStarted event) {
            calls.append("  TestStep started\n");
        }
    };
    private final EventHandler<TestStepFinished> testStepFinishedHandler = new EventHandler<TestStepFinished>() {
        @Override
        public void receive(TestStepFinished event) {
            calls.append("  TestStep finished\n");
        }
    };

    @Override
    public void setEventBus(EventBus bus) {
        bus.registerHandlerFor(TestCaseStarted.class, testCaseStartedHandler);
        bus.registerHandlerFor(TestCaseFinished.class, testCaseFinishedHandler);
        bus.registerHandlerFor(TestStepStarted.class, testStepStartedHandler);
        bus.registerHandlerFor(TestStepFinished.class, testStepFinishedHandler);
    }

    @Override
    public void close() {
        calls.append("close\n");
    }

    @Override
    public String toString() {
        return calls.toString();
    }
}
