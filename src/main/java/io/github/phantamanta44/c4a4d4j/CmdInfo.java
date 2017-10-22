package io.github.phantamanta44.c4a4d4j;

import java.lang.reflect.Method;
import java.util.Arrays;

import io.github.phantamanta44.c4a4d4j.CmdCtx;
import io.github.phantamanta44.commands4a.annot.Command;
import io.github.phantamanta44.commands4a.command.CommandExecution;

public class CmdInfo {
	private CommandExecution<CmdCtx> commandExecution;

	public CmdInfo(CommandExecution<CmdCtx> ce) {
		setCommandExecution(ce);
	}

	public CommandExecution<CmdCtx> getCommandExecution() {
		return commandExecution;
	}

	public void setCommandExecution(CommandExecution<CmdCtx> commandExecution) {
		this.commandExecution = commandExecution;
	}

	public Command getCommand() {
		return getCommandExecution().getCommand();
	}

	public String getName() {
		return getCommand().name();
	}

	public String getUsage() {
		return getCommand().usage();
	}

	public String getDescription() {
		return getCommandExecution().getDescription();
	}

	public String[] getAliasesArr() {
		return getCommandExecution().getAliases();
	}

	public String getAliases() {
		return Arrays.toString(getAliasesArr());
	}

	@Override
	public String toString() {
		String ret = "";
		ret += "**" + getName() + "**\n";
		ret += getDescription() + "\n";
		ret += "**Usage:** `" + getUsage() + "`\n";
		ret += "**Aliases:** `" + getAliases() + "`\n";
		return ret;
	}

	public Method getExecutor() {
		return getCommandExecution().getExecutor();
	}

	public Class<?> getExecClass() {
		return getExecutor().getDeclaringClass();
	}

	public String getExecClassName() {
		return getExecClass().getSimpleName();
	}
}
