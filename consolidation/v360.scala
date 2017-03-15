import kafka.serializer.StringDecoder
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._
import org.apache.spark.SparkConf
import org.apache.spark._
import org.apache.spark.streaming.StreamingContext._
import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession
import java.util.Properties
import org.apache.kafka.clients.producer._
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.neo4j.driver.v1._

object consolidationJob {

  /**
    *
    * @param msisdn
    * @param customer_code
    * @param customer_type
    * @param name
    * @param driver
    * @return
    */
  def checkNodes(msisdn: String, customer_code: String, customer_type: String, name: String, driver: org.neo4j.driver.v1.Driver): Int = {

    val session = driver.session

    var allMsisdn = msisdn.split(",").toSet

    val userExist = s"MATCH (a:Account) where a.idUser = " + customer_code + " return a ";
    val reqForuserExist = session.run(userExist)

    if (reqForuserExist.hasNext()) {
      allMsisdn.foreach { elem =>
        val createMsisdnNodes = s"MATCH (a:Account{idUser:" + customer_code + "}) MERGE(m:MsisdnClient{idMsisdn:" + elem + "}) SET a.user_name='" + name + "' SET a.type='" + customer_type + "' MERGE (a)-[r:INCLUDE]->(m) RETURN r ";
        val exqCreateMsisdnNodes = session.run(createMsisdnNodes)

      }
    }
    else {
      val createAccountNode = s"MERGE (a:Account{idUser:" + customer_code + " ,user_name:'" + name + "',type:'" + customer_type + "' } ) RETURN * ";
      val reqCreateAccountNode = session.run(createAccountNode);
      //delete nodes MATCH (a:Account)-[r:INCLUDE]->(m:MsisdnClient) WHERE m.idMsisdn="300"  DELETE r,m,a
      allMsisdn.foreach { elem =>
        val createMsisdnNodes = s" MATCH (a:Account{idUser:" + customer_code + "}) MERGE(m:MsisdnClient{idMsisdn:" + elem + "}) CREATE (a)-[r:INCLUDE]->(m) RETURN r ";
        val exqCreateMsisdnNodes = session.run(createMsisdnNodes)
      }
    }

    session.close()
    return 1;
  }


  /**
    *
    * @param allJsonContainer
    * @param customer_type
    * @param Brooker
    * @param TopicB2B
    * @param TopicB2C
    * @return
    */
  def identifiedConsolidation(allJsonContainer: String, customer_type: String, Brooker: String, TopicB2B: String, TopicB2C: String): Int = {

    //Add libs to send Producer Message
    import java.util.Properties
    import org.apache.kafka.clients.producer._

    val props = new Properties()
    props.put("bootstrap.servers", Brooker)
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    val producer = new KafkaProducer[String, String](props)


    //var TOPIC = "";
    val TOPIC = if (customer_type.asInstanceOf[String].contains("B2B")) {TopicB2B} else if (customer_type.asInstanceOf[String].contains("B2C")) {TopicB2C }else{null}

    if (TOPIC != null){
      val record = new ProducerRecord(TOPIC, "key", "" + allJsonContainer + "")
      producer.send(record)
      println("sended")
    }


    producer.close();
    return 1
  }


  /**
    * Main class
    *
    * @param args
    */
  def main(args: Array[String]) {

    //Hide Log Message
    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)

    val sparkConf = new SparkConf().setAppName(args(3))
      .set(args(4), args(5))
    val ssc = new StreamingContext(sparkConf, Seconds(2))

    // Create direct kafka stream with brokers and topics
    val topicsSet = args(7).split(",").toSet
    var kafkaParams = Map[String, String]("metadata.broker.list" -> args(6));

    if (args(10) == "true") {
      kafkaParams += ("auto.offset.reset" -> "smallest")
    }

    val messages = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
      ssc, kafkaParams, topicsSet)


    messages.map(_._2).foreachRDD { rdd =>
      if (!rdd.isEmpty()) {

        val result = rdd.foreachPartition { partitionOfRecords =>
          partitionOfRecords.foreach { pair =>

            val driver = GraphDatabase.driver("bolt://" + args(0) + "/7474", AuthTokens.basic(args(1), args(2)))

            val jsonData = new JSONArray(pair);

            var mapDataJson = scala.collection.mutable.Map[String, String]()
            var alljsonValueToCSv = "";
            for (x <- 0 to jsonData.length - 1) {
              val jsonItem = new JSONObject(jsonData.get(x).toString);
              val jsonArray = jsonItem.getJSONArray("value")
              if (x == 0) {
                alljsonValueToCSv = jsonItem.getString("value");
              } else {
                alljsonValueToCSv = alljsonValueToCSv + "\t" + jsonItem.getString("value");
              }

              mapDataJson(jsonItem.getString("key")) = jsonArray.join(",")
            }

            val isNull = "";

            val msisdn = mapDataJson.get("msisdn") match {
              case None => isNull
              case Some(value) => value
            }

            val customer_code = mapDataJson.get("customer_code") match {
              case None => isNull
              case Some(value) => value
            }
            val customer_type = mapDataJson.get("customer_type") match {
              case None => isNull
              case Some(value) => value
            }

            val name = mapDataJson.get("name") match {
              case None => isNull
              case Some(value) => value
            }

            try {
              identifiedConsolidation(alljsonValueToCSv, customer_type, args(6), args(8), args(9));
              checkNodes(msisdn, customer_code, customer_type, name, driver);

            } catch {
              case e: Exception => println("exception caught: " + e);
            } finally {

              driver.close()

            }

          }
        }

      }
    }


    ssc.start()
    ssc.awaitTermination()

  }


}