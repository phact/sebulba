// setup the basics
let selectedNeighborhood = null;
let unselectedNodes = null;
let initialized = false;
const animationDuration = 500;
const easing = "ease-in-out";
const layoutPadding = 20;

const getNodeLabel = data => {
  const labelData = {
    type: data.nodeType,
    primary: null,
    secondary: null
  };
  switch (data.nodeType) {
    case "person":
      labelData.primary = data.name;
      labelData.secondary = data.company;
      break;
    case "session":
      labelData.secondary = data.name;
      break;
    case "flight":
      labelData.secondary = data.duration;
      break;
    case "topic":
      labelData.secondary = data.name;
      break;
    default:
      break;
  }
  let label =
    '<div class="graph-label"><p class="label-type">' + data.nodeType + "</p>";
  if (labelData.primary) {
    label = label + '<p class="label-primary">' + labelData.primary + "</p>";
  }
  if (labelData.secondary) {
    label =
      label + '<p class="label-secondary">' + labelData.secondary + "</p>";
  }
  label += "</div>";
  return label;
};

// fade cy elements in
const fadeElementsIn = elements => {
  elements.animate({
    style: { opacity: 0.8 },
    duration: animationDuration,
    easing: easing
  });
};

// fade cy elements out
const fadeElementsOut = elements => {
  elements.animate({
    style: { opacity: 0.08 },
    duration: animationDuration,
    easing: easing
  });
};

// fit to elements
const fitToElements = (cy, elements) => {
  let fitLayoutPadding = layoutPadding;
  if(elements.length && elements.length < 6) {
    fitLayoutPadding = layoutPadding * 10;
  } 

  cy.animation({
    fit: {
      eles: elements,
      padding: fitLayoutPadding
    },
    easing: easing,
    duration: animationDuration
  }).play();
};

// reset the graph after deselecting a person
const clear = cy => {
  // stop in progress animations
  cy.stop();
  cy.nodes().stop();

  const showOthers = function() {
    cy.batch(() => {
      fadeElementsIn(unselectedNodes);
    });
    // reset module globals
    selectedNeighborhood = unselectedNodes = null;
  };

  const restorePositions = function() {
    fitToElements(cy, cy.elements());
    return Promise.all(
      selectedNeighborhood.nodes().map(element => {
        const originalPosition = element.data("orgPos");
        return element
          .animation({
            position: { x: originalPosition.x, y: originalPosition.y },
            duration: animationDuration,
            easing: easing
          })
          .play()
          .promise();
      })
    );
  };

  const resetHighlight = () => {
    // remove all labels
    document.getElementById("cy").firstChild.lastChild.remove();
    selectedNeighborhood.removeClass("highlighted");
  };

  return Promise.resolve()
    .then(restorePositions)
    .then(resetHighlight)
    .then(showOthers);
};

// highlight a person
const highlightPerson = (node, cy) => {
  // get the neighborhood of the selected person
  selectedNeighborhood = node.closedNeighborhood();
  unselectedNodes = cy.elements().not(selectedNeighborhood);

  const layoutNeighborhood = () => {
    fadeElementsOut(unselectedNodes);
    selectedNeighborhood.addClass("highlighted");
    const originalPosition = node.data("orgPos");
    const layout = selectedNeighborhood.makeLayout({
      name: "concentric",
      fit: false,
      animate: true,
      animationDuration: animationDuration,
      animationEasing: easing,
      boundingBox: {
        x1: originalPosition.x - 1,
        x2: originalPosition.x + 1,
        y1: originalPosition.y - 1,
        y2: originalPosition.y + 1
      },
      avoidOverlap: true,
      padding: layoutPadding,
      concentric: function(ele) {
        if (ele.same(node)) {
          return 2;
        } else {
          return 1;
        }
      },
      levelWidth: function() {
        return 1;
      }
    });

    const layoutPromise = cy.promiseOn("layoutstop");
    layout.run();
    return layoutPromise;
  };

  // run the highlight animation
  layoutNeighborhood().then(() => {
    fitToElements(cy, selectedNeighborhood);
    // add labels to nodes
    cy.nodeHtmlLabel([
      {
        query: "node.highlighted",
        valign: "bottom",
        valignBox: "bottom",
        tpl: getNodeLabel
      }
    ]);
  });
};

// run the initial layout after the data in the graph is updated
const initialLayout = cy => {
  const layout = cy.layout(fcoseLayoutConfig);
  // when the layout is complete, iterate over the notes and set their original
  // position so that animations can reset
  layout.pon("layoutstop").then(event => {
    cy.nodes().forEach(node => {
      node._private.data.orgPos = {
        x: node._private.position.x,
        y: node._private.position.y
      };
    });
  });

  layout.run();
};

// setup the graph object
export const graph = {
  id: "cy",
  motionBlur: true,
  layout: {
    name: "preset",
    zoom: 3
  }
};

// work with the cy graph when ready
export const leaderBoardGraphReady = (cy, currentLeaderId) => {
  // if we don't have any elements, then don't do anything
  if (cy.elements().length === 0) {
    return false;
  }

  // if a current leader is selected, select it
  if (currentLeaderId) {
    const selectedNode = cy.$("#" + currentLeaderId);
    highlightPerson(selectedNode, cy);
  }
  if (!currentLeaderId && initialized && selectedNeighborhood) {
    // reset the graph on empty leaders
    clear(cy);
  }

  // re-layout when new elements are added
  if (!currentLeaderId && initialized) {
    initialLayout(cy);
  }

  // we don't need to re-initialize on renders
  if (initialized) {
    return false;
  }
  initialized = true;

  // when the graph is ready, add the main event listener
  cy.ready(e => {
    // initialize the layout after a delay
    setTimeout(() => {
      initialLayout(cy);
    }, 1000);

    cy.on("select unselect", "node", e => {
      const selectedNode = cy.$("node:selected");
      if (selectedNode.nonempty()) {
        // move to the selected person
        highlightPerson(selectedNode, cy);
      } else {
        // reset the graph on empty events
        clear(cy);
      }
    });
  });
};

const fcoseLayoutConfig = {
  name: "fcose",
  nodeSeparation: 75,
  nodeDimensionsIncludeLabels: true,
  animate: true,
  animationDuration: 2200,
  animationEasing: easing,
  quality: "default",
  nodeRepulsion: 4500,
  idealEdgeLength: 250,
  edgeElasticity: 0.45,
  nestingFactor: 0.1,
  gravity: 2
};

const eulerLayoutConfig = {
  name: "euler",
  fit: true,
  animate: true,
  animationDuration: animationDuration,
  animationEasing: easing,
  avoidOverlap: true,
  padding: layoutPadding,
  // The ideal length of a spring
  // - This acts as a hint for the edge length
  // - The edge length can be longer or shorter if the forces are set to extreme values
  springLength: edge => 80,

  // Hooke's law coefficient
  // - The value ranges on [0, 1]
  // - Lower values give looser springs
  // - Higher values give tighter springs
  springCoeff: edge => 0.0008,

  // The mass of the node in the physics simulation
  // - The mass affects the gravity node repulsion/attraction
  mass: node => 20,

  // Coulomb's law coefficient
  // - Makes the nodes repel each other for negative values
  // - Makes the nodes attract each other for positive values
  gravity: -1.2,

  // A force that pulls nodes towards the origin (0, 0)
  // Higher values keep the components less spread out
  pull: 0.001,

  // Theta coefficient from Barnes-Hut simulation
  // - Value ranges on [0, 1]
  // - Performance is better with smaller values
  // - Very small values may not create enough force to give a good result
  theta: 0.666,

  // Friction / drag coefficient to make the system stabilise over time
  dragCoeff: 0.02,

  // When the total of the squared position deltas is less than this value, the simulation ends
  movementThreshold: 1,

  // The amount of time passed per tick
  // - Larger values result in faster runtimes but might spread things out too far
  // - Smaller values produce more accurate results
  timeStep: 20,

  // The number of ticks per frame for animate:true
  // - A larger value reduces rendering cost but can be jerky
  // - A smaller value increases rendering cost but is smoother
  refresh: 10
};
