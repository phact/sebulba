const AppReducer = (state = '', action) => {
    switch (action.type) {
        case "UPDATE":
            return {
                ...state,
                [action.data.key]: action.data.value
            }
        default:
            return state
    }
}

export default AppReducer
