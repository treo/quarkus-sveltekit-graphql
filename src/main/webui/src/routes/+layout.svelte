<script lang="ts">
    import "../app.postcss";
    import {writable} from "svelte/store";
    import {graphql, type UserUpdates$result} from "$houdini";

    export let data;
    $: ({OverviewQuery} = data)
    $: channels = $OverviewQuery.data?.listChannels || [];

    const users = graphql(`
        subscription UserUpdates {
            subscribeToUsers {
                users {
                    id
                    onlineSince
                    username
                }
            }
        }
    `)

    $: users.listen();
    $: userList = $users.data?.subscribeToUsers?.users || [];
</script>

<div class="layout">
    <nav>
        <h1 class="font-bold">Kanäle</h1>
        <ul>
            {#each channels as channel (channel.id)}
                <li><a href="/{channel.id}">{channel.name}</a></li>
            {/each}
        </ul>
    </nav>
    <section class="content">
        <slot />
    </section>
    <section class="users">
        <h2 class="font-bold">User</h2>
        <ul>
            {#each userList as user}
                <li>{user.username}</li>
            {/each}
        </ul>
    </section>
</div>

<style>
    .layout {
        @apply grid grid-cols-[auto,1fr,auto] h-[100vh] w-[100vw];
    }
    nav {
        @apply w-32 h-full border border-l border-gray-300 p-4;
    }
    .content {
        @apply p-4 h-full;
    }
    .users {
        @apply w-32 h-full border border-r border-gray-300 p-4;
    }
</style>
