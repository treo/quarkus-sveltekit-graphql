/// <references types="houdini-svelte">

/** @type {import('houdini').ConfigFile} */
const config = {
    "schemaFile": "schema.graphql",
    "defaultCachePolicy": "CacheAndNetwork",
    "defaultFragmentMasking": "disable",
    "plugins": {
        "houdini-svelte": {
            "static": true
        }
    },
    "scalars": {
        "Void": {
            type: "Void"
        },
        "DateTime": {
            type: 'Date',
            unmarshal(val) {
                return val ? new Date(val) : null
            },
            marshal(date) {
                return date && date.getTime()
            }
        }
    }
}

export default config
