# Config Editor

The Config Editor project is a tool in the channel manager, as an Open UI Page Tool extension, to edit HST configuration.

Although the project has the ambition to edit all hst configuration we currently have only support for the "Current Page" feature.

The current page feature will allow a CMS user with the appropriate user roles to edit the page model of a landingpage.


## Build the configuration api and the open ui frontend project

The Configuration API is an API which allows modification to the HST configuration of a particular channel

The Open UI frontend project is the frontend project (built in React) which is the UI for the Config API. This project is being bundled in a jar to be added in the CMS. The URL endpoint of the react application will be /cms/angular/xm-config-editor/index.html/#/current-page

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

## How it works:

- Go to the CMS and Open a channel in the Experience Manager
- Create a new page using the Page menu in the Experience Manager
- Choose any layout template, but preferably the "Simple Content Page"
- Click again on the Page menu, this time select "Page Tools"
Page Tools: 
![Page Tools](https://github.com/bloomreach/xm-configuration-editor/blob/master/resources/page-tools.png?raw=true "Page Tools")
- On the right you should see the "Current Page" feature of the Config editor
Current page allows the cms editor to create a page model based on predefined components from the developers. These components are located in the hst:components section of the HST configuration
- Edit the current page model using the UI
[Current Page](https://github.com/bloomreach/xm-configuration-editor/blob/master/resources/Channel%20Config%20Editor-1.png?raw=true "Current Page")
[Current Page2](https://github.com/bloomreach/xm-configuration-editor/blob/master/resources/channel-config-editor2.png?raw=true "Current Page2")
Also see the demo video:
[Video](https://github.com/bloomreach/xm-configuration-editor/blob/master/resources/current%20page.mp4)
- Click on the save icon to store the page model and publish it with the channel menu


## Authorization

| Userrole  |Implied Userrole  | Description  |
|---|---|---|
|xm.config-editor.current-page.editor   |xm.config-editor.current-page.viewer   | Allows editing of current page structure via "save" button  |
|xm.config-editor.current-page.viewer   |xm.config-editor.user   |Allows viewing of current page tab   |
|xm.config-editor.user  |   |Required to see the Channel Config Editor OpenUi Extension   |


-----

# Development mode:

When developing the frontend you will need to run the frontend project locally instead of the bundled js which is incoporated in the cms.

Build & Start the Open UI frontend project which is at /open-ui-frontend

(Port should be 3000)
```bash
yarn
yarn start
```

Go to the console and change the OpenUI extension URL to http://localhost:3000/#/current-page
http://localhost:8080/cms/console/?1&path=/hippo:configuration/hippo:frontend/cms/ui-extensions/channelConfigEditor




