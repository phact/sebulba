import React from "react";
import { makeStyles } from "@material-ui/styles";
import { Route, Switch } from "react-router-dom";
import ExplorerLeaderBoard from "./ExplorerLeaderBoard";
import Grid from "@material-ui/core/Grid";
import SebulbaHeader from "./SebulbaHeader";

const useStyles = makeStyles(theme => ({
  root: {
    minHeight: "100vh",
    padding: theme.spacing(3)
  },
  grid: {
    paddingTop: theme.spacing(12)
  }
}));

const Explorer = () => {
  const classes = useStyles();
  return (
    <React.Fragment>
      <SebulbaHeader />
      <Grid
        container
        className={classes.root}
        justify="center"
 
      >
        <Grid item className={classes.grid}>
          <Switch>
            <Route exact path="/explorer" component={ExplorerLeaderBoard} />
          </Switch>
        </Grid>
      </Grid>
    </React.Fragment>
  );
};

export default Explorer;
