get_github_resources() {
  local filter='[ .[] | if .type == "dir" then $repo + .name else empty end ] | join(" ")'
  local cdis_manifest_resources=$(curl -sf "https://api.github.com/repos/uc-cdis/cdis-manifest/contents" | jq -r --arg repo cdis-manifest/ "$filter")
  local gitops_dev_resources=$(curl -sf "https://api.github.com/repos/uc-cdis/gitops-dev/contents" | jq -r --arg repo gitops-dev/ "$filter")
  local gitops_qa_resources=$(curl -sf "https://api.github.com/repos/uc-cdis/gitops-qa/contents" | jq -r --arg repo gitops-qa/ "$filter")

  github_resources="$cdis_manifest_resources $gitops_dev_resources $gitops_qa_resources"
  echo "$github_resources"
}

_script()
{
  # Look for completions in a temporary file; if they don't already exist,
  # pull the completions from github.
  completions_filename='/tmp/config-diff-completions.txt'
  if [ ! -f $completions_filename ]
    then
      completions_file=$(mktemp $completions_filename)
      echo "$(get_github_resources)" >> $completions_file
  fi

  suggestions=($(cat $completions_filename))

  local cur
  COMPREPLY=()
  cur="${COMP_WORDS[COMP_CWORD]}"
  COMPREPLY=( $(compgen -W "${suggestions[*]}" -- ${cur}) )

  return 0
}
complete -o nospace -F _script config-diff