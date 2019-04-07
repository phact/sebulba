import React, {PureComponent} from 'react';
import { connect } from 'react-redux';

//MUI
import Typography from '@material-ui/core/Typography';
import Paper from '@material-ui/core/Paper';
import Grid from '@material-ui/core/Grid';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import AddIcon from '@material-ui/icons/AddCircle';
import ZoomInIcon from '@material-ui/icons/ZoomIn';

//css
import style from '../../css/style.css';

class Dashboard extends PureComponent {
  componentWillMount() {
    this.props.init();
  }

  
  render() {
    const rows = [];
    return (
      <div className="container">
        <Grid             container
            direction="row"
            justify="center"
        >
          <Paper className="half-paper" elevation={1}>
            <div className="inside-container">
            <Typography variant="h6" color="inherit" noWrap>
              Races by Racer
            </Typography>

            <Grid className="overflow-holder">
            <Table className={"table"}>
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
                {this.props.races.map((row, index) => {
                  return (
                    <TableRow key={row.id}>
                      <TableCell component="th" scope="row">
                        {row.id}
                      </TableCell>
                      <TableCell align="right">{row.racer}</TableCell>
                      <TableCell align="right">{row.type}</TableCell>
                      <TableCell align="right">{row.start_time}</TableCell>
                      <TableCell align="right">{row.end_time}</TableCell>
                    </TableRow>
                  );
                })}
              </TableBody>
            </Table>
            </Grid>

          </div>
          </Paper>
        </Grid >
      </div>
    );
  }
}

const mapStateToProps = (state) => {
  return {
    transactionList: state.app.transactionList,
    investigationList: state.app.investigationList,
    races: []
  }
}

const mapDispatchToProps = (dispatch, ownProps) => {
  return {
    init: () => {
    },
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(Dashboard);
