import { getMockGraphData } from "./mockData";
import axios from "axios";
import constants from "./constants";
import _ from "lodash";

const parseDuration = duration => {
  return (
    duration.substring(0, duration.length - 3) +
    "." +
    duration.substring(duration.length - 3)
  );
};

const reduceGraph = graphData => {
  // get a list of persons we care about
  const persons = _(graphData)
    .filter(["data.nodeType", "flight"])
    .map(flight => {
      return flight.data.racer_id;
    })
    .uniq()
    .value();

  const newGraph = [];
  graphData.forEach(item => {
    // cleanup edges
    if (
      item.group === "edges" &&
      item.data.label !== "worksWith" &&
      persons.includes(item.data.source)
    ) {
      return newGraph.push(item);
    }

    // add everything else
    if (item.group === "nodes" && item.data.label !== "person") {
      return newGraph.push(item);
    }
    // cleanup person verticies
    if (persons.includes(item.data.id)) {
      return newGraph.push(item);
    }
  });

  return newGraph;
};

export const parseLeaders = graphData => {
  const flights = _(graphData)
    .filter(["data.nodeType", "flight"])
    .orderBy(["data.duration"], ["asc"])
    .value();
  const leaders = [];
  flights.forEach(flight => {
    if (flight.data.duration) {
      flight.data.duration = parseDuration(flight.data.duration.toString());
    } else {
      flight.data.duration = flight.data.race_status;
    }
    const person = _.find(graphData, [
      "data.attendee_id",
      flight.data.racer_id
    ]);
    const duplicate = _.find(leaders, [
      "data.attendee_id",
      flight.data.racer_id
    ]);
    if (!duplicate && person && person.data && flight.data.duration) {
      person.data.bestTime = flight.data.duration;
      leaders.push(person);
    }
  });
  return leaders;
};

const parseGraph = graphData => {
  const graph = [];
  // parse the edges
  graphData.edgeList.forEach(edge => {
    if (edge.source !== "unknown" && edge.target !== "unknown") {
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
        const lastName = vertex.last_name ? vertex.last_name : "";
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

  // reduce the graph size
  if (constants.REDUCE_GRAPH) {
    return reduceGraph(graph);
  }
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
