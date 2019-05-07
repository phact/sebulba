import React from "react";
import { makeStyles } from "@material-ui/styles";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemText from "@material-ui/core/ListItemText";
import Avatar from "@material-ui/core/Avatar";
import Divider from "@material-ui/core/Divider";

const useStyles = makeStyles(theme => ({
  root: {
    position: "fixed",
    top: 0,
    left: 0,
    right: 0,
    paddingLeft: theme.spacing(3),
    paddingRight: theme.spacing(3)
  },
  appAvatar: {
    "& img": {
      objectFit: "contain"
    },
    marginRight: theme.spacing(2)
  }
}));

const SebulbaHeader = ({ className }) => {
  const classes = useStyles();
  return (
    <div className={classes.root}>
      <List className={className}>
        <ListItem>
          <Avatar
            className={classes.appAvatar}
            src={require("../assets/images/goggles.svg")}
          />
          <ListItemText
            primary="Sebulba"
            secondary="DataStax Accelerate, 2019"
          />
        </ListItem>
        <Divider />
      </List>
    </div>
  );
};

export default SebulbaHeader;
