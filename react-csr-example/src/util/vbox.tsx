import React from "react";
import {BrProps} from "@bloomreach/react-sdk";

export default function Vbox(props: React.PropsWithChildren<BrProps>) {

  return(
    <div className={props.page.isPreview() ? ' col-sm hst-container' : 'col-sm'}>
      {React.Children.map(props.children, child => (
        <div className={props.page.isPreview() ? 'hst-container-item' : undefined}>
          {child}
        </div>
      ))}
    </div>

  );
}