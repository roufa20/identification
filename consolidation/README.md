# Consolidation


## Synopsis

This Folder contain the Conso Spark Job

## Requirement

scalaVersion 2.11.6

## Installation

Provide code examples and explanations of how to get the project.

## Test in SBT

```
sbt "run localhost neo4j admin DirectKafkaWordCount spark.master local[*] bigdata2.ip-188-165-248.eu:9092,bigdata3.ip-188-165-248.eu:9092,bigdata4.ip-188-165-248.eu:9092 consolidation identifiedConsolidationB2B identifiedConsolidationB2C true"
```

## Tests in SPARK SUBMIT

```
spark-submit "JAR_PATH" localhost neo4j admin DirectKafkaWordCount spark.master local[*] bigdata2.ip-188-165-248.eu:9092,bigdata3.ip-188-165-248.eu:9092,bigdata4.ip-188-165-248.eu:9092 consolidation identifiedConsolidationB2B identifiedConsolidationB2C true
```
* localhost : host of Neo4j
 
* neo4j : neo4j login
 
* admin : neo4j password
 
* DirectKafkaWordCount: ApplicationName
 
* spark.master : master param
 
* local[2] : cluster Param
 
* bigdata2.ip-188-165-248.eu:9092,bigdata3.ip-188-165-248.eu:9092,bigdata4.ip-188-165-248.eu:9092 : broker.list
 
* consolidation : GET topic
 
* identifiedConsolidationB2B : POST topic to B2B Conso
 
* identifiedConsolidationB2C : POST topic to B2C Conso

* true : if you want to read from beginning, if not Pleas Make It false

## identified USER (CSV)

```
givenname"\t"parentid"\t"product_id"\t"location"\t"displayedcategory"\t"address"\t"value_segment"\t"customer_code"\t" product_code"\t"familyname"\t"identification_type"\t"imsi"\t"id_carte_icc"\t"identification_number"\t"tel"\t" billing_account_id"\t"gender"\t"birthdate"\t"msisdn"\t"contract_number"\t"lifecycle_status"\t"formattedname"\t"customer_type"\t"fullname"\t"customer_segment
```
