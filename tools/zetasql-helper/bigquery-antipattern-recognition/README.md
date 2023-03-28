# BigQuery Anti-Pattern

This projects provides utilities to analyze BigQuery SQL to identify a set of 
anti-patterns and provide commendation on how to correct them

## Quick Start 
```
mvn clean
mvn install
mvn compile jib:dockerBuild

docker run -v "$HOME/.config/gcloud:/gcp/config:ro" \
-v /gcp/config/logs \
--env CLOUDSDK_CONFIG=/gcp/config \
--env GOOGLE_APPLICATION_CREDENTIALS=/gcp/config/application_default_credentials.json \
-i bigquery-antipattern-recognition --read_from_info_schema --processing_project_id pso-dev-whaite --output_project_id pso-dev-whaite
```
## Anti patterns
### Anti Pattern 1: Selecting all columns 
Example:
```
SELECT 
    * 
FROM 
    `project.dataset.table1`
```

Output: 
```
All columns on table: project.dataset.table1 are being selected. Please be sure that all columns are needed
```


### Anti Pattern 2: Using CROSS JOINs when INNER JOINs are an option
Example:
```
SELECT
   t1.col1
FROM 
   `project.dataset.table1` t1 
cross JOIN " +
    `project.dataset.table2` t2
WHERE
   t1.col1 = t2.col1;
```

Output:
```
CROSS JOIN between tables: project.dataset.table1 and project.dataset.table2. Try to change for a INNER JOIN if possible.
```

### Anti Pattern 3: Not aggregating subquery in the WHERE clause,
Example:
```
SELECT 
   t1.col1 
FROM 
   `project.dataset.table1` t1 
WHERE 
    t1.col2 not in (select col2 from `project.dataset.table2`);
```

Output:
```
You are using an IN filter with a subquery without a DISTINCT on the following columns: project.dataset.table1.col2
```


### Packaging examples into containers

You can package an example into a container
using [Jib](https://cloud.google.com/java/getting-started/jib)
by running the following command on the [root project directory](..).

`mvn clean packge jib:build -Dcontainer.mainClass=MAIN_CLASS`

Example:

`mvn package jib:dockerBuild -Dcontainer.mainClass=com.google.zetasql.toolkit.examples.AnalyzeWithoutCatalog`
