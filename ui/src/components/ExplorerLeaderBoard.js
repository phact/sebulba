import React from "react";
import { makeStyles } from "@material-ui/styles";
import { tableData } from "../mockData";
import Typography from "@material-ui/core/Typography";
import Table from "@material-ui/core/Table";
import TableBody from "@material-ui/core/TableBody";
import TableCell from "@material-ui/core/TableCell";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";

const useStyles = makeStyles(theme => ({
  root: {}
}));

const ExplorerLeaderBoard = () => {
  const classes = useStyles();
  return (
    <React.Fragment>
      <Typography variant="h5" display="block" gutterBottom>
        Current Leader Board
      </Typography>
      <Table className={classes.root}>
        <TableHead>
          <TableRow>
            <TableCell>Event Id</TableCell>
            <TableCell align="right">Racer</TableCell>
            <TableCell align="right">Event Type</TableCell>
            <TableCell align="right">Start Time</TableCell>
            <TableCell align="right">End Time</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {tableData.map(race => {
            return (
              <TableRow key={race.id}>
                <TableCell component="th" scope="row">
                  {race.id}
                </TableCell>
                <TableCell align="right">{race.racer}</TableCell>
                <TableCell align="right">{race.type}</TableCell>
                <TableCell align="right">{race.start_time}</TableCell>
                <TableCell align="right">{race.end_time}</TableCell>
              </TableRow>
            );
          })}
        </TableBody>
      </Table>
    </React.Fragment>
  );
};

export default ExplorerLeaderBoard;
