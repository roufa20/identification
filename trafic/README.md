# Trafic


## Synopsis

This Folder contain the Trafic Spark Job

## Requirement

scalaVersion 2.11.6

## Installation

Provide code examples and explanations of how to get the project.

## Test in SBT

```
sbt "run localhost neo4j admin DirectKafkaWordCount spark.master local[*] bigdata2.ip-188-165-248.eu:9092,bigdata3.ip-188-165-248.eu:9092,bigdata4.ip-188-165-248.eu:9092 trafic identifiedTrafic true"
```


## Tests in SPARK SUBMIT

```
spark-submit "JAR_PATH" localhost neo4j admin DirectKafkaWordCount spark.master local[*] bigdata2.ip-188-165-248.eu:9092,bigdata3.ip-188-165-248.eu:9092,bigdata4.ip-188-165-248.eu:9092 trafic identifiedTrafic true
```

* DirectKafkaWordCount: ApplicationName
 
* spark.master : master param
 
* local[2] : cluster Param
 
* bigdata2.ip-188-165-248.eu:9092,bigdata3.ip-188-165-248.eu:9092,bigdata4.ip-188-165-248.eu:9092 : broker.list
 
* trafic : GET topic
 
* identifiedTrafic : POST topic
 
* true : if you want to read from beginning, if not Pleas Make It false


## identified USER (CSV)

```
lastmodifieddate"\t"file_path"\t"file_name"\t"displayurl"\t"file_size"\t"file_extension"\t"line_number"\t"owner_name"\t"security"\t"id"\t"start_date"\t"end_date"\t"duration"\t"unit_code"\t"value"\t"msisdn"\t"installed_product_id"\t"affected_balance_id"\t"called_number"\t"calling_number"\t"destination_code"\t"destination_label"\t"is_billed"\t"type"\t"real_duration"\t"balance_name"\t"balanceamount_beforeeventbalanceamount_afterevent"\t"customerid
```
