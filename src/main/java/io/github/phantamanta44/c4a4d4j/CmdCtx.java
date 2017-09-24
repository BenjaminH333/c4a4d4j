package io.github.phantamanta44.c4a4d4j;

import java.time.ZoneId;

import io.github.phantamanta44.commands4a.command.ICommandContext;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public class CmdCtx implements ICommandContext {

    private final IDiscordClient client;
    private final IGuild guild;
    private final IChannel channel;
    private final IUser author;
    private final IMessage msg;
    private final long timestamp;

    public CmdCtx(IMessage msg) {
        this.client = msg.getClient();
        this.guild = msg.getGuild();
        this.channel = msg.getChannel();
        this.author = msg.getAuthor();
        this.msg = msg;
        this.timestamp = msg.getTimestamp().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public CmdCtx(MessageReceivedEvent event) {
        this(event.getMessage());
    }

    public IDiscordClient getClient() {
        return client;
    }

    public IGuild getGuild() {
        return guild;
    }

    public IChannel getChannel() {
        return channel;
    }

    public IUser getAuthor() {
        return author;
    }

    public IMessage getMessage() {
        return msg;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isPrivate() {
        return msg.getChannel().isPrivate();
    }

}
