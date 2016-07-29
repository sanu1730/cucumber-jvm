package cucumber.runtime.scala

import _root_.gherkin.TagExpression
import _root_.gherkin.formatter.model.Tag
import _root_.java.util.Collection
import cucumber.api.{Step, Scenario}
import _root_.cucumber.runtime.HookDefinition
import collection.JavaConverters._

class ScalaHookDefinition(f:Scenario => Unit,
                          order:Int,
                          tags:Seq[String]) extends HookDefinition {

  val tagExpression = new TagExpression(tags.asJava)

  def getLocation(detail: Boolean) = "TODO: Implement getLocation in similar fashion to ScalaStepDefinition"

  def execute(scenario: Scenario) { f(scenario) }

  def executeStepHook(step: Step) = "TODO: Implement executeStepHook in similar fashion to execute"

  def matches(tags: Collection[Tag]) = tagExpression.evaluate(tags)

  def getOrder = order

  def isScenarioScoped = false

  def reportingEnabled = true
}