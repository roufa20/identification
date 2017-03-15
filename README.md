# Job SPARK


## Synopsis

This Folder contain Spark Job

## Requirement for this project

* ScalaVersion [2.11.6](https://www.scala-lang.org/download/2.11.6.html)

* Neo4J [3.1](https://neo4j.com/download/)

* Kafka Producer

* Kafka Consumer


## Consolidation Job Spark

Please Open [CONSOLIDATION](http://188.165.159.218/RD/identificationJobSpark/tree/master/consolidation) folder

## FingerPrint Job Spark

Please Open [FINGERPRINT](http://188.165.159.218/RD/identificationJobSpark/tree/master/fingerPrint) folder

## Facebook Job Spark

Please Open [FACEBOOK](http://188.165.159.218/RD/identificationJobSpark/tree/master/facebook) folder

## Twitter Job Spark

Please Open [TWITTER](http://188.165.159.218/RD/identificationJobSpark/tree/master/tweeter) folder

## CAMPAIGN Job Spark

Please Open [CAMPAIGN](http://188.165.159.218/RD/identificationJobSpark/tree/develop/campaign) folder

## CUSTOMERORDER Job Spark

Please Open [CUSTOMERORDER](http://188.165.159.218/RD/identificationJobSpark/tree/develop/customerOrder) folder

## OPPIRTUNITY Job Spark

Please Open [OPPIRTUNITY](http://188.165.159.218/RD/identificationJobSpark/tree/develop/opportunity) folder

## TRAFIC Job Spark

Please Open [TRAFIC](http://188.165.159.218/RD/identificationJobSpark/tree/develop/trafic) folder



## NEO4J Preparation

```
CREATE CONSTRAINT ON(a:Account) ASSERT a.idUser IS UNIQUE;

CREATE CONSTRAINT ON(m:MsisdnClient) ASSERT m.idMsisdn IS UNIQUE;

CREATE CONSTRAINT ON(f:Facebook) ASSERT f.idFacebook IS UNIQUE;

CREATE CONSTRAINT ON(t:Twitter) ASSERT t.idTwitter IS UNIQUE;

CREATE CONSTRAINT ON(p:FingerPrint) ASSERT p.user_fingerprint IS UNIQUE;

CREATE CONSTRAINT ON(i:IpAdress) ASSERT i.user_ipaddress IS UNIQUE;
```

