import React from "react";
import Routes from "./routes.js";
import { HashRouter } from "react-router-dom";
import "./assets/animation.css";

const App = () => (
  <HashRouter>
    <Routes />
  </HashRouter>
);

export default App;
