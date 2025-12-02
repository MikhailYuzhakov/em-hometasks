package com.example.appender;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

import java.io.Serializable;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Plugin(name = "CustomKafka", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE, printObject = true)
public class KafkaAppender extends AbstractAppender {

    private final KafkaProducer<String, String> producer;
    private final String topic;

    protected KafkaAppender(String name, Filter filter, Layout<? extends Serializable> layout,
                            String bootstrapServers, String topic) {
        super(name, filter, layout, true, null);
        this.topic = topic;

        // Настройка Kafka Producer
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        this.producer = new KafkaProducer<>(props);
    }

    @Override
    public void append(LogEvent event) {
        try {
            // Форматируем сообщение согласно Layout (например, PatternLayout)
            String message = getLayout().toSerializable(event).toString();

            // Отправляем в Kafka (ключ null, значение - лог)
            producer.send(new ProducerRecord<>(topic, message));
        } catch (Exception e) {
            // В случае ошибки не роняем приложение, а пишем во внутренний лог Log4j
            error("Unable to write to Kafka", e);
        }
    }

    @Override
    public boolean stop(long timeout, TimeUnit timeUnit) {
        super.stop(timeout, timeUnit);
        if (producer != null) {
            producer.close(java.time.Duration.ofMillis(timeout));
        }
        return true;
    }

    // Фабричный метод, который Log4j вызывает при чтении XML
    @PluginFactory
    public static KafkaAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginAttribute("topic") String topic,
            @PluginAttribute("bootstrapServers") String bootstrapServers,
            @PluginElement("Layout") Layout<? extends Serializable> layout,
            @PluginElement("Filter") final Filter filter) {

        if (name == null) {
            LOGGER.error("No name provided for KafkaAppender");
            return null;
        }
        if (layout == null) {
            layout = org.apache.logging.log4j.core.layout.PatternLayout.createDefaultLayout();
        }
        if (topic == null) {
            LOGGER.error("No topic provided for KafkaAppender");
            return null;
        }
        if (bootstrapServers == null) {
            LOGGER.error("No bootstrapServers provided for KafkaAppender");
            return null;
        }

        return new KafkaAppender(name, filter, layout, bootstrapServers, topic);
    }
}