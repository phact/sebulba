import { getMockGraphData } from "./mockData";
import axios from "axios";
import constants from "./constants";
import _ from "lodash";

export const parseLeaders = graphData => {
  const flights = _(graphData)
    .filter(["data.nodeType", "flight"])
    .orderBy(["data.duration"], ["asc"])
    .value();
  const leaders = [];
  flights.forEach(flight => {
    const person = _.find(graphData, [
      "data.attendee_id",
      flight.data.racer_id
    ]);
    const duplicate = _.find(leaders, [
      "data.attendee_id",
      flight.data.racer_id
    ]);
    if (!duplicate && person && person.data && flight.data.duration) {
      const duration = flight.data.duration.toString();
      const newDuration =
        duration.substring(0, duration.length - 3) +
        "." +
        duration.substring(duration.length - 3);
      person.data.bestTime = newDuration;
      leaders.push(person);
    }
  });
  console.log(leaders);
  return leaders;
};

const parseGraph = graphData => {
  const graph = [];
  // parse the edges
  graphData.edgeList.forEach(edge => {
    if (edge.source !== "unknown") {
      graph.push({
        group: "edges",
        data: { ...edge }
      });
    }
  });
  // parse the vertices
  graphData.vertexList.forEach(vertex => {
    // try to get the right id:
    let nodeId = null;
    try {
      if (vertex.label === "person") {
        nodeId = vertex["attendee_id"];
        const firstName = vertex.first_name ? vertex.first_name : "";
        const lastName = vertex.last_name ? vertex.first_name : "";
        vertex.name = firstName + " " + lastName;
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
