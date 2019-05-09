import { createStore } from "redux";

// setup actions
export const actions = {
  myAction: () => ({
    type: "MY_ACTION"
  })
};

// setup a reducer
const initialState = {
  myValue: false
};

const reducer = (state = initialState, action) => {
  switch (action.type) {
    case "MY_ACTION":
      return {
        ...state,
        myValue: !state.myValue
      };
    default:
      return state;
  }
};

// export the store
export const store = createStore(reducer);
