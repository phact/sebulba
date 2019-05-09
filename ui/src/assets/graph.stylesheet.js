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
    selector: 'node[nodeType = "person"]',
    style: {
      "background-color": theme.palette.secondary.main
    }
  },
  {
    selector: 'node[nodeType = "drone"]',
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
    selector: 'node[nodeType = "flight"]',
    style: {
      "background-color": theme.palette.primary.main
    }
  },
  {
    selector: 'node[nodeType = "topic"]',
    style: {
      "background-color": theme.palette.skyBlue
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
      width: 4,
      "z-index": 9999
    }
  },
  {
    selector: ".faded",
    style: {
      events: "no"
    }
  },
  {
    selector: "node.faded",
    style: {
      opacity: 0.08
    }
  },
  {
    selector: "edge.faded",
    style: {
      opacity: 0.06
    }
  },
  {
    selector: ".hidden",
    style: {
      display: "none"
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
  }
];
