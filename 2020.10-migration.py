import sys
import subprocess

env = sys.argv[1]

# Find all doc_type properties in etlMapping.yaml
etlMappingPath = env + "/etlMapping.yaml"
portalConfigPath = env + "/portal/gitops.json"
with open(etlMappingPath, "r+") as etlMappingFile:
    doc_types = []
    for line in etlMappingFile:
        if "doc_type:" in line:
            doc_type = line.split("doc_type:")[1].strip()
            doc_types.append(doc_type)

print(doc_types)

# Replace all instances of `{doc_type}_id` with `_{doc_type}_id` in gitops.json and etlMapping.yaml
for doc_type in doc_types:
    print(f"Replacing `{doc_type}_id` with `_{doc_type}_id` in {portalConfigPath}...")
    # add a space before doc_type to prevent adding a double underscore
    subprocess.check_output(
        fr"sed -i '' 's/\"{doc_type}_id/\"_{doc_type}_id/' {portalConfigPath}",
        shell=True,
    )
    print(f"Replacing `{doc_type}_id` with `_{doc_type}_id` in {etlMappingPath}...")
    # add a quotmark before doc_type to prevent adding a double underscore
    subprocess.check_output(
        fr"sed -i '' 's/\ {doc_type}_id/\ _{doc_type}_id/' {etlMappingPath}", shell=True
    )
