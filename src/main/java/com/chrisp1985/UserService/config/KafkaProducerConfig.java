package com.chrisp1985.UserService.config;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
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

    @Value("${spring.kafka.producer.sasl.jaas.config}")
    String sasl_jaas_config;

    @Value("${spring.kafka.producer.bootstrap-servers}")
    String bootstrap_servers;

    @Value("${spring.kafka.producer.security.protocol}")
    String security_protocol;

    @Value("${spring.kafka.producer.sasl.mechanism}")
    String sasl_mechanism;

    @Value("${spring.kafka.properties.schema.registry.url}")
    String schema_registry_url;

    @Value("${spring.kafka.auth.user.api.key}")
    String schema_registry_api_key;

    @Value("${spring.kafka.auth.user.api.password}")
    String schema_registry_api_password;

    @Bean
    public <T> ProducerFactory<String, T> producerFactory() {
        final Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap_servers);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        producerProps.put(SaslConfigs.SASL_JAAS_CONFIG, sasl_jaas_config);
        producerProps.put(SaslConfigs.SASL_MECHANISM, sasl_mechanism);
        producerProps.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, schema_registry_url);
        producerProps.put("security.protocol", security_protocol);
        producerProps.put("basic.auth.credentials.source", "USER_INFO");
        producerProps.put("basic.auth.user.info", String.format("%s:%s", schema_registry_api_key, schema_registry_api_password));

        return new DefaultKafkaProducerFactory<>(producerProps);
    }

    @Bean
    public <T> KafkaTemplate<String, T> kafkaTemplate() {
        return new KafkaTemplate<String, T>(producerFactory());
    }

}
