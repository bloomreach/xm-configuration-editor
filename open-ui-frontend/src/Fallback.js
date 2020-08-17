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
        <ul>
          <li>
            <a
              className="App-link"
              href="https://github.com/bloomreach/xm-configuration-editor"
              target="_blank"
              rel="noopener noreferrer"
            >
              Documentation BrXM Configuration Editor (Open UI)
            </a>
          </li>
          <li>
            <a
              className="App-link"
              href="https://documentation.bloomreach.com/14/library/concepts/open-ui/configure-a-page-tool.html"
              target="_blank"
              rel="noopener noreferrer"
            >
              Configure a Page Tool Extension in BrXM
            </a>
          </li>

        </ul>

      </header>
    </div>
  );
}

export default Fallback;
