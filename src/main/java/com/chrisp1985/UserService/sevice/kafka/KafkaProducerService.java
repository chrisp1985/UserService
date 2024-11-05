package com.chrisp1985.UserService.sevice.kafka;

import com.chrisp1985.UserService.dto.User;
import com.chrisp1985.UserService.metrics.UserServiceMetrics;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
public class KafkaProducerService {

    private static final String TOPIC_NAME = "test-topic";

    private final KafkaTemplate<String, User> kafkaTemplate;

    private final UserServiceMetrics userServiceMetrics;

    @Autowired
    public KafkaProducerService(UserServiceMetrics userServiceMetrics, KafkaTemplate<String, User> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.userServiceMetrics = userServiceMetrics;
    }

    public User generateUser() {
        String[] names = {
                "Chris", "Bert", "Bob", "Dave", "Kev", "Sue", "Polly", "Harold", "Kumar", "Mitch", "Troy", "Vince",
                "Mark", "Pete", "Lou", "Steve", "Eric", "Ash", "Paul", "Ian", "Derek", "April", "Elaine", "Hermione",
                "Ron", "Sade", "Ursula", "Neil", "Babs", "Scott", "Ann", "Eleanor", "Ryan", "George"
        };
        String name = names[ThreadLocalRandom.current().nextInt(names.length)];
        int id = ThreadLocalRandom.current().nextInt(0, 10000);
        int value = ThreadLocalRandom.current().nextInt(0, 23000);
        User randomUser = new User(name, id, value);
        log.info("Creating user {} with an id of {} and a value of {}", randomUser.name(), randomUser.id(), randomUser.value());
        return randomUser;
    }

    @Scheduled(fixedRate = 20000)
    @SneakyThrows
    public void sendTransaction() {

        User randomUser = generateUser();
        log.info("Sending: {}", randomUser);
        sendKafkaMessage(randomUser);

    }

    public void sendKafkaMessage(User user) {
        kafkaTemplate.send(TOPIC_NAME, user.name(), user)
                .whenComplete((sendResult, throwable) -> {
                    if(throwable!=null) {
                        onFailure(throwable);
                    }
                    else {
                        onSuccess(sendResult);
                    }
                });
    }

    private void onSuccess(SendResult<String, User> sendResult) {
        userServiceMetrics.recordSuccess();
        log.info("Received :\n" +
                        "Topic: {}, Partition: {}, Offset: {}, Timestamp: {}",
                sendResult.getRecordMetadata().topic(),
                sendResult.getRecordMetadata().partition(),
                sendResult.getRecordMetadata().offset(),
                sendResult.getRecordMetadata().timestamp());
        log.info("Created {} new events since start.", userServiceMetrics.getRecordCount());
    }

    private void onFailure(Throwable throwable) {
        log.error("Error occurred: {}", throwable.getMessage());
    }

}