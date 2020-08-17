#Config Editor & Demo Project

## How to run

Start the brX project:

```bash
cd demo && mvn verify && mvn -Pcargo.run
```

Start the react-csr project (PORT 3001)

```bash
cd react-csr-example
PORT=3001 npm run dev 
```

Start the UI Extension project which is at /open-ui-frontend
(Port should be 3000)
```bash
yarn start
```

## Authorization

| Userrole  |Implied Userrole  | Description  |
|---|---|---|
|xm.config-editor.current-page.editor   |xm.config-editor.current-page.viewer   | Allows editing of current page structure via "save" button  |
|xm.config-editor.current-page.viewer   |xm.config-editor.user   |Allows viewing of current page tab   |
|xm.config-editor.user  |   |Required to see the Channel Config Editor OpenUi Extension   |
