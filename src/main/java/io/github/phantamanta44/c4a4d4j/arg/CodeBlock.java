package io.github.phantamanta44.c4a4d4j.arg;

public class CodeBlock extends InlineCodeBlock {

    final String lang;

    public CodeBlock(String code, String lang) {
        super(code);
        this.lang = lang;
    }

    public CodeBlock(String code) {
        this(code, "");
    }

    public String getLang() {
        return lang;
    }

}
