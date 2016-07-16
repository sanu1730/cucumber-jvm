package cucumber.runtime.formatter;

import cucumber.runner.EventBus;

import java.io.Closeable;

/**
 * This is the interface you should implement if you want your own custom
 * formatter.
 */
public interface Formatter extends Closeable {

    /**
     * Set the event bus that the formatter can register event listeners in.
     */
    void setEventBus(EventBus bus);

    /**
     * Closes all underlying streams.
     */
    void close();

}
