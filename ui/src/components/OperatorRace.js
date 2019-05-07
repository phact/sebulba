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
import EndRaceDialog from "./EndRaceDialog";

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
  id: null,
  status: null,
  running: false,
  racer: {
    id: null,
    name: null,
    company: null,
    loading: true
  }
};

const OperatorRace = ({ history, match }) => {
  const classes = useStyles();
  // setup the race state
  const [race, setRace] = React.useState(raceTemplate);

  // load racer based on url param
  React.useEffect(() => {
    //TODO: get the racer details from the REST API, handle errors
    //GET "/sebulba/setup/" + match.params.id
    setTimeout(() => {
      setRace({
        ...race,
        racer: {
          id: "crw-a7knr32",
          name: "Chris Wilhite",
          company: "DataStax",
          loading: false
        }
      });
    }, 3000);
  }, []); // run once!

  const startRace = () => {
    setRace({
      ...race,
      running: true
    });
    //TODO: post the race to the REST API
    //GET "/sebulba/start"
  };

  const endRace = raceStatus => {
    setRace({
      ...race,
      status: raceStatus,
      running: false
    });
    //TODO: save the race to the REST API
    //GET "/sebulba/stop"
    history.push("/operator");
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
            <EndRaceDialog
              raceStatus="cancelled"
              buttonVariant="outlined"
              buttonTitle="Cancel Race"
              saveRace={() => endRace("cancelled")}
            />
          </Grid>
          <Grid item xs={12}>
            <EndRaceDialog
              raceStatus="crashed"
              buttonVariant="outlined"
              buttonTitle="Racer Crashed"
              saveRace={() => endRace("crashed")}
            />
          </Grid>
          <Grid item xs={12}>
            <EndRaceDialog
              raceStatus="completed"
              buttonVariant="contained"
              buttonColor="primary"
              buttonTitle="Course Completed"
              saveRace={() => endRace("completed")}
            />
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
