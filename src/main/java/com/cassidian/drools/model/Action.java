package com.cassidian.drools.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * @author jary
 */
public class Action {
    
    private Message message;

    public Action(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof Action) {
            Action other = (Action) o;
            return new EqualsBuilder()
                    .append(message, other.message)
                    .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(message)
                .toHashCode();
    }
    
    public void tell(String recipient) {
        System.out.println("Action: telling " + recipient + " about message " + getMessage().toString());
    }
    
    public void tell(String[] recipients) {
        for (String recipient : recipients) {
            System.out.println("Action: telling " + recipient + " about message " + getMessage().toString());
        }
    }

    @Override
    public String toString() {
        return "Action{" +
                "message=" + message +
                '}';
    }
}
