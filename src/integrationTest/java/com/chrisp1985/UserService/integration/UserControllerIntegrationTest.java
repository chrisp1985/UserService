package com.chrisp1985.UserService.integration;

import com.chrisp1985.UserService.controller.UserController;
import com.chrisp1985.UserService.metrics.UserServiceMetrics;
import com.chrisp1985.UserService.model.UserDto;
import com.chrisp1985.UserService.service.kafka.KafkaProducerService;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Collections;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class UserControllerIntegrationTest {

    @LocalServerPort
    private Integer port;

    @Container
    static final KafkaContainer kafkaContainer =
            new KafkaContainer(DockerImageName.parse("apache/kafka-native:3.8.0"))
                    .waitingFor(Wait.forListeningPort());

    private Consumer<String, UserDto> consumer;

    private final static String TOPIC_NAME = "user-creation";

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private KafkaTemplate<String, UserDto> kafkaTemplate;

    @Autowired
    private UserServiceMetrics userServiceMetrics;

    @Autowired
    private UserController userController;

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
    void setConsumer() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(
                kafkaContainer.getBootstrapServers(),
                "test-group",
                "false"
        );
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        consumerProps.put("spring.kafka.producer.security.protocol", "PLAINTEXT");
        consumerProps.put("spring.kafka.producer.sasl.mechanism", "PLAIN");
        consumerProps.put("spring.kafka.producer.user.topic", TOPIC_NAME);
        consumerProps.put("spring.kafka.producer.sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"\" password=\"\";");

        consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Collections.singletonList(TOPIC_NAME));
    }

    @AfterEach
    void tearDown() {
        consumer.close();
    }

    @Test
    void testAddCustomUser() throws InterruptedException {
        // Act
        RestAssured.given()
                .baseUri("http://localhost:" + port)
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "    \"name\": \"testAddCustomUser\",\n" +
                        "    \"id\": 23200,\n" +
                        "    \"value\": 1111\n" +
                        "}")
                .when()
                .post("/user/v1/kafkaUser")
                .then()
                .statusCode(200);

        Thread.sleep(1000); // Required for data to be added to Kafka topic.

        // Assert
        ConsumerRecord<String, UserDto> record = KafkaTestUtils.getRecords(consumer)
                .records(new TopicPartition(TOPIC_NAME, 0))
                .stream()
                .filter(a -> a.value().getName().equals("testAddCustomUser"))
                .findFirst().orElse(null);
        Assertions.assertNotNull(record);
        Assertions.assertEquals("testAddCustomUser", record.key());
        Assertions.assertEquals(1111, record.value().getValue());
    }

    @Test
    void testAddInvalidUser() {
        RestAssured.given()
                .baseUri("http://localhost:" + port)
                .contentType(ContentType.JSON)
                .body("[{\n" +
                        "    \"testParam\": \"testValue\",\n" +
                        "    \"id\": 23200\n" +
                        "}]")
                .when()
                .post("/user/v1/kafkaUser")
                .then()
                .statusCode(400);
    }

    @Test
    void testAddInvalidTypeUser() {
        RestAssured.given()
                .baseUri("http://localhost:" + port)
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "    \"name\": \"testAddInvalidTypeUser\",\n" +
                        "    \"id\": 23200,\n" +
                        "    \"value\": \"testAddInvalidTypeUser\"\n" +
                        "}")
                .when()
                .post("/user/v1/kafkaUser")
                .then()
                .statusCode(400);
    }
}
