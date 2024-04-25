<script lang="ts">
    import {graphql} from "$houdini";
    import {page} from "$app/stores";
    import {writable} from "svelte/store";

    const messages = writable([]);

    const messageUpdates = graphql(`
        subscription MessageUpdates($channelId: String!){
           subscribeToMessages(channelId: $channelId){
              user {
                id username
              }
              timestamp
              text
           }
        }
    `)
    function changeChannel(channelId: string){
        $messages = [];
        messageUpdates.listen({channelId: channelId})
    }

    $: changeChannel($page.params.channelId)
    $: messages.update((state) => {
        if($messageUpdates.data && $messageUpdates.variables?.channelId === $page.params.channelId){
            state.push($messageUpdates.data.subscribeToMessages);
        }
        return state;
    })

    const sendMessageAction = graphql(`
        mutation SendMessage($channelId: String!, $message: String!) {
            sendMessage(channelId: $channelId, message: $message)
        }
    `);

    let input: HTMLInputElement;
    let sending = false;
    let message: string = "";
    async function sendMessage(){
        sending = true;
        await sendMessageAction.mutate({
            message,
            channelId: $page.params.channelId
        })
        message = "";
        sending = false;
        input.focus();
    }
</script>

<div class="grid grid-rows-[1fr,auto] h-full gap-2">
    <section class="messages">
        {#each $messages as message}
            <div class="chat chat-start">
                <div class="chat-header">
                    {message.user.username}
                    <time class="text-xs opacity-50">{message.timestamp.toLocaleString()}</time>
                </div>
                <div class="chat-bubble">{message.text}</div>
            </div>
        {/each}
    </section>
    <form on:submit={sendMessage} class="flex gap-2">
        <input class="input input-bordered flex-grow" type="text" bind:value={message} bind:this={input}/>
        <button class="btn btn-primary" type="submit" disabled={sending || message.trim().length === 0}>Senden</button>
    </form>
</div>
