import _ from "lodash";
import * as Promise from "bluebird";

// setup the basics
let lastHighlighted = null;
let lastUnhighlighted = null;
const aniDur = 500;
const easing = "ease-in-out";
const layoutPadding = 20;

// reset node positions
const restoreElesPositions = nhood => {
  return Promise.all(
    nhood.map(function(ele) {
      var p = ele.data("orgPos");
      return ele
        .animation({
          position: { x: p.x, y: p.y },
          duration: aniDur,
          easing: easing
        })
        .play()
        .promise();
    })
  );
};

// reset the graph
const clear = cy => {
  cy.stop();
  cy.nodes().stop();

  const nhood = lastHighlighted;
  const others = lastUnhighlighted;

  lastHighlighted = lastUnhighlighted = null;

  const hideOthers = function() {
    return Promise.delay(125).then(function() {
      others.addClass("hidden");
      return Promise.delay(125);
    });
  };

  const showOthers = function() {
    cy.batch(function() {
      cy.elements()
        .removeClass("hidden")
        .removeClass("faded");
    });
    return Promise.delay(aniDur);
  };

  const restorePositions = function() {
    cy.batch(function() {
      others.nodes().forEach(function(n) {
        var p = n.data("orgPos");
        n.position({ x: p.x, y: p.y });
      });
    });

    return restoreElesPositions(nhood.nodes());
  };

  const resetHighlight = function() {
    nhood.removeClass("highlighted");
  };

  return Promise.resolve()
    .then(resetHighlight)
    .then(hideOthers)
    .then(restorePositions)
    .then(showOthers);
};

// highlight a person
const highlightPerson = (node, cy) => {
  const nhood = (lastHighlighted = node.closedNeighborhood());
  const others = (lastUnhighlighted = cy.elements().not(nhood));
  const reset = () => {
    cy.batch(() => {
      others.addClass("hidden");
      nhood.removeClass("hidden");
      cy.elements().removeClass("faded highlighted");
      nhood.addClass("highlighted");
      others.nodes().forEach(n => {
        var p = n.data("orgPos");
        n.animate(
          {
            position: { x: p.x, y: p.y }
          },
          {
            duration: aniDur,
            easing: easing
          }
        );
      });
    });
    return Promise.resolve()
      .then(function() {
        return Promise.resolve();
      })
      .then(function() {
        return Promise.delay(aniDur);
      });
  };

  const runLayout = () => {
    const p = node.data("orgPos");

    const l = nhood.filter(":visible").makeLayout({
      name: "euler",
      fit: false,
      animate: true,
      animationDuration: aniDur,
      animationEasing: easing,
      boundingBox: {
        x1: p.x - 1,
        x2: p.x + 1,
        y1: p.y - 1,
        y2: p.y + 1
      },
      avoidOverlap: true,
      padding: layoutPadding
    });

    const promise = cy.promiseOn("layoutstop");
    l.run();
    return promise;
  };
  const fit = () => {
    return cy
      .animation({
        fit: {
          eles: nhood.filter(":visible"),
          padding: layoutPadding
        },
        easing: easing,
        duration: aniDur
      })
      .play()
      .promise();
  };

  const showOthersFaded = () => {
    return Promise.delay(250).then(() => {
      cy.batch(() => {
        others.removeClass("hidden").addClass("faded");
      });
    });
  };

  return Promise.resolve()
    .then(reset)
    .then(runLayout)
    .then(fit)
    .then(showOthersFaded);
};

// work with the cy graph when ready
export const leaderBoardGraphReady = cy => {
  // lay it out bruh
  cy.ready(e => {
    const myLayout = cy.layout({
      name: "fcose",
      nodeSeparation: 75,
      quality: "default",
      nodeRepulsion: 4500,
      idealEdgeLength: 250,
      edgeElasticity: 0.45,
      nestingFactor: 0.1,
      gravity: 2

      // name: "euler",
      // fit: true,
      // animate: true,
      // animationDuration: aniDur,
      // animationEasing: easing,
      // avoidOverlap: true,
      // padding: layoutPadding,
      // // The ideal length of a spring
      // // - This acts as a hint for the edge length
      // // - The edge length can be longer or shorter if the forces are set to extreme values
      // springLength: edge => 80,

      // // Hooke's law coefficient
      // // - The value ranges on [0, 1]
      // // - Lower values give looser springs
      // // - Higher values give tighter springs
      // springCoeff: edge => 0.0008,

      // // The mass of the node in the physics simulation
      // // - The mass affects the gravity node repulsion/attraction
      // mass: node => 20,

      // // Coulomb's law coefficient
      // // - Makes the nodes repel each other for negative values
      // // - Makes the nodes attract each other for positive values
      // gravity: -1.2,

      // // A force that pulls nodes towards the origin (0, 0)
      // // Higher values keep the components less spread out
      // pull: 0.001,

      // // Theta coefficient from Barnes-Hut simulation
      // // - Value ranges on [0, 1]
      // // - Performance is better with smaller values
      // // - Very small values may not create enough force to give a good result
      // theta: 0.666,

      // // Friction / drag coefficient to make the system stabilise over time
      // dragCoeff: 0.02,

      // // When the total of the squared position deltas is less than this value, the simulation ends
      // movementThreshold: 1,

      // // The amount of time passed per tick
      // // - Larger values result in faster runtimes but might spread things out too far
      // // - Smaller values produce more accurate results
      // timeStep: 20,

      // // The number of ticks per frame for animate:true
      // // - A larger value reduces rendering cost but can be jerky
      // // - A smaller value increases rendering cost but is smoother
      // refresh: 10
    });

    myLayout.pon("layoutstop").then(event => {
      cy.nodes().forEach(node => {
        node._private.data.orgPos = {
          x: node._private.position.x,
          y: node._private.position.y
        };
      });
    });

    myLayout.run();
    cy.on(
      "select unselect",
      "node",
      _.debounce(e => {
        let selectedNode = cy.$("node:selected");
        if (selectedNode.nonempty()) {
          highlightPerson(selectedNode, cy);
        } else {
          clear(cy);
        }
      }, 100)
    );
  });
};
