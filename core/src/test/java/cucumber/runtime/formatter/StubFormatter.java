package cucumber.runtime.formatter;

import cucumber.runner.EventBus;
import cucumber.runtime.formatter.Formatter;

public class StubFormatter implements Formatter {

    @Override
    public void setEventBus(EventBus bus) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException();
    }

}
