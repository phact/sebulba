import theme from "../theme";

export default [
  {
    selector: "node",
    style: {
      opacity: 0.8,
      "border-width": 4,
      "border-color": theme.palette.background.paper,
      "background-color": "#2B65EC"
    }
  },
  {
    selector: "edge",
    style: {
      width: 1,
      opacity: 0.6,
      "line-color": theme.palette.background.paper,
      events: "no"
    }
  },
  {
    selector: "node.highlighted",
    style: {
      "min-zoomed-font-size": 0,
      "z-index": 9999
    }
  },
  {
    selector: "edge.highlighted",
    style: {
      opacity: 0.8,
      width: 6,
      "z-index": 9999
    }
  },
  {
    selector: "node:selected",
    style: {
      width: 40,
      height: 40,
      "border-color": "rgb(187, 219, 247)",
      "border-opacity": 0.5,
      "border-width": 10
    }
  },
  {
    selector: "edge[label].highlighted",
    style: {
      label: "data(label)",
      "font-size": 4,
      "font-family": ["Roboto"],
      "font-weight": 300,
      "edge-text-rotation": "autorotate",
      color: "#f8f9f7",
      "text-outline-color": "#374c51",
      "text-outline-width": 1
    }
  },
  {
    selector: 'node[nodeType = "person"]',
    style: {
      "background-color": theme.palette.secondary.main
    }
  },
  {
    selector: 'node[nodeType = "flight"]',
    style: {
      "background-color": theme.palette.common.white
    }
  },
  {
    selector: 'node[nodeType = "session"]',
    style: {
      "background-color": theme.palette.orange
    }
  },
  {
    selector: 'node[nodeType = "topic"]',
    style: {
      "background-color": theme.palette.skyBlue
    }
  }
];
