import React from "react";
import clsx from "clsx";
import { makeStyles } from "@material-ui/styles";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemText from "@material-ui/core/ListItemText";
import ListItemAvatar from "@material-ui/core/ListItemAvatar";
import Avatar from "@material-ui/core/Avatar";
import { Typography } from "@material-ui/core";

const useStyles = makeStyles(theme => ({
  list: {
    width: 400,
    borderRight: "1px solid",
    borderColor: theme.palette.background.paper,
    height: "100%"
  },
  subHeader: {
    opacity: 0.3
  },
  avatarFirst: {
    backgroundColor: theme.palette.green
  },
  avatarSecond: {
    backgroundColor: theme.palette.purple
  },
  leader: {
    transition: theme.transitions.create("all", {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.leavingScreen
    })
  },
  leaderGrow: {
    transform: "scale(1.15)",
    backgroundColor: theme.palette.background.paper,
    boxShadow: theme.shadows[1]
  }
}));

const LeaderBoardList = ({ currentLeaderId, leaders }) => {
  const classes = useStyles();
  return (
    <List
      subheader={
        <Typography variant="overline" className={classes.subHeader}>
          Accelerate 2019
        </Typography>
      }
      className={classes.list}
    >
      {leaders.map((leader, index) => (
        <ListItem
          key={index}
          className={clsx(classes.leader, {
            [classes.leaderGrow]: currentLeaderId === leader.data.id
          })}
        >
          <ListItemAvatar>
            <React.Fragment>
              {index === 0 && (
                <Avatar className={classes.avatarFirst}>{index + 1}</Avatar>
              )}
              {index < 3 && 0 < index && (
                <Avatar className={classes.avatarSecond}>{index + 1}</Avatar>
              )}
            </React.Fragment>
          </ListItemAvatar>
          <ListItemText
            primary={leader.data.name}
            primaryTypographyProps={{ noWrap: true, display: "block" }}
            secondaryTypographyProps={{ component: "div" }}
            secondary={
              <React.Fragment>
                <Typography display="block" variant="body2" noWrap>
                  {leader.data.company}
                </Typography>
                <Typography variant="overline" color="secondary">
                  {leader.data.bestTime} seconds
                </Typography>
              </React.Fragment>
            }
          />
        </ListItem>
      ))}
    </List>
  );
};

export default LeaderBoardList;
