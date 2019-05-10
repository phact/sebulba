import { createStore } from "redux";

// setup actions
export const actions = {
  myAction: leaderId => ({
    type: "SET_HIGHLIGHTED_LEADER",
    payload: leaderId
  })
};

// setup a reducer
const initialState = {
  highlightedLeader: null
};

const reducer = (state = initialState, action) => {
  switch (action.type) {
    case "SET_HIGHLIGHTED_LEADER":
      return {
        ...state,
        highlightedLeader: action.payload
      };
    default:
      return state;
  }
};

// export the store
export const store = createStore(reducer);
