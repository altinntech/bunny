package org.lombrozo.bunny.connection;

import org.lombrozo.bunny.domain.binding.ExchangeBinding;
import org.lombrozo.bunny.domain.binding.QueueBinding;
import org.lombrozo.bunny.domain.destination.Destination;
import org.lombrozo.bunny.domain.exchange.Exchange;
import org.lombrozo.bunny.domain.queue.Queue;
import org.lombrozo.bunny.function.Work;
import org.lombrozo.bunny.message.Message;
import org.lombrozo.bunny.message.ReplyToDestination;
import org.lombrozo.bunny.util.exceptions.RabbitException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class TestChannel implements Channel {

    private final Map<String, BlockingDeque<Message>> queuesMap;
    private final ExecutorService listenService = Executors.newFixedThreadPool(1);
    private final ExecutorService publishService = Executors.newFixedThreadPool(1);

    public TestChannel() {
        this(new HashMap<>());
    }

    public TestChannel(Map<String, BlockingDeque<Message>> queuesMap) {
        this.queuesMap = queuesMap;
    }

    @Override
    public void listenQueue(Queue queue, Work work) {
        listenService.submit(() -> submitListenCommand(queue, work));
    }

    @Override
    public void publish(Destination rabbitDestination, Message message) {
        publishService.submit(() -> submitPublishCommand(rabbitDestination, message));
    }

    private void submitListenCommand(Queue queue, Work work) {
        try {
            String key = key(queue.toDestination());
            Message message = queueByKey(key).take();
            work.doWork(message);
        } catch (InterruptedException | RabbitException e) {
            e.printStackTrace();
        }
    }

    private void submitPublishCommand(Destination rabbitDestination, Message message) {
        String key = key(rabbitDestination);
        queueByKey(key).add(message);
    }

    @Override
    public void create(Queue queue) {
        initInMemoryQueue(new ReplyToDestination(queue.toDestination()).value());
    }

    @Override
    public void create(Exchange exchange) {
        initInMemoryQueue(new ReplyToDestination(exchange).value());
    }

    @Override
    public void create(QueueBinding ignore) {

    }

    @Override
    public void create(ExchangeBinding ignore) {

    }

    private BlockingDeque<Message> queueByKey(String key) {
        initInMemoryQueue(key);
        return queuesMap.get(key);
    }

    private synchronized void initInMemoryQueue(String key) {
        if (!queuesMap.containsKey(key))
            queuesMap.put(key, new LinkedBlockingDeque<>());
    }

    private String key(Destination destination) {
        return destination.exchangeName() + "." + destination.routingKey();
    }
}
