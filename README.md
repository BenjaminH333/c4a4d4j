Commands4A for Discord4J (C4A4D4J)
=====
A [Commands4A](https://github.com/phantamanta44/commands4a) implementation for processing [Discord4J](https://github.com/austinv11/Discord4J) commands.

## Usage ##
### Obtaining a Command Engine ###
The engine is instantiated by calling the CommandEngineProvider API with the appropriate engine descriptor.
```java
CommandEngine<CmdCtx> engine = CommandEngineProvider.getEngine(C4A4D4J.DESCRIPTOR);
engine.scan("package.of.commands");
```
Alternatively, you can call `scan()` without any arguments to scan the entire classpath. This isn't recommended, since it can be extremely memory-inefficient.

### Defining a Command ###
Commands are defined as static methods in a scanned class with annotations providing data about the command.
```java
@Command(name="say", usage="<message> [repetitions]")
@Alias("echo") @Alias("print")
@Desc("Prints a provided message.")
@Prereq("perm:manage_server")
public static void commandSay(CmdCtx ctx, String message, @Omittable Integer repetitions) {
    if (repetitions == null)
        repetitions = 1;
    for (int i = Math.max(repetitions, 1); i > 0; i--)
        ctx.getChannel().sendMessage(message);
}
```
Arguments are discovered using reflection, and are parsed automatically. An InvalidSyntaxException is thrown if provided arguments don't match the expected arguments. This behaviour can be overridden by annotating an argument with the `@Omittable` annotation, marking it as optional.

### Executing a Command ###
To execute a command, construct an approprtiate command context, then call `CommandEngine#execute`.
```java
engine.execute(new CmdCtx(event), event.getMessage().getContent());
```

### Available Argument Types ###
These are the possible types for automatic command argument resolvation:
* `Integer`
* `Float`
* `Double`
* `IUser`
* `IChannel`
* `String`
* `CodeBlock`
* `InlineCodeBlock`
* `CommandExecution`
