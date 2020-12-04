import React from "react";
import {BrProps} from "@bloomreach/react-sdk";

export default function Nomarkup (props: React.PropsWithChildren<BrProps>) {

  return (
    props.page.isPreview()
      ? (
        // tslint:disable:jsx-no-multiline-js
        <>
          {React.Children.map(props.children, child => (
            <div className="hst-container-item">{child}</div>
          ))}
        </>
        // tslint:enable:jsx-no-multiline-js
      )
      : <>{props.children}</>
  );
}