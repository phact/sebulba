package com.datastax.powertools.api;

/*
 *
 * @author Sebastián Estévez on 5/20/19.
 *
 */



import javax.json.bind.annotation.JsonbProperty;

public class EdgeRepresentation {
    private String id;
    private String label;
    private String source;
    private String target;

    public void setId(String id) {
        this.id = id;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }
}
