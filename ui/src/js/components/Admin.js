import React, {PureComponent} from 'react';
import { connect } from 'react-redux';
import { GithubPicker } from 'react-color';
import Paper from '@material-ui/core/Paper';
import Grid from '@material-ui/core/Grid';
import Divider from '@material-ui/core/Divider';
import Button from '@material-ui/core/Button';
import Menu from '@material-ui/core/Menu';
import MenuItem from '@material-ui/core/MenuItem';
import AddIcon from '@material-ui/icons/Add';
import Typography from '@material-ui/core/Typography';
import TextField from '@material-ui/core/TextField';
import Fab from '@material-ui/core/Fab';


import style from '../../css/style.css';

import {updateValue} from '../actions/actions';

class Admin extends PureComponent {
  componentWillMount() {
    this.props.init();
  }



  render() {
    var uniqueProperties = [];
    return (
      <Paper className="paper" elevation={1}>
        <div className="inside-container">
          <Typography variant="h6" color="inherit" noWrap>
            Admin Controls:
          </Typography>
          <div>
                  <TextField
                    id="standard-full-width"
                    label="App Name"
                    style={{ margin: 8 }}
                    placeholder="App Name"
                    fullWidth
                    margin="normal"
                    InputLabelProps={{
                      shrink: true,
                    }}
                  />

              </div>

          <Typography variant="h6" color="inherit" noWrap>
            Buttons:
          </Typography>
          <div>
            <br/>

            <div>
              <Fab variant="extended" onClick={(e) => { console.log("button clicked") }}>
                Example button
              </Fab>
            </div>
        </div>
      </div>
    </Paper>
    );
  }
}

const mapStateToProps = (state, ownProps) => {
  return {
    app: state.app,
  }
}

const mapDispatchToProps = (dispatch, ownProps) => {
  return {
    init: () => {
    },
    updateValue : (key, value) => {
      dispatch(updateValue(key, value))
    }
  }
}


const AdminContainer = connect(
    mapStateToProps,
    mapDispatchToProps
)(Admin)
export default AdminContainer;
