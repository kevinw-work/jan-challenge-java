package com.noisy.wobbler.formatter;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noisy.wobbler.formatter.FormatRequest.ComponentSection;
import com.noisy.wobbler.formatter.FormatRequest.RequestRecord;

@Component
public class RabbitMQConsumer {

  @Autowired
  private S3Operations s3Operations;

  public void receiveMessage(byte[] message) {
    System.out.println("Received Message From RabbitMQ: " + new String(message));

    FormatRequest request = getFormatRequest(new String(message));

    ComponentSection objectDetails = null;
    
    if (request != null) {
      objectDetails = getObjectDetails(request);
    }
    
    // Read the object, format it, write it to a differnet bucket,
    // alter the received message and publish it to a different queue.
    if (objectDetails != null) {
      try {
        String object = s3Operations.read(objectDetails);

        String formattedObject = format(object);

        ByteArrayInputStream bais = new ByteArrayInputStream(formattedObject.getBytes());

        s3Operations.write(bais, "formatted", objectDetails.getKey());

        addFormatterSection(request, objectDetails);

        System.out.println("Added formatter section: " + request);
      } catch (Exception e) {
        System.out.println("Failed to read, format and publish: " + e.getMessage());
      }

      // TODO write to RabbitMQ
      System.out.println("No time to complete task to write to RabbitMQ");
    }
  }

  private void addFormatterSection(FormatRequest request, ComponentSection objectDetails) {
    List<RequestRecord> requestRecords = request.getRecords();
    RequestRecord newRecord = new RequestRecord();
    ComponentSection formatterSection = new ComponentSection();
    formatterSection.setBucket(objectDetails.getBucket());
    formatterSection.setKey(objectDetails.getKey());

    newRecord.setFormatter(formatterSection);
    requestRecords.add(newRecord);
  }

  private String format(String object) {
    return StringUtils.replace(object, ".", ".\n");
  }

  private FormatRequest getFormatRequest(String message) {
    FormatRequest request = null;
    try {
      request = new ObjectMapper().readValue(new String(message), FormatRequest.class);
    } catch (Exception e) {
      System.out.println("Failed to parse message into a FormatRequest: " + e.getMessage());
    }

    return request;
  }

  private ComponentSection getObjectDetails(FormatRequest request) {
    ComponentSection unpackerSection = null;

    List<RequestRecord> records = request.getRecords();

    for(RequestRecord record : records) {
      if (record.getUnpacker() != null) {
        unpackerSection = record.getUnpacker();
        break;
      }
    }

    return unpackerSection;
  }
}
