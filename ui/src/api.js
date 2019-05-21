import { getMockGraphData } from "./mockData";
import axios from "axios";
import constants from "./constants";
import _ from "lodash";

export const parseLeaders = graphData => {
  return _(graphData)
    .filter(["data.nodeType", "person"])
    .slice(0, 20)
    .value();
};

const parseGraph = graphData => {
  const graph = [];
  // parse the edges
  graphData.edgeList.forEach(edge => {
    graph.push({
      group: "edges",
      data: { ...edge }
    });
  });
  // parse the vertices
  graphData.vertexList.forEach(vertex => {
    // try to get the right id:
    let nodeId = null;
    try {
      if (vertex.label === "person") {
        nodeId = vertex["attendee_id"];
        vertex.name = vertex.first_name + " " + vertex.last_name;
      } else {
        nodeId = vertex[vertex.label + "_id"];
      }
    } catch (e) {
      console.error(e);
    }

    graph.push({
      group: "nodes",
      data: {
        ...vertex,
        nodeType: vertex.label,
        id: nodeId
      }
    });
  });
  return graph;
};

export const getGraphData = () => {
  if (constants.USE_MOCK) {
    return getMockGraphData();
  }

  return new Promise((resolve, reject) => {
    axios
      .get(constants.API_BASE_URL + "/sebulba/graph")
      .then(res => {
        const graphData = parseGraph(res.data);
        resolve(graphData);
      })
      .catch(err => {
        console.error(err);
      });
  });
};
