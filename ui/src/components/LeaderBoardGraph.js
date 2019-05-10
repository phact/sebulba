import React from "react";
import CytoscapeComponent from "react-cytoscapejs";
import Cytoscape from "cytoscape";
import euler from "cytoscape-euler";
import fcose from "cytoscape-fcose";
import nodeHtmlLabel from "cytoscape-node-html-label";
import stylesheet from "../assets/graph.stylesheet";
import { graph, leaderBoardGraphReady } from "../assets/graph.leaderboard";

// register layouts
Cytoscape.use(euler);
Cytoscape.use(fcose);
nodeHtmlLabel(Cytoscape);

const LeaderBoardGraph = ({ graphData, currentLeaderId }) => {
  return (
    <CytoscapeComponent
      {...graph}
      stylesheet={stylesheet}
      elements={graphData}
      cy={cy => {
        leaderBoardGraphReady(cy, currentLeaderId);
      }}
      style={{ width: "100%", height: "100%" }}
    />
  );
};

export default LeaderBoardGraph;
