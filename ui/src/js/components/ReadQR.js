import React, {PureComponent} from 'react';
import { connect } from 'react-redux';

import {updateValue} from '../actions/actions';

//MUI
import Typography from '@material-ui/core/Typography';
import Paper from '@material-ui/core/Paper';
import Grid from '@material-ui/core/Grid';

//css
import style from '../../css/style.css';

import QrReader from 'react-qr-reader'

class ReadQR extends PureComponent {
  
  render() {
    const rows = [];
    return (
      <div className="inside-container">
        <QrReader
          delay={300}
          onScan={x => x != null ? this.props.updateValue("qrCode", x) : null } 
        />
        <p>{this.props.app.qrCode}</p>
      </div>
    );
  }
}

const mapStateToProps = (state) => {
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

export default connect(mapStateToProps, mapDispatchToProps)(ReadQR);
