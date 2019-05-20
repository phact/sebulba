import { getMockGraphData } from "./mockData";
import axios from "axios";
import constants from "./constants";

const parseGraph = graphData => {
  console.log(graphData);
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
    graph.push({
      group: "nodes",
      data: {
        nodeType: vertex.label,
        id: vertex.id,
        ...vertex.properties
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
    //
    axios
      .get(constants.API_BASE_URL + "/sebulba/graph")
      .then(res => {
        resolve(parseGraph(res.data));
      })
      .catch(err => {
        console.error(err);
      });
  });
};
