package io.github.phantamanta44.c4a4d4j;

import io.github.phantamanta44.commands4a.command.Prerequisite;

public class PrereqParser {

    private final String pStr;

    public PrereqParser(String prereq) {
        this.pStr = prereq;
    }

    public Prerequisite<CmdCtx> getPrereq() {
        return new Prerequisite<>(ctx -> true, "Impossible condition!"); // TODO Implement
    }

}
