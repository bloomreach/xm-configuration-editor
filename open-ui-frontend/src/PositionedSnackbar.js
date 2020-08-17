import React from 'react';
import Snackbar from '@material-ui/core/Snackbar';
import Alert from "@material-ui/lab/Alert";

export default function PositionedSnackbar(props) {

    const {vertical, horizontal, open} = props;

    return (
        <div>
            <Snackbar
                anchorOrigin={{vertical, horizontal}}
                open={open}
                onClose={props.handleClose}
                key={vertical + horizontal}
                autoHideDuration={props.autoHideDuration ? props.autoHideDuration : 3000}
            >
                <Alert onClose={props.handleClose} severity={props.severity ? props.severity : 'success'}>
                    {props.message}
                </Alert>
            </Snackbar>
        </div>
    );
}
