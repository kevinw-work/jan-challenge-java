package com.noisy.wobbler.formatter;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FormatRequest {
    @JsonProperty(value = "Records")
    private List<RequestRecord> records = new ArrayList<>();

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RequestRecord {
        private ComponentSection unpacker;
        private ComponentSection formatter;
    
        public ComponentSection getUnpacker() {
            return unpacker;
        }
    
        public void setUnpacker(ComponentSection unpacker) {
            this.unpacker = unpacker;
        }

        public ComponentSection getFormatter() {
            return formatter;
        }

        public void setFormatter(ComponentSection formatter) {
            this.formatter = formatter;
        }

        @Override
        public String toString() {
            return "RequestRecord [unpacker=" + unpacker + ", formatter=" + formatter + "]";
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ComponentSection {
        private String bucket;
        private String key;

        public String getBucket() {
            return bucket;
        }
        public void setBucket(String bucket) {
            this.bucket = bucket;
        }
        public String getKey() {
            return key;
        }
        public void setKey(String key) {
            this.key = key;
        }
        @Override
        public String toString() {
            return "UnpackerSection [bucket=" + bucket + ", key=" + key + "]";
        }
    }

    public List<RequestRecord> getRecords() {
        return records;
    }

    public void setRecords(List<RequestRecord> records) {
        this.records = records;
    }

    @Override
    public String toString() {
        return "FormatRequest [records=" + records + "]";
    }
}
