/* tslint:disable */
/* eslint-disable */
/**
 *
 * No description provided (generated by Swagger Codegen https://github.com/swagger-api/swagger-codegen)
 *
 * OpenAPI spec version: 1.0.0
 *
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */
import globalAxios, { AxiosPromise, AxiosInstance } from 'axios';
import { Configuration } from '../configuration';
// Some imports not used depending on template conditions
// @ts-ignore
import { BASE_PATH, COLLECTION_FORMATS, RequestArgs, BaseAPI, RequiredError } from '../base';
import { Page } from '../models';
/**
 * ChannelFlexPageOperationsApi - axios parameter creator
 * @export
 */
export const ChannelFlexPageOperationsApiAxiosParamCreator = function (configuration?: Configuration) {
    return {

        /**
         *
         * @summary Get a channel page
         * @param {string} channelId
         * @param {string} pagePath
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        getChannelPage: async (channelId: string, pagePath: string, options: any = {}): Promise<RequestArgs> => {
            // verify required parameter 'channelId' is not null or undefined
            if (channelId === null || channelId === undefined) {
                throw new RequiredError('channelId','Required parameter channelId was null or undefined when calling getChannelPage.');
            }
            // verify required parameter 'pagePath' is not null or undefined
            if (pagePath === null || pagePath === undefined) {
                throw new RequiredError('pagePath','Required parameter pagePath was null or undefined when calling getChannelPage.');
            }
            const localVarPath = `/channels/{channel_id}/flexpage{page_name}`
              .replace(`{${"channel_id"}}`, encodeURIComponent(String(channelId)))
              .replace(`{${"page_name"}}`, String(pagePath));
            // use dummy base URL string because the URL constructor only accepts absolute URLs.
            const localVarUrlObj = new URL(localVarPath, 'https://example.com');
            let baseOptions;
            if (configuration) {
                baseOptions = configuration.baseOptions;
            }
            const localVarRequestOptions = { method: 'GET', ...baseOptions, ...options};
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
         * @summary Create or update a channel page
         * @param {string} channelId
         * @param {string} pagePath
         * @param {Page} [body]
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        putChannelPage: async (channelId: string, pagePath: string, body?: Page, options: any = {}): Promise<RequestArgs> => {
            // verify required parameter 'channelId' is not null or undefined
            if (channelId === null || channelId === undefined) {
                throw new RequiredError('channelId','Required parameter channelId was null or undefined when calling putChannelPage.');
            }
            // verify required parameter 'pagePath' is not null or undefined
            if (pagePath === null || pagePath === undefined) {
                throw new RequiredError('pagePath','Required parameter pagePath was null or undefined when calling putChannelPage.');
            }
            const localVarPath = `/channels/{channel_id}/flexpage{page_name}`
              .replace(`{${"channel_id"}}`, encodeURIComponent(String(channelId)))
              .replace(`{${"page_name"}}`, String(pagePath));
            // use dummy base URL string because the URL constructor only accepts absolute URLs.
            const localVarUrlObj = new URL(localVarPath, 'https://example.com');
            let baseOptions;
            if (configuration) {
                baseOptions = configuration.baseOptions;
            }
            const localVarRequestOptions = { method: 'PUT', ...baseOptions, ...options};
            const localVarHeaderParameter = {} as any;
            const localVarQueryParameter = {} as any;


            localVarHeaderParameter['Content-Type'] = 'application/json';

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
            const needsSerialization = (typeof body !== "string") || localVarRequestOptions.headers['Content-Type'] === 'application/json';
            localVarRequestOptions.data =  needsSerialization ? JSON.stringify(body !== undefined ? body : {}) : (body || "");

            return {
                url: localVarUrlObj.pathname + localVarUrlObj.search + localVarUrlObj.hash,
                options: localVarRequestOptions,
            };
        },
    }
};

/**
 * ChannelFlexPageOperationsApi - functional programming interface
 * @export
 */
export const ChannelFlexPageOperationsApiFp = function(configuration?: Configuration) {
    return {

        /**
         *
         * @summary Get a channel page
         * @param {string} channelId
         * @param {string} pagePath
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        async getChannelPage(channelId: string, pagePath: string, options?: any): Promise<(axios?: AxiosInstance, basePath?: string) => AxiosPromise<Page>> {
            const localVarAxiosArgs = await ChannelFlexPageOperationsApiAxiosParamCreator(configuration).getChannelPage(channelId, pagePath, options);
            return (axios: AxiosInstance = globalAxios, basePath: string = BASE_PATH) => {
                const axiosRequestArgs = {...localVarAxiosArgs.options, url: basePath + localVarAxiosArgs.url};
                return axios.request(axiosRequestArgs);
            };
        },
        /**
         *
         * @summary Create or update a channel page
         * @param {string} channelId
         * @param {string} pagePath
         * @param {Page} [body]
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        async putChannelPage(channelId: string, pagePath: string, body?: Page, options?: any): Promise<(axios?: AxiosInstance, basePath?: string) => AxiosPromise<Page>> {
            const localVarAxiosArgs = await ChannelFlexPageOperationsApiAxiosParamCreator(configuration).putChannelPage(channelId, pagePath, body, options);
            return (axios: AxiosInstance = globalAxios, basePath: string = BASE_PATH) => {
                const axiosRequestArgs = {...localVarAxiosArgs.options, url: basePath + localVarAxiosArgs.url};
                return axios.request(axiosRequestArgs);
            };
        },
    }
};

/**
 * ChannelFlexPageOperationsApi - factory interface
 * @export
 */
export const ChannelFlexPageOperationsApiFactory = function (configuration?: Configuration, basePath?: string, axios?: AxiosInstance) {
    return {

        /**
         *
         * @summary Get a channel page
         * @param {string} channelId
         * @param {string} pagePath
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        getChannelPage(channelId: string, pagePath: string, options?: any): AxiosPromise<Page> {
            return ChannelFlexPageOperationsApiFp(configuration).getChannelPage(channelId, pagePath, options).then((request) => request(axios, basePath));
        },

        /**
         *
         * @summary Create or update a channel page
         * @param {string} channelId
         * @param {string} pagePath
         * @param {Page} [body]
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        putChannelPage(channelId: string, pagePath: string, body?: Page, options?: any): AxiosPromise<Page> {
            return ChannelFlexPageOperationsApiFp(configuration).putChannelPage(channelId, pagePath, body, options).then((request) => request(axios, basePath));
        },
    };
};

/**
 * ChannelFlexPageOperationsApi - object-oriented interface
 * @export
 * @class ChannelFlexPageOperationsApi
 * @extends {BaseAPI}
 */
export class ChannelFlexPageOperationsApi extends BaseAPI {

    /**
     *
     * @summary Get a channel page
     * @param {string} channelId
     * @param {string} pagePath
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     * @memberof ChannelFlexPageOperationsApi
     */
    public getChannelPage(channelId: string, pagePath: string, options?: any) {
        return ChannelFlexPageOperationsApiFp(this.configuration).getChannelPage(channelId, pagePath, options).then((request) => request(this.axios, this.basePath));
    }



    /**
     *
     * @summary Create or update a channel page
     * @param {string} channelId
     * @param {string} pagePath
     * @param {Page} [body]
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     * @memberof ChannelFlexPageOperationsApi
     */
    public putChannelPage(channelId: string, pagePath: string, body?: Page, options?: any) {
        return ChannelFlexPageOperationsApiFp(this.configuration).putChannelPage(channelId, pagePath, body, options).then((request) => request(this.axios, this.basePath));
    }
}
