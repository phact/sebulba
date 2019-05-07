import React from "react";
import Button from "@material-ui/core/Button";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import DialogContent from "@material-ui/core/DialogContent";
import DialogContentText from "@material-ui/core/DialogContentText";
import DialogTitle from "@material-ui/core/DialogTitle";

const EndRaceDialog = ({
  raceStatus,
  saveRace,
  buttonTitle,
  buttonVariant,
  buttonColor
}) => {
  const [open, setOpen] = React.useState(false);
  const handleClickOpen = () => setOpen(true);
  const handleClose = () => setOpen(false);

  return (
    <React.Fragment>
      <Button
        fullWidth
        size="large"
        color={buttonColor}
        variant={buttonVariant}
        onClick={handleClickOpen}
      >
        {buttonTitle}
      </Button>
      <Dialog open={open} onClose={handleClose}>
        <DialogTitle>{"End current race?"}</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Are you sure you would like to end the current race with the status
            of <b>{raceStatus}</b>?
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>Cancel</Button>
          <Button variant="contained" color="primary" onClick={saveRace}>
            End Race
          </Button>
        </DialogActions>
      </Dialog>
    </React.Fragment>
  );
};

export default EndRaceDialog;
