package com.chrisp1985.UserService.integration;

import com.chrisp1985.UserService.dto.User;
import com.chrisp1985.UserService.integration.libs.UserDeserializer;
import com.chrisp1985.UserService.metrics.UserServiceMetrics;
import com.chrisp1985.UserService.sevice.kafka.KafkaProducerService;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Testcontainers
public class KafkaProducerIntegrationTests {

    @Container
    static final KafkaContainer kafkaContainer =
            new KafkaContainer(DockerImageName.parse("apache/kafka-native:3.8.0"))
            .waitingFor(Wait.forListeningPort());

    private Consumer<String, User> consumer;

    private final static String TOPIC_NAME = "test-topic";

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private KafkaTemplate<String, User> kafkaTemplate;

    @Autowired
    private UserServiceMetrics userServiceMetrics;

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.producer.bootstrap-servers", kafkaContainer::getBootstrapServers);
        registry.add("spring.kafka.producer.security.protocol", () -> "PLAINTEXT");
        registry.add("spring.kafka.producer.sasl.mechanism", () -> "PLAIN");
        registry.add("spring.kafka.producer.user.topic", () -> TOPIC_NAME);
        registry.add("spring.kafka.producer.sasl.jaas.config", () ->
                "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"\" password=\"\";");
    }

    @BeforeEach
    public void setConsumer() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(
                kafkaContainer.getBootstrapServers(),
                "test-group",
                "false"
        );
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, UserDeserializer.class);
        consumerProps.put("spring.kafka.producer.security.protocol", "PLAINTEXT");
        consumerProps.put("spring.kafka.producer.sasl.mechanism", "PLAIN");
        consumerProps.put("spring.kafka.producer.user.topic", TOPIC_NAME);
        consumerProps.put("spring.kafka.producer.sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"\" password=\"\";");

        consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Collections.singletonList(TOPIC_NAME));
    }

    @AfterEach
    public void tearDown() {
        consumer.close();
    }

    @Test
    public void connectionEstablished() {
        assertThat(kafkaContainer.isRunning()).isTrue();
    }

    @Test
    public void testSendKafkaMessageSuccess() throws InterruptedException {
        // Arrange
        User testUser = new User("TestUser", 123, 100);

        // Act
        kafkaProducerService.sendKafkaMessage(testUser);

        Thread.sleep(1000); // Required for the kafka message to be sent/recorded.

        ConsumerRecord<String, User> testRecord = KafkaTestUtils.getRecords(consumer)
                .records(new TopicPartition(TOPIC_NAME, 0))
                .stream()
                .filter(a -> a.value().name().equals(testUser.name()))
                .findFirst().orElse(null);

        // Assert
        Assertions.assertNotNull(testRecord);
        Assertions.assertEquals(testUser.name(), testRecord.key());
        Assertions.assertEquals(testUser, testRecord.value());
    }

    @Test
    public void testSendKafkaMessageMetricsRecorded() throws InterruptedException {
        // Arrange
        User testUser = new User("TestUser", 123, 100);
        double starter = userServiceMetrics.getRecordCount();

        // Act
        kafkaProducerService.sendKafkaMessage(testUser);
        Thread.sleep(1000); // Required for the kafka message to be sent/recorded.

        // Assert
        Assertions.assertEquals(starter + 1, userServiceMetrics.getRecordCount());
    }

    @Test
    public void testSendTransactionMessageSuccess() throws InterruptedException {
        // Act
        kafkaProducerService.sendTransaction();
        Thread.sleep(1000); // Required for the kafka message to be sent/recorded.
        List<ConsumerRecord<String, User>> testRecord = KafkaTestUtils.getRecords(consumer)
                .records(new TopicPartition(TOPIC_NAME, 0))
                .stream().toList();

        // Assert
        Assertions.assertFalse(testRecord.isEmpty());
    }



}
