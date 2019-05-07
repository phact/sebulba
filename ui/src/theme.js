import { createMuiTheme } from "@material-ui/core/styles";

export default createMuiTheme({
  typography: {
    fontSize: 16,
    fontFamily: [
      '"Montserrat"',
      '"Source Sans Pro"',
      '"Helvetica Neue"',
      "Arial",
      "sans-serif"
    ].join(","),
    h5: {
      fontWeight: "bold"
    },
    body1: {
      fontFamily: '"Roboto", "Helvetica", "Arial", sans-serif',
      fontWeight: 200
    }
  },
  palette: {
    type: "dark",
    green: "#a4d233",
    purple: "#8031a7",
    orange: "#ca5f14",
    indigo: "#310459",
    skyBlue: "#0cb7e1",
    common: {
      white: "#f8f9f7"
    },
    primary: {
      // DS Blue
      main: "#007a97"
    },
    secondary: {
      // DS Yellow
      main: "#ffc72c"
    },
    background: {
      default: "#11262B",
      // DS Charcoal
      paper: "#374c51"
    },
    grey: {
      500: "9eacab"
    }
  }
});
