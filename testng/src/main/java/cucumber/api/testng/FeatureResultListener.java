package cucumber.api.testng;

import cucumber.runtime.CucumberException;
import cucumber.runtime.formatter.Formatter;
import cucumber.runner.EventBus;
import cucumber.runner.EventHandler;
import cucumber.runner.Result;
import cucumber.runner.TestStepFinished;

public class FeatureResultListener implements Formatter {
    static final String PENDING_STATUS = "pending";
    static final String UNDEFINED_MESSAGE = "There are undefined steps";
    static final String PENDING_MESSAGE = "There are pending steps";
    private boolean strict;
    private Throwable error = null;
    private final EventHandler<TestStepFinished> testStepFinishedHandler = new EventHandler<TestStepFinished>() {
        @Override
        public void receive(TestStepFinished event) {
            collectError(event.result);
        }
    };

    public FeatureResultListener(boolean strict) {
        this.strict = strict;
    }

    @Override
    public void setEventBus(EventBus bus) {
        bus.registerHandlerFor(TestStepFinished.class, testStepFinishedHandler);
    }

    @Override
    public void close() {
    }

    void collectError(Result result) {
        if (result.getStatus().equals(Result.FAILED)) {
            if (error == null || isUndefinedError(error) || isPendingError(error)) {
                error = result.getError();
            }
        } else if (result.getStatus().equals(PENDING_STATUS) && strict) {
            if (error == null || isUndefinedError(error)) {
                error = new CucumberException(PENDING_MESSAGE);
            }
        } else if (result.getStatus().equals(Result.UNDEFINED.getStatus()) && strict) {
            if (error == null) {
                error = new CucumberException(UNDEFINED_MESSAGE);
            }
        }
    }

    private boolean isPendingError(Throwable error) {
        return (error instanceof CucumberException) && error.getMessage().equals(PENDING_MESSAGE);
    }

    private boolean isUndefinedError(Throwable error) {
        return (error instanceof CucumberException) && error.getMessage().equals(UNDEFINED_MESSAGE);
    }

    public boolean isPassed() {
        return error == null;
    }

    public Throwable getFirstError() {
        return error;
    }

    public void startFeature() {
        error = null;
    }
}
