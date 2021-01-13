/* tslint:disable */
/* eslint-disable */
import globalAxios, {AxiosInstance, AxiosPromise} from 'axios';
import {Configuration} from '../configuration';
// Some imports not used depending on template conditions
// @ts-ignore
import {BASE_PATH, BaseAPI, RequestArgs, RequiredError} from '../base';
import {AbstractComponent, Page} from '../models';

/**
 * ChannelOtherOperationsApi - axios parameter creator
 * @export
 */
export const ChannelOtherOperationsApiAxiosParamCreator = function (configuration?: Configuration) {
  return {
    /**
     *
     * @summary Get all components belonging to a channel
     * @param {string} channelId
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     */
    getAllComponents: async (channelId: string, options: any = {}): Promise<RequestArgs> => {
      // verify required parameter 'channelId' is not null or undefined
      if (channelId === null || channelId === undefined) {
        throw new RequiredError('channelId', 'Required parameter channelId was null or undefined when calling getChannelPage.');
      }

      const localVarPath = `/channels/{channel_id}/components`
        .replace(`{${"channel_id"}}`, encodeURIComponent(String(channelId)))
      // use dummy base URL string because the URL constructor only accepts absolute URLs.
      const localVarUrlObj = new URL(localVarPath, 'https://example.com');
      let baseOptions;
      if (configuration) {
        baseOptions = configuration.baseOptions;
      }
      const localVarRequestOptions = {method: 'GET', ...baseOptions, ...options};
      const localVarHeaderParameter = {} as any;
      const localVarQueryParameter = {} as any;

      const query = new URLSearchParams(localVarUrlObj.search);
      for (const key in localVarQueryParameter) {
        query.set(key, localVarQueryParameter[key]);
      }
      for (const key in options.query) {
        query.set(key, options.query[key]);
      }
      localVarUrlObj.search = (new URLSearchParams(query)).toString();
      let headersFromBaseOptions = baseOptions && baseOptions.headers ? baseOptions.headers : {};
      localVarRequestOptions.headers = {...localVarHeaderParameter, ...headersFromBaseOptions, ...options.headers};

      return {
        url: localVarUrlObj.pathname + localVarUrlObj.search + localVarUrlObj.hash,
        options: localVarRequestOptions,
      };
    },
    /**
     *
     * @summary Get the channel pages
     * @param {string} channelId
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     */
    getAcl: async (options: any = {}): Promise<RequestArgs> => {
      // verify required parameter 'channelId' is not null or undefined
      const localVarPath = `/acl`;
      // use dummy base URL string because the URL constructor only accepts absolute URLs.
      const localVarUrlObj = new URL(localVarPath, 'https://example.com');
      let baseOptions;
      if (configuration) {
        baseOptions = configuration.baseOptions;
      }
      const localVarRequestOptions = {method: 'GET', ...baseOptions, ...options};
      const localVarHeaderParameter = {} as any;
      const localVarQueryParameter = {} as any;

      const query = new URLSearchParams(localVarUrlObj.search);
      for (const key in localVarQueryParameter) {
        query.set(key, localVarQueryParameter[key]);
      }
      for (const key in options.query) {
        query.set(key, options.query[key]);
      }
      localVarUrlObj.search = (new URLSearchParams(query)).toString();
      let headersFromBaseOptions = baseOptions && baseOptions.headers ? baseOptions.headers : {};
      localVarRequestOptions.headers = {...localVarHeaderParameter, ...headersFromBaseOptions, ...options.headers};

      return {
        url: localVarUrlObj.pathname + localVarUrlObj.search + localVarUrlObj.hash,
        options: localVarRequestOptions,
      };
    }
  }
};

/**
 * ChannelOtherOperationsApi - functional programming interface
 * @export
 */
export const ChannelOtherOperationsApiFp = function (configuration?: Configuration) {
  return {

    /**
     *
     * @summary Get all components belonging to a channel
     * @param {string} channelId
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     */
    async getAllComponents (channelId: string, options?: any): Promise<(axios?: AxiosInstance, basePath?: string) => AxiosPromise<Array<AbstractComponent>>> {
      const localVarAxiosArgs = await ChannelOtherOperationsApiAxiosParamCreator(configuration).getAllComponents(channelId, options);
      return (axios: AxiosInstance = globalAxios, basePath: string = BASE_PATH) => {
        const axiosRequestArgs = {...localVarAxiosArgs.options, url: basePath + localVarAxiosArgs.url};
        return axios.request(axiosRequestArgs);
      };
    },
    /**
     *
     * @summary Get ACLs
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     */
    async getAcl (options?: any): Promise<(axios?: AxiosInstance, basePath?: string) => AxiosPromise<{[key: string]: boolean}>> {
      const localVarAxiosArgs = await ChannelOtherOperationsApiAxiosParamCreator(configuration).getAcl(options);
      return (axios: AxiosInstance = globalAxios, basePath: string = BASE_PATH) => {
        const axiosRequestArgs = {...localVarAxiosArgs.options, url: basePath + localVarAxiosArgs.url};
        return axios.request(axiosRequestArgs);
      };
    }
  }
};

/**
 * ChannelOtherOperationsApi - factory interface
 * @export
 */
export const ChannelOtherOperationsApiFactory = function (configuration?: Configuration, basePath?: string, axios?: AxiosInstance) {
  return {

    /**
     *
     * @summary Get a channel page
     * @param {string} channelId
     * @param {string} pageName
     * @param {boolean} [resolved]
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     */
    getAllComponents (channelId: string, options?: any): AxiosPromise<Array<AbstractComponent>> {
      return ChannelOtherOperationsApiFp(configuration).getAllComponents(channelId, options).then((request) => request(axios, basePath));
    },
    /**
     *
     * @summary Get the channel pages
     * @param {string} channelId
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     */
    getAcl (options?: any): AxiosPromise {
      return ChannelOtherOperationsApiFp(configuration).getAcl(options).then((request) => request(axios, basePath));
    },

  };
};

/**
 * ChannelOtherOperationsApi - object-oriented interface
 * @export
 * @class ChannelOtherOperationsApi
 * @extends {BaseAPI}
 */
export class ChannelOtherOperationsApi extends BaseAPI {

  /**
   *
   * @summary Get a channel page
   * @param {string} channelId
   * @param {string} pageName
   * @param {boolean} [resolved]
   * @param {*} [options] Override http request option.
   * @throws {RequiredError}
   * @memberof ChannelOtherOperationsApi
   */
  public getAllComponents (channelId: string, options?: any) {
    return ChannelOtherOperationsApiFp(this.configuration).getAllComponents(channelId, options).then((request) => request(this.axios, this.basePath));
  }

  /**
   *
   * @summary Get the channel pages
   * @param {string} channelId
   * @param {*} [options] Override http request option.
   * @throws {RequiredError}
   * @memberof ChannelOtherOperationsApi
   */
  public getAcl (options?: any) {
    return ChannelOtherOperationsApiFp(this.configuration).getAcl(options).then((request) => request(this.axios, this.basePath));
  }

}
