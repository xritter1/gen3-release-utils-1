import org.jenkinsci.plugins.workflow.job.WorkflowRun
import org.jenkinsci.plugins.workflow.flow.FlowExecution
import org.jenkinsci.plugins.workflow.graph.FlowNode
import org.jenkinsci.plugins.workflow.graph.FlowGraphWalker
import io.jenkins.blueocean.rest.impl.pipeline.FlowNodeWrapper
import io.jenkins.blueocean.rest.impl.pipeline.PipelineNodeGraphVisitor
import org.jenkinsci.plugins.workflow.actions.LogAction

import groovy.json.JsonBuilder

@NonCPS
def assembleMetrics() {
  JsonBuilder builder = new JsonBuilder();
  Map<String, Object> prChecks = new HashMap<String, Object>();

  Jenkins.instance.getAllItems(Job.class).each{
    if (it.getFullName().contains("CDIS GitHub Org/gen3-qa") && it.isBuilding() ) {
      println('short name: ' + it.getFullName().split("/")[2])
      full_build_name = it.getFullName()
      short_name = full_build_name.split("/")[2]
      full_build_name = it.getFullName()
      WorkflowRun run = Jenkins.instance.getItemByFullName(full_build_name)._getRuns()[0]

      FlowExecution exec = run.getExecution()
      PipelineNodeGraphVisitor visitor = new PipelineNodeGraphVisitor(run)
      def flowNodes = visitor.getPipelineNodes()
      for (Iterator iterator = flowNodes.iterator(); iterator.hasNext();)
      {
        def node = iterator.next()
        if (node.getType() != FlowNodeWrapper.NodeType.STAGE)
        {
          continue;
        }
        String stageName = node.getDisplayName()
        if ( stageName != "SelectNamespace")
        {
          continue;
        }
        node_output = node.getRun().getLog(100)
        for (log_entry in node_output) {
          //println "trying to find the namespace for PR: ${full_build_name}"
          def kubectl_namespace = log_entry =~ /jenkins-.*\.planx\-pla\.net/
          if (kubectl_namespace.size() > 0) {
            println("The Jenkins namespace is: ${kubectl_namespace[0]}")
            prChecks.put("${short_name}", "${kubectl_namespace[0]}")
            break
          }
        }
      }
    }
  }

  builder.call(prChecks);
  writeFile file: 'prChecks.json', text: builder.toString()    
}

node {
  assembleMetrics()
}

