# CustomerOrder


## Synopsis

This Folder contain the CustomerOrder Spark Job

## Requirement

scalaVersion 2.11.6

## Installation

Provide code examples and explanations of how to get the project.

## Test in SBT

```
sbt "run DirectKafkaWordCount spark.master local[*] bigdata2.ip-188-165-248.eu:9092,bigdata3.ip-188-165-248.eu:9092,bigdata4.ip-188-165-248.eu:909 customerOrder identifiedCustomerOrder true"
```

## Tests in SPARK SUBMIT

```
spark-submit "JAR_PATH" DirectKafkaWordCount spark.master local[*] bigdata2.ip-188-165-248.eu:9092,bigdata3.ip-188-165-248.eu:9092,bigdata4.ip-188-165-248.eu:909 customerOrder identifiedCustomerOrder true
```

* DirectKafkaWordCount: ApplicationName
 
* spark.master : master param
 
* local[2] : cluster Param
 
* bigdata2.ip-188-165-248.eu:9092,bigdata3.ip-188-165-248.eu:9092,bigdata4.ip-188-165-248.eu:9092 : broker.list
 
* customerOrder : GET topic
 
* identifiedCustomerOrder : POST topic
 
* true : if you want to read from beginning, if not Pleas Make It false


## identified USER (CSV)

```
lastmodifieddate"\t"file_path"\t"file_name"\t"displayurl"\t"file_size"\t"file_extension"\t"line_number"\t"owner_name"\t"security"\t"eventid date"\t"date2"\t"duration"\t"statut"\t"version external_id"\t"priority"\t"is_configuration_checked"\t"configuration_check_date"\t"customerid
```