import React from "react";
import { makeStyles } from "@material-ui/styles";
import Typography from "@material-ui/core/Typography";
import Grid from "@material-ui/core/Grid";
import Paper from "@material-ui/core/Paper";
import { Link } from "react-router-dom";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemText from "@material-ui/core/ListItemText";
import Avatar from "@material-ui/core/Avatar";
import SebulbaHeader from "./SebulbaHeader";
import GraphqlIcon from "mdi-material-ui/Graphql";
import MonitorDashboardIcon from "mdi-material-ui/MonitorDashboard";
import CellphoneTextIcon from "mdi-material-ui/CellphoneText";

const views = [
  {
    icon: <GraphqlIcon />,
    title: "Leaderboard Mode",
    subTitle: "Automated for passive consumption",
    path: "/leaderboard"
  },
  // {
  //   icon: <MonitorDashboardIcon />,
  //   title: "Explorer Mode",
  //   subTitle: "Explore the entire racer graph",
  //   path: "/explorer"
  // },
  {
    icon: <CellphoneTextIcon />,
    title: "Operator Mode",
    subTitle: "Manage the registration of racers and races",
    path: "/operator"
  }
];

const useStyles = makeStyles(theme => ({
  root: {
    height: "100vh",
    padding: theme.spacing(3)
  },
  grid: {
    maxWidth: theme.spacing(44)
  },
  logo: {
    marginBottom: theme.spacing(3)
  },
  avatar: {
    backgroundColor: theme.palette.indigo,
    marginRight: theme.spacing(2),
    color: theme.palette.common.white,
    padding: 2
  },
  appAvatar: {
    "& img": {
      objectFit: "contain"
    },
    marginRight: theme.spacing(2)
  }
}));

const Chooser = () => {
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
          <Typography variant="overline" gutterBottom>
            Select a view from the list below
          </Typography>
          <Paper elevation={1}>
            <List>
              {views.map((view, index) => (
                <ListItem key={index} button component={Link} to={view.path}>
                  <Avatar className={classes.avatar}>{view.icon}</Avatar>
                  <ListItemText
                    primary={view.title}
                    secondary={view.subTitle}
                  />
                </ListItem>
              ))}
            </List>
          </Paper>
        </Grid>
      </Grid>
    </React.Fragment>
  );
};

export default Chooser;
