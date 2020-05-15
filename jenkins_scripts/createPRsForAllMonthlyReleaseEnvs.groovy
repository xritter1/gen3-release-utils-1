List<String> environments = Arrays.asList(LIST_OF_ENVIRONMENTS.split("\\s*,\\s*"));

def quietPeriod = 0;

def jenkins = Jenkins.getInstance()
def job = jenkins.getItem("hello-world-job-test");

// Comment the line above and uncomment the lines below to create real PRs
//def job = jenkins.getItem("deploy-gen3-release-to-environment");

environments.each {env ->
  println "Creating PR for ${env}...";
  
  def params = []
  
  params += new StringParameterValue("TARGET_ENVIRONMENT", env);
  
  //params += new StringParameterValue("GEN3_RELEASE", "${GEN3_RELEASE}");
  //params += new StringParameterValue("PR_TITLE", "${PR_TITLE}");
  //params += new StringParameterValue("REPO_NAME", "${REPO_NAME}");
  
  def paramsAction = new ParametersAction(params);
  hudson.model.Hudson.instance.queue.schedule(job, quietPeriod, null, paramsAction);
  quietPeriod += 10;
}
