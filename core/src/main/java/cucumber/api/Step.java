package cucumber.api;

/**
 * Before or After Step Hooks that declare a parameter of this type will receive an instance of this class.
 * It allows writing text and embedding media into reports, as well as inspecting results (in an After block).
 */
public interface Step {

    /**
     * @return the step status. One of "passed", "undefined", "pending", "skipped", "failed"
     */
    String getStatus();

    /**
     * @set the step status. One of "passed", "undefined", "pending", "skipped", "failed"
     */
    void setStatus(String status);

    /**
     * @return true if and only if {@link #getStatus()} returns "failed"
     */
    boolean isFailed();

    /**
     * Embeds data into the report(s). Some reporters (such as the progress one) don't embed data, but others do (html and json).
     * Example:
     *
     * <pre>
     * {@code
     * // Embed a screenshot. See your UI automation tool's docs for
     * // details about how to take a screenshot.
     * step.embed(pngBytes, "image/png");
     * }
     * </pre>
     *
     * @param data     what to embed, for example an image.
     * @param mimeType what is the data?
     */
    void embed(byte[] data, String mimeType);

    /**
     * Outputs some text into the report.
     *
     * @param text what to put in the report.
     */
    void write(String text);

    /**
     *
     * @return the name of the step
     */
    String getName();

    gherkin.formatter.model.Step getGherkinStep();
}
