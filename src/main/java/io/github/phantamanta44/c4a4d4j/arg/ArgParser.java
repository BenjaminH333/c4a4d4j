package io.github.phantamanta44.c4a4d4j.arg;

import com.github.fge.lambdas.functions.ThrowingFunction;
import io.github.phantamanta44.c4a4d4j.C4A4D4J;
import io.github.phantamanta44.c4a4d4j.CmdCtx;
import io.github.phantamanta44.commands4a.args.IArgumentTokenizer;
import io.github.phantamanta44.commands4a.command.CommandEngine;
import io.github.phantamanta44.commands4a.command.CommandExecution;
import io.github.phantamanta44.commands4a.exception.CommandExecutionException;
import io.github.phantamanta44.commands4a.exception.InvalidSyntaxException;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArgParser implements IArgumentTokenizer {

    private final String[] args;
    private final CmdCtx ctx;
    private final C4A4D4J engine;
    private int pos;

    public ArgParser(String[] args, CmdCtx ctx, C4A4D4J engine) {
        this.args = args;
        this.ctx = ctx;
        this.engine = engine;
        this.pos = 0;
    }

    @Override
    public String nextString() {
        return args[pos++];
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T nextOfType(Class<T> type) throws InvalidSyntaxException {
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
    }

    public InlineCodeBlock nextInlineCode() throws InvalidSyntaxException {
        StringBuilder sb = new StringBuilder(nextString());
        if (sb.charAt(0) != '`') {
            pos--;
            throw new InvalidSyntaxException(args, "Expected inline code block!");
        }
        final int start = pos - 1;
        while (!(
                sb.charAt(sb.length() - 1) == '`'
                        && (sb.charAt(sb.length() - 3) == '\\' || sb.charAt(sb.length() - 2) != '\\')
        )) {
            if (hasNext())
                sb.append(' ').append(nextString());
            else {
                pos = start;
                throw new InvalidSyntaxException(args, "Expected inline code block!!");
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
            pos--;
            throw new InvalidSyntaxException(args, "Expected user tag!");
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
            throw new InvalidSyntaxException(args, "Expected channel tag!");
        }
        IChannel chan = ctx.getClient().getChannelByID(Long.parseUnsignedLong(tag.substring(2, tag.length() - 1)));
        if (chan == null) {
            pos--;
            throw new InvalidSyntaxException(args, "Unknown channel!");
        }
        return chan;
    }

    @SuppressWarnings("unchecked")
    public CommandExecution<CmdCtx> nextCommand() throws InvalidSyntaxException, IllegalAccessException, NoSuchFieldException {
        String cmdName = nextString();
        Field commandsField = CommandEngine.class.getDeclaredField("commands");
        commandsField.setAccessible(true);
        List<CommandExecution<CmdCtx>> commands = (List<CommandExecution<CmdCtx>>)commandsField.get(engine);
        CommandExecution<CmdCtx> cmd = commands.stream()
                .filter(c -> c.getCommand().name().equalsIgnoreCase(cmdName))
                .findAny().orElse(null);
        if (cmd == null) {
            pos--;
            throw new InvalidSyntaxException(args, "Unknown command!");
        }
        return cmd;
    }

}
