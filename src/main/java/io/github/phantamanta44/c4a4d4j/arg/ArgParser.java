package io.github.phantamanta44.c4a4d4j.arg;

import io.github.phantamanta44.commands4a.args.IArgumentTokenizer;
import io.github.phantamanta44.commands4a.exception.InvalidSyntaxException;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ArgParser implements IArgumentTokenizer {

    private final String[] args;
    private int index;

    public ArgParser(String[] args) {
        this.args = args;
        this.index = 0;
    }

    @Override
    public String nextString() {
        return args[index++];
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T nextOfType(Class<T> type) throws InvalidSyntaxException {
        return (T)typeMap.get(type).apply(this);
    }

    @Override
    public boolean hasNext() {
        return index < args.length;
    }

    @Override
    public void reset() {
        index = 0;
    }

    private static final Map<Class<?>, Function<ArgParser, Object>> typeMap = new HashMap<>();

    static {
        typeMap.put(String.class, ArgParser::nextString); // TODO Implement
    }

}
