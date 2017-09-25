package io.github.phantamanta44.c4a4d4j;

import io.github.phantamanta44.c4a4d4j.arg.ArgParser;
import io.github.phantamanta44.commands4a.EngineDescriptor;
import io.github.phantamanta44.commands4a.args.IArgumentTokenizer;
import io.github.phantamanta44.commands4a.command.CommandEngine;
import io.github.phantamanta44.commands4a.command.Prerequisite;

public class C4A4D4J extends CommandEngine<CmdCtx> {

	public static final EngineDescriptor<CmdCtx, C4A4D4J> DESCRIPTOR = new EngineDescriptor<>("D4J Command Engine",
			C4A4D4J.class.getName());

	protected String[] prefixes;

	// Ugly hacks addition
	// I'm doing this because I can't modify commands4a
	// (Well I could, but that'd be a mess)
	public void setPrefix(String prefix) {
		this.setPrefixes(new String[] { prefix });
	}

	// Support for multiple prefixes
	public void setPrefixes(String[] prefixes) {
		this.prefixes = prefixes;
	}

	public String[] getPrefixes() {
		return prefixes;
	}

	@Override
	protected Prerequisite<CmdCtx> resolvePrereq(String prereq) {
		return new PrereqParser(prereq).getPrereq();
	}

	@Override
	protected IArgumentTokenizer tokenize(String[] args, CmdCtx ctx) {
		return new ArgParser(args, ctx, this);
	}

	public String trimUsingPrefixes(String str) {
		for (int i = 0; i < prefixes.length; i++) {
			if (str.startsWith(prefixes[i])) {
				str = str.substring(prefixes[i].length());
			}
		}
		return str;
	}

}
