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

object customerOrderFromKafkaToSpark {

  /**
    *
    * @param csvContainer
    * @param broockers
    * @param TOPIC
    * @return
    */
  def identifiedCustomerOrder(csvContainer: String, broockers: String, TOPIC: String): Int = {

    //Add libs to send Producer Message
    import java.util.Properties
    import org.apache.kafka.clients.producer._

    val props = new Properties()
    props.put("bootstrap.servers", broockers)
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    val producer = new KafkaProducer[String, String](props)
    val record = new ProducerRecord(TOPIC, "key", "" + csvContainer + "")
    producer.send(record)
    System.out.println("send Record");

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

    val sparkConf = new SparkConf().setAppName(args(0))
      .set(args(1), args(2))
    val ssc = new StreamingContext(sparkConf, Seconds(2))

    // Create direct kafka stream with brokers and topics
    val topicsSet = args(4).split(",").toSet
    var kafkaParams = Map[String, String]("metadata.broker.list" -> args(3));

    if (args(6) == "true") {
      kafkaParams += ("auto.offset.reset" -> "smallest")
    }

    val messages = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
      ssc, kafkaParams, topicsSet)

    messages.map(_._2).foreachRDD { rdd =>
      if (!rdd.isEmpty()) {

        val result = rdd.foreachPartition { partitionOfRecords =>

          partitionOfRecords.foreach { pair =>

            val pairData = new JSONObject(pair)
            var globalMap = pairData.get("map").toString
            var globalMapcontain = new JSONObject(globalMap)
            var connectorName = globalMapcontain.get("connectorName")
            var data = globalMapcontain.get("data").toString

            var dataArray = new JSONArray(data)

            for (x <- 0 to dataArray.length - 1) {
              var dataArraySingle = new JSONObject(dataArray.get(x).toString)
              var metaContainer = dataArraySingle.get("metaContainer").toString
              var metaContainerSingle = new JSONObject(metaContainer)
              //metaList
              var metaList = metaContainerSingle.get("metaList").toString

              var metaListArray = new JSONArray(metaList);
              var csvToSend = ""
              for (y <- 0 to metaListArray.length - 1) {
                val jsonItem = new JSONObject(metaListArray.get(y).toString);
                val jsonArrayValue = jsonItem.getString("value")
                val lineArray = jsonItem.getString("name")
                if (y == 0) {
                  csvToSend = jsonArrayValue

                } else {
                  csvToSend = csvToSend + "\t" + jsonArrayValue
                }

              }
             identifiedCustomerOrder(csvToSend, args(3), args(5));

            }
          }
        }

      }
    }

    ssc.start()
    ssc.awaitTermination()

  }


}