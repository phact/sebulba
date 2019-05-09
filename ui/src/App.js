import React from "react";
import Routes from "./routes.js";
import { HashRouter } from "react-router-dom";
import { SnackbarProvider } from "notistack";
import "./assets/animation.css";

const App = () => (
  <SnackbarProvider
    anchorOrigin={{
      vertical: "bottom",
      horizontal: "center"
    }}
  >
    <HashRouter>
      <Routes />
    </HashRouter>
  </SnackbarProvider>
);

export default App;
