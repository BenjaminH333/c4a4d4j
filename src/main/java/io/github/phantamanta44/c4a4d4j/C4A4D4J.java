package io.github.phantamanta44.c4a4d4j;

import io.github.phantamanta44.c4a4d4j.arg.ArgParser;
import io.github.phantamanta44.commands4a.EngineDescriptor;
import io.github.phantamanta44.commands4a.args.IArgumentTokenizer;
import io.github.phantamanta44.commands4a.command.CommandEngine;
import io.github.phantamanta44.commands4a.command.Prerequisite;

public class C4A4D4J extends CommandEngine<CmdCtx> {

    public static final EngineDescriptor<CmdCtx, C4A4D4J> DESCRIPTOR = new EngineDescriptor<>("D4J Command Engine", C4A4D4J.class.getName());

    protected Prerequisite<CmdCtx> resolvePrereq(String prereq) {
        return new PrereqParser(prereq).getPrereq();
    }

    protected IArgumentTokenizer tokenize(String[] args) {
        return new ArgParser(args);
    }

}
