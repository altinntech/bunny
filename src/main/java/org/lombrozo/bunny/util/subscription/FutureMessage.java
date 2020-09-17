package org.lombrozo.bunny.util.subscription;

import org.lombrozo.bunny.message.Message;

import java.util.function.Consumer;

public interface FutureMessage {

    FutureMessage thenAccept(Consumer<Message> consumer);

    void register(Message message);

    Message block();

    class Empty implements FutureMessage {

        @Override
        public FutureMessage thenAccept(Consumer<Message> ignore) {
            return this;
        }

        @Override
        public void register(Message ignore) {
        }

        @Override
        public Message block() {
            return new Message.Fake();
        }
    }
}
