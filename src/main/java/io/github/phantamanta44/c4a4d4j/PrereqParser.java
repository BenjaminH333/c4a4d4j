package io.github.phantamanta44.c4a4d4j;

import io.github.phantamanta44.commands4a.command.Prerequisite;
import sx.blah.discord.handle.obj.Permissions;

import java.util.function.Predicate;

public class PrereqParser {

    private final String pStr;
    private Prerequisite<CmdCtx> prereq;

    public PrereqParser(String prereq) {
        this.pStr = prereq;
    }

    public Prerequisite<CmdCtx> getPrereq() {
        if (prereq == null)
            resolvePrereq();
        return prereq;
    }

    private void resolvePrereq() {
        if (!pStr.contains(":"))
            throw new IllegalArgumentException("Illegal prerequisite format!");
        String type = pStr.substring(0, pStr.indexOf(":")), spec = pStr.substring(pStr.indexOf(":") + 1);
        switch (type) {
            case "perm":
                try {
                    Permissions perm = Permissions.valueOf(spec.toUpperCase());
                    Predicate<CmdCtx> test = c -> c.getChannel().getModifiedPermissions(c.getAuthor()).contains(perm);
                    prereq = new Prerequisite<>(test, String.format("Requires permission %s!", perm.name()));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("No such permission: " + spec);
                }
                break;
            case "guild":
                boolean guildOnly = spec.equalsIgnoreCase("true");
                Predicate<CmdCtx> test = c -> guildOnly ^ c.isPrivate();
                prereq = new Prerequisite<>(test, guildOnly ? "This command only works in a server!" : "This command only works in a DM!");
        }
    }

}
