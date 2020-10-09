package org.lombrozo.bunny.message.delivery;

public class NonPersistent implements DeliveryMode{

    @Override
    public String toString() {
        return "NonPersistent{}";
    }

    @Override
    public int toInt() {
        return 1;
    }
}
