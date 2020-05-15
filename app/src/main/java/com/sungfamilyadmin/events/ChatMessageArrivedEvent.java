package com.sungfamilyadmin.events;

import com.sungfamilyadmin.models.ChatMessage;

public class ChatMessageArrivedEvent
{
    private ChatMessage message;
    public ChatMessageArrivedEvent(ChatMessage message)
    {
        this.message = message;
    }

    public ChatMessage getChatMessage()
    {
        return this.message;
    }
}
