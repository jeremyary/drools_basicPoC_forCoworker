package com.cassidian.drools.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * @author jary
 */
public class Header {

    private Integer uniqueId;

    public Header(Integer uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Integer getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(Integer uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof Header) {
            Header other = (Header) o;
            return new EqualsBuilder()
                    .append(uniqueId, other.uniqueId)
                    .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(uniqueId)
                .toHashCode();
    }

    @Override
    public String toString() {
        return uniqueId.toString();
    }
}
