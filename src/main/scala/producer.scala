package claudiostahl

import java.util.Properties
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord

object Producer extends JsonSupport {
  def buildProducer(): KafkaProducer[String, String] = {
    val config = new Properties()
    config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    config.put(
      "key.serializer",
      "org.apache.kafka.common.serialization.StringSerializer"
    )
    config.put(
      "value.serializer",
      "org.apache.kafka.common.serialization.StringSerializer"
    )
    config.put("acks", "all")

    new KafkaProducer[String, String](config)
  }

  def produce(producer: KafkaProducer[String, String], host: String, topic: String, input: ValidationInput): Unit = {
    val inputWithMetadata = ValidationInputWithMetadata(input.id, input.amount, MessageMetadata(host))
    val message = validationInputWithMetadataFormat.write(inputWithMetadata).compactPrint
    var record = new ProducerRecord[String, String](topic, input.id, message)
    producer.send(record)
  }

  def requestPoolControl(producer: KafkaProducer[String, String], host: String): Unit = {
        val input = PoolControlInput(host)
        val message = poolControlInputFormat.write(input).compactPrint
    var record = new ProducerRecord[String, String]("sandbox_akka_pool_control_input", "request", message)
    producer.send(record)
  }
}
