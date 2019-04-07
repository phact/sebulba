import {createStore, applyMiddleware, compose} from 'redux';

import window from 'global/window';
import {taskMiddleware} from 'react-palm';
import {routerMiddleware} from 'react-router-redux';
import thunkMiddleware from 'redux-thunk'
import {hashHistory} from 'react-router';
import reducers from './reducers/reducers';

export const middlewares = [
  taskMiddleware,
  routerMiddleware(hashHistory),
  thunkMiddleware
];

export const enhancers = [applyMiddleware(...middlewares)];

const initialAppState = {
  appName: 'Accelerate Drone Race',
  primaryColor: '#1AB5E0',
  viewport: {
    width: "100%",
    height: "100%"
  },
  qrCode: "scan qr code"
};

const initialState = {
  app: initialAppState,
  NavigationReducer: {
    drawerOpen: false,
    page: "Home"
  }
}

// add redux devtools
const composeEnhancers = window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ || compose;

export default createStore(
  reducers,
  initialState,
  composeEnhancers(...enhancers)
);
