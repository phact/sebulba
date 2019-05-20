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

    @JsonbProperty
    private List<VertexRepresentation> vertexList;
    @JsonbProperty
    private List<EdgeRepresentation> edgeList;

    public List<VertexRepresentation> getVertexList() {
        return vertexList;
    }

    public void setVertexList(List<VertexRepresentation> vertexList) {
        this.vertexList = vertexList;
    }

    public List<EdgeRepresentation> getEdgeList() {
        return edgeList;
    }

    public void setEdgeList(List<EdgeRepresentation> edgeList) {
        this.edgeList = edgeList;
    }
}
