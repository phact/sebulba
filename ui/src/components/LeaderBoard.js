import React from "react";
import { makeStyles } from "@material-ui/styles";
import Grid from "@material-ui/core/Grid";
import LeaderBoardGraph from "./LeaderBoardGraph";
import LeaderBoardList from "./LeaderBoardList";
import { graphData } from "../mockData";
import _ from "lodash";

const useStyles = makeStyles(theme => ({
  grid: {
    height: "100vh",
    overflow: "hidden"
  },
  graph: {
    flexGrow: 1,
    maxHeight: "100vh"
    // add some subtle animation
    // animation: "graph-motion 30s linear infinite"
  },
  listContainer: {
    padding: theme.spacing(3),
    paddingRight: 0
  },
  brand: {
    position: "fixed",
    bottom: 0,
    right: 0,
    height: 64
  }
}));

const leaders = _(graphData)
  .filter(["data.nodeType", "person"])
  .sampleSize(7)
  .value();

const cycleAnimation = setLeaderId => {
  console.log("animation: start");
  setTimeout(() => {
    const newLeader = _.sample(leaders);
    setLeaderId(newLeader.data.id);
    console.log("animation: highlight");
  }, 10000);
  setTimeout(() => {
    setLeaderId(null);
    console.log("animation: clear");
  }, 20000);
};

const LeaderBoard = () => {
  const classes = useStyles();
  const [currentLeaderId, setCurrentLeaderId] = React.useState(null);

  //TODO: setup a timer that cycles through the leaders
  //TODO: select the leaders node and isolate their neighborhood
  //TODO: add new data to the graph when the race is complete

  // animation loop:
  // wait 10 seconds
  // select random person from the list
  // highlight list person
  // highlight neighborhood
  // wait 10 seconds
  // unhighlight neighborhood

  // setup and start the animation loop
  React.useEffect(() => {
    cycleAnimation(setCurrentLeaderId);
    setInterval(() => {
      cycleAnimation(setCurrentLeaderId);
    }, 20000);
  }, []); // run once!

  return (
    <React.Fragment>
      <Grid container alignItems="stretch" className={classes.grid}>
        <Grid item className={classes.listContainer}>
          <LeaderBoardList
            currentLeaderId={currentLeaderId}
            leaders={leaders}
          />
        </Grid>
        <Grid item className={classes.graph}>
          <LeaderBoardGraph
            currentLeaderId={currentLeaderId}
            graphData={graphData}
          />
        </Grid>
      </Grid>
      <img
        className={classes.brand}
        alt="DataStax Logo"
        src={require("../assets/images/datastax-logo-inverted.png")}
      />
    </React.Fragment>
  );
};

export default LeaderBoard;
