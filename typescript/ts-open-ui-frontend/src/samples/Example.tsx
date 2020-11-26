import {Channel, Page} from "../api/models";

export const exampleChannels: Array<Channel> = [
  {
    "id": "brxsaas:vT9bR",
    "name": "BrX SaaS",
    "branch": "vT9bR",
    "branchOf": "brxsaas",
    "contentRootPath": "/content/documents/brxsaas",
    "locale": null,
    "devices": [],
    "defaultDevice": null,
    "responseHeaders": null,
    "linkurlPrefix": null,
    "cdnHost": null,
    "parameters": {
      "spaUrl": "https://brxm-react-spa.herokuapp.com/"
    }
  },
  {
    "id": "brxsaas",
    "name": "BrX SaaS",
    "branch": null,
    "branchOf": null,
    "contentRootPath": "/content/documents/brxsaas",
    "locale": null,
    "devices": [],
    "defaultDevice": null,
    "responseHeaders": null,
    "linkurlPrefix": null,
    "cdnHost": null,
    "parameters": {
      "spaUrl": "https://brxm-react-spa.herokuapp.com/"
    }
  }
]

export const examplePages: Array<Page> = [
  {
    "name": "base",
    "description": null,
    "parameters": {},
    "type": "abstract",
    "extends": null,
    "components": [
      {
        "name": "menu",
        "description": null,
        "parameters": {
          "selectedMenu": "on",
          "menu": "main",
          "level": "2"
        },
        "xtype": null,
        "definition": null,
        "components": [],
        "type": "static"
      },
      {
        "name": "top",
        "description": null,
        "parameters": {},
        "xtype": null,
        "definition": null,
        "components": [],
        "type": "static"
      },
      {
        "name": "main",
        "description": null,
        "parameters": {},
        "xtype": null,
        "definition": null,
        "components": [],
        "type": "static"
      },
      {
        "name": "bottom",
        "description": null,
        "parameters": {},
        "xtype": null,
        "definition": null,
        "components": [],
        "type": "static"
      },
      {
        "name": "footer",
        "description": null,
        "parameters": {},
        "xtype": null,
        "definition": null,
        "components": [
          {
            "name": "container",
            "description": null,
            "parameters": {},
            "xtype": "hst.nomarkup",
            "label": "Footer",
            "type": "managed"
          }
        ],
        "type": "static"
      }
    ]
  },
  {
    "name": "content",
    "description": null,
    "parameters": {},
    "type": "page",
    "extends": "base",
    "components": [
      {
        "name": "main",
        "description": null,
        "parameters": {},
        "xtype": null,
        "definition": null,
        "components": [
          {
            "name": "container",
            "description": null,
            "parameters": {},
            "xtype": "hst.nomarkup",
            "label": null,
            "type": "managed"
          }
        ],
        "type": "static"
      },
      {
        "name": "right",
        "description": null,
        "parameters": {},
        "xtype": null,
        "definition": null,
        "components": [
          {
            "name": "container",
            "description": null,
            "parameters": {},
            "xtype": "hst.nomarkup",
            "label": null,
            "type": "managed"
          }
        ],
        "type": "static"
      }
    ]
  },
  {
    "name": "one-column",
    "description": null,
    "parameters": {},
    "type": "xpage",
    "extends": "base",
    "components": [
      {
        "name": "top",
        "description": null,
        "parameters": {},
        "xtype": null,
        "definition": null,
        "components": [
          {
            "name": "container",
            "description": null,
            "parameters": {},
            "xtype": "hst.nomarkup",
            "label": "Top",
            "type": "managed"
          }
        ],
        "type": "static"
      },
      {
        "name": "main",
        "description": null,
        "parameters": {},
        "xtype": null,
        "definition": null,
        "components": [
          {
            "name": "container",
            "description": null,
            "parameters": {},
            "xtype": "hst.nomarkup",
            "label": "Main",
            "type": "managed"
          }
        ],
        "type": "static"
      },
      {
        "name": "bottom",
        "description": null,
        "parameters": {},
        "xtype": null,
        "definition": null,
        "components": [
          {
            "name": "container",
            "description": null,
            "parameters": {},
            "xtype": "hst.nomarkup",
            "label": "Bottom",
            "type": "managed"
          }
        ],
        "type": "static"
      }
    ]
  },
  {
    "name": "two-column",
    "description": null,
    "parameters": {},
    "type": "xpage",
    "extends": "base",
    "components": [
      {
        "name": "top",
        "description": null,
        "parameters": {},
        "xtype": null,
        "definition": null,
        "components": [
          {
            "name": "container",
            "description": null,
            "parameters": {},
            "xtype": "hst.nomarkup",
            "label": "Top",
            "type": "managed"
          }
        ],
        "type": "static"
      },
      {
        "name": "main",
        "description": null,
        "parameters": {},
        "xtype": null,
        "definition": null,
        "components": [
          {
            "name": "container",
            "description": null,
            "parameters": {},
            "xtype": "hst.nomarkup",
            "label": "Main",
            "type": "managed"
          }
        ],
        "type": "static"
      },
      {
        "name": "right",
        "description": null,
        "parameters": {},
        "xtype": null,
        "definition": null,
        "components": [
          {
            "name": "container",
            "description": null,
            "parameters": {},
            "xtype": "hst.nomarkup",
            "label": "Right",
            "type": "managed"
          }
        ],
        "type": "static"
      },
      {
        "name": "bottom",
        "description": null,
        "parameters": {},
        "xtype": null,
        "definition": null,
        "components": [
          {
            "name": "container",
            "description": null,
            "parameters": {},
            "xtype": "hst.nomarkup",
            "label": "Bottom",
            "type": "managed"
          }
        ],
        "type": "static"
      }
    ]
  }
]