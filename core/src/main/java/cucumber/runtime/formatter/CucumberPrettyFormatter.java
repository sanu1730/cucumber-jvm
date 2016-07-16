package cucumber.runtime.formatter;

import cucumber.runner.EventBus;

public class CucumberPrettyFormatter implements Formatter, ColorAware {
    private boolean monochrome;

    public CucumberPrettyFormatter(Appendable out) {
    }

    @Override
    public void setEventBus(EventBus bus) {
    }

    @Override
    public void setMonochrome(boolean monochrome) {
        this.monochrome = monochrome;
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub

    }
}
