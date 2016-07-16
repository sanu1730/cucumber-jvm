package cucumber.runtime.android;

import android.util.Log;
import cucumber.runner.EventBus;
import cucumber.runner.EventHandler;
import cucumber.runner.TestCaseStarted;
import cucumber.runner.TestStepStarted;
import cucumber.runtime.Runtime;
import cucumber.runtime.formatter.Formatter;

/**
 * Logs information about the currently executed statements to androids logcat.
 */
public class  AndroidLogcatReporter implements Formatter {

    /**
     * The {@link cucumber.runtime.Runtime} to get the errors and snippets from for writing them to the logcat at the end of the execution.
     */
    private final Runtime runtime;

    /**
     * The log tag to be used when logging to logcat.
     */
    private final String logTag;

    /**
     * The event handler that logs the {@link TestCaseStarted} events.
     */
    private final EventHandler<TestCaseStarted> testCaseStartedHandler = new EventHandler<TestCaseStarted>() {
        @Override
        public void receive(TestCaseStarted event) {
            Log.d(logTag, String.format("%s", event.testCase.getName()));
        }
    };

    /**
     * The event handler that logs the {@link TestStepStarted} events.
     */
    private final EventHandler<TestStepStarted> testStepStartedHandler = new EventHandler<TestStepStarted>() {
        @Override
        public void receive(TestStepStarted event) {
            Log.d(logTag, String.format("%s", event.definitionMatch.getStep().getText()));
        }
    };

    /**
     * Creates a new instance for the given parameters.
     *
     * @param runtime the {@link cucumber.runtime.Runtime} to get the errors and snippets from
     * @param logTag the tag to use for logging to logcat
     */
    public AndroidLogcatReporter(final Runtime runtime, final String logTag) {
        this.runtime = runtime;
        this.logTag = logTag;
    }

    @Override
    public void setEventBus(final EventBus bus) {
        bus.registerHandlerFor(TestCaseStarted.class, testCaseStartedHandler);
        bus.registerHandlerFor(TestStepStarted.class, testStepStartedHandler);
    }

    @Override
    public void close() {
        for (final Throwable throwable : runtime.getErrors()) {
            Log.e(logTag, throwable.toString());
        }

        for (final String snippet : runtime.getSnippets()) {
            Log.w(logTag, snippet);
        }
    }
}
