import { HoudiniClient } from '$houdini';
import { createClient } from 'graphql-ws';
import { subscription } from '$houdini/plugins'
import {error} from "@sveltejs/kit";


// @ts-ignore
export default new HoudiniClient({
    url: window.location.protocol+'//'+window.location.host+'/graphql',
    plugins: [
        subscription(() => createClient({
            url: (window.location.protocol == 'http:' ? "ws:" : "wss:") + '//'+window.location.host+'/graphql'
        }))
    ],

    // uncomment this to configure the network call (for things like authentication)
    // for more information, please visit here: https://www.houdinigraphql.com/guides/authentication
    fetchParams({ session }) {
        return {
            credentials: 'include'
        }
    },

    throwOnError: {
        operations: ['all'],
        //@ts-ignore
        error: (errors: {extensions: {code: string}}[], ctx) => {
            for (let e of errors) {
                if(e.extensions.code === 'unauthorized'){
                    window.location.href = '/login?redirect='+encodeURIComponent(window.location.toString());
                    return new Promise(() => {});
                }else if(e.extensions.code === 'forbidden'){
                    window.location.href = '/logout';
                    return new Promise(() => {});
                }
            }
            error(500, 'Internal Server Error: Unexpected response from API.')
        }
    }
})
