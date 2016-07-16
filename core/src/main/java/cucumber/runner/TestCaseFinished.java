package cucumber.runner;

public class TestCaseFinished implements Event {
    public final Result result;
    public final TestCase testCase;

    public TestCaseFinished(TestCase testCase, Result result) {
        this.testCase = testCase;
        this.result = result;
    }

}
