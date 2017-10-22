package io.github.phantamanta44.c4a4d4j;

import java.util.List;

import io.github.phantamanta44.c4a4d4j.CmdCtx;
import io.github.phantamanta44.commands4a.command.CommandEngine;
import io.github.phantamanta44.commands4a.command.CommandExecution;

public class CmdUtil {
	public static List<CommandExecution<CmdCtx>> getCommandList(CommandEngine<CmdCtx> engine) {
		return engine.getCommands();
	}

	public static CommandExecution<CmdCtx> getCommandFromName(CommandEngine<CmdCtx> engine, String name) {
		return getCommandFromName(getCommandList(engine), name);
	}

	public static CommandExecution<CmdCtx> getCommandFromName(List<CommandExecution<CmdCtx>> list, String name) {
		for (int i = 0; i < list.size(); i++) {
			CommandExecution<CmdCtx> ce = list.get(i);
			if (ce.getCommand().name().equals(name)) {
				return ce;
			}
		}
		return null;
	}

	public static CommandExecution<CmdCtx> getCommandFromAlias(CommandEngine<CmdCtx> engine, String alias) {
		return getCommandFromAlias(getCommandList(engine), alias);
	}

	public static CommandExecution<CmdCtx> getCommandFromAlias(List<CommandExecution<CmdCtx>> list, String alias) {
		for (int i = 0; i < list.size(); i++) {
			CommandExecution<CmdCtx> ce = list.get(i);
			String[] aliases = ce.getAliases();
			for (int q = 0; q < aliases.length; q++) {
				String alias2 = aliases[q];
				if (alias.equals(alias2)) {
					return ce;
				}
			}
		}
		return null;
	}

	public static CommandExecution<CmdCtx> getCommand(CommandEngine<CmdCtx> engine, String name) {
		return getCommand(getCommandList(engine), name);
	}

	public static CommandExecution<CmdCtx> getCommand(List<CommandExecution<CmdCtx>> list, String name) {
		CommandExecution<CmdCtx> ret = getCommandFromName(list, name);
		if (ret == null) {
			return getCommandFromAlias(list, name);
		} else {
			return ret;
		}
	}

	public static CmdInfo getInfo(CommandEngine<CmdCtx> engine, String name) {
		return getInfo(getCommandList(engine), name);
	}

	public static CmdInfo getInfo(List<CommandExecution<CmdCtx>> list, String name) {
		return new CmdInfo(getCommand(list, name));
	}

	public static CmdInfo getInfo(CommandExecution<CmdCtx> commandName) {
		return new CmdInfo(commandName);
	}
}
