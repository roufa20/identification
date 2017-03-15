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


object FingerPrintStreamingFromKafkaToSpark {

  /**
    *
    * @param user_id
    * @param user_fingerprint
    * @param app_id
    * @param user_ipaddress
    * @param csvLine
    * @param driver
    * @return
    */
  def checkNodes(user_id: String, user_fingerprint: String, app_id: String, user_ipaddress: String, csvLine: String, driver: org.neo4j.driver.v1.Driver, param1: String, param2: String, param3: String): Int = {

    val session = driver.session

    val splitedRdd = csvLine.split("\t")
    val user_id = splitedRdd(12)
    val app_id = splitedRdd(0)
    val user_ipaddress = splitedRdd(13)
    val user_fingerprint = splitedRdd(14)

    if (user_id == "") {
      System.out.println("user_id is null");
      val reqForExistingFP = s"MATCH (f:FingerPrint) where f.user_fingerprint='" + user_fingerprint + "' RETURN f.user_fingerprint AS FP"
      val streamReqForExistingFP = session.run(reqForExistingFP)

      if (streamReqForExistingFP.hasNext()) {

        System.out.println("finger print node exite and has no UserID");
        //get userId
        val reqForGetUserIdFP = s"MATCH (a:Account)-[h:HasFingerPrint]->(f:FingerPrint)where f.user_fingerprint='" + user_fingerprint + "' return a.idUser AS user_id";
        val streamRreqForGetUserIdFP = session.run(reqForGetUserIdFP);
        val recordStreamRreqForGetUserIdFP = streamRreqForGetUserIdFP.next()
        val identifieUserId = recordStreamRreqForGetUserIdFP.get("user_id").asString()

        val splitedCsvLine = csvLine.split("\t");

        var allcsvLine = ""

        splitedCsvLine.zipWithIndex.map { case (element, index) =>
          if (index == 0) {
            allcsvLine = element
          }
          else if (index == 12) {
            allcsvLine = allcsvLine + "\t" + identifieUserId
          } else {
            allcsvLine = allcsvLine + "\t" + element
          }
        }

        identified(allcsvLine, param1, param2);

      } else {
        System.out.println("send line to topic without Identified");
        nonIdentified(csvLine, param1, param3);
      }

    }
    else {

      System.out.println("user_id is not null");
      // test If The User Is exist
      val reqFoundUser = s"MATCH (n:Account) where n.idUser='" + user_id + "' RETURN n ";

      val streamFoundUser = session.run(reqFoundUser)

      if (streamFoundUser.hasNext()) {
        System.out.println("not empty");
        val reqExistRelation = s"MATCH (a:Account)-[r:HasFingerPrint]->(f:FingerPrint) WHERE a.idUser = '" + user_id + "' AND f.user_fingerprint='" + user_fingerprint + "' RETURN r ";
        val ifExistRelation = session.run(reqExistRelation);
        if (ifExistRelation.hasNext()) {

          System.out.println("Relationship exist");
          //Looking for relation between fingerPrint and IpAdress
          val reqExistRelationFpAndIp = s"MATCH (f:FingerPrint) -[r:IP]->(i:IpAdress) WHERE f.user_fingerprint='" + user_fingerprint + "' AND i.user_ipaddress='" + user_ipaddress + "' RETURN r ";
          val ifExistRelationFpAndIp = session.run(reqExistRelationFpAndIp);
          if (ifExistRelationFpAndIp.hasNext()) {
            println("fingerPrint and Ip adress existe")
          } else {
            //Create Fingerprint and IpAdress relation
            val reqCreateRelationFpAndIp = s"MATCH (f:FingerPrint) Where f.user_fingerprint='" + user_fingerprint + "' MERGE (ipAdress:IpAdress{user_ipaddress:'" + user_ipaddress + "'}) CREATE (f)-[:IP]->(ipAdress) RETURN *";
            val ifReqCreateRelationFpAndIp = session.run(reqCreateRelationFpAndIp);
          }

        } else {
          System.out.println("Not exist");
          //create Nodes (Account And FingerPrint AND ipAdress) and Relationship (HasFingerPrint AND IP)
          val reqCreateNewRelation = s"MATCH (account:Account) WHERE account.idUser='" + user_id + "' MERGE (fingerPrint:FingerPrint{user_fingerprint:'" + user_fingerprint + "'}) MERGE (ipAdress:IpAdress{user_ipaddress:'" + user_ipaddress + "'}) CREATE (fingerPrint)-[:IP]->(ipAdress) CREATE (account)-[:HasFingerPrint]->(fingerPrint) RETURN *";
          val execReqCreateNewRelation = session.run(reqCreateNewRelation);
          System.out.println("Relationship Created");

        }

      }
      else {
        System.out.println("Not exist");
        //create Nodes (Account And FingerPrint AND ipAdress) and Relationship (HasFingerPrint AND IP)
        val reqCreateNode = s"MERGE (account:Account{idUser:'" + user_id + "'}) MERGE (fingerPrint:FingerPrint{user_fingerprint:'" + user_fingerprint + "'}) MERGE (ipAdress:IpAdress{user_ipaddress:'" + user_ipaddress + "'}) CREATE (account)-[:HasFingerPrint]->(fingerPrint) CREATE (fingerPrint)-[:IP]->(ipAdress) RETURN * ";
        val exect = session.run(reqCreateNode);
        System.out.println("Node created");
      }

      //this is an identified User
      identified(csvLine, param1, param3)
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
  def identified(allJsonContainer: String, param1: String, param2: String): Int = {

    //Add libs to send Producer Message
    import java.util.Properties
    import org.apache.kafka.clients.producer._

    val props = new Properties()
    props.put("bootstrap.servers", param1)
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    val producer = new KafkaProducer[String, String](props)

    val record = new ProducerRecord(param2, "key", allJsonContainer)
    producer.send(record)
    System.out.println("send Record");


    producer.close();

    System.out.println("kafka send message is sended");

    return 1
  }

  /**
    *
    * @param allJsonContainer
    * @return
    */
  def nonIdentified(allJsonContainer: String, param1: String, param2: String): Int = {

    //Add libs to send Producer Message
    import java.util.Properties
    import org.apache.kafka.clients.producer._

    val props = new Properties()
    props.put("bootstrap.servers", param1)
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    val producer = new KafkaProducer[String, String](props)

    val record = new ProducerRecord(param2, "key", allJsonContainer)
    producer.send(record)
    System.out.println("send Record");


    producer.close();

    System.out.println("kafka send message is sended");

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
      kafkaParams +=("auto.offset.reset" -> "smallest")
    }

    val messages = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
      ssc, kafkaParams, topicsSet)

    val input = messages.map(_._2)

    input.foreachRDD { rdd =>

      if (!rdd.isEmpty()) {

        val result = rdd.foreachPartition { partitionOfRecords =>
          partitionOfRecords.foreach { pair =>
            val driver = GraphDatabase.driver("bolt://" + args(0) + "/7474", AuthTokens.basic(args(1), args(2)))

            val splitedRdd = pair.split("\t")

            checkNodes(splitedRdd(12), splitedRdd(14), splitedRdd(0), splitedRdd(13), pair, driver, args(6), args(8), args(9));
            driver.close()
          }

        }
      }

    }

    // Start the computation
    ssc.start()
    ssc.awaitTermination()
  }


}