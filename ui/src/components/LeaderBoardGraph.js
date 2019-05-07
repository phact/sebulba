import React from "react";
import CytoscapeComponent from "react-cytoscapejs";
import Cytoscape from "cytoscape";
import { graphData } from "../mockData";
import euler from "cytoscape-euler";
import fcose from "cytoscape-fcose";
import stylesheet from "../assets/graph.stylesheet";
import { leaderBoardGraphReady } from "../assets/graph.leaderboard";

// register layouts
Cytoscape.use(euler);
Cytoscape.use(fcose);

// setup the graph object
const graph = {
  id: "cy",
  motionBlur: true,
  layout: {
    name: "grid"
  },
  stylesheet: stylesheet,
  elements: graphData
};

const LeaderBoardGraph = () => {
  //TODO: setup a timer that cycles through the leaders
  //TODO: select the leaders node and isolate their neighborhood
  //TODO: add new data to the graph when the race is complete
  return (
    <CytoscapeComponent
      {...graph}
      cy={leaderBoardGraphReady}
      style={{ width: "100%", height: "100%" }}
    />
  );
};

export default LeaderBoardGraph;
