package cucumber.runtime.formatter;

import cucumber.runner.EventBus;
import cucumber.runner.EventHandler;
import cucumber.runner.Result;
import cucumber.runner.TestStepFinished;
import cucumber.runner.WriteEvent;
import cucumber.runtime.formatter.Formatter;
import cucumber.runtime.formatter.NiceAppendable;
import cucumber.runtime.formatter.ansi.AnsiEscapes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ProgressFormatter implements Formatter, ColorAware {
    private static final Map<String, Character> CHARS = new HashMap<String, Character>() {{
        put("passed", '.');
        put("undefined", 'U');
        put("pending", 'P');
        put("skipped", '-');
        put("failed", 'F');
    }};
    private static final Map<String, AnsiEscapes> ANSI_ESCAPES = new HashMap<String, AnsiEscapes>() {{
        put("passed", AnsiEscapes.GREEN);
        put("undefined", AnsiEscapes.YELLOW);
        put("pending", AnsiEscapes.YELLOW);
        put("skipped", AnsiEscapes.CYAN);
        put("failed", AnsiEscapes.RED);
    }};

    private final NiceAppendable out;
    private boolean monochrome = false;
    private EventHandler<TestStepFinished> stepFinishedhandler = new EventHandler<TestStepFinished>() {

        @Override
        public void receive(TestStepFinished event) {
            if (!event.definitionMatch.isHook() || event.result.getStatus().equals(Result.FAILED)) {
                if (!monochrome) {
                    ANSI_ESCAPES.get(event.result.getStatus()).appendTo(out);
                }
                out.append(CHARS.get(event.result.getStatus()));
                if (!monochrome) {
                    AnsiEscapes.RESET.appendTo(out);
                }
            }
        }

    };
    private EventHandler<WriteEvent> writeHandler = new EventHandler<WriteEvent>() {

        @Override
        public void receive(WriteEvent event) {
            out.append(event.text);
        }
    };

    public ProgressFormatter(Appendable appendable) {
        out = new NiceAppendable(appendable);
    }

    @Override
    public void close() {
        out.close();
    }

    @Override
    public void setMonochrome(boolean monochrome) {
        this.monochrome = monochrome;
    }

    @Override
    public void setEventBus(EventBus bus) {
        bus.registerHandlerFor(TestStepFinished.class, stepFinishedhandler);
        bus.registerHandlerFor(WriteEvent.class, writeHandler );
    }
}
