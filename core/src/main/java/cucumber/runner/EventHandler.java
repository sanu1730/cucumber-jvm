package cucumber.runner;


public interface EventHandler<T extends Event> {

    public void receive(T event);

}
