import React from "react";
import { Route, Switch } from "react-router-dom";
import Chooser from "./components/Chooser";
import LeaderBoard from "./components/LeaderBoard";
import Operator from "./components/Operator";
import Explorer from "./components/Explorer";

const Routes = () => (
  <React.Fragment>
    <Switch>
      <Route exact path="/" component={Chooser} />
      <Route path="/leaderboard" component={LeaderBoard} />
      <Route path="/operator" component={Operator} />
      <Route path="/explorer" component={Explorer} />
    </Switch>
  </React.Fragment>
);

export default Routes;
