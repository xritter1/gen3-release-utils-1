#compdef _config-diff config-diff

get_github_resources() {
  local filter='[ .[] | if .type == "dir" then $repo + .name else empty end ] | join(" ")'
  local cdis_manifest_resources=$(curl -sf "https://api.github.com/repos/uc-cdis/cdis-manifest/contents" | jq -r --arg repo cdis-manifest/ "$filter")
  local gitops_dev_resources=$(curl -sf "https://api.github.com/repos/uc-cdis/gitops-dev/contents" | jq -r --arg repo gitops-dev/ "$filter")
  local gitops_qa_resources=$(curl -sf "https://api.github.com/repos/uc-cdis/gitops-qa/contents" | jq -r --arg repo gitops-qa/ "$filter")

  github_resources="$cdis_manifest_resources $gitops_dev_resources $gitops_qa_resources"
  echo "$github_resources"
}

function _config-diff {
    # load environments list line-by-line into an array
    local -a suggestions
    suggestions=($(get_github_resources))
    _describe 'command' suggestions
}
