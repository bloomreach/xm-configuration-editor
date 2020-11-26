import React from 'react';
import './Fallback.css';
import logo from './logo.svg';

function Fallback () {
  return (
    <div className="App">
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo"/>
        <p>
          This is an <code>Open UI Plugin</code> and should be added in the context of BRX
        </p>
      </header>
    </div>
  );
}

export default Fallback;
