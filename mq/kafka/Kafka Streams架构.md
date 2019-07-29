# Kafka Streams架构

1. 运行流处理示例

   ```java
   bin/kafka-topics.sh --create --bootstrap-server 192.168.1.120:9092 --replication-factor 1 --partitions 1 --topic streams-plaintext-input
   
   bin/kafka-topics.sh --create --bootstrap-server 192.168.1.120:9092 --replication-factor 1 --partitions 1 --topic streams-wordcount-output --config cleanup.policy=compact
   
   CLASSPATH=$CLASSPATH:/tmp/streams-quickstart-java-1.0-SNAPSHOT.jar
   bin/kafka-run-class.sh org.zhu.demo.WordCountDemo
   
   bin/kafka-console-producer.sh --broker-list 192.168.1.120:9092 --topic streams-plaintext-input
   
   bin/kafka-console-consumer.sh --bootstrap-server 192.168.1.120:9092 --topic streams-wordcount-output --from-beginning --formatter kafka.tools.DefaultMessageFormatter --property print.key=true --property print.value=true --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer
   ```

   