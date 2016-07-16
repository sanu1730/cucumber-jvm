package cucumber.runner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventBus {
    private Map<Class<? extends Event>, List<EventHandler>> handlers = new HashMap<Class<? extends Event>, List<EventHandler>>();

    public void send(Event event) {
        if (handlers.containsKey(event.getClass())) {
            for (EventHandler handler : handlers.get(event.getClass())) {
                handler.receive(event);
            }
        }
    }

    public void registerHandlerFor(Class<? extends Event> eventType, EventHandler<? extends Event> handler) {
        if (handlers.containsKey(eventType)) {
            handlers.get(eventType).add(handler);
        } else {
            List<EventHandler> list = new ArrayList<EventHandler>();
            list.add(handler);
            handlers.put(eventType, list);
        }
    }

}
