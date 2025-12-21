package com.puttysoftware.tap.adventure.parsers;

import java.io.IOException;
import java.util.ArrayList;

import com.puttysoftware.xio.XDataReader;
import com.puttysoftware.xio.XDataWriter;

public interface InputParser {
    // Methods
    void doInitial(ArrayList<String> data);

    void doResume();

    void parseCommand(String command);

    ArrayList<String> loadState(XDataReader xdr) throws IOException;

    void saveState(XDataWriter xdw) throws IOException;
}