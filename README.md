Commands4A for Discord4J (C4A4D4J) Improvements
=====
A bunch of misc improvements for [C4A4D4J](https://github.com/phantamanta44/c4a4d4j) which is made for [Discord4J](https://github.com/austinv11/Discord4J) commands.

## Usage ##
### Obtaining a Command Engine ###
The engine is instantiated by calling the CommandEngineProvider API with the appropriate engine descriptor. This is now slightly different.
```java
C4A4D4J engine = CommandEngineProvider.getEngine(C4A4D4J.DESCRIPTOR); // Create an engine
engine.setPrefix("bot!"); // Set an engine's 'prefix' (OPTIONAL)
engine.scan("package.of.commands"); // Scan for potential commands
```
The prefixes are an addition in these improvements. They are useful in automating certain aspects of your bot. It is highly recommended that you tell the engine what prefixes the bot uses in commands (multiple are supported, pass them as a String[]). Also note that you can call `scan()` without any arguments to scan the entire classpath. This isn't recommended, since it can be extremely memory-inefficient.

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
To execute a command, construct an appropriate command context, then call `CommandEngine#execute`.
```java
engine.execute(new CmdCtx(event), event.getMessage().getContent());
```
If you supplied the engine with a prefix or array of prefixes as demonstrated above, you do not need to worry about trimming the prefix your bot uses for commands. This is done automatically.

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

The following are also given support in these additions:
* `Byte`
* `Short`
* `Boolean`
* Any `Enum` type, even random ones that you create


There are also some special arguments that aren't parsed from the user's input, but instead supply data about the command execution environment.
* `String[]` supplies the original argument list passed by the user.
* `CmdCtx` supplies the command's context.

### Some Improvements Made: ###
* Complies with the most recent version of Discord4J (as of writing: 2.9)
* New argument types (shown above)
* `IUser` resolvation in commands has also been improved. The people using your bot can now pass any user's id, the user's name, or the user's nickname in the server and they all automagically get resolved. Therefore, 'pinging' someone is not necessary to run a command anymore
* An ugly hack was developed allowing excess arguments near the end to be put back together into a single argument. This only works if your prefixes (shown above) are set up correctly.
* More fields and information in `CmdCtx` classes.
There may be more...

### Legal Stuff ###
This is all operating under the same license that the original C4A4D4J is running under. Discord and Discord4J are property of their own owners and I do not claim ownership of the original code for C4A4D4J nor do I claim ownership of Discord4J, Commands4A, or Discord. You are welcome to modify my code and improve it, and you are welcome to build your bot with this library. I provide no warranty and no guarantee that this code will be maintained, you are using it at your own risk.