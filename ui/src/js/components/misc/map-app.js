import React, {Component} from 'react';
import {connect} from 'react-redux';
import {
  toggleSidePanel
//} from 'kepler.gl/actions/ui-state-actions';
} from 'kepler.gl/actions';
import AutoSizer from 'react-virtualized/dist/commonjs/AutoSizer';
import KeplerGl from 'kepler.gl';
// Kepler.gl actions
import {addDataToMap} from 'kepler.gl/actions';
// Kepler.gl Data processing APIs
import Processors from 'kepler.gl/processors';
// Kepler.gl Schema APIs
import KeplerGlSchema from 'kepler.gl/schemas';
import Button from './button';
import LeftButton from './leftButton';
import downloadJsonFile from "./file-download";

import nycTrips from './data/dsedata.csv.js';
import dseConfig from './data/dse-config.json';
import nycTripsSubset from './data/nyc-subset.csv';
import {getData, fetchData, pause} from './actions';

const MAPBOX_TOKEN = process.env.MapboxAccessToken; // eslint-disable-line

class App extends Component {

  componentDidMount() {
      // Use processCsvData helper to convert csv file into kepler.gl structure {fields, rows}
      const data = Processors.processCsvData(nycTrips);
      // Create dataset structure
      const dataset = {
        data,
        info: {
          // `info` property are optional, adding an `id` associate with this dataset makes it easier
          // to replace it later
          id: 'dsedata'
        }
      };

      this.props.dispatch(addDataToMap({datasets: dataset, config: dseConfig}));
  }

  // Created to show how to replace dataset with new data and keeping the same configuration
  fetchData = () => {
    const config = this.getMapConfig();
    this.props.dispatch(toggleSidePanel(null));
    this.props.dispatch(fetchData(`/api/v0/powertrain/allEvents`));
  };

  pause= () => {
    this.props.dispatch(toggleSidePanel('layer'));
    this.props.dispatch(pause());
  }
  // This method is used as reference to show how to export the current kepler.gl instance configuration
  // Once exported the configuration can be imported using parseSavedConfig or load method from KeplerGlSchema
  getMapConfig() {
    // retrieve kepler.gl store
    const {keplerGl} = this.props;
    // retrieve current kepler.gl instance store
    const {map} = keplerGl;
    // create the config object
    return KeplerGlSchema.getConfigToSave(map);
  }

  // This method is used as reference to show how to export the current kepler.gl instance configuration
  // Once exported the configuration can be imported using parseSavedConfig or load method from KeplerGlSchema
  exportMapConfig = () => {
    // create the config object
    const mapConfig = this.getMapConfig();
    // save it as a json file
    // TODO: Save this to DSE
    downloadJsonFile(mapConfig, 'kepler.gl.json');
  };


  render() {
    var activeSidePanel = "";
    if (this.props.keplerGl.map !== undefined){
        activeSidePanel = this.props.keplerGl.map.uiState.activeSidePanel;
    }
    return (
      <div style={{position: 'absolute', width: '100%', height: '100%'}}>
        <LeftButton onClick={this.exportMapConfig}>Export Config</LeftButton>
        {
            (activeSidePanel == 'layer') ?
                <Button onClick={this.fetchData}>Fetch Data</Button>
            :
                <Button onClick={this.pause}>Pause</Button>
        }
        <AutoSizer>
          {({height, width}) => (
            <KeplerGl
              mapboxApiAccessToken={MAPBOX_TOKEN}
              id="map"
              width={width}
              height={height}
              debug
            />
          )}
        </AutoSizer>
      </div>
    );
  }
}


const mapStateToProps = state => state;
const dispatchToProps = dispatch => ({dispatch});

export default connect(mapStateToProps, dispatchToProps)(App);
//export default connect(mapStateToProps, mapDispatchToProps)(App);
