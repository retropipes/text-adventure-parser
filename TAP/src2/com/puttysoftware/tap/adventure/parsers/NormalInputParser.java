/*  TAP: A Text Adventure Parser
Copyright (C) 2010 Eric Ahnell

Any questions should be directed to the author via email at: tap@worldwizard.net
 */
package com.puttysoftware.tap.adventure.parsers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import com.puttysoftware.tap.TAP;
import com.puttysoftware.tap.game.Game;
import com.puttysoftware.tap.messages.Message;
import com.puttysoftware.tap.messages.MessageArgument;
import com.puttysoftware.xio.XDataReader;
import com.puttysoftware.xio.XDataWriter;

class NormalInputParser implements InputParser {
    // Fields
    private ArrayList<String> advData;
    private final ArrayList<String> inventory;
    private final ArrayList<String> grabbedAlready;
    private ArrayList<ArrayList<String>> synonymList;
    private final Hashtable<String, String> objectState;
    private final Hashtable<String, Boolean> questState;
    private final Hashtable<String, Integer> questProgress;
    private static Game game;
    private static final MessageArgument RESULT_SUCCESS = MessageArgument
            .fromMessage(Message.RESULT_SUCCESS);
    private static final MessageArgument RESULT_FAILURE = MessageArgument
            .fromMessage(Message.RESULT_FAILURE);
    private static final MessageArgument RESULT_DONE = MessageArgument
            .fromMessage(Message.RESULT_DONE);
    private static final MessageArgument RESULT_STARTED = MessageArgument
            .fromMessage(Message.RESULT_STARTED);
    private static final MessageArgument RESULT_NOT_STARTED = MessageArgument
            .fromMessage(Message.RESULT_NOT_STARTED);
    private static final MessageArgument RESULT_GREATER = MessageArgument
            .fromMessage(Message.RESULT_GREATER);
    private static final MessageArgument RESULT_LESS = MessageArgument
            .fromMessage(Message.RESULT_LESS);
    private static final MessageArgument RESULT_EQUAL = MessageArgument
            .fromMessage(Message.RESULT_EQUAL);
    private static final MessageArgument RESULT_INSIDE = MessageArgument
            .fromMessage(Message.RESULT_INSIDE);
    private static final MessageArgument RESULT_OUTSIDE = MessageArgument
            .fromMessage(Message.RESULT_OUTSIDE);
    private String currentRoomDescription;
    private int counter;
    private int subCounter;

    // Constructor
    public NormalInputParser() {
        this.inventory = new ArrayList<>();
        this.grabbedAlready = new ArrayList<>();
        this.objectState = new Hashtable<>();
        this.questState = new Hashtable<>();
        this.questProgress = new Hashtable<>();
        this.counter = 0;
        this.subCounter = 0;
        NormalInputParser.game = TAP.getGame();
    }

    // Methods
    private static void displayCommandMessage(final Message msg,
            final NormalCommand ncmd, final MessageArgument... otherArgs) {
        final ArrayList<MessageArgument> argList = new ArrayList<>();
        argList.add(MessageArgument.fromValueBearer(ncmd));
        for (final MessageArgument otherArg : otherArgs) {
            argList.add(otherArg);
        }
        NormalInputParser.game.showMessage(msg,
                argList.toArray(new MessageArgument[argList.size()]));
    }

    private static void displayQuestCommandMessage(final Message msg,
            final NormalCommand qsub, final MessageArgument... otherArgs) {
        final ArrayList<MessageArgument> argList = new ArrayList<>();
        argList.add(NormalInputParser.messageArgFromQuestSubCommand(qsub));
        for (final MessageArgument otherArg : otherArgs) {
            argList.add(otherArg);
        }
        NormalInputParser.game.showMessage(msg,
                argList.toArray(new MessageArgument[argList.size()]));
    }

    private static void displayAdventureMessage(final String msg) {
        NormalInputParser.game.showMessage(Message.CUSTOM,
                NormalInputParser.messageArgFromString(msg));
    }

    private static void displayStatelessItemMessage(final String item) {
        NormalInputParser.game.showMessage(Message.STATELESS_ITEM,
                NormalInputParser.messageArgFromString(item));
    }

    private static void displayStatefulItemMessage(final String item,
            final String form) {
        NormalInputParser.game.showMessage(Message.STATEFUL_ITEM,
                NormalInputParser.messageArgFromString(item),
                NormalInputParser.messageArgFromString(form));
    }

    private static MessageArgument messageArgFromQuestSubCommand(
            final NormalCommand qsub) {
        return NormalInputParser.messageArgFromString(
                NormalCommand.SPECIAL_COMMAND_QUEST.value() + " " //$NON-NLS-1$
                        + qsub.value());
    }

    private static MessageArgument messageArgFromString(final String msg) {
        return MessageArgument
                .fromValueBearer(NormalAdventureMessage.create(msg));
    }

    @Override
    public void doInitial(final ArrayList<String> data) {
        this.advData = data;
        final boolean initialJump1 = this
                .warpToRoom(NormalCommand.ROOM_MAIN.value());
        if (!initialJump1) {
            NormalInputParser.game
                    .showMessage(Message.ERROR_MAIN_ROOM_NONEXISTENT);
            TAP.getAdventureManager().closeAdventure();
            return;
        }
        final boolean synonymCommand = this.findRoomCommand(
                NormalCommand.MAIN_COMMAND_SYNONYM_TABLE.value());
        if (synonymCommand) {
            final String syncmd = this.advData
                    .get(this.counter + this.subCounter);
            final String synArg = syncmd.substring(7);
            final boolean initialJump2 = this.warpToRoom(synArg);
            if (!initialJump2) {
                NormalInputParser.game
                        .showMessage(Message.ERROR_SYNONYM_TABLE_NONEXISTENT);
                TAP.getAdventureManager().closeAdventure();
                return;
            }
            // Load synonym list
            String line = ""; //$NON-NLS-1$
            this.synonymList = new ArrayList<>();
            while (line != null) {
                line = this.advData.get(this.counter + this.subCounter);
                if (line != null) {
                    if (line.length() > 0) {
                        if (line.startsWith("[")) { //$NON-NLS-1$
                            // Found next room
                            line = null;
                        }
                    }
                }
                if (line != null) {
                    if (line.length() > 0) {
                        final String[] splitLine = line
                                .split(NormalCommand.SYNONYM_SEPARATOR.value());
                        final ArrayList<String> temp = new ArrayList<>();
                        for (final String element : splitLine) {
                            temp.add(element);
                        }
                        this.synonymList.add(temp);
                    }
                    this.subCounter++;
                }
            }
            NormalInputParser.game.clearOutput();
            // Go back
            final boolean initialJump3 = this
                    .warpToRoom(NormalCommand.ROOM_MAIN.value());
            if (!initialJump3) {
                NormalInputParser.game
                        .showMessage(Message.ERROR_MAIN_ROOM_NONEXISTENT);
                TAP.getAdventureManager().closeAdventure();
                return;
            }
        }
        final boolean startCommand = this
                .findRoomCommand(NormalCommand.MAIN_COMMAND_START.value());
        if (!startCommand) {
            NormalInputParser.game.showMessage(Message.ERROR_NO_MAIN_ROOM);
            TAP.getAdventureManager().closeAdventure();
            return;
        }
        final String cmd = this.advData.get(this.counter + this.subCounter);
        final String startArg = cmd.substring(6);
        final boolean initialJump4 = this.warpToRoom(startArg);
        if (!initialJump4) {
            NormalInputParser.game
                    .showMessage(Message.ERROR_MAIN_ROOM_NONEXISTENT);
            TAP.getAdventureManager().closeAdventure();
            return;
        }
    }

    @Override
    public void doResume() {
        NormalInputParser.displayAdventureMessage(this.currentRoomDescription);
    }

    @Override
    public void parseCommand(final String command) {
        boolean foundUnknown = false;
        String preParsedCommand = command;
        String[] commandArray = new String[] { preParsedCommand };
        commandArray = CompoundNormalCommands
                .splitCompoundInput(preParsedCommand);
        for (final String element : commandArray) {
            preParsedCommand = element;
            // Strip input of needless words
            preParsedCommand = InputStripper.stripInput(preParsedCommand);
            // Substitute synonyms
            preParsedCommand = this.substituteSynonyms(preParsedCommand);
            if (NormalInputParser.isOmniCommand(preParsedCommand)) {
                // Found omni command
                final int cmdLen = NormalInputParser
                        .omniCommandLength(preParsedCommand);
                this.parseOmniCommand(preParsedCommand, cmdLen);
            } else {
                if (this.findRoomCommand(preParsedCommand)) {
                    // Found known command
                    this.parseCommandResult();
                } else {
                    // Found unknown command
                    foundUnknown = true;
                }
            }
        }
        if (foundUnknown) {
            NormalInputParser.displayUnknownCommandMessage(command);
        }
    }

    private boolean findRoomCommand(final String cmdName) {
        for (int z = this.counter; z < this.advData.size(); z++) {
            final String data = this.advData.get(z);
            if (data.length() > 0) {
                final String check = data.substring(0, 1);
                if (check.equals(NormalCommand.ROOM_DELIMITER.value())) {
                    return false;
                }
                final String[] dataSplit = data
                        .split(NormalCommand.RESULT_DELIMITER.value());
                if (dataSplit[0].equals(cmdName)) {
                    this.subCounter = z - this.counter;
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isOmniCommand(final String cmdName) {
        if (cmdName.length() >= 4) {
            if (cmdName.substring(0, 4).equalsIgnoreCase(
                    NormalCommand.OMNI_COMMAND_SHOW.value())) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private static int omniCommandLength(final String cmdName) {
        if (cmdName.substring(0, 4)
                .equalsIgnoreCase(NormalCommand.OMNI_COMMAND_SHOW.value())) {
            return 4;
        } else {
            return 0;
        }
    }

    private void parseCommandResult() {
        final String raw = this.advData.get(this.counter + this.subCounter);
        this.parseCommandResultOuter(raw);
    }

    private void parseCommandResultOuter(final String raw) {
        final int cmdPos = raw.indexOf(NormalCommand.RESULT_DELIMITER.value());
        final String preres = raw.substring(cmdPos + 1);
        String res;
        final String[] resarr = preres
                .split(NormalCommand.CHAIN_COMMAND_DELIM.value());
        for (final String element : resarr) {
            res = element;
            this.parseCommandResultInner(res);
        }
    }

    private void parseCommandResultInner(final String res) {
        try {
            final String specialCheck = res.substring(0, 1);
            if (specialCheck.equals(NormalCommand.SPECIAL_DELIMITER.value())) {
                // Special command
                final String specialCmd = res.substring(1, res.length() - 1);
                final String terminator = res.substring(res.length() - 1);
                if (!terminator
                        .equals(NormalCommand.SPECIAL_END_DELIMITER.value())) {
                    // Malformed special command found
                    NormalInputParser
                            .displayMalformedSpecialCommandMessage(res);
                    return;
                }
                String warpKillTest = ""; //$NON-NLS-1$
                String stateQuestTest = ""; //$NON-NLS-1$
                String specialCmdTest8 = ""; //$NON-NLS-1$
                if (specialCmd.length() >= 5) {
                    stateQuestTest = specialCmd.substring(0, 5);
                }
                if (specialCmd.length() >= 4) {
                    warpKillTest = specialCmd.substring(0, 4);
                }
                if (specialCmd.length() >= 8) {
                    specialCmdTest8 = specialCmd.substring(0, 8);
                }
                if (stateQuestTest.equalsIgnoreCase(
                        NormalCommand.SPECIAL_COMMAND_ALTER_STATE.value())) {
                    // Process alter state command
                    String alterArg = ""; //$NON-NLS-1$
                    if (specialCmd.length() >= 6) {
                        alterArg = specialCmd.substring(6);
                    } else {
                        // No arguments specified
                        NormalInputParser.displayNoArgsForSpecialCommandMessage(
                                specialCmd);
                        return;
                    }
                    final String[] alterArgs = alterArg.split(" "); //$NON-NLS-1$
                    if (alterArgs.length % 2 == 0) {
                        for (int x = 0; x < alterArgs.length; x += 2) {
                            final String alterObjName = alterArgs[x];
                            final String alterObjState = alterArgs[x + 1];
                            this.objectState.put(alterObjName, alterObjState);
                        }
                    } else {
                        // Invalid alter arguments found
                        NormalInputParser.displayCommandMessage(
                                Message.ERROR_INVALID_ARGUMENTS,
                                NormalCommand.SPECIAL_COMMAND_ALTER_STATE,
                                MessageArgument.fromInteger(this.counter));
                    }
                } else if (stateQuestTest.equalsIgnoreCase(
                        NormalCommand.SPECIAL_COMMAND_CHECK_STATE.value())) {
                    // Process check state command
                    String checkArg = ""; //$NON-NLS-1$
                    if (specialCmd.length() >= 6) {
                        checkArg = specialCmd.substring(6);
                    } else {
                        // No arguments specified
                        NormalInputParser.displayNoArgsForSpecialCommandMessage(
                                specialCmd);
                        return;
                    }
                    final String[] checkArgs = checkArg.split(" "); //$NON-NLS-1$
                    if (checkArgs.length == 2) {
                        final String checkObjName = checkArgs[0];
                        final String checkObjState = checkArgs[1];
                        final String checkAgainst = this.objectState
                                .get(checkObjName);
                        if (!checkObjState.equalsIgnoreCase(checkAgainst)) {
                            final String checkFail = this.getCommandAtOffset(2)
                                    .substring(1);
                            final String delim = this.getCommandAtOffset(2)
                                    .substring(0, 1);
                            if (!delim.equals(
                                    NormalCommand.RESULT_DELIMITER.value())) {
                                NormalInputParser.displayCommandMessage(
                                        Message.ERROR_BAD_RESULT_SYNTAX,
                                        NormalCommand.SPECIAL_COMMAND_CHECK_STATE,
                                        MessageArgument
                                                .fromInteger(this.counter),
                                        NormalInputParser.RESULT_FAILURE);
                                return;
                            }
                            this.parseCommandResultOuter(checkFail);
                        } else {
                            final String checkSuccess = this
                                    .getCommandAtOffset(1).substring(1);
                            final String delim = this.getCommandAtOffset(1)
                                    .substring(0, 1);
                            if (!delim.equals(
                                    NormalCommand.RESULT_DELIMITER.value())) {
                                NormalInputParser.displayCommandMessage(
                                        Message.ERROR_BAD_RESULT_SYNTAX,
                                        NormalCommand.SPECIAL_COMMAND_CHECK_STATE,
                                        MessageArgument
                                                .fromInteger(this.counter),
                                        NormalInputParser.RESULT_SUCCESS);
                                return;
                            }
                            this.parseCommandResultOuter(checkSuccess);
                        }
                    } else {
                        // Invalid check arguments found
                        NormalInputParser.displayCommandMessage(
                                Message.ERROR_INVALID_ARGUMENTS,
                                NormalCommand.SPECIAL_COMMAND_CHECK_STATE,
                                MessageArgument.fromInteger(this.counter));
                    }
                } else if (stateQuestTest.equalsIgnoreCase(
                        NormalCommand.SPECIAL_COMMAND_QUEST.value())) {
                    // Process quest command
                    String questArg = ""; //$NON-NLS-1$
                    if (specialCmd.length() >= 6) {
                        questArg = specialCmd.substring(6);
                    } else {
                        // No arguments specified
                        NormalInputParser.displayNoArgsForSpecialCommandMessage(
                                specialCmd);
                        return;
                    }
                    final String[] questArgs = questArg.split(" "); //$NON-NLS-1$
                    if (questArgs.length == 2) {
                        final String questSubtype = questArgs[0];
                        final String questName = questArgs[1];
                        if (questSubtype.equalsIgnoreCase(
                                NormalCommand.SPECIAL_QUEST_ARG_BEGIN
                                        .value())) {
                            if (!this.questState.containsKey(questName)) {
                                this.questState.put(questName, Boolean.FALSE);
                                this.questProgress.put(questName, 0);
                            } else {
                                // Invalid quest state
                                NormalInputParser.game
                                        .showMessage(Message.QUEST_ERROR_START);
                            }
                        } else if (questSubtype.equalsIgnoreCase(
                                NormalCommand.SPECIAL_QUEST_ARG_FINISH
                                        .value())) {
                            if (this.questState.containsKey(questName)) {
                                this.questState.put(questName, Boolean.TRUE);
                                this.questProgress.put(questName, 10000);
                            } else {
                                // Invalid quest state
                                NormalInputParser.game.showMessage(
                                        Message.QUEST_ERROR_FINISH);
                            }
                        } else if (questSubtype.equalsIgnoreCase(
                                NormalCommand.SPECIAL_QUEST_ARG_STATUS
                                        .value())) {
                            if (this.questState.containsKey(questName)) {
                                if (this.questState.get(questName)) {
                                    // Quest done
                                    final String questDone = this
                                            .getCommandAtOffset(3).substring(1);
                                    final String delim = this
                                            .getCommandAtOffset(3)
                                            .substring(0, 1);
                                    if (!delim.equals(
                                            NormalCommand.RESULT_DELIMITER
                                                    .value())) {
                                        NormalInputParser
                                                .displayQuestCommandMessage(
                                                        Message.ERROR_BAD_RESULT_SYNTAX,
                                                        NormalCommand.SPECIAL_QUEST_ARG_STATUS,
                                                        MessageArgument
                                                                .fromInteger(
                                                                        this.counter),
                                                        NormalInputParser.RESULT_DONE);
                                        return;
                                    }
                                    this.parseCommandResultOuter(questDone);
                                } else {
                                    // Quest started
                                    final String questStarted = this
                                            .getCommandAtOffset(2).substring(1);
                                    final String delim = this
                                            .getCommandAtOffset(2)
                                            .substring(0, 1);
                                    if (!delim.equals(
                                            NormalCommand.RESULT_DELIMITER
                                                    .value())) {
                                        NormalInputParser
                                                .displayQuestCommandMessage(
                                                        Message.ERROR_BAD_RESULT_SYNTAX,
                                                        NormalCommand.SPECIAL_QUEST_ARG_STATUS,
                                                        MessageArgument
                                                                .fromInteger(
                                                                        this.counter),
                                                        NormalInputParser.RESULT_STARTED);
                                        return;
                                    }
                                    this.parseCommandResultOuter(questStarted);
                                }
                            } else {
                                // Quest not started
                                final String questNotStarted = this
                                        .getCommandAtOffset(1).substring(1);
                                final String delim = this.getCommandAtOffset(1)
                                        .substring(0, 1);
                                if (!delim.equals(NormalCommand.RESULT_DELIMITER
                                        .value())) {
                                    NormalInputParser
                                            .displayQuestCommandMessage(
                                                    Message.ERROR_BAD_RESULT_SYNTAX,
                                                    NormalCommand.SPECIAL_QUEST_ARG_STATUS,
                                                    MessageArgument.fromInteger(
                                                            this.counter),
                                                    NormalInputParser.RESULT_NOT_STARTED);
                                    return;
                                }
                                this.parseCommandResultOuter(questNotStarted);
                            }
                        } else {
                            // Found unknown arguments for quest command
                            NormalInputParser.displayCommandMessage(
                                    Message.ERROR_INVALID_ARGUMENTS_SUB,
                                    NormalCommand.SPECIAL_COMMAND_QUEST,
                                    NormalInputParser
                                            .messageArgFromString(questSubtype),
                                    MessageArgument.fromInteger(this.counter));
                        }
                    } else if (questArgs.length == 3) {
                        final String questSubtype = questArgs[0];
                        final String questName = questArgs[1];
                        final int questValue = Integer.parseInt(questArgs[2]);
                        if (questSubtype.equalsIgnoreCase(
                                NormalCommand.SPECIAL_QUEST_ARG_ADD.value())) {
                            if (this.questProgress.containsKey(questName)
                                    && !this.questState.get(questName)
                                            .booleanValue()) {
                                final int newValue = this.questProgress
                                        .get(questName).intValue() + questValue;
                                this.questProgress.put(questName, newValue);
                            } else {
                                // Invalid quest state
                                NormalInputParser.game.showMessage(
                                        Message.QUEST_ERROR_PROGRESS);
                            }
                        } else if (questSubtype.equalsIgnoreCase(
                                NormalCommand.SPECIAL_QUEST_ARG_SET.value())) {
                            if (this.questProgress.containsKey(questName)
                                    && !this.questState.get(questName)
                                            .booleanValue()) {
                                this.questProgress.put(questName, questValue);
                            } else {
                                // Invalid quest state
                                NormalInputParser.game.showMessage(
                                        Message.QUEST_ERROR_PROGRESS);
                            }
                        } else if (questSubtype.equalsIgnoreCase(
                                NormalCommand.SPECIAL_QUEST_ARG_GET.value())) {
                            if (this.questProgress.containsKey(questName)
                                    && !this.questState.get(questName)
                                            .booleanValue()) {
                                final int currentValue = this.questProgress
                                        .get(questName);
                                if (currentValue > questValue) {
                                    final String questGreater = this
                                            .getCommandAtOffset(3).substring(1);
                                    final String delim = this
                                            .getCommandAtOffset(3)
                                            .substring(0, 1);
                                    if (!delim.equals(
                                            NormalCommand.RESULT_DELIMITER
                                                    .value())) {
                                        NormalInputParser
                                                .displayQuestCommandMessage(
                                                        Message.ERROR_BAD_RESULT_SYNTAX,
                                                        NormalCommand.SPECIAL_QUEST_ARG_GET,
                                                        MessageArgument
                                                                .fromInteger(
                                                                        this.counter),
                                                        NormalInputParser.RESULT_GREATER);
                                        return;
                                    }
                                    this.parseCommandResultOuter(questGreater);
                                } else if (currentValue < questValue) {
                                    final String questLess = this
                                            .getCommandAtOffset(2).substring(1);
                                    final String delim = this
                                            .getCommandAtOffset(2)
                                            .substring(0, 1);
                                    if (!delim.equals(
                                            NormalCommand.RESULT_DELIMITER
                                                    .value())) {
                                        NormalInputParser
                                                .displayQuestCommandMessage(
                                                        Message.ERROR_BAD_RESULT_SYNTAX,
                                                        NormalCommand.SPECIAL_QUEST_ARG_GET,
                                                        MessageArgument
                                                                .fromInteger(
                                                                        this.counter),
                                                        NormalInputParser.RESULT_LESS);
                                        return;
                                    }
                                    this.parseCommandResultOuter(questLess);
                                } else {
                                    final String questEqual = this
                                            .getCommandAtOffset(1).substring(1);
                                    final String delim = this
                                            .getCommandAtOffset(1)
                                            .substring(0, 1);
                                    if (!delim.equals(
                                            NormalCommand.RESULT_DELIMITER
                                                    .value())) {
                                        NormalInputParser
                                                .displayQuestCommandMessage(
                                                        Message.ERROR_BAD_RESULT_SYNTAX,
                                                        NormalCommand.SPECIAL_QUEST_ARG_GET,
                                                        MessageArgument
                                                                .fromInteger(
                                                                        this.counter),
                                                        NormalInputParser.RESULT_EQUAL);
                                        return;
                                    }
                                    this.parseCommandResultOuter(questEqual);
                                }
                            } else {
                                // Invalid quest state
                                NormalInputParser.game.showMessage(
                                        Message.QUEST_ERROR_PROGRESS);
                            }
                        } else {
                            // Found unknown arguments for quest command
                            NormalInputParser.displayCommandMessage(
                                    Message.ERROR_INVALID_ARGUMENTS_SUB,
                                    NormalCommand.SPECIAL_COMMAND_QUEST,
                                    NormalInputParser
                                            .messageArgFromString(questSubtype),
                                    MessageArgument.fromInteger(this.counter));
                        }
                    } else if (questArgs.length == 4) {
                        final String questSubtype = questArgs[0];
                        final String questName = questArgs[1];
                        final int questValue1 = Integer.parseInt(questArgs[2]);
                        final int questValue2 = Integer.parseInt(questArgs[3]);
                        if (questSubtype.equalsIgnoreCase(
                                NormalCommand.SPECIAL_QUEST_ARG_TEST.value())) {
                            if (this.questProgress.containsKey(questName)
                                    && !this.questState.get(questName)
                                            .booleanValue()) {
                                final int currentValue = this.questProgress
                                        .get(questName);
                                if (currentValue > questValue1
                                        && currentValue < questValue2) {
                                    final String questBetween = this
                                            .getCommandAtOffset(1).substring(1);
                                    final String delim = this
                                            .getCommandAtOffset(1)
                                            .substring(0, 1);
                                    if (!delim.equals(
                                            NormalCommand.RESULT_DELIMITER
                                                    .value())) {
                                        NormalInputParser
                                                .displayQuestCommandMessage(
                                                        Message.ERROR_BAD_RESULT_SYNTAX,
                                                        NormalCommand.SPECIAL_QUEST_ARG_GET,
                                                        MessageArgument
                                                                .fromInteger(
                                                                        this.counter),
                                                        NormalInputParser.RESULT_INSIDE);
                                        return;
                                    }
                                    this.parseCommandResultOuter(questBetween);
                                } else {
                                    final String questOutside = this
                                            .getCommandAtOffset(3).substring(1);
                                    final String delim = this
                                            .getCommandAtOffset(3)
                                            .substring(0, 1);
                                    if (!delim.equals(
                                            NormalCommand.RESULT_DELIMITER
                                                    .value())) {
                                        NormalInputParser
                                                .displayQuestCommandMessage(
                                                        Message.ERROR_BAD_RESULT_SYNTAX,
                                                        NormalCommand.SPECIAL_QUEST_ARG_GET,
                                                        MessageArgument
                                                                .fromInteger(
                                                                        this.counter),
                                                        NormalInputParser.RESULT_OUTSIDE);
                                        return;
                                    }
                                    this.parseCommandResultOuter(questOutside);
                                }
                            } else {
                                // Invalid quest state
                                NormalInputParser.game.showMessage(
                                        Message.QUEST_ERROR_PROGRESS);
                            }
                        } else {
                            // Found unknown arguments for quest command
                            NormalInputParser.displayCommandMessage(
                                    Message.ERROR_INVALID_ARGUMENTS_SUB,
                                    NormalCommand.SPECIAL_COMMAND_QUEST,
                                    NormalInputParser
                                            .messageArgFromString(questSubtype),
                                    MessageArgument.fromInteger(this.counter));
                        }
                    } else {
                        // Found no arguments for quest command
                        NormalInputParser.displayNoArgsForSpecialCommandMessage(
                                NormalCommand.SPECIAL_COMMAND_QUEST.value());
                    }
                } else if (warpKillTest.equalsIgnoreCase(
                        NormalCommand.SPECIAL_COMMAND_KILL.value())) {
                    // Process kill command
                    String killArg = ""; //$NON-NLS-1$
                    if (specialCmd.length() >= 5) {
                        killArg = specialCmd.substring(5);
                    } else {
                        // No arguments specified
                        NormalInputParser.displayNoArgsForSpecialCommandMessage(
                                specialCmd);
                        return;
                    }
                    NormalInputParser.displayAdventureMessage(killArg);
                    TAP.getAdventureManager().closeAdventure();
                } else if (warpKillTest.equalsIgnoreCase(
                        NormalCommand.SPECIAL_COMMAND_WARP.value())) {
                    // Process warp command
                    String warpArg = ""; //$NON-NLS-1$
                    if (specialCmd.length() >= 5) {
                        warpArg = specialCmd.substring(5);
                    } else {
                        // No arguments specified
                        NormalInputParser.displayNoArgsForSpecialCommandMessage(
                                specialCmd);
                        return;
                    }
                    if (!this.warpToRoom(warpArg)) {
                        NormalInputParser.game.showMessage(
                                Message.ERROR_WARP_NONEXISTENT_ROOM,
                                NormalInputParser
                                        .messageArgFromString(warpArg));
                    }
                } else if (specialCmdTest8.equalsIgnoreCase(
                        NormalCommand.SPECIAL_COMMAND_ONCE_GRAB.value())) {
                    String onceGrabArg = ""; //$NON-NLS-1$
                    if (specialCmd.length() >= 9) {
                        onceGrabArg = specialCmd.substring(9);
                    } else {
                        // No arguments specified
                        NormalInputParser.displayNoArgsForSpecialCommandMessage(
                                specialCmd);
                        return;
                    }
                    final String[] onceGrabArgSplit = onceGrabArg
                            .split(NormalCommand.HAVE_MULTIPLE.value());
                    boolean onceGrabbedAny = false;
                    for (final String element : onceGrabArgSplit) {
                        if (!this.inventory.contains(element)
                                && !this.grabbedAlready.contains(element)) {
                            onceGrabbedAny = true;
                            this.inventory.add(element);
                            this.grabbedAlready.add(element);
                        }
                    }
                    if (onceGrabbedAny) {
                        final String onceGrabSuccess = this
                                .getCommandAtOffset(1);
                        NormalInputParser
                                .displayAdventureMessage(onceGrabSuccess);
                    } else {
                        final String onceGrabFail = this.getCommandAtOffset(2);
                        NormalInputParser.displayAdventureMessage(onceGrabFail);
                    }
                } else if (specialCmdTest8.equalsIgnoreCase(
                        NormalCommand.SPECIAL_COMMAND_ONCE_GAIN.value())) {
                    String onceGainArg = ""; //$NON-NLS-1$
                    if (specialCmd.length() >= 9) {
                        onceGainArg = specialCmd.substring(9);
                    } else {
                        // No arguments specified
                        NormalInputParser.displayNoArgsForSpecialCommandMessage(
                                specialCmd);
                        return;
                    }
                    final String[] onceGainArgSplit = onceGainArg
                            .split(NormalCommand.HAVE_MULTIPLE.value());
                    for (final String element : onceGainArgSplit) {
                        if (!this.inventory.contains(element)
                                && !this.grabbedAlready.contains(element)) {
                            this.inventory.add(element);
                            this.grabbedAlready.add(element);
                        }
                    }
                } else if (warpKillTest.equalsIgnoreCase(
                        NormalCommand.SPECIAL_COMMAND_GAIN.value())) {
                    String gainArg = ""; //$NON-NLS-1$
                    if (specialCmd.length() >= 5) {
                        gainArg = specialCmd.substring(5);
                    } else {
                        // No arguments specified
                        NormalInputParser.displayNoArgsForSpecialCommandMessage(
                                specialCmd);
                        return;
                    }
                    final String[] gainArgSplit = gainArg
                            .split(NormalCommand.HAVE_MULTIPLE.value());
                    for (final String element : gainArgSplit) {
                        if (!this.inventory.contains(element)) {
                            this.inventory.add(element);
                            if (!this.grabbedAlready.contains(element)) {
                                this.grabbedAlready.add(element);
                            }
                        }
                    }
                } else if (warpKillTest.equalsIgnoreCase(
                        NormalCommand.SPECIAL_COMMAND_GRAB.value())) {
                    String grabArg = ""; //$NON-NLS-1$
                    if (specialCmd.length() >= 5) {
                        grabArg = specialCmd.substring(5);
                    } else {
                        // No arguments specified
                        NormalInputParser.displayNoArgsForSpecialCommandMessage(
                                specialCmd);
                        return;
                    }
                    final String[] grabArgSplit = grabArg
                            .split(NormalCommand.HAVE_MULTIPLE.value());
                    boolean grabbedAny = false;
                    for (final String element : grabArgSplit) {
                        if (!this.inventory.contains(element)) {
                            grabbedAny = true;
                            this.inventory.add(element);
                            if (!this.grabbedAlready.contains(element)) {
                                this.grabbedAlready.add(element);
                            }
                        }
                    }
                    if (grabbedAny) {
                        final String grabSuccess = this.getCommandAtOffset(1);
                        NormalInputParser.displayAdventureMessage(grabSuccess);
                    } else {
                        final String grabFail = this.getCommandAtOffset(2);
                        NormalInputParser.displayAdventureMessage(grabFail);
                    }
                } else if (warpKillTest.equalsIgnoreCase(
                        NormalCommand.SPECIAL_COMMAND_HAVE.value())) {
                    String haveArg = ""; //$NON-NLS-1$
                    if (specialCmd.length() >= 5) {
                        haveArg = specialCmd.substring(5);
                    } else {
                        // No arguments specified
                        NormalInputParser.displayNoArgsForSpecialCommandMessage(
                                specialCmd);
                        return;
                    }
                    final String[] haveArgSplit = haveArg
                            .split(NormalCommand.HAVE_MULTIPLE.value());
                    boolean haveIt = true;
                    for (final String element : haveArgSplit) {
                        haveIt = haveIt && this.inventory.contains(element);
                    }
                    if (!haveIt) {
                        final String haveFail = this.getCommandAtOffset(2)
                                .substring(1);
                        final String delim = this.getCommandAtOffset(2)
                                .substring(0, 1);
                        if (!delim.equals(
                                NormalCommand.RESULT_DELIMITER.value())) {
                            NormalInputParser.displayCommandMessage(
                                    Message.ERROR_BAD_RESULT_SYNTAX,
                                    NormalCommand.SPECIAL_COMMAND_HAVE,
                                    MessageArgument.fromInteger(this.counter),
                                    NormalInputParser.RESULT_FAILURE);
                            return;
                        }
                        this.parseCommandResultOuter(haveFail);
                    } else {
                        final String haveSuccess = this.getCommandAtOffset(1)
                                .substring(1);
                        final String delim = this.getCommandAtOffset(1)
                                .substring(0, 1);
                        if (!delim.equals(
                                NormalCommand.RESULT_DELIMITER.value())) {
                            NormalInputParser.displayCommandMessage(
                                    Message.ERROR_BAD_RESULT_SYNTAX,
                                    NormalCommand.SPECIAL_COMMAND_HAVE,
                                    MessageArgument.fromInteger(this.counter),
                                    NormalInputParser.RESULT_SUCCESS);
                            return;
                        }
                        this.parseCommandResultOuter(haveSuccess);
                    }
                } else if (warpKillTest.equalsIgnoreCase(
                        NormalCommand.SPECIAL_COMMAND_DROP.value())) {
                    String dropArg = ""; //$NON-NLS-1$
                    if (specialCmd.length() >= 5) {
                        dropArg = specialCmd.substring(5);
                    } else {
                        // No arguments specified
                        NormalInputParser.displayNoArgsForSpecialCommandMessage(
                                specialCmd);
                        return;
                    }
                    final String[] dropArgSplit = dropArg
                            .split(NormalCommand.HAVE_MULTIPLE.value());
                    boolean dropIt = true;
                    for (final String element : dropArgSplit) {
                        dropIt = dropIt && this.inventory.contains(element);
                    }
                    if (!dropIt) {
                        final String dropFail = this.getCommandAtOffset(2);
                        NormalInputParser.displayAdventureMessage(dropFail);
                    } else {
                        final String dropSuccess = this.getCommandAtOffset(1);
                        NormalInputParser.displayAdventureMessage(dropSuccess);
                        for (final String element : dropArgSplit) {
                            if (this.inventory.contains(element)) {
                                this.inventory.remove(element);
                            }
                        }
                    }
                } else if (warpKillTest.equalsIgnoreCase(
                        NormalCommand.SPECIAL_COMMAND_LOSE.value())) {
                    String loseArg = ""; //$NON-NLS-1$
                    if (specialCmd.length() >= 5) {
                        loseArg = specialCmd.substring(5);
                    } else {
                        // No arguments specified
                        NormalInputParser.displayNoArgsForSpecialCommandMessage(
                                specialCmd);
                        return;
                    }
                    final String[] loseArgSplit = loseArg
                            .split(NormalCommand.HAVE_MULTIPLE.value());
                    boolean loseIt = true;
                    for (final String element : loseArgSplit) {
                        loseIt = loseIt && this.inventory.contains(element);
                    }
                    if (loseIt) {
                        for (final String element : loseArgSplit) {
                            if (this.inventory.contains(element)) {
                                this.inventory.remove(element);
                            }
                        }
                    }
                } else {
                    // Found unknown special command
                    this.displayUnknownSpecialCommandMessage(specialCmd);
                }
            } else {
                // Text to output
                NormalInputParser.displayAdventureMessage(res);
            }
        } catch (final RuntimeException re) {
            // Parse error
            NormalInputParser.displayParsingErrorMessage(res);
        }
    }

    private void parseOmniCommand(final String cmd, final int cmdLen) {
        try {
            final String omni = cmd.substring(0, cmdLen);
            final String cmdArg = cmd.substring(cmdLen + 1);
            if (omni.equalsIgnoreCase(
                    NormalCommand.OMNI_COMMAND_SHOW.value())) {
                if (cmdArg.equalsIgnoreCase(
                        NormalCommand.OMNI_SHOW_ARG_ITEMS.value())
                        || cmdArg.equalsIgnoreCase(
                                NormalCommand.OMNI_SHOW_ARG_INVENTORY
                                        .value())) {
                    if (this.inventory.isEmpty()) {
                        NormalInputParser.game
                                .showMessage(Message.YOU_HAVE_NOTHING);
                    } else {
                        NormalInputParser.game.showMessage(Message.YOU_HAVE);
                        for (final String s : this.inventory) {
                            NormalInputParser.displayAdventureMessage(s);
                        }
                    }
                } else if (cmdArg.equalsIgnoreCase(
                        NormalCommand.OMNI_SHOW_ARG_STATE.value())
                        || cmdArg.equalsIgnoreCase(
                                NormalCommand.OMNI_SHOW_ARG_FORM.value())) {
                    if (this.inventory.isEmpty()) {
                        NormalInputParser.game
                                .showMessage(Message.YOU_HAVE_NOTHING);
                    } else {
                        NormalInputParser.game.showMessage(Message.YOU_HAVE);
                        for (final String s : this.inventory) {
                            final String form = this.objectState.get(s);
                            if (form != null) {
                                NormalInputParser
                                        .displayStatelessItemMessage(s);
                            } else {
                                NormalInputParser.displayStatefulItemMessage(s,
                                        form);
                            }
                        }
                    }
                } else if (cmdArg.equalsIgnoreCase(
                        NormalCommand.OMNI_SHOW_ARG_QUESTS.value())) {
                    if (this.questState.isEmpty()) {
                        NormalInputParser.game
                                .showMessage(Message.YOU_HAVE_NO_QUESTS);
                    } else {
                        NormalInputParser.game
                                .showMessage(Message.YOU_HAVE_QUESTS);
                        final Enumeration<String> quests = this.questState
                                .keys();
                        while (quests.hasMoreElements()) {
                            final String quest = quests.nextElement();
                            final boolean done = this.questState.get(quest);
                            if (done) {
                                NormalInputParser
                                        .displayQuestCompleteMessage(quest);
                            } else {
                                NormalInputParser
                                        .displayQuestInProgressMessage(quest);
                            }
                        }
                    }
                } else if (cmdArg.equalsIgnoreCase(
                        NormalCommand.OMNI_SHOW_ARG_QUEST_DETAILS.value())) {
                    if (this.questProgress.isEmpty()) {
                        NormalInputParser.game
                                .showMessage(Message.YOU_HAVE_NO_QUESTS);
                    } else {
                        NormalInputParser.game
                                .showMessage(Message.YOU_HAVE_QUESTS);
                        final Enumeration<String> quests = this.questProgress
                                .keys();
                        while (quests.hasMoreElements()) {
                            final String quest = quests.nextElement();
                            final int progress = this.questProgress.get(quest);
                            final String questPercentFirst = Integer
                                    .toString(progress / 100);
                            String questPercentSecond = Integer
                                    .toString(progress % 100);
                            if (progress % 100 < 10) {
                                questPercentSecond = "0" + questPercentSecond; //$NON-NLS-1$
                            }
                            final String questPercent = questPercentFirst + "." //$NON-NLS-1$
                                    + questPercentSecond + "%"; //$NON-NLS-1$
                            NormalInputParser.displayQuestProgressDetailMessage(
                                    quest, questPercent);
                        }
                    }
                } else {
                    // Found unknown argument for show omni-command
                    NormalInputParser.displayCommandMessage(
                            Message.ERROR_INVALID_ARGUMENTS_SUB,
                            NormalCommand.OMNI_COMMAND_SHOW,
                            NormalInputParser.messageArgFromString(cmdArg),
                            MessageArgument.fromInteger(this.counter));
                }
            }
        } catch (final RuntimeException re) {
            // Parse error
            NormalInputParser.displayParsingErrorMessage(cmd);
        }
    }

    private String getCommandAtOffset(final int offset) {
        return this.advData.get(this.counter + this.subCounter + offset);
    }

    private boolean warpToRoom(final String roomName) {
        for (int z = 0; z < this.advData.size(); z++) {
            final String data = this.advData.get(z);
            if (data.length() > 0) {
                final String data1 = data.substring(0, 1);
                if (data1.equals(NormalCommand.ROOM_DELIMITER.value())) {
                    final String data2 = data.substring(1, data.length() - 1);
                    if (data2.equals(roomName)) {
                        final String roomDesc = this.advData.get(z + 1);
                        this.currentRoomDescription = roomDesc;
                        NormalInputParser.displayAdventureMessage(roomDesc);
                        this.counter = z + 2;
                        this.subCounter = 0;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private String substituteSynonyms(final String in) {
        if (this.synonymList != null) {
            final String inFix = in.toLowerCase();
            final String[] inSplit = inFix.split(" "); //$NON-NLS-1$
            for (int z = 0; z < inSplit.length; z++) {
                for (final ArrayList<String> element : this.synonymList) {
                    final String key = element.get(0);
                    for (final String element2 : element) {
                        if (inSplit[z].equals(element2)) {
                            inSplit[z] = key;
                        }
                    }
                }
            }
            // Build out string
            final StringBuilder out = new StringBuilder();
            for (int z = 0; z < inSplit.length; z++) {
                out.append(inSplit[z]);
                if (z != inSplit.length - 1) {
                    out.append(" "); //$NON-NLS-1$
                }
            }
            return out.toString();
        }
        return in;
    }

    private void displayUnknownSpecialCommandMessage(final String cmd) {
        NormalInputParser.game.showMessage(Message.UNKNOWN_SPECIAL_COMMAND,
                NormalInputParser.messageArgFromString(cmd),
                MessageArgument.fromInteger(this.counter));
    }

    private static void displayQuestCompleteMessage(final String questName) {
        NormalInputParser.game.showMessage(Message.QUEST_STATUS_COMPLETE,
                NormalInputParser.messageArgFromString(questName));
    }

    private static void displayQuestInProgressMessage(final String questName) {
        NormalInputParser.game.showMessage(Message.QUEST_STATUS_IN_PROGRESS,
                NormalInputParser.messageArgFromString(questName));
    }

    private static void displayQuestProgressDetailMessage(
            final String questName, final String progress) {
        NormalInputParser.game.showMessage(Message.QUEST_STATUS_PROGRESS_DETAIL,
                NormalInputParser.messageArgFromString(questName),
                NormalInputParser.messageArgFromString(progress));
    }

    private static void displayUnknownCommandMessage(final String cmd) {
        NormalInputParser.game.showMessage(Message.UNKNOWN_COMMAND,
                NormalInputParser.messageArgFromString(cmd));
    }

    private static void displayMalformedSpecialCommandMessage(
            final String cmd) {
        NormalInputParser.game.showMessage(Message.MALFORMED_COMMAND,
                NormalInputParser.messageArgFromString(cmd));
    }

    private static void displayNoArgsForSpecialCommandMessage(
            final String cmd) {
        NormalInputParser.game.showMessage(Message.ERROR_NO_ARGS,
                NormalInputParser.messageArgFromString(cmd));
    }

    private static void displayParsingErrorMessage(final String cmd) {
        NormalInputParser.game.showMessage(Message.PARSE_FAILURE,
                NormalInputParser.messageArgFromString(cmd));
    }

    @Override
    public ArrayList<String> loadState(final XDataReader xdr)
            throws IOException {
        // Current room description
        this.currentRoomDescription = xdr.readString();
        // Counters
        this.counter = xdr.readInt();
        this.subCounter = xdr.readInt();
        // Adventure data
        this.advData = new ArrayList<>();
        final int advDataSize = xdr.readInt();
        for (int a = 0; a < advDataSize; a++) {
            this.advData.add(xdr.readString());
        }
        // Synonym list
        final int synonymListSize = xdr.readInt();
        if (synonymListSize > 0) {
            this.synonymList = new ArrayList<>();
            for (int s = 0; s < synonymListSize; s++) {
                final int innerSize = xdr.readInt();
                final ArrayList<String> innerList = new ArrayList<>();
                for (int i = 0; i < innerSize; i++) {
                    innerList.add(xdr.readString());
                }
                this.synonymList.add(innerList);
            }
        } else {
            this.synonymList = null;
        }
        // Inventory
        this.inventory.clear();
        final int inventorySize = xdr.readInt();
        for (int a = 0; a < inventorySize; a++) {
            this.inventory.add(xdr.readString());
        }
        // Grabbed already
        this.grabbedAlready.clear();
        final int grabbedAlreadySize = xdr.readInt();
        for (int a = 0; a < grabbedAlreadySize; a++) {
            this.grabbedAlready.add(xdr.readString());
        }
        // Object state
        this.objectState.clear();
        final int objectStateSize = xdr.readInt();
        for (int a = 0; a < objectStateSize; a++) {
            final String k = xdr.readString();
            final String v = xdr.readString();
            this.objectState.put(k, v);
        }
        // Quest state
        this.questState.clear();
        final int questStateSize = xdr.readInt();
        for (int a = 0; a < questStateSize; a++) {
            final String k = xdr.readString();
            final boolean v = xdr.readBoolean();
            this.questState.put(k, v);
        }
        // Quest progress
        this.questProgress.clear();
        final int questProgressSize = xdr.readInt();
        for (int a = 0; a < questProgressSize; a++) {
            final String k = xdr.readString();
            final int v = xdr.readInt();
            this.questProgress.put(k, v);
        }
        return this.advData;
    }

    @Override
    public void saveState(final XDataWriter xdw) throws IOException {
        // Current room description
        xdw.writeString(this.currentRoomDescription);
        // Counters
        xdw.writeInt(this.counter);
        xdw.writeInt(this.subCounter);
        // Adventure data
        xdw.writeInt(this.advData.size());
        for (final String s : this.advData) {
            xdw.writeString(s);
        }
        // Synonym list
        if (this.synonymList != null) {
            xdw.writeInt(this.synonymList.size());
            for (final ArrayList<String> as : this.synonymList) {
                xdw.writeInt(as.size());
                for (final String s : as) {
                    xdw.writeString(s);
                }
            }
        } else {
            xdw.writeInt(0);
        }
        // Inventory
        xdw.writeInt(this.inventory.size());
        for (final String s : this.inventory) {
            xdw.writeString(s);
        }
        // Grabbed already
        xdw.writeInt(this.grabbedAlready.size());
        for (final String s : this.grabbedAlready) {
            xdw.writeString(s);
        }
        // Object state
        xdw.writeInt(this.objectState.size());
        for (final String k : this.objectState.keySet()) {
            xdw.writeString(k);
            xdw.writeString(this.objectState.get(k));
        }
        // Quest state
        xdw.writeInt(this.questState.size());
        for (final String k : this.questState.keySet()) {
            xdw.writeString(k);
            xdw.writeBoolean(this.questState.get(k));
        }
        // Quest progress
        xdw.writeInt(this.questProgress.size());
        for (final String k : this.questProgress.keySet()) {
            xdw.writeString(k);
            xdw.writeInt(this.questProgress.get(k));
        }
    }
}
