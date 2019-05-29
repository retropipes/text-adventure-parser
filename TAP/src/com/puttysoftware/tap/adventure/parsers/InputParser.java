package com.puttysoftware.tap.adventure.parsers;

import java.util.ArrayList;

public interface InputParser {
    // Methods
    void doInitial(ArrayList<String> data);

    void parseCommand(String command);
}