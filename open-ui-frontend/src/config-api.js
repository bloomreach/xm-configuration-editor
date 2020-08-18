const axios = require('axios').default;

export function putPageWithName (baseUrl, channelId, pagePath, pageModel) {
  return axios.put(`${baseUrl}ws/config/channels/${channelId}/pages${getPageNameFromPagePath(pagePath)}`,
    pageModel, {withCredentials: true});
}

export function getPageWithName (baseUrl, channelId, pagePath) {
  return axios.get(`${baseUrl}ws/config/channels/${channelId}/pages${getPageNameFromPagePath(pagePath)}`, {withCredentials: true})
}

function getPageNameFromPagePath (pagePath) {
  return pagePath === "/" || !pagePath ? "/root" : pagePath
}

export function getAllComponents (baseUrl, channelId) {
  return axios.get(`${baseUrl}ws/config/channels/${channelId}/components`, {withCredentials: true})
    .then(result => {
      return result.data
    }).catch(exception => {
      console.error(exception);
    });
}

export function putComponentWithName (baseUrl, channelId, componentName, component) {
  return axios.put(`${baseUrl}ws/config/channels/${channelId}/components/${componentName}`, component, {withCredentials: true})
    .then(result => {
      return result.data
    }).catch(exception => {
      console.error(exception);
    });
}

export function deleteComponentWithName (baseUrl, channelId, componentName) {
  return axios.delete(`${baseUrl}ws/config/channels/${channelId}/components/${componentName}`, {withCredentials: true});
}

export function getUrl (url) {
  return axios.get(url, {withCredentials: true})
}

export function getAcl (baseUrl) {
  return axios.get(`${baseUrl}ws/config/acl`, {withCredentials: true})
    .then(result => {
      return result.data;
    }).catch(reason => {
      console.error(reason);
    });
}
