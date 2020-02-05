import org.jenkinsci.plugins.workflow.job.WorkflowRun
import org.jenkinsci.plugins.workflow.flow.FlowExecution
import org.jenkinsci.plugins.workflow.graph.FlowNode
import org.jenkinsci.plugins.workflow.graph.FlowGraphWalker
import io.jenkins.blueocean.rest.impl.pipeline.FlowNodeWrapper
import io.jenkins.blueocean.rest.impl.pipeline.PipelineNodeGraphVisitor
import org.jenkinsci.plugins.workflow.actions.LogAction

import groovy.json.JsonSlurperClassic
import groovy.json.JsonBuilder

@NonCPS
def assembleMetrics() {
  JsonBuilder builder = new JsonBuilder();
  Map<String, Object> prChecks = new HashMap<String, Object>();

  Jenkins.instance.getAllItems(Job.class).each{
    if (it.getFullName().contains("CDIS GitHub Org/") && it.isBuilding() ) {
      println('short name: ' + it.getFullName().split("/")[2])
      full_build_name = it.getFullName()
      repo_name = full_build_name.split("/")[1]
      pr_number = full_build_name.split("/")[2]
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
        println('stage: ' + stageName)
        run = node.getRun()
        envVars = run.getEnvVars()
        //println(envVars)
        author = envVars['CHANGE_AUTHOR']
        node_output = run.getLog(80)
        for (log_entry in node_output) {
          //println "trying to find the namespace for PR: ${full_build_name}"
          def kubectl_namespace = log_entry =~ /\/\/(.*)\.planx\-pla\.net/
          if (kubectl_namespace.size() > 0) {
            println("The Jenkins namespace is: ${kubectl_namespace[0][1]}")
            prChecks.put("${pr_number}", new groovy.json.JsonSlurperClassic().parseText('{ "repo": "' + repo_name + '", "namespace": "' + kubectl_namespace[0][1] + '", "by": "'+ author +'" }') )
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

