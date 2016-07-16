package cucumber.api.testng;

import cucumber.runtime.Utils;
import cucumber.runtime.formatter.Formatter;
import cucumber.runtime.formatter.NiceAppendable;
import gherkin.pickles.PickleStep;
import cucumber.runner.EventBus;
import cucumber.runner.EventHandler;
import cucumber.runner.Result;
import cucumber.runner.TestStepFinished;
import org.testng.ITestResult;

import static org.testng.Reporter.getCurrentTestResult;
import static org.testng.Reporter.log;

public class TestNgReporter implements Formatter {
    private final NiceAppendable out;
    private final EventHandler<TestStepFinished> testStepFinishedHandler = new EventHandler<TestStepFinished>() {
        @Override
        public void receive(TestStepFinished event) {
            if (!event.definitionMatch.isHook()) {
                result(event.definitionMatch.getStep(), event.result);
            }
        }
    };


    public TestNgReporter(Appendable appendable) {
        out = new NiceAppendable(appendable);
    }

    @Override
    public void setEventBus(EventBus bus) {
        bus.registerHandlerFor(TestStepFinished.class, testStepFinishedHandler);
    }

    public void uri(String uri) {
        // TODO: find an appropriate keyword
        String keyword = "Feature File";
        logDiv(keyword, uri, "featureFile");
    }

    @Override
    public void close() {
        out.close();
    }

    private void result(PickleStep pickleStep, Result result) {
        logResult(pickleStep, result);

        if (Result.FAILED.equals(result.getStatus())) {
            ITestResult tr = getCurrentTestResult();
            tr.setThrowable(result.getError());
            tr.setStatus(ITestResult.FAILURE);
        } else if (Result.SKIPPED.equals(result)) {
            ITestResult tr = getCurrentTestResult();
            tr.setThrowable(result.getError());
            tr.setStatus(ITestResult.SKIP);
        } else if (Result.UNDEFINED.equals(result)) {
            ITestResult tr = getCurrentTestResult();
            tr.setThrowable(result.getError());
            tr.setStatus(ITestResult.FAILURE);
        }
    }

    private void logResult(PickleStep pickleStep, Result result) {
        String timing = computeTiming(result);

        String format = "%s (%s%s)";
        String message = String.format(format, pickleStep.getText(), result.getStatus(), timing);

        logDiv(message, "result");
    }

    private String computeTiming(Result result) {
        String timing = "";

        if (result.getDuration() != null) {
            // TODO: Get known about the magic nature number and get rid of it.
            int duration = Math.round(result.getDuration() / 1000000000);
            timing = " : " + duration + "s";
        }

        return timing;
    }

    private void logDiv(String message, String cssClassName) {
        String format = "<div \"%s\">%s</div>";
        String output = String.format(format, cssClassName, Utils.htmlEscape(message));
        log(output);
    }

    private void logDiv(String message, String message2, String cssClassName) {
        logDiv(message + ": " + message2, cssClassName);
    }

}
