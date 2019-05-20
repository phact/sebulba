package com.datastax.powertools.api;

/*
 *
 * @author Sebastián Estévez on 5/15/19.
 *
 */

import javax.json.bind.annotation.JsonbProperty;
import java.util.List;
import java.util.Map;

/**
 * Created by sebastianestevez on 7/12/18.
 */
public class GraphRepresentation {
    public GraphRepresentation(List<VertexRepresentation> vertexList, List<EdgeRepresentation> edgeList) {
        this.vertexList  = vertexList;
        this.edgeList= edgeList;
    }

    /*
    public static class VertexRepresentation {
        @JsonbProperty
        private String id;
        @JsonbProperty
        private String label;
        @JsonbProperty
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
    }

    public static class EdgeRepresentation {
        @JsonbProperty
        private String id;
        @JsonbProperty
        private String label;
        @JsonbProperty
        private String source;
        @JsonbProperty
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
    }
    */

    @JsonbProperty
    private List<VertexRepresentation> vertexList;
    @JsonbProperty
    private List<EdgeRepresentation> edgeList;
}
