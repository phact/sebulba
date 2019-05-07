import React from "react";
import { makeStyles } from "@material-ui/styles";
import { Route, Switch } from "react-router-dom";
import OperatorDashboard from "./OperatorDashboard";
import OperatorRace from "./OperatorRace";
import OperatorScanner from "./OperatorScanner";
import Grid from "@material-ui/core/Grid";
import SebulbaHeader from "./SebulbaHeader";

const useStyles = makeStyles(theme => ({
  root: {
    minHeight: "100vh",
    padding: theme.spacing(3)
  },
  grid: {
    maxWidth: theme.spacing(44)
  }
}));

const Operator = () => {
  const classes = useStyles();
  return (
    <React.Fragment>
      <SebulbaHeader />
      <Grid
        container
        className={classes.root}
        justify="center"
        alignItems="center"
      >
        <Grid item className={classes.grid}>
          <Switch>
            <Route exact path="/operator" component={OperatorDashboard} />
            <Route path="/operator/scanner" component={OperatorScanner} />
            <Route path="/operator/race/:id" component={OperatorRace} />
          </Switch>
        </Grid>
      </Grid>
    </React.Fragment>
  );
};

export default Operator;
