import React from "react";
import { Link } from "react-router-dom";
import Button from "@material-ui/core/Button";
import Typography from "@material-ui/core/Typography";

const OperatorDashboard = () => (
  <React.Fragment>
    <Typography variant="overline" gutterBottom>
      Operator Dashboard
    </Typography>
    <Typography variant="body1" gutterBottom>
      To begin a race, first register a racer by clicking below.
    </Typography>
    <Button
      fullWidth
      size="large"
      variant="contained"
      color="primary"
      component={Link}
      to="/operator/scanner"
    >
      Register Racer
    </Button>
  </React.Fragment>
);

export default OperatorDashboard;
