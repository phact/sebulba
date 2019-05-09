import React from "react";
import { makeStyles } from "@material-ui/styles";
import { withRouter, Link } from "react-router-dom";
import Typography from "@material-ui/core/Typography";
import Button from "@material-ui/core/Button";
import CircularProgress from "@material-ui/core/CircularProgress";
import Grid from "@material-ui/core/Grid";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemText from "@material-ui/core/ListItemText";
import ListItemIcon from "@material-ui/core/ListItemIcon";
import Paper from "@material-ui/core/Paper";
import HelicopterIcon from "mdi-material-ui/Helicopter";
import { useSnackbar } from "notistack";
import axios from "axios";
import constants from "../constants";

const useStyles = makeStyles(theme => ({
  loading: {
    zIndex: 9999,
    position: "fixed",
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    background: theme.palette.background.paper,
    opacity: 0.8
  },
  loader: {
    marginRight: theme.spacing(1)
  },
  buttonContainer: {
    marginTop: theme.spacing(3)
  }
}));

const raceTemplate = {
  status: null,
  running: false,
  racer: {
    name: null,
    company: null,
    loading: true
  }
};

const OperatorRace = ({ history, match }) => {
  const classes = useStyles();
  const { enqueueSnackbar } = useSnackbar();
  // setup the race state
  const [race, setRace] = React.useState(raceTemplate);

  // load racer based on url param
  React.useEffect(() => {
    if (!match.params.id) {
      enqueueSnackbar("A valid racer ID is required", { variant: "error" });
      return history.push("/operator");
    }
    const racer = {
      name: match.params.id,
      loading: false
    };
    axios
      .get(constants.API_BASE_URL + "/sebulba/setup/" + match.params.id)
      .then(res => {
        console.log(res);
        // if user data exists, set it
        if (res.data.racer) {
          racer.name = res.data.racer.name;
          racer.company = res.data.racer.company;
        }
        setRace({
          ...race,
          racer: racer
        });
      })
      .catch(err => {
        console.error(err);
        enqueueSnackbar(err.message, { variant: "error" });
        setRace({
          ...race,
          racer: racer
        });
      });
  }, []); // run once!

  const startRace = () => {
    axios
      .get(constants.API_BASE_URL + "/sebulba/start")
      .then(res => {
        console.log(res);
        setRace({
          ...race,
          running: true
        });
        enqueueSnackbar("Race started!");
      })
      .catch(err => {
        console.error(err);
        enqueueSnackbar(err.message, { variant: "error" });
      });
  };

  const endRace = raceStatus => {
    axios
      .get(constants.API_BASE_URL + "/sebulba/stop?status=" + raceStatus)
      .then(res => {
        console.log(res);
        setRace({
          ...race,
          status: raceStatus,
          running: false
        });
        enqueueSnackbar("Race ended with status: " + raceStatus);
        history.push("/operator");
      })
      .catch(err => {
        console.error(err);
        enqueueSnackbar(err.message, { variant: "error" });
      });
  };

  return (
    <React.Fragment>
      <Typography variant="overline" gutterBottom>
        Racer - {race.running ? "Running" : "Stopped"}
      </Typography>
      <Paper elevation={1}>
        <List>
          <ListItem>
            <ListItemIcon>
              <HelicopterIcon />
            </ListItemIcon>
            <ListItemText
              primary={race.racer.name}
              secondary={race.racer.company}
            />
          </ListItem>
        </List>
      </Paper>
      {race.running === false && (
        <Grid container spacing={2} className={classes.buttonContainer}>
          <Grid item xs={5}>
            <Button
              size="large"
              fullWidth
              variant="outlined"
              component={Link}
              to="/operator"
            >
              Cancel
            </Button>
          </Grid>
          <Grid item xs={7}>
            <Button
              size="large"
              fullWidth
              variant="contained"
              color="primary"
              onClick={startRace}
            >
              Start Race
            </Button>
          </Grid>
        </Grid>
      )}
      {race.running === true && (
        <Grid container spacing={2} className={classes.buttonContainer}>
          <Grid item xs={12}>
            <Button
              fullWidth
              size="large"
              variant="outlined"
              onClick={() => endRace("cancelled")}
            >
              Cancel Race
            </Button>
          </Grid>
          <Grid item xs={12}>
            <Button
              fullWidth
              size="large"
              variant="outlined"
              onClick={() => endRace("crashed")}
            >
              Racer Crashed
            </Button>
          </Grid>
          <Grid item xs={12}>
            <Button
              fullWidth
              size="large"
              variant="contained"
              color="primary"
              onClick={() => endRace("completed")}
            >
              Course Completed
            </Button>
          </Grid>
        </Grid>
      )}
      {race.racer.loading && (
        <Grid
          container
          alignItems="center"
          justify="center"
          className={classes.loading}
        >
          <Grid item>
            <CircularProgress
              size={22}
              className={classes.loader}
              thickness={8}
            />
          </Grid>
          <Grid item>
            <Typography variant="overline">loading racer data</Typography>
          </Grid>
        </Grid>
      )}
    </React.Fragment>
  );
};
export default withRouter(OperatorRace);
