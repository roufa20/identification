import sbtassembly.AssemblyPlugin._

scalaVersion := "2.11.6"
val sparkVersion = "1.6.1"

resolvers in Global ++= Seq(
  "Sbt plugins"                   at "https://dl.bintray.com/sbt/sbt-plugin-releases",
  "Maven Central Server"          at "http://repo1.maven.org/maven2",
  "TypeSafe Repository Releases"  at "http://repo.typesafe.com/typesafe/releases/",
  "TypeSafe Repository Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/",
  "Spark Packages Repo" at "http://dl.bintray.com/spark-packages/maven"
)

resolvers += "Cloudera Repository" at "https://repository.cloudera.com/artifactory/cloudera-repos/"


libraryDependencies ++= Seq(
  "org.apache.spark" % "spark-core_2.11" % "2.0.0-preview",
  "org.apache.spark" % "spark-streaming_2.11" % "2.0.0",
  "org.apache.spark" % "spark-sql_2.11" % "2.0.0-preview",
  "org.apache.spark" % "spark-streaming-kafka-0-8_2.11" % "2.0.2",
  "com.databricks" % "spark-csv_2.11" % "1.2.0"
)

resolvers += "Spark Packages Repo" at "http://dl.bintray.com/spark-packages/maven"


resolvers ++= Seq(
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
  "org.anormcypher" %% "anormcypher" % "0.9.1"
)

libraryDependencies += "org.neo4j.driver" % "neo4j-java-driver" % "1.0.4"


assemblyMergeStrategy in assembly := {
  case PathList("META-INF", "maven",xs @ _* ) => MergeStrategy.last
  case PathList("javax", xs @ _* ) => MergeStrategy.last
  case PathList("org", xs @ _* ) => MergeStrategy.last
  case PathList("javax", "servlet", xs @ _*) => MergeStrategy.last
  case PathList("javax", "activation", xs @ _*) => MergeStrategy.last
  case PathList("org", "apache", xs @ _*) => MergeStrategy.last
  case PathList("com", "google", xs @ _*) => MergeStrategy.last
  case PathList("com", "esotericsoftware", xs @ _*) => MergeStrategy.last
  case PathList("com", "codahale", xs @ _*) => MergeStrategy.last
  case PathList("com", "yammer", xs @ _*) => MergeStrategy.last
  case "about.html" => MergeStrategy.rename
  case "META-INF/ECLIPSEF.RSA" => MergeStrategy.last
  case "META-INF/mailcap" => MergeStrategy.last
  case "META-INF/mimetypes.default" => MergeStrategy.last
  case "plugin.properties" => MergeStrategy.last
  case "log4j.properties" => MergeStrategy.last
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}