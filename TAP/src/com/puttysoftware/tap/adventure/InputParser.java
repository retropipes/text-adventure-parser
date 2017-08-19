/*  TAP: A Text Adventure Parser
Copyright (C) 2010 Eric Ahnell

Any questions should be directed to the author via email at: tap@worldwizard.net
 */
package com.puttysoftware.tap.adventure;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import com.puttysoftware.tap.Game;
import com.puttysoftware.tap.Messager;
import com.puttysoftware.tap.TAP;

class InputParser {
    // Fields
    private ArrayList<String> advData;
    private final ArrayList<String> inventory;
    private final ArrayList<String> grabbedAlready;
    private ArrayList<ArrayList<String>> synonymList;
    private final Hashtable<String, String> objectState;
    private final Hashtable<String, Boolean> questState;
    private final Hashtable<String, Integer> questProgress;
    private int counter;
    private int subCounter;

    // Constructor
    public InputParser() {
        this.inventory = new ArrayList<>();
        this.grabbedAlready = new ArrayList<>();
        this.objectState = new Hashtable<>();
        this.questState = new Hashtable<>();
        this.questProgress = new Hashtable<>();
        this.counter = 0;
        this.subCounter = 0;
    }

    // Methods
    protected void doInitial(final ArrayList<String> data) {
        final Game game = TAP.getGame();
        this.advData = data;
        final boolean initialJump1 = this.warpToRoom(Commands.ROOM_MAIN);
        if (!initialJump1) {
            Messager.showErrorMessage("The main room doesn't exist!");
            game.getAdventureManager().setLoaded(false);
            return;
        }
        final boolean synonymCommand = this
                .findRoomCommand(Commands.MAIN_COMMAND_SYNONYM_TABLE);
        if (synonymCommand) {
            final String syncmd = this.advData
                    .get(this.counter + this.subCounter);
            final String synArg = syncmd.substring(7);
            final boolean initialJump2 = this.warpToRoom(synArg);
            if (!initialJump2) {
                Messager.showErrorMessage("The synonym table doesn't exist!");
                game.getAdventureManager().setLoaded(false);
                return;
            }
            // Load synonym list
            String line = "";
            this.synonymList = new ArrayList<>();
            while (line != null) {
                line = this.advData.get(this.counter + this.subCounter);
                if (line != null) {
                    if (line.length() > 0) {
                        if (line.startsWith("[")) {
                            // Found next room
                            line = null;
                        }
                    }
                }
                if (line != null) {
                    if (line.length() > 0) {
                        final String[] splitLine = line
                                .split(Commands.SYNONYM_SEPARATOR);
                        final ArrayList<String> temp = new ArrayList<>();
                        for (final String element : splitLine) {
                            temp.add(element);
                        }
                        this.synonymList.add(temp);
                    }
                    this.subCounter++;
                }
            }
            TAP.getGame().getGUIManager().clearCommandOutput();
            // Go back
            final boolean initialJump3 = this.warpToRoom(Commands.ROOM_MAIN);
            if (!initialJump3) {
                Messager.showErrorMessage("The main room doesn't exist!");
                game.getAdventureManager().setLoaded(false);
                return;
            }
        }
        final boolean startCommand = this
                .findRoomCommand(Commands.MAIN_COMMAND_START);
        if (!startCommand) {
            Messager.showErrorMessage("No starting room is defined!");
            game.getAdventureManager().setLoaded(false);
            return;
        }
        final String cmd = this.advData.get(this.counter + this.subCounter);
        final String startArg = cmd.substring(6);
        final boolean initialJump4 = this.warpToRoom(startArg);
        if (!initialJump4) {
            Messager.showErrorMessage(
                    "The defined starting room doesn't exist!");
            game.getAdventureManager().setLoaded(false);
            return;
        }
    }

    protected void parseCommand(final String command) {
        boolean foundUnknown = false;
        String preParsedCommand = command;
        String[] commandArray = new String[] { preParsedCommand };
        commandArray = CompoundCommands.splitCompoundInput(preParsedCommand);
        for (final String element : commandArray) {
            preParsedCommand = element;
            // Strip input of needless words
            preParsedCommand = InputStripper.stripInput(preParsedCommand);
            // Substitute synonyms
            preParsedCommand = this.substituteSynonyms(preParsedCommand);
            if (InputParser.isOmniCommand(preParsedCommand)) {
                // Found omni command
                final int cmdLen = InputParser
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
            InputParser.displayUnknownCommandMessage(command);
        }
    }

    private boolean findRoomCommand(final String cmdName) {
        for (int z = this.counter; z < this.advData.size(); z++) {
            final String data = this.advData.get(z);
            if (data.length() > 0) {
                final String check = data.substring(0, 1);
                if (check.equals(Commands.ROOM_DELIMITER)) {
                    return false;
                }
                final String[] dataSplit = data
                        .split(Commands.RESULT_DELIMITER);
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
            if (cmdName.substring(0, 4)
                    .equalsIgnoreCase(Commands.OMNI_COMMAND_SHOW)) {
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
                .equalsIgnoreCase(Commands.OMNI_COMMAND_SHOW)) {
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
        final int cmdPos = raw.indexOf(Commands.RESULT_DELIMITER);
        final String preres = raw.substring(cmdPos + 1);
        String res;
        final String[] resarr = preres.split(Commands.CHAIN_COMMAND_DELIM);
        for (final String element : resarr) {
            res = element;
            this.parseCommandResultInner(res);
        }
    }

    private void parseCommandResultInner(final String res) {
        try {
            final String specialCheck = res.substring(0, 1);
            if (specialCheck.equals(Commands.SPECIAL_DELIMITER)) {
                // Special command
                final String specialCmd = res.substring(1, res.length() - 1);
                final String terminator = res.substring(res.length() - 1);
                if (!terminator.equals(Commands.SPECIAL_END_DELIMITER)) {
                    // Malformed special command found
                    InputParser.displayMalformedSpecialCommandMessage(res);
                    return;
                }
                String warpKillTest = "";
                String stateQuestTest = "";
                String specialCmdTest8 = "";
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
                        Commands.SPECIAL_COMMAND_ALTER_STATE)) {
                    // Process alter state command
                    String alterArg = "";
                    if (specialCmd.length() >= 6) {
                        alterArg = specialCmd.substring(6);
                    } else {
                        // No arguments specified
                        InputParser.displayNoArgsForSpecialCommandMessage(
                                specialCmd);
                        return;
                    }
                    final String[] alterArgs = alterArg.split(" ");
                    if (alterArgs.length % 2 == 0) {
                        for (int x = 0; x < alterArgs.length; x += 2) {
                            final String alterObjName = alterArgs[x];
                            final String alterObjState = alterArgs[x + 1];
                            this.objectState.put(alterObjName, alterObjState);
                        }
                    } else {
                        // Invalid alter arguments found
                        Messager.showErrorMessage(
                                "Invalid alter state arguments found!");
                    }
                } else if (stateQuestTest.equalsIgnoreCase(
                        Commands.SPECIAL_COMMAND_CHECK_STATE)) {
                    // Process check state command
                    String checkArg = "";
                    if (specialCmd.length() >= 6) {
                        checkArg = specialCmd.substring(6);
                    } else {
                        // No arguments specified
                        InputParser.displayNoArgsForSpecialCommandMessage(
                                specialCmd);
                        return;
                    }
                    final String[] checkArgs = checkArg.split(" ");
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
                            if (!delim.equals(Commands.RESULT_DELIMITER)) {
                                Messager.showErrorMessage(
                                        "Missing = before failure result for check command!");
                                return;
                            }
                            this.parseCommandResultOuter(checkFail);
                        } else {
                            final String checkSuccess = this
                                    .getCommandAtOffset(1).substring(1);
                            final String delim = this.getCommandAtOffset(1)
                                    .substring(0, 1);
                            if (!delim.equals(Commands.RESULT_DELIMITER)) {
                                Messager.showErrorMessage(
                                        "Missing = before success result for check command!");
                                return;
                            }
                            this.parseCommandResultOuter(checkSuccess);
                        }
                    } else {
                        // Invalid check arguments found
                        Messager.showErrorMessage(
                                "Invalid check state arguments found!");
                    }
                } else if (stateQuestTest
                        .equalsIgnoreCase(Commands.SPECIAL_COMMAND_QUEST)) {
                    // Process quest command
                    String questArg = "";
                    if (specialCmd.length() >= 6) {
                        questArg = specialCmd.substring(6);
                    } else {
                        // No arguments specified
                        InputParser.displayNoArgsForSpecialCommandMessage(
                                specialCmd);
                        return;
                    }
                    final String[] questArgs = questArg.split(" ");
                    if (questArgs.length == 2) {
                        final String questSubtype = questArgs[0];
                        final String questName = questArgs[1];
                        if (questSubtype.equalsIgnoreCase(
                                Commands.SPECIAL_QUEST_ARG_BEGIN)) {
                            if (!this.questState.containsKey(questName)) {
                                this.questState.put(questName, Boolean.FALSE);
                                this.questProgress.put(questName,
                                        Integer.valueOf(0));
                            } else {
                                // Invalid quest state
                                Messager.showErrorMessage(
                                        "Cannot start a finished or started quest!");
                            }
                        } else if (questSubtype.equalsIgnoreCase(
                                Commands.SPECIAL_QUEST_ARG_FINISH)) {
                            if (this.questState.containsKey(questName)) {
                                this.questState.put(questName, Boolean.TRUE);
                                this.questProgress.put(questName,
                                        Integer.valueOf(10000));
                            } else {
                                // Invalid quest state
                                Messager.showErrorMessage(
                                        "Cannot finish an unstarted quest!");
                            }
                        } else if (questSubtype.equalsIgnoreCase(
                                Commands.SPECIAL_QUEST_ARG_STATUS)) {
                            if (this.questState.containsKey(questName)) {
                                if (this.questState.get(questName)
                                        .booleanValue()) {
                                    // Quest done
                                    final String questDone = this
                                            .getCommandAtOffset(3).substring(1);
                                    final String delim = this
                                            .getCommandAtOffset(3)
                                            .substring(0, 1);
                                    if (!delim.equals(
                                            Commands.RESULT_DELIMITER)) {
                                        Messager.showErrorMessage(
                                                "Missing = before done result for quest status command!");
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
                                            Commands.RESULT_DELIMITER)) {
                                        Messager.showErrorMessage(
                                                "Missing = before started result for quest status command!");
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
                                if (!delim.equals(Commands.RESULT_DELIMITER)) {
                                    Messager.showErrorMessage(
                                            "Missing = before not started result for quest status command!");
                                    return;
                                }
                                this.parseCommandResultOuter(questNotStarted);
                            }
                        } else {
                            // Found unknown arguments for quest command
                            Messager.showErrorMessage(
                                    "Unknown arguments found for quest command: "
                                            + questSubtype);
                        }
                    } else if (questArgs.length == 3) {
                        final String questSubtype = questArgs[0];
                        final String questName = questArgs[1];
                        final int questValue = Integer.parseInt(questArgs[2]);
                        if (questSubtype.equalsIgnoreCase(
                                Commands.SPECIAL_QUEST_ARG_ADD)) {
                            if (this.questProgress.containsKey(questName)
                                    && !this.questState.get(questName)
                                            .booleanValue()) {
                                final int newValue = this.questProgress
                                        .get(questName).intValue() + questValue;
                                this.questProgress.put(questName,
                                        Integer.valueOf(newValue));
                            } else {
                                // Invalid quest state
                                Messager.showErrorMessage(
                                        "Cannot add progress to an unstarted or finished quest!");
                            }
                        } else if (questSubtype.equalsIgnoreCase(
                                Commands.SPECIAL_QUEST_ARG_SET)) {
                            if (this.questProgress.containsKey(questName)
                                    && !this.questState.get(questName)
                                            .booleanValue()) {
                                this.questProgress.put(questName,
                                        Integer.valueOf(questValue));
                            } else {
                                // Invalid quest state
                                Messager.showErrorMessage(
                                        "Cannot set progress for an unstarted or finished quest!");
                            }
                        } else if (questSubtype.equalsIgnoreCase(
                                Commands.SPECIAL_QUEST_ARG_GET)) {
                            if (this.questProgress.containsKey(questName)
                                    && !this.questState.get(questName)
                                            .booleanValue()) {
                                final int currentValue = this.questProgress
                                        .get(questName).intValue();
                                if (currentValue > questValue) {
                                    final String questGreater = this
                                            .getCommandAtOffset(3).substring(1);
                                    final String delim = this
                                            .getCommandAtOffset(3)
                                            .substring(0, 1);
                                    if (!delim.equals(
                                            Commands.RESULT_DELIMITER)) {
                                        Messager.showErrorMessage(
                                                "Missing = before greater than result for quest get command!");
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
                                            Commands.RESULT_DELIMITER)) {
                                        Messager.showErrorMessage(
                                                "Missing = before less than result for quest get command!");
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
                                            Commands.RESULT_DELIMITER)) {
                                        Messager.showErrorMessage(
                                                "Missing = before equal result for quest get command!");
                                        return;
                                    }
                                    this.parseCommandResultOuter(questEqual);
                                }
                            } else {
                                // Invalid quest state
                                Messager.showErrorMessage(
                                        "Cannot get progress for an unstarted or finished quest!");
                            }
                        } else {
                            // Found unknown arguments for quest command
                            Messager.showErrorMessage(
                                    "Unknown arguments found for quest command: "
                                            + questSubtype);
                        }
                    } else if (questArgs.length == 4) {
                        final String questSubtype = questArgs[0];
                        final String questName = questArgs[1];
                        final int questValue1 = Integer.parseInt(questArgs[2]);
                        final int questValue2 = Integer.parseInt(questArgs[3]);
                        if (questSubtype.equalsIgnoreCase(
                                Commands.SPECIAL_QUEST_ARG_TEST)) {
                            if (this.questProgress.containsKey(questName)
                                    && !this.questState.get(questName)
                                            .booleanValue()) {
                                final int currentValue = this.questProgress
                                        .get(questName).intValue();
                                if (currentValue > questValue1
                                        && currentValue < questValue2) {
                                    final String questBetween = this
                                            .getCommandAtOffset(1).substring(1);
                                    final String delim = this
                                            .getCommandAtOffset(1)
                                            .substring(0, 1);
                                    if (!delim.equals(
                                            Commands.RESULT_DELIMITER)) {
                                        Messager.showErrorMessage(
                                                "Missing = before inside result for quest get command!");
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
                                            Commands.RESULT_DELIMITER)) {
                                        Messager.showErrorMessage(
                                                "Missing = before outside result for quest test command!");
                                        return;
                                    }
                                    this.parseCommandResultOuter(questOutside);
                                }
                            } else {
                                // Invalid quest state
                                Messager.showErrorMessage(
                                        "Cannot get progress for an unstarted or finished quest!");
                            }
                        } else {
                            // Found unknown arguments for quest command
                            Messager.showErrorMessage(
                                    "Unknown arguments found for quest command: "
                                            + questSubtype);
                        }
                    } else {
                        // Found unknown arguments for quest command
                        Messager.showErrorMessage(
                                "Unknown arguments found for quest command");
                    }
                } else if (warpKillTest
                        .equalsIgnoreCase(Commands.SPECIAL_COMMAND_KILL)) {
                    // Process kill command
                    String killArg = "";
                    if (specialCmd.length() >= 5) {
                        killArg = specialCmd.substring(5);
                    } else {
                        // No arguments specified
                        InputParser.displayNoArgsForSpecialCommandMessage(
                                specialCmd);
                        return;
                    }
                    Messager.showMessage(killArg);
                    TAP.getGame().getAdventureManager().setLoaded(false);
                } else if (warpKillTest
                        .equalsIgnoreCase(Commands.SPECIAL_COMMAND_WARP)) {
                    // Process warp command
                    String warpArg = "";
                    if (specialCmd.length() >= 5) {
                        warpArg = specialCmd.substring(5);
                    } else {
                        // No arguments specified
                        InputParser.displayNoArgsForSpecialCommandMessage(
                                specialCmd);
                        return;
                    }
                    if (!this.warpToRoom(warpArg)) {
                        Messager.showErrorMessage(
                                "Warp attempted to nonexistent room: "
                                        + warpArg);
                    }
                } else if (specialCmdTest8
                        .equalsIgnoreCase(Commands.SPECIAL_COMMAND_ONCE_GRAB)) {
                    String onceGrabArg = "";
                    if (specialCmd.length() >= 9) {
                        onceGrabArg = specialCmd.substring(9);
                    } else {
                        // No arguments specified
                        InputParser.displayNoArgsForSpecialCommandMessage(
                                specialCmd);
                        return;
                    }
                    final String[] onceGrabArgSplit = onceGrabArg
                            .split(Commands.HAVE_MULTIPLE);
                    boolean onceGrabbedAny = false;
                    for (int x = 0; x < onceGrabArgSplit.length; x++) {
                        if (!this.inventory.contains(onceGrabArgSplit[x])
                                && !this.grabbedAlready
                                        .contains(onceGrabArgSplit[x])) {
                            onceGrabbedAny = true;
                            this.inventory.add(onceGrabArgSplit[x]);
                            this.grabbedAlready.add(onceGrabArgSplit[x]);
                        }
                    }
                    if (onceGrabbedAny) {
                        final String onceGrabSuccess = this
                                .getCommandAtOffset(1);
                        Messager.showMessage(onceGrabSuccess);
                    } else {
                        final String onceGrabFail = this.getCommandAtOffset(2);
                        Messager.showMessage(onceGrabFail);
                    }
                } else if (specialCmdTest8
                        .equalsIgnoreCase(Commands.SPECIAL_COMMAND_ONCE_GAIN)) {
                    String onceGainArg = "";
                    if (specialCmd.length() >= 9) {
                        onceGainArg = specialCmd.substring(9);
                    } else {
                        // No arguments specified
                        InputParser.displayNoArgsForSpecialCommandMessage(
                                specialCmd);
                        return;
                    }
                    final String[] onceGainArgSplit = onceGainArg
                            .split(Commands.HAVE_MULTIPLE);
                    for (int x = 0; x < onceGainArgSplit.length; x++) {
                        if (!this.inventory.contains(onceGainArgSplit[x])
                                && !this.grabbedAlready
                                        .contains(onceGainArgSplit[x])) {
                            this.inventory.add(onceGainArgSplit[x]);
                            this.grabbedAlready.add(onceGainArgSplit[x]);
                        }
                    }
                } else if (warpKillTest
                        .equalsIgnoreCase(Commands.SPECIAL_COMMAND_GAIN)) {
                    String gainArg = "";
                    if (specialCmd.length() >= 5) {
                        gainArg = specialCmd.substring(5);
                    } else {
                        // No arguments specified
                        InputParser.displayNoArgsForSpecialCommandMessage(
                                specialCmd);
                        return;
                    }
                    final String[] gainArgSplit = gainArg
                            .split(Commands.HAVE_MULTIPLE);
                    for (int x = 0; x < gainArgSplit.length; x++) {
                        if (!this.inventory.contains(gainArgSplit[x])) {
                            this.inventory.add(gainArgSplit[x]);
                            if (!this.grabbedAlready
                                    .contains(gainArgSplit[x])) {
                                this.grabbedAlready.add(gainArgSplit[x]);
                            }
                        }
                    }
                } else if (warpKillTest
                        .equalsIgnoreCase(Commands.SPECIAL_COMMAND_GRAB)) {
                    String grabArg = "";
                    if (specialCmd.length() >= 5) {
                        grabArg = specialCmd.substring(5);
                    } else {
                        // No arguments specified
                        InputParser.displayNoArgsForSpecialCommandMessage(
                                specialCmd);
                        return;
                    }
                    final String[] grabArgSplit = grabArg
                            .split(Commands.HAVE_MULTIPLE);
                    boolean grabbedAny = false;
                    for (int x = 0; x < grabArgSplit.length; x++) {
                        if (!this.inventory.contains(grabArgSplit[x])) {
                            grabbedAny = true;
                            this.inventory.add(grabArgSplit[x]);
                            if (!this.grabbedAlready
                                    .contains(grabArgSplit[x])) {
                                this.grabbedAlready.add(grabArgSplit[x]);
                            }
                        }
                    }
                    if (grabbedAny) {
                        final String grabSuccess = this.getCommandAtOffset(1);
                        Messager.showMessage(grabSuccess);
                    } else {
                        final String grabFail = this.getCommandAtOffset(2);
                        Messager.showMessage(grabFail);
                    }
                } else if (warpKillTest
                        .equalsIgnoreCase(Commands.SPECIAL_COMMAND_HAVE)) {
                    String haveArg = "";
                    if (specialCmd.length() >= 5) {
                        haveArg = specialCmd.substring(5);
                    } else {
                        // No arguments specified
                        InputParser.displayNoArgsForSpecialCommandMessage(
                                specialCmd);
                        return;
                    }
                    final String[] haveArgSplit = haveArg
                            .split(Commands.HAVE_MULTIPLE);
                    boolean haveIt = true;
                    for (final String element : haveArgSplit) {
                        haveIt = haveIt && this.inventory.contains(element);
                    }
                    if (!haveIt) {
                        final String haveFail = this.getCommandAtOffset(2)
                                .substring(1);
                        final String delim = this.getCommandAtOffset(2)
                                .substring(0, 1);
                        if (!delim.equals(Commands.RESULT_DELIMITER)) {
                            Messager.showErrorMessage(
                                    "Missing = before failure result for have command!");
                            return;
                        }
                        this.parseCommandResultOuter(haveFail);
                    } else {
                        final String haveSuccess = this.getCommandAtOffset(1)
                                .substring(1);
                        final String delim = this.getCommandAtOffset(1)
                                .substring(0, 1);
                        if (!delim.equals(Commands.RESULT_DELIMITER)) {
                            Messager.showErrorMessage(
                                    "Missing = before success result for have command!");
                            return;
                        }
                        this.parseCommandResultOuter(haveSuccess);
                    }
                } else if (warpKillTest
                        .equalsIgnoreCase(Commands.SPECIAL_COMMAND_DROP)) {
                    String dropArg = "";
                    if (specialCmd.length() >= 5) {
                        dropArg = specialCmd.substring(5);
                    } else {
                        // No arguments specified
                        InputParser.displayNoArgsForSpecialCommandMessage(
                                specialCmd);
                        return;
                    }
                    final String[] dropArgSplit = dropArg
                            .split(Commands.HAVE_MULTIPLE);
                    boolean dropIt = true;
                    for (final String element : dropArgSplit) {
                        dropIt = dropIt && this.inventory.contains(element);
                    }
                    if (!dropIt) {
                        final String dropFail = this.getCommandAtOffset(2);
                        Messager.showMessage(dropFail);
                    } else {
                        final String dropSuccess = this.getCommandAtOffset(1);
                        Messager.showMessage(dropSuccess);
                        for (final String element : dropArgSplit) {
                            if (this.inventory.contains(element)) {
                                this.inventory.remove(element);
                            }
                        }
                    }
                } else if (warpKillTest
                        .equalsIgnoreCase(Commands.SPECIAL_COMMAND_LOSE)) {
                    String loseArg = "";
                    if (specialCmd.length() >= 5) {
                        loseArg = specialCmd.substring(5);
                    } else {
                        // No arguments specified
                        InputParser.displayNoArgsForSpecialCommandMessage(
                                specialCmd);
                        return;
                    }
                    final String[] loseArgSplit = loseArg
                            .split(Commands.HAVE_MULTIPLE);
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
                    Messager.showErrorMessage(
                            "Unknown special command found: " + specialCmd);
                }
            } else {
                // Text to output
                Messager.showMessage(res);
            }
        } catch (final RuntimeException re) {
            // Parse error
            InputParser.displayParsingErrorMessage(res);
        }
    }

    private void parseOmniCommand(final String cmd, final int cmdLen) {
        try {
            final String omni = cmd.substring(0, cmdLen);
            final String cmdArg = cmd.substring(cmdLen + 1);
            if (omni.equalsIgnoreCase(Commands.OMNI_COMMAND_SHOW)) {
                if (cmdArg.equalsIgnoreCase(Commands.OMNI_SHOW_ARG_ITEMS)
                        || cmdArg.equalsIgnoreCase(
                                Commands.OMNI_SHOW_ARG_INVENTORY)) {
                    if (this.inventory.isEmpty()) {
                        Messager.showMessage("You have nothing.");
                    } else {
                        Messager.showMessage("You have:");
                        for (final String s : this.inventory) {
                            Messager.showMessage(s);
                        }
                    }
                } else if (cmdArg.equalsIgnoreCase(Commands.OMNI_SHOW_ARG_STATE)
                        || cmdArg.equalsIgnoreCase(
                                Commands.OMNI_SHOW_ARG_FORM)) {
                    if (this.inventory.isEmpty()) {
                        Messager.showMessage("You have nothing.");
                    } else {
                        Messager.showMessage("You have:");
                        for (final String s : this.inventory) {
                            final String form = this.objectState.get(s);
                            String msg = s + " (no state set)";
                            if (form != null) {
                                msg = s + " (in state " + form + ")";
                            }
                            Messager.showMessage(msg);
                        }
                    }
                } else if (cmdArg
                        .equalsIgnoreCase(Commands.OMNI_SHOW_ARG_QUESTS)) {
                    if (this.questState.isEmpty()) {
                        Messager.showMessage("You have no quests.");
                    } else {
                        Messager.showMessage("You have the following quests:");
                        final Enumeration<String> quests = this.questState
                                .keys();
                        while (quests.hasMoreElements()) {
                            final String quest = quests.nextElement();
                            final boolean done = this.questState.get(quest)
                                    .booleanValue();
                            if (done) {
                                Messager.showMessage(quest + ": Completed");
                            } else {
                                Messager.showMessage(quest + ": In Progress");
                            }
                        }
                    }
                } else if (cmdArg.equalsIgnoreCase(
                        Commands.OMNI_SHOW_ARG_QUEST_DETAILS)) {
                    if (this.questProgress.isEmpty()) {
                        Messager.showMessage("You have no quests.");
                    } else {
                        Messager.showMessage("You have the following quests:");
                        final Enumeration<String> quests = this.questProgress
                                .keys();
                        while (quests.hasMoreElements()) {
                            final String quest = quests.nextElement();
                            final int progress = this.questProgress.get(quest)
                                    .intValue();
                            final String questPercentFirst = Integer
                                    .toString(progress / 100);
                            String questPercentSecond = Integer
                                    .toString(progress % 100);
                            if (progress % 100 < 10) {
                                questPercentSecond = "0" + questPercentSecond;
                            }
                            final String questPercent = questPercentFirst + "."
                                    + questPercentSecond + "%";
                            Messager.showMessage(
                                    quest + ": " + questPercent + " Completed");
                        }
                    }
                } else {
                    // Found unknown argument for show omni-command
                    Messager.showErrorMessage(
                            "Unknown argument for show command found: "
                                    + cmdArg);
                }
            }
        } catch (final RuntimeException re) {
            // Parse error
            InputParser.displayParsingErrorMessage(cmd);
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
                if (data1.equals(Commands.ROOM_DELIMITER)) {
                    final String data2 = data.substring(1, data.length() - 1);
                    if (data2.equals(roomName)) {
                        final String roomDesc = this.advData.get(z + 1);
                        Messager.showMessage(roomDesc);
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
            final String[] inSplit = inFix.split(" ");
            for (int z = 0; z < inSplit.length; z++) {
                for (int x = 0; x < this.synonymList.size(); x++) {
                    final String key = this.synonymList.get(x).get(0);
                    for (int y = 0; y < this.synonymList.get(x).size(); y++) {
                        if (inSplit[z].equals(this.synonymList.get(x).get(y))) {
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
                    out.append(" ");
                }
            }
            return out.toString();
        }
        return in;
    }

    private static void displayUnknownCommandMessage(final String cmd) {
        Messager.showMessage("Huh? I don't understand " + cmd + ".");
    }

    private static void displayMalformedSpecialCommandMessage(
            final String cmd) {
        Messager.showErrorMessage(
                "Malformed special command found: " + cmd + ".");
    }

    private static void displayNoArgsForSpecialCommandMessage(
            final String cmd) {
        Messager.showErrorMessage(
                "No arguments were specified for special command: " + cmd
                        + ", and arguments are required!");
    }

    private static void displayParsingErrorMessage(final String cmd) {
        Messager.showErrorMessage(
                "A parsing error occurred while trying to parse " + cmd + ".");
    }
}
