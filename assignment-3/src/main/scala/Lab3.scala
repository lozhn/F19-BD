import java.util.Calendar

import org.apache.spark.{SparkConf, SparkContext}

object Lab3 {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("appName").setMaster("local")
    val sc = new SparkContext(conf)

    val inputPath = args(0)
    val outputPath = args(1)
    val puncts = "[,.!?:;]"
    val textFile = sc.textFile(inputPath)

    val counts = textFile.flatMap(line => line.split("\\s"))
      .filter(_.length > 1)
      .filter(w => w.filter(puncts.contains(_)).length == 0) // filter out if any char of the word is in pucts
      .map(word => (word, 1))
      .reduceByKey(_ + _)
      .map { case (word, count) => (count, word) }
      .sortByKey(ascending = false)
      .map { case (count, word) => "%5s".format(count) + "\t" + word }

    counts.take(20).foreach(println)
    val dateTime = Calendar.getInstance().getTime
    counts.saveAsTextFile(outputPath + s"/$dateTime")
  }
}
