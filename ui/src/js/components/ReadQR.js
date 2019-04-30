import React, {PureComponent} from 'react';
import { connect } from 'react-redux';

import {updateValue, getRequest} from '../actions/actions';

//MUI
import Typography from '@material-ui/core/Typography';
import Paper from '@material-ui/core/Paper';
import Grid from '@material-ui/core/Grid';
import PlayIcon from '@material-ui/icons/PlayCircleFilled';
import StopIcon from '@material-ui/icons/Stop';

//css
import style from '../../css/style.css';

import QrReader from 'react-qr-reader'


class ReadQR extends PureComponent {

  newCode  = x => {
    if (this.props.app.qrCode != x){
      this.props.updateValue("qrCode", x) 
      this.props.getRequest("/sebulba/setup/" + x) 
    }
  }

  render() {
 
        return (
      <div className="inside-container">
        <QrReader
          delay={300}
          onScan={x => x != null ? this.newCode(x) : null } 
        />
        <p>{this.props.app.qrCode}</p>
        <PlayIcon 
          color="primary"
          fontSize="large"
          onClick={(e) => { this.props.getRequest("/sebulba/start")}}
        />
        <StopIcon
          color="primary"
          fontSize="large"
          onClick={(e) => { this.props.getRequest("/sebulba/stop")}}
        />
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
    },
    getRequest: (path) => {
      dispatch(getRequest(path))
    },
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(ReadQR);
