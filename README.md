# gen3-release-utils
Gen3 release process automation tools

## Config Diff

This script diffs the configurations of two or more gen3 commons.
By default, the script will diff the commons' main configuration (manifest.json) and then its portal configuration (gitops.json).
To diff only manifest.json, pass the -(-m)anifest flag. Likewise with the -(-portal) flag.

### Usage
```
config-diff [-(-m)anifest] [-(-p)ortal] <[repo/]namespace[:branch]> <[repo/]namespace[:branch]>
```
e.g., `config-diff --manifest gen3.datastage.io:stable gitops-qa/qa-datastage.planx-pla.net`.

If 'repo' is not specified, it is assumed to be 'cdis-manifest'.
If 'branch' is not specified, it is assumed to be 'master'
If neither the -m or -p flags are passed, the tool only diffs the manifest.json file.

### Installation
> NOTE: Requires jq https://stedolan.github.io/jq/ and vimdiff.
1. `git clone https://github.com/uc-cdis/gen3-release-utils`
2. `install ./gen3-release-utils/config-diff ~/bin`

To install bash completions, add this line to your .bashrc/.bash_profile:
`source path/to/gen3-release-utils/_config-diff-completions.sh`

To install oh-my-zsh completions, follow these steps:
1. `cp ./gen3-release-utils/_config-diff-completions.zsh ~/.oh-my-zsh/completions/_config-diff`
2. `rm “$ZSH_COMPDUMP”`
3. `exec zsh`


## Manifest replication

This utility (replicate_manifest_config.sh) should facilitate the copy of versions (including dictionary_url, etc.) between `manifest.json` files, i.e., improving the release process so it will be more automated & less error-prone.

 - How to use it:
 Here is the syntax:
 > /bin/bash replicate_manifest_config.sh &lt;branch>/&lt;environment>/manifest.json &lt;target_manifest>

 Example:
 > % /bin/bash ../gen3-release-utils/replicate_manifest_config.sh master/internalstaging.datastage.io/manifest.json gen3.datastage.io/manifest.json

## Release Notes Generation

This utility uses the gen3git tool to generate release notes for Gen3 monthly releases

- How to use it:
    - Install Python and [gen3git](https://github.com/uc-cdis/release-helper/) command line utility
    ```pip install --editable git+https://github.com/uc-cdis/release-helper.git@gen3release#egg=gen3git```
    - Update `repo_list.txt` with the repositories from which release notes need to be generated
    - Update the variables `githubAccessToken`, `startDate`, `endDate` in the script `generate_release_notes.sh`
    - Execute generate_release_notes.sh
    ```./generate_release_notes.sh```

## Make new branches from existing ones

This utility is used to create integration and stable branches in multiple repos.

- How to use it:
    - Update `repo_list.txt` with the repositories where branches need to be created
    - Execute the script as follows:
    ```./make_branch.sh <sourceBranchName> <targetBranchName```
