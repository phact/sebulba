import {createAction} from 'redux-actions';

import requestActions from './requestActions.js';
import {get} from '../common/requests.js';
import {changeScreen} from './NavigationActions'

export function updateValue(key, value){
   return(dispatch, getState) => {
        dispatch(updateData("UPDATE", {"key": key, "value": value}))
   }
}

export function getRequest(path){
   return(dispatch, getState) => {
        get({
            url:path,
            success: function(res){
              console.log("successful: "+ JSON.stringify(res) + " get request " + path)
            },
            failure: function(error){
              alert(error)
            },
        dispatch: dispatch
        });
   }
}

export const updateData = (type, data) => {
    return {
        type: type,
        data: data
    }
}

export default {updateValue, getRequest};
