package com.datastax.powertools.api;

/*
 *
 * @author Sebastián Estévez on 5/20/19.
 *
 */


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class VertexRepresentation {
    @JsonProperty
    private String id;
    @JsonProperty
    private String label;
    @JsonProperty
    private Map<String, Object> properties;

    public void setId(String id) {
        this.id = id;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }
}
