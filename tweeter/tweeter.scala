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
import org.neo4j.driver.v1._

object TwitterStreamingFromKafkaToSpark {

  /**
    * Function CheckNode is for Create and Update nodes in Neo4j
    *
    * @param msisdn
    * @param customer_code
    * @param customer_type
    * @param name
    * @return
    */
  def checkNodes(TwitterLine: String, driver: org.neo4j.driver.v1.Driver, userLine: Array[String], param1: String, param2: String, param3: String): Int = {
    val session = driver.session
    val TwitterLineSplitet = TwitterLine.split("\t").toArray;
    val idUserTwitter = TwitterLineSplitet(1)

    if (!idUserTwitter.isEmpty) {
      val userExist = s"MATCH (t:Twitter) where t.idTwitter = '" + idUserTwitter + "' return t.idTwitter AS a"
      val reqForuserExist = session.run(userExist);
      if (reqForuserExist.hasNext()) {

        val getIdUserOfTwitter = session.run(s"Match (a:Account)-[r:HasTwitter]->(t:Twitter) WHERE t.idTwitter ='" + idUserTwitter + "' Return a.idUser AS UserId")
        if (getIdUserOfTwitter.hasNext()) {
          val idUserFound = getIdUserOfTwitter.next().get("UserId").asString()
          var TwitterLineCopleet = TwitterLine + "\t" + idUserFound;
          println(TwitterLineCopleet);
          identifiedOrNotProducer(TwitterLineCopleet, true, param1, param2, param3)
        }
        else {
          //TODO THIS is FOR USER not found TwitterLine
          identifiedOrNotProducer(TwitterLine, false, param1, param2, param3)
        }
      }
      else {
        println("there is no user with this Twitter Id")
        var idUserIs = "";
        for (i <- 0 to (userLine.length - 1)) {
          //println(userLine(i));
          val oneLineUser = userLine(i).split("\t").toArray
          val msisdnUserExist = oneLineUser(0)
          val iduserTwitterExist = oneLineUser(1)
          if (iduserTwitterExist == idUserTwitter) {

            val userExistMsisdn = s"MATCH (a:Account)-[r:INCLUDE]->(m:MsisdnClient) where m.idMsisdn = '" + msisdnUserExist + "' return a.idUser AS User"
            val execUserExistMsisdn = session.run(userExistMsisdn);
            if (execUserExistMsisdn.hasNext()) {
              idUserIs = execUserExistMsisdn.next().get("User").asString()
              val creatRelationTwitter = session.run(s"MATCH (a:Account)where a.idUser = '" + idUserIs + "' CREATE (a)-[r:HasTwitter]->(f:Twitter{idTwitter:'" + idUserTwitter + "'}) return r")

              var TwitterLineCopleet = TwitterLine + "\t" + idUserIs;
              println(TwitterLineCopleet);
              identifiedOrNotProducer(TwitterLineCopleet, true, param1, param2, param3)

            }
          }
        }
        if (idUserIs.isEmpty) {
          //TODO TwitterLine
          identifiedOrNotProducer(TwitterLine, false, param1, param2, param3)
        }
      }
    } else {
      identifiedOrNotProducer(TwitterLine, false, param1, param2, param3)
    }
    session.close()
    return 1;
  }


  /**
    * Function to push the result to KAFKA Producer
    *
    * @param allJsonContainer
    * @return
    */
  def identifiedOrNotProducer(allJsonContainer: String, boolean: Boolean, param1: String, param2: String, param3: String): Int = {

    //Add libs to send Producer Message
    import java.util.Properties
    import org.apache.kafka.clients.producer._

    val props = new Properties()
    props.put("bootstrap.servers", param1)
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    val producer = new KafkaProducer[String, String](props)

    var TOPIC = ""
    if (boolean == true) {
      TOPIC = param2
    } else {
      TOPIC = param3
    }

    val record = new ProducerRecord(TOPIC, "key", "" + allJsonContainer + "")
    producer.send(record)
    System.out.println("send Record")

    producer.close()
    System.out.println("kafka send message is sended")
    return 1
  }

  //Main class
  def main(args: Array[String]) {

    val raouf: String = "23721943\t362454062"
    val anwar: String = "22334455\t2472957009"
    val dubois: String = "33445566\t279648652"
    val achref: String = "44556677\t811603495736311808"

    var userLine = new Array[String](4);
    userLine(0) = raouf
    userLine(1) = anwar
    userLine(2) = dubois
    userLine(3) = achref

    //Hide Log Message
    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)


    val sparkConf = new SparkConf().setAppName("DirectKafkaWordCount")
      .set(args(4), args(5))
    val ssc = new StreamingContext(sparkConf, Seconds(2))

    // Create direct kafka stream with brokers and topics

    val topicsSet = args(7).split(",").toSet
    var kafkaParams = Map[String, String]("metadata.broker.list" -> args(6));

    if (args(10) == "true") {
      kafkaParams +=("auto.offset.reset" -> "smallest")
    }

    val messages = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
      ssc, kafkaParams, topicsSet)

    val lines = messages.map(_._2)

    lines.foreachRDD { rdd =>
      if (!rdd.isEmpty()) {
        val result = rdd.foreachPartition { partitionOfRecords =>

          partitionOfRecords.foreach { pair =>

            val driver = GraphDatabase.driver("bolt://" + args(0) + "/7474", AuthTokens.basic(args(1), args(2)))
            checkNodes(pair, driver, userLine, args(6), args(8), args(9))
            driver.close()
          }
        }
      }
    }

    System.out.println("End KFKA");

    ssc.start()
    ssc.awaitTermination()

  }


}