package cucumber.runtime.formatter;

import cucumber.runner.EventBus;
import cucumber.runtime.formatter.Formatter;

import java.util.List;

class NullFormatter implements Formatter {
    public NullFormatter() {
    }

    @Override
    public void setEventBus(EventBus bus) {
    }

    @Override
    public void close() {
    }
}
