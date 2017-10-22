package io.github.phantamanta44.c4a4d4j;

import java.time.ZoneId;
import java.util.List;

import io.github.phantamanta44.commands4a.command.ICommandContext;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public class CmdCtx implements ICommandContext, Cloneable {

	private final IDiscordClient client;
	private final IGuild guild;
	private final IChannel channel;
	private final IUser author;
	private final IMessage msg;
	private final long timestamp;
	// Additions
	private final List<IUser> usersHere;

	public CmdCtx(IMessage msg, IUser author) {
		this.client = msg.getClient();
		this.guild = msg.getGuild();
		this.channel = msg.getChannel();
		this.author = author;
		this.msg = msg;
		this.timestamp = msg.getTimestamp().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

		if (guild != null) {
			usersHere = guild.getUsers();
		} else {
			usersHere = channel.getUsersHere();
		}
	}

	public CmdCtx(IMessage msg) {
		this(msg, msg.getAuthor());
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
		return isPrivateChannel();
	}

	public List<IUser> getUsersHere() {
		return usersHere;
	}

	public boolean isPrivateChannel() {
		return channel.isPrivate() || guild == null;
	}

	public String getMessageText() {
		return msg.getContent();
	}

	@Override
	public CmdCtx clone() {
		return new CmdCtx(msg);
	}
}
