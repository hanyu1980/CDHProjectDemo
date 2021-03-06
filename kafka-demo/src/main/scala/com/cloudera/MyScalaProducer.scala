package com.cloudera

import java.text.SimpleDateFormat
import java.util.{Date, Properties, UUID}

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer

object MyScalaProducer {

  def main(args: Array[String]): Unit = {

    // 设置 Kafka 配置属性
    val props = new Properties
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "10.101.71.42:9092")
    props.put(ProducerConfig.ACKS_CONFIG, "all")
    props.put(ProducerConfig.RETRIES_CONFIG, "0")
    props.put(ProducerConfig.BATCH_SIZE_CONFIG, "16384")
    //批量发送的字节数
    props.put(ProducerConfig.LINGER_MS_CONFIG, "1")
    //将会减少请求数目，但是同时会增加1ms的延迟
    props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, "33554432")
    //用来缓存数据的内存大小
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)

    val producer = new KafkaProducer[String, String](props)
    val a = 3
    val TOPIC_NAME = s"topic_$a"

    // 产生并发送消息
    val events = 50 * a
    for (i <- 0 until events) {
      val key = s"Key-topic_$a-" + i
      val runtime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date())
      val message = s"topic_$a|$i|$runtime|${UUID.randomUUID()}"
      val record = new ProducerRecord[String, String](TOPIC_NAME, key, message)
      producer.send(record)
      println(key + " ---- " + message)
    }
    // 关闭producer
    producer.flush()
    producer.close()
  }
}
