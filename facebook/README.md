# Facebook


## Synopsis

This Folder contain the Facebook Spark Job

## Requirement

scalaVersion 2.11.6

## Installation

Provide code examples and explanations of how to get the project.

## Test in SBT

```
sbt "run localhost neo4j admin DirectKafkaWordCount spark.master local[*] bigdata2.ip-188-165-248.eu:9092,bigdata3.ip-188-165-248.eu:9092,bigdata4.ip-188-165-248.eu:9092 facebook  identifiedFacebook NonIdentifiedFacebook true"
```

## Tests in SPARK SUBMIT

```
spark-submit "JAR_PATH" localhost neo4j admin DirectKafkaWordCount spark.master local[*] bigdata2.ip-188-165-248.eu:9092,bigdata3.ip-188-165-248.eu:9092,bigdata4.ip-188-165-248.eu:9092 facebook  identifiedFacebook NonIdentifiedFacebook true
```

## Unidentified USER (CSV)

```
id"\t"from_id"\t"from_name"\t"created_time"\t"message"\t"language"\t"type"\t"likes_count"\t"shares_count"\t"comment_count
```

## Identified USER (CSV)

```
id"\t"from_id"\t"from_name"\t"created_time"\t"message"\t"language"\t"type"\t"likes_count"\t"shares_count"\t"comment_count"\t"IdUser
```