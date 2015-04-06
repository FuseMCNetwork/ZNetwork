package me.johnking.zmaster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Mic
 * Date: 26.12.13
 * Time: 20:14
 */
public class CommandBuilder {
    private List<String> strings = new ArrayList<String>();

    public void append(String part) {
        Collections.addAll(strings, part.split(" "));
    }

    public void append(String... parts) {
        Collections.addAll(strings, parts);
    }

    public void append(Collection<String> parts) {
        strings.addAll(parts);
    }

    public void appendPart(String part) {
        strings.add(part);
    }

    public ProcessBuilder toBuilder() {
        return new ProcessBuilder(strings);
    }
}