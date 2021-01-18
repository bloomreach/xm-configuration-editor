import React from 'react';
import Snackbar from '@material-ui/core/Snackbar';
import Alert, {Color} from "@material-ui/lab/Alert";

type PositionedSnackbarProps = {
  open: boolean
  severity: Color
  message: string
  onClose: () => void
}

export default function PositionedSnackbar (props: PositionedSnackbarProps) {
  const {open, severity, message} = props;
  return (
    <>
      <Snackbar
        anchorOrigin={{vertical: 'top', horizontal: 'right'}}
        open={open}
        autoHideDuration={2000}
        onClose={() => props.onClose()}
      >
        <Alert severity={severity ? severity : 'success'}>
          {message}
        </Alert>
      </Snackbar>
    </>
  );
}
