package com.cassidian.drools.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.Random;

/**
 * @author jary
 */
public class Message {

    private Header header;
    private Data data;
    private Payload payload;

    public Message(Data data) {
        this.data = data;
        this.header = new Header(new Random().nextInt(1000));
        this.payload = new Payload();
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof Message) {
            Message other = (Message) o;
            return new EqualsBuilder()
                    .append(header, other.header)
                    .append(data, other.data)
                    .append(payload, other.payload)
                    .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(header)
                .append(data)
                .append(payload)
                .toHashCode();
    }

    @Override
    public String toString() {
        return getHeader().toString();
    }
}