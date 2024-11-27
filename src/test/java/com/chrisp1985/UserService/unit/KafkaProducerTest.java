package com.chrisp1985.UserService.unit;

import com.chrisp1985.UserService.metrics.UserServiceMetrics;
import com.chrisp1985.UserService.service.kafka.KafkaProducerService;
import com.chrisp1985.UserService.userdata.User;
import groovy.util.logging.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import java.util.concurrent.CompletableFuture;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class KafkaProducerTest {

	@InjectMocks
	private KafkaProducerService kafkaProducerService;

	@Mock
	private KafkaTemplate<String, User> kafkaTemplate;

	@Mock
	private UserServiceMetrics userServiceMetrics;

	@Test
	public void testGenerateUser() {

		User generatedUser = kafkaProducerService.generateUser();

		assertNotNull(generatedUser);
		assertNotNull(generatedUser.getName());
		assertNotNull(generatedUser.getId());
		assertNotNull(generatedUser.getValue());
	}

	@Test
	public void onSuccessMetricsCapturedTest() {

		User testUser = new User(123, "TestUser", 100);
		SendResult<String, User> sendResult = mock(SendResult.class);

		when(kafkaTemplate.send(any(), any(), any())).thenReturn(CompletableFuture.completedFuture(sendResult));
		kafkaProducerService.sendKafkaMessage(testUser);

		verify(userServiceMetrics, times(1)).recordSuccess();
	}

	@Test
	public void onFailureNoMetricsCapturedTest() {

		User testUser = new User( 123, "TestUser",100);
		CompletableFuture<SendResult<String, User>> future = new CompletableFuture<>();
		future.completeExceptionally(new RuntimeException("Kafka send failed"));

		when(kafkaTemplate.send(any(), any(), any())).thenReturn(future);
		kafkaProducerService.sendKafkaMessage(testUser);

		verify(userServiceMetrics, times(0)).recordSuccess();
	}

}
