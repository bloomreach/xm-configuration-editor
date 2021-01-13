import React from "react";
import {UiScope} from "@bloomreach/ui-extension";

type CurrentPageState = {}
type CurrentPageProps = {
  ui: UiScope
}

class CurrentPage extends React.Component<CurrentPageProps, CurrentPageState> {


  render () {

    return <>
      <AppBar position="sticky" variant={'outlined'} color={'default'}>
        <Toolbar>
           <Button
             variant="outlined"
             color="primary"
             style={{marginRight: '10px'}}
             startIcon={<AddOutlinedIcon/>}
             onClick={() => this.openAddDialog()}>
            Add Page
          </Button>
        </Toolbar>
      </AppBar>

    </>
  }

}

export default CurrentPage