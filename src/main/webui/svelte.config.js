import adapter from '@sveltejs/adapter-static';
import { vitePreprocess } from '@sveltejs/vite-plugin-svelte';

/** @type {import('@sveltejs/kit').Config} */
const config = {
	// Consult https://kit.svelte.dev/docs/integrations#preprocessors
	// for more information about preprocessors
	preprocess: [vitePreprocess({})],

	kit: {
		adapter: adapter({
			fallback: 'index.html'
		}),
		alias: {
			$houdini: './$houdini'
		},
		// Mark path non-relative, as it otherwise breaks on SPA routing with Quarkus Dev Mode
		paths: {
			relative: false
		}

	}
};

export default config;
