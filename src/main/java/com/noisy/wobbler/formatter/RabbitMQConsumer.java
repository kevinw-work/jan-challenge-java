package com.noisy.wobbler.formatter;

import org.springframework.stereotype.Component;

@Component
public class RabbitMQConsumer {
  public void receiveMessage(byte[] message) {
    System.out.println("Received Message From RabbitMQ: " + new String(message));
  }
}
