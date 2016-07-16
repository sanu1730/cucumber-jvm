package cucumber.runtime.formatter;

import cucumber.runner.EventBus;
import cucumber.runner.Result;
import cucumber.runtime.formatter.Formatter;
import cucumber.runtime.formatter.NiceAppendable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Formatter for reporting all failed features and print their locations
 * Failed means: (failed, undefined, pending) test result
 */
class RerunFormatter implements Formatter, StrictAware {
    private final NiceAppendable out;
    private String featureLocation;
    private boolean isTestFailed = false;
    private Map<String, ArrayList<Integer>> featureAndFailedLinesMapping = new HashMap<String, ArrayList<Integer>>();
    private boolean isStrict = false;

    public RerunFormatter(Appendable out) {
        this.out = new NiceAppendable(out);
    }

    @Override
    public void setEventBus(EventBus bus) {
    }

//    @Override
//    public void uri(String uri) {
//        this.featureLocation = uri;
//    }

//    @Override
//    public void done() {
//        reportFailedScenarios();
//    }

    private void reportFailedScenarios() {
        Set<Map.Entry<String, ArrayList<Integer>>> entries = featureAndFailedLinesMapping.entrySet();
        boolean firstFeature = true;
        for (Map.Entry<String, ArrayList<Integer>> entry : entries) {
            if (!entry.getValue().isEmpty()) {
                if (!firstFeature) {
                    out.append(" ");
                }
                out.append(entry.getKey());
                firstFeature = false;
                for (Integer line : entry.getValue()) {
                    out.append(":").append(line.toString());
                }
            }
        }
    }

    @Override
    public void close() {
        this.out.close();
    }

    private boolean isTestFailed(Result result) {
        String status = result.getStatus();
        return Result.FAILED.equals(status) || isStrict && (Result.UNDEFINED.getStatus().equals(status) || "pending".equals(status));
    }

    private void recordTestFailed() {
        ArrayList<Integer> failedScenarios = this.featureAndFailedLinesMapping.get(featureLocation);
        if (failedScenarios == null) {
            failedScenarios = new ArrayList<Integer>();
            this.featureAndFailedLinesMapping.put(featureLocation, failedScenarios);
        }

        //failedScenarios.add(scenario.getLine());
    }

    @Override
    public void setStrict(boolean strict) {
        isStrict = strict;
    }
}
