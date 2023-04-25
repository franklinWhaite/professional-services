# BigQuery Optimization via Anti-Pattern Recognition

This utility scans a BigQuery SQL in search for several possible anti-patterns. \
Identifying these anti-patterns is the first step in optimizing a SQL since these \
usually have high performance impact. 

For example:

Example:
Input:
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



### Quick Start

Prerequisites:
* [gcloud CLI](https://cloud.google.com/sdk/gcloud)
* Docker

Build ZetaSQL Toolkit 
```
cd ../zetasql-toolkit-core
mvn clean
mvn install
```

Build utility
```
cd ../bigquery-antipattern-recognition
mvn clean
mvn install
mvn compile jib:dockerBuild
```

Run simple inline query
```
docker run \
  -i bigquery-antipattern-recognition \
  --query "SELECT * FROM \`project.dataset.table1\`" 
```

Read from information schema and write to output table: 
1) Create a table with the following DDL:
```
CREATE TABLE dataset.antipattern_output_table (
  job_id STRING,
  query STRING,
  recommendation STRING,
  slot_hours FLOAT64,
  process_timestamp TIMESTAMP
);
```

2) Authenticate 
```
gcloud auth login
```

3) Run 
```
docker run \
  -v ~/.config:/root/.config \
  -i bigquery-antipattern-recognition \
  --read_from_info_schema \
  --read_from_info_schema_days 1 \
  --processing_project_id <my-project> \
  --output_table "my-project.dataset.antipattern_output_table" 
```





## Flags and arguments
### Specify Input
#### To read inline query
`--query="SELECT ... FROM ..."`
<ul>
To parse SQL string provided via CLI.
</ul>

#### To read from INFORMATION_SCHEMA
`--read_from_info_schema`
<ul>
To read input queries from INFORMATION_SCHEMA.JOBS.
</ul>

`--read_from_info_schema_days=n`
<ul>
Specifies how many days of INFORMATION_SCHEMA to read <br> 
Must be set along with `--read_from_info_schema`. <br>
Defaults to 1.
</ul>

``--read_from_info_schema_days="\`region-us\`.INFORMATION_SCHEMA.JOBS"``
<ul>
Specifies what variant of INFORMATION_SCHEMA.JONS to read from.
</ul>

#### To read from a files
`--input_file_path=/path/to/file.sql`
<ul>
Specifies path to file with SQL string to be parsed. Can be local file or GCS file.
</ul>

`--input_folder_path=/path/to/folder/with/sql/files`
<ul>
Specifies path to folder with SQL files to be parsed. Will parse all .sql in directory.<br>
Can be a local path or a GCS path
</ul>

`--input_csv_file_path=/path/to/input/file.csv`
<ul>
Specifies a CSV file as input, each row is a SQL string to be parsed.<br>
Columns must be ""id,query"
</ul>


### Specify output
`--output_file_path=/path/to/output/file.csv`
<ul>
Specifies a CSV file as output, each row is a SQL string to be parsed.<br>
Columns are "id,recommendation"
</ul>

`--output_table="my-project.dataset.antipattern_output_table" `
<ul>
Specifies table to which write results to. Assumes that the table already exits.
</ul>

### Specify compute project
`--processing_project_id=<my-processing-project>`
<ul>
Specifies what project provides the compute used to read from INFORMATION_SCHEMA <br> 
and/or to write to output table (i.e. project where BQ jobs will execute) <br>
Only needed if the input is INFORMATION_SCHEMA or if the output is a BQ table. 
</ul>




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

