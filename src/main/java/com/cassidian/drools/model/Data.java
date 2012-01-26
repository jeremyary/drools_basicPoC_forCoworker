package com.cassidian.drools.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * @author jary
 */
public class Data {
    
    private String type;
    private Boolean withALI;
    private Boolean withData;

    public Data(String type, Boolean withALI, Boolean withData) {
        this.type = type;
        this.withALI = withALI;
        this.withData = withData;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getWithALI() {
        return withALI;
    }

    public void setWithALI(Boolean withALI) {
        this.withALI = withALI;
    }

    public Boolean getWithData() {
        return withData;
    }

    public void setWithData(Boolean withData) {
        this.withData = withData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { 
            return true;
        } else if (o instanceof Data) {
            Data other = (Data) o;
            return new EqualsBuilder()
                    .append(type, other.type)
                    .append(withALI, other.withALI)
                    .append(withData, other.withData)
                    .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(type)
                .append(withALI)
                .append(withData)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "Data{" +
                "type=" + type +
                ", withALI=" + withALI +
                ", withData=" + withData +
                '}';
    }
}