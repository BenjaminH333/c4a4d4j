package io.github.phantamanta44.c4a4d4j.arg;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.fge.lambdas.functions.ThrowingFunction;

import io.github.phantamanta44.c4a4d4j.C4A4D4J;
import io.github.phantamanta44.c4a4d4j.CmdCtx;
import io.github.phantamanta44.commands4a.args.IArgumentTokenizer;
import io.github.phantamanta44.commands4a.command.CommandEngine;
import io.github.phantamanta44.commands4a.command.CommandExecution;
import io.github.phantamanta44.commands4a.exception.CommandExecutionException;
import io.github.phantamanta44.commands4a.exception.InvalidSyntaxException;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

public class ArgParser implements IArgumentTokenizer {

	private String[] args; // It's not final anymore so fight me.
	private final CmdCtx ctx;
	private final C4A4D4J engine;
	private int pos;

	public ArgParser(String[] commandArgs, CmdCtx ctx, C4A4D4J engine) {
		this.ctx = ctx;
		this.engine = engine;
		this.pos = 0;

		// Ugly hacks because editing commands4a is off limits
		// The goal is to combine all extra args all back into a single arg.
		try {
			String text = engine.trimUsingPrefixes(ctx.getMessageText());
			String commandName = text.split(" ")[0];
			CommandExecution<CmdCtx> cexec = getCommand(commandName, engine);
			int count = cexec.getExecutor().getParameterCount() - 1;
			if (commandArgs.length > count && count > 0) {
				String[] newArgs = new String[count];
				for (int i = 0; i < count - 1; i++) {
					newArgs[i] = commandArgs[i];
				}
				String finalStr = "";
				for (int i = count - 1; i < commandArgs.length; i++) {
					finalStr += commandArgs[i] + " ";
				}
				newArgs[newArgs.length - 1] = finalStr.trim();
				this.args = newArgs;
			} else {
				this.args = commandArgs;
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.args = commandArgs;
		}
	}

	@Override
	public String nextString() {
		return args[pos++];
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> T nextOfType(Class<T> type) throws InvalidSyntaxException {
		if (type.isEnum()) {
			String str = nextString();
			T ret = (T) valueOf((Class<Enum>) type, str);
			if (ret == null) {
				ret = (T) valueOf((Class<Enum>) type, str.toUpperCase());
			}
			if (ret == null) {
				ret = (T) valueOf((Class<Enum>) type, str.toLowerCase());
			}
			if (ret == null) {
				try {
					int id = Integer.parseInt(str);
					// Ugly hacks
					ret = (T) ((Object[]) type.getDeclaredMethod("values", new Class[] {}).invoke(null,
							new Object[] {}))[id];
				} catch (Exception e) {
					e.printStackTrace();
					// Failure is to be expected
				}
			}
			if (ret != null) {
				return ret;
			} else {
				throw new InvalidSyntaxException(args, "Invalid " + type.getSimpleName() + "!");
			}
		}
		try {
			return (T) typeMap.get(type).doApply(this);
		} catch (IndexOutOfBoundsException e) {
			throw new InvalidSyntaxException(args, "Not enough arguments!");
		} catch (InvalidSyntaxException e) {
			throw e;
		} catch (Throwable e) {
			throw new CommandExecutionException(e);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <T> T valueOf(Class<T> type, String name) {
		try{
			return (T) Enum.valueOf((Class<Enum>) type, name);
		}catch(Exception e){
			return null;
		}
	}

	@Override
	public boolean hasNext() {
		return pos < args.length;
	}

	@Override
	public void reset() {
		pos = 0;
	}

	private static final Map<Class<?>, ThrowingFunction<ArgParser, Object>> typeMap = new HashMap<>();

	static {
		typeMap.put(String.class, ArgParser::nextString);
		typeMap.put(InlineCodeBlock.class, ArgParser::nextInlineCode);
		typeMap.put(CodeBlock.class, ArgParser::nextCode);
		typeMap.put(Integer.class, ArgParser::nextInt);
		typeMap.put(Float.class, ArgParser::nextFloat);
		typeMap.put(Double.class, ArgParser::nextDouble);
		typeMap.put(IUser.class, ArgParser::nextUserTag);
		typeMap.put(IChannel.class, ArgParser::nextChannelTag);
		typeMap.put(CommandExecution.class, ArgParser::nextCommand);
		// Additions
		typeMap.put(Byte.class, ArgParser::nextByte);
		typeMap.put(Short.class, ArgParser::nextShort);
		typeMap.put(Boolean.class, ArgParser::nextBoolean);
		typeMap.put(IRole.class, ArgParser::nextRole);
	}

	public InlineCodeBlock nextInlineCode() throws InvalidSyntaxException {
		StringBuilder sb = new StringBuilder(nextString());
		if (sb.charAt(0) != '`') {
			pos--;
			throw new InvalidSyntaxException(args, "Expected inline code block!");
		}
		final int start = pos - 1;
		while (!(sb.charAt(sb.length() - 1) == '`'
				&& (sb.charAt(sb.length() - 3) == '\\' || sb.charAt(sb.length() - 2) != '\\'))) {
			if (hasNext())
				sb.append(' ').append(nextString());
			else {
				pos = start;
				throw new InvalidSyntaxException(args, "Expected inline code block!");
			}
		}
		return new InlineCodeBlock(sb.substring(1, sb.length() - 1));
	}

	public CodeBlock nextCode() throws InvalidSyntaxException {
		StringBuilder sb = new StringBuilder(nextString());
		if (!sb.substring(0, 3).equals("```")) {
			pos--;
			throw new InvalidSyntaxException(args, "Expected code block!");
		}
		final int start = pos - 1, str0Len = sb.length();
		while (!sb.substring(sb.length() - 3, sb.length()).equals("```")) {
			if (hasNext())
				sb.append(' ').append(nextString());
			else {
				pos = start;
				throw new InvalidSyntaxException(args, "Expected code block!");
			}
		}
		return new CodeBlock(sb.substring(str0Len, sb.length() - 3).trim(), sb.substring(3, str0Len));
	}

	public Integer nextInt() throws InvalidSyntaxException {
		try {
			String str = nextString();
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
			pos--;
			throw new InvalidSyntaxException(args, "Expected integer!");
		}
	}

	public Float nextFloat() throws InvalidSyntaxException {
		try {
			String str = nextString();
			return Float.parseFloat(str);
		} catch (NumberFormatException e) {
			pos--;
			throw new InvalidSyntaxException(args, "Expected float!");
		}
	}

	public Double nextDouble() throws InvalidSyntaxException {
		try {
			String str = nextString();
			return Double.parseDouble(str);
		} catch (NumberFormatException e) {
			pos--;
			throw new InvalidSyntaxException(args, "Expected double!");
		}
	}

	public IUser nextUserTag() throws InvalidSyntaxException {
		String tag = nextString();
		if (!tag.startsWith("<@") || !tag.endsWith(">") || tag.charAt(2) == '&') {
			// Normally, we'd call it quits here, but we wont.
			// First, let's try to get an ID
			try {
				long id = Long.parseUnsignedLong(tag);
				IUser user = ctx.getClient().getUserByID(id);
				if (user != null) {
					return user;
				}
			} catch (NumberFormatException nfe) {
			}
			// Okay, maybe they typed in someone's username
			List<IUser> list = ctx.getUsersHere();
			// Let's find someone based on their raw username first
			for (int i = 0; i < list.size(); i++) {
				IUser u = list.get(i);
				if (u.getDisplayName(null).toLowerCase().startsWith(tag.toLowerCase())) {
					return u;
				}
			}
			// Last resort, check nicknames in the guild
			for (int i = 0; i < list.size(); i++) {
				IUser u = list.get(i);
				if (u.getDisplayName(ctx.getGuild()).toLowerCase().startsWith(tag.toLowerCase())) {
					return u;
				}
			}
			// If we got here, that means that there is no user to be found
			// after all.
			pos--;
			throw new InvalidSyntaxException(args, "Expected user! Got: " + tag);
		}
		if (tag.charAt(2) == '!')
			tag = tag.substring(3, tag.length() - 1);
		else
			tag = tag.substring(2, tag.length() - 1);
		IUser user = ctx.getClient().getUserByID(Long.parseUnsignedLong(tag));
		if (user == null) {
			pos--;
			throw new InvalidSyntaxException(args, "Unknown user!");
		}
		return user;
	}

	public IChannel nextChannelTag() throws InvalidSyntaxException {
		String tag = nextString();
		if (!tag.startsWith("<#") || !tag.endsWith(">")) {
			pos--;
			throw new InvalidSyntaxException(args, "Expected channel! Got: " + tag);
		}
		IChannel chan = ctx.getClient().getChannelByID(Long.parseUnsignedLong(tag.substring(2, tag.length() - 1)));
		if (chan == null) {
			pos--;
			throw new InvalidSyntaxException(args, "Unknown channel!");
		}
		return chan;
	}

	public CommandExecution<CmdCtx> nextCommand()
			throws InvalidSyntaxException, IllegalAccessException, NoSuchFieldException {
		String cmdName = nextString();
		CommandExecution<CmdCtx> cmd = getCommand(cmdName, engine);
		if (cmd == null) {
			pos--;
			throw new InvalidSyntaxException(args, "Unknown command!");
		}
		return cmd;
	}

	@SuppressWarnings("unchecked")
	public static CommandExecution<CmdCtx> getCommand(String cmdName, CommandEngine<CmdCtx> engine)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field commandsField = CommandEngine.class.getDeclaredField("commands");
		commandsField.setAccessible(true);
		List<CommandExecution<CmdCtx>> commands = (List<CommandExecution<CmdCtx>>) commandsField.get(engine);
		CommandExecution<CmdCtx> cmd = commands.stream().filter(c -> c.getCommand().name().equalsIgnoreCase(cmdName))
				.findAny().orElse(null);
		if (cmd != null) {
			return cmd;
		}
		// Crazy new addition (weird this wasn't in vanilla c4a4d4j)
		return engine.getAliasMap().get(cmdName);
	}

	public Byte nextByte() throws InvalidSyntaxException {
		String str = nextString();
		try {
			return Byte.parseByte(str);
		} catch (NumberFormatException e) {
			pos--;
			throw new InvalidSyntaxException(args, "Expected byte! Got: " + str);
		}
	}

	public Short nextShort() throws InvalidSyntaxException {
		String str = nextString();
		try {
			return Short.parseShort(str);
		} catch (NumberFormatException e) {
			pos--;
			throw new InvalidSyntaxException(args, "Expected Short! Got: " + str);
		}
	}

	public Boolean nextBoolean() throws InvalidSyntaxException {
		String str = nextString();
		switch (str.toLowerCase()) {
		case "true":
		case "t":
		case "1":
		case "yes":
		case "y":
		case "on":
			return true;
		default:
			return false;
		}
	}

	public IRole nextRole() throws InvalidSyntaxException {
		String tag = nextString();
		// This will be similar to getting a user
		// First, let's try to get an ID
		try {
			long id = Long.parseUnsignedLong(tag);
			IRole role = ctx.getClient().getRoleByID(id);
			if (role != null) {
				if (ctx.getGuild().getRoles().contains(role)) {
					return role;
				}
			}
		} catch (NumberFormatException nfe) {
		}
		// Okay, maybe they typed in a role's name
		List<IRole> list = ctx.getGuild().getRoles();
		for (int i = 0; i < list.size(); i++) {
			IRole r = list.get(i);
			if (r.getName().toLowerCase().startsWith(tag.toLowerCase())) {
				return r;
			}
		}
		// If we got here, that means that there is no role to be found
		// after all.
		pos--;
		throw new InvalidSyntaxException(args, "Expected Role! Got: " + tag);
	}
}
