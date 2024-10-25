package com.chrisp1985.UserService.config;

import com.chrisp1985.UserService.dto.UserSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${kafka.sasl.jaas.config}")
    String sasl_jaas_config;

    @Value("${kafka.bootstrap.servers}")
    String bootstrap_servers;

    @Value("${kafka.security.protocol}")
    String security_protocol;

    @Value("${kafka.sasl.mechanism}")
    String sasl_mechanism;

    @Value("${kafka.schema.registry.url}")
    String schema_registry_url;

    @Bean
    public <T> ProducerFactory<String, T> producerFactory() {
        final Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap_servers);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, UserSerializer.class);
        producerProps.put(SaslConfigs.SASL_JAAS_CONFIG, sasl_jaas_config);
        producerProps.put(SaslConfigs.SASL_MECHANISM, sasl_mechanism);
        producerProps.put("security.protocol", security_protocol);

        return new DefaultKafkaProducerFactory<>(producerProps);
    }

    @Bean
    public <T> KafkaTemplate<String, T> kafkaTemplate() {
        return new KafkaTemplate<String, T>(producerFactory());
    }

}
