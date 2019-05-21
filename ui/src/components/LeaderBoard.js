import React from "react";
import { makeStyles } from "@material-ui/styles";
import Grid from "@material-ui/core/Grid";
import LeaderBoardGraph from "./LeaderBoardGraph";
import LeaderBoardList from "./LeaderBoardList";
import { getGraphData, parseLeaders } from "../api";
import _ from "lodash";

const useStyles = makeStyles(theme => ({
  grid: {
    height: "100vh",
    overflow: "hidden"
  },
  graph: {
    flexGrow: 1,
    maxHeight: "100vh"
  },
  listContainer: {
    padding: theme.spacing(3),
    paddingRight: 0,
    height: "100%"
  },
  brand: {
    position: "fixed",
    bottom: 0,
    right: 0,
    height: 64
  }
}));

const cycleAnimation = (setCurrentLeaderId, setLeaders, setGraphData) => {
  console.log("animation: start");
  // load the graph from the network
  getGraphData().then(graphData => {
    // setup the data from the api response
    const leaders = parseLeaders(graphData);
    setLeaders(leaders);
    setGraphData(graphData);

    // set the animations
    setTimeout(() => {
      const newLeader = _.sample(leaders);
      if (newLeader) {
        setCurrentLeaderId(newLeader.data.id);
      }
      console.log("animation: highlight");
    }, 10000);
    setTimeout(() => {
      setCurrentLeaderId(null);
      console.log("animation: clear");
    }, 20000);
  });
};

const LeaderBoard = () => {
  const classes = useStyles();
  const [currentLeaderId, setCurrentLeaderId] = React.useState(null);
  const [graphData, setGraphData] = React.useState([]);
  const [leaders, setLeaders] = React.useState([]);

  // setup and start the animation loop
  React.useEffect(() => {
    cycleAnimation(setCurrentLeaderId, setLeaders, setGraphData);
    setInterval(() => {
      cycleAnimation(setCurrentLeaderId, setLeaders, setGraphData);
    }, 25000);
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
