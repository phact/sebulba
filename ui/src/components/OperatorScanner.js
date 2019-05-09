import React from "react";
import { makeStyles } from "@material-ui/styles";
import { withRouter, Link } from "react-router-dom";
import QrReader from "react-qr-reader";
import Typography from "@material-ui/core/Typography";
import Button from "@material-ui/core/Button";

const useStyles = makeStyles(theme => ({
  scanner: {
    marginBottom: theme.spacing(3),
    "& div": {
      boxShadow:
        theme.palette.secondary.main + " 0px 0px 0px 5px inset !important"
    }
  }
}));

const OperatorScanner = ({ history }) => {
  const classes = useStyles();

  const onScan = result => {
    if (result === null) {
      return false;
    }
    console.log(result);
    history.push("/operator/race/" + result);
  };

  const onScanError = error => {
    console.error(error);
  };

  return (
    <React.Fragment>
      <Typography variant="overline" gutterBottom>
        Badge Scanner
      </Typography>
      <Typography variant="body1" gutterBottom>
        Use the scanner to center the participant's badge in your camera.
      </Typography>
      <QrReader
        className={classes.scanner}
        delay={300}
        onScan={onScan}
        onError={onScanError}
      />
      <Button
        variant="outlined"
        size="large"
        fullWidth
        component={Link}
        to="/operator"
      >
        Cancel
      </Button>
    </React.Fragment>
  );
};
export default withRouter(OperatorScanner);
