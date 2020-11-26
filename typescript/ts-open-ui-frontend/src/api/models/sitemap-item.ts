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
/**
 * 
 * @export
 * @interface SitemapItem
 */
export interface SitemapItem {
    /**
     * the name (url path element) of the sitemap item. Each sitemap item name must be unique between its siblings.
     * @type {string}
     * @memberof SitemapItem
     */
    name: string;
    /**
     * the name of the (default) page definition for rendering a url matching this sitemap item
     * @type {string}
     * @memberof SitemapItem
     */
    page?: string;
    /**
     * default page (header) title
     * @type {string}
     * @memberof SitemapItem
     */
    pageTitle?: string;
    /**
     * the (base) content path, relative to the channel (base) content path to use for rendering this sitemap item page, or any of its children
     * @type {string}
     * @memberof SitemapItem
     */
    relativeContentPath?: string;
    /**
     * a map of string parameters (names/values) for rendering this sitemap item page
     * @type {{ [key, string]: string;}}
     * @memberof SitemapItem
     */
    parameters?: any;
    /**
     * a more specialized page (name) mapping based on the matched document its type
     * @type {{ [key, string]: string;}}
     * @memberof SitemapItem
     */
    doctypePages?: any;
    /**
     * a list of child sitemap items.
     * @type {Array&lt;SitemapItem&gt;}
     * @memberof SitemapItem
     */
    items?: any;
}
