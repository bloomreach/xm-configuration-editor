# Config Editor (WIP documentation)

The Config Editor project is a tool in the channel manager, as an Open UI Page Tool extension, to edit HST configuration.

Although the project has the ambition to edit all hst configuration we currently have only support for the "Current Page" feature.

The current page feature will allow a CMS user with the appropriate user roles to edit the page model of a landingpage.


## Build the configuration api and the open ui frontend project

```bash
mvn clean install
```

## Run the demo project

Build and Start the brX project:

```bash
cd demo && mvn verify && mvn -Pcargo.run
```

Start the react-csr project (PORT 3001)

```bash
cd react-csr-example
npm run dev 
```





-----
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
