# Configuration Editor

The Configuration Editor project is a tool in the channel manager, as an Open UI Page Tool extension, to edit HST configuration.

Although the project has the ambition to edit all hst configuration we currently have only support for the "Current Page (a.k.a Flex Page)" feature.

The current page feature will allow a CMS user with the appropriate user roles to edit the page model of a landingpage or XPage.

**Please note**  this plugin is not yet suited to work in combination with the "Projects" addon yet.

## Release Notes

| CMS Version | Plugin Version | Description                                                                                                                                                                                                                                       |
|-------------|----------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 14.2        | 0.1.0          | Initial Release                                                                                                                                                                                                                                   |
| 14.4        | 0.2.0          | XPage support, note that the "projects" feature will not work well in combination of this plugin. Awaiting https://issues.onehippo.com/browse/CMS-14297                                                                                           |
| 14.4        | 0.9.0          | Refactoring of the API Model to compatible with brxSaaS and some other backend and frontend refactoring, note that the "projects" feature will not work well in combination of this plugin. Awaiting https://issues.onehippo.com/browse/CMS-14297 |
| 14.4        | 0.9.1          | Changed the logic so that only components from the workspace will be a choice for flex pages plugin. Note that the "projects" feature will not work well in combination of this plugin. Awaiting https://issues.onehippo.com/browse/CMS-14297     |
| 14.4        | 0.9.2          | Virtual host fix                                                                                                                                                                                                                                  |
| 14.4        | 0.9.3          | Page layout fix                                                                                                                                                                                                                                   |
| 14.6        | 0.9.4          | Index matcher fix + Add component as last child                                                                                                                                                                                                   |
| 14.6        | 0.9.6          | added support for componentclasses                                                                                                                                                                                                                |
| 15.2.2      | 0.9.8          | Added support for CMS v15                                                                                                                                                                                                                         |
| 16.0.0      | 2.0.0          | Upgraded to brXM v16                                                                                                                                                                                                                              |


## Installation

- In the root pom.xml configure in the properties a version for the plugin and add the bellow dependency in the dependencyManagement section
```xml
    <properties>
        <brxm-configuration-editor.version>x.y.z</brxm-configuration-editor.version>
    </properties>

    <dependencyManagement>
        <dependencies>
          <!--SNIP-->
          
           <dependency>
                <groupId>com.bloomreach.xm</groupId>
                <artifactId>brxm-configuration-editor-api</artifactId>
                <version>${brxm-configuration-editor.version}</version>
           </dependency>
          
           <dependency>
                <groupId>com.bloomreach.xm</groupId>
                <artifactId>brxm-configuration-editor-openui-frontend</artifactId>
                <version>${brxm-configuration-editor.version}</version>
           </dependency>
          
          <!--SNIP-->
        </dependencies>
    </dependencyManagement>
    
```

- Add the below dependencies in the `cms-dependencies/pom.xml`

```xml
     <dependency>
        <groupId>com.bloomreach.xm</groupId>
        <artifactId>brxm-configuration-editor-api</artifactId>
     </dependency>
              
     <dependency>
        <groupId>com.bloomreach.xm</groupId>
        <artifactId>brxm-configuration-editor-openui-frontend</artifactId>
     </dependency>
              
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
![Current Page](https://github.com/bloomreach/xm-configuration-editor/blob/master/resources/Channel%20Config%20Editor-1.png?raw=true "Current Page")
![Current Page2](https://github.com/bloomreach/xm-configuration-editor/blob/master/resources/channel-config-editor2.png?raw=true "Current Page2")
Also see the demo video:
[Video](https://github.com/bloomreach/xm-configuration-editor/blob/master/resources/current%20page.mp4)
- Click on the save icon to store the page model and publish it with the channel menu


## Authorization

xm.config-editor.current-page.editor ideally should be assigned to a user with editor and webmaster privileges

| Userrole  |Implied Userrole  | Description  |
|---|---|---|
|xm.config-editor.current-page.editor   |xm.config-editor.current-page.viewer   | Allows editing of current page / flex page structure via "save" button  |
|xm.config-editor.current-page.viewer   |xm.config-editor.user   |Allows viewing of current page / flex page tab   |
|xm.config-editor.user  |   |Required to see the Channel Config Editor OpenUi Extension   |



-----

# Development mode:


## Build the configuration api and the open ui frontend project

The Configuration API is an API which allows modification to the HST configuration of a particular channel

The Open UI frontend project is the frontend project (built in React) which is the UI for the Config API. This project is being bundled in a jar to be added in the CMS. The URL endpoint of the react application will be /cms/angular/xm-config-editor/index.html/#/current-page

```bash
mvn clean install
```

## Run the demo project(s)

Build and Start the brX project:

```bash
cd demo && mvn verify && mvn -Pcargo.run
```

Start the react-csr project (PORT 3001)

```bash
cd react-csr
yarn
yarn dev
```

## Configuration Editor Frontend

When developing the frontend you will need to run the frontend project locally instead of the bundled js which is incoporated in the cms.

Build & Start the Open UI frontend project which is at /open-ui-frontend

(Port should be 3000)
```bash
yarn
yarn start
```

Go to the console and change the OpenUI extension URL to http://localhost:3000/#/flex-page
http://localhost:8080/cms/console/?1&path=/hippo:configuration/hippo:frontend/cms/ui-extensions/channelConfigEditor




