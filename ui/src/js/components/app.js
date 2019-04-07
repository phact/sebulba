//React/Redux
import {connect} from 'react-redux';
import React, { Component } from 'react'
import green from '@material-ui/core/colors/green';



//MUI
import Grid from '@material-ui/core/Grid';
import { MuiThemeProvider, createMuiTheme } from '@material-ui/core/styles';

// components
import HomePage from './Homepage';
import Dashboard from './Dashboard';
import Admin from './Admin';
import MenuContainer from './Menu';

const theme = createMuiTheme({
  palette: {
    primary: {
      main: '#1AB5E0',
    },
    secondary: {
      main: '#ca5f14',
    },
  },
});

class App extends Component {
  render() {
   theme.palette.primary.main = this.props.app.primaryColor;
   return (
      <MuiThemeProvider theme={theme}>
          <Grid>
          <MenuContainer/>
          <Grid className="basepage">
        {
        this.props.NavigationReducer.page === 'Home' ?
          <HomePage/>
        :
        null
        }
        {
        this.props.NavigationReducer.page === 'Dashboard' ?
          <Dashboard/>
        :
        null
        }
        {
        this.props.NavigationReducer.page === 'Admin' ?
          <Admin/>
        :
        null
        }
 
        </Grid>
      </Grid>
      </MuiThemeProvider>
    );
  }
}


const mapStateToProps = state => state;
const dispatchToProps = dispatch => ({dispatch});

export default connect(mapStateToProps, dispatchToProps)(App);
//export default connect(mapStateToProps, mapDispatchToProps)(App);
