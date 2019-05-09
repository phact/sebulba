import React from "react";
import { makeStyles } from "@material-ui/styles";
import Grid from "@material-ui/core/Grid";
import LeaderBoardGraph from "./LeaderBoardGraph";
import LeaderBoardList from "./LeaderBoardList";

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

const LeaderBoard = () => {
  const classes = useStyles();

  return (
    <React.Fragment>
      <Grid container alignItems="stretch" className={classes.grid}>
        <Grid item className={classes.listContainer}>
          <LeaderBoardList />
        </Grid>
        <Grid item className={classes.graph}>
          <LeaderBoardGraph />
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
