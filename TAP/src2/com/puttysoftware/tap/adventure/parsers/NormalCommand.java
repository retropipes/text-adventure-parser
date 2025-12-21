package com.puttysoftware.tap.adventure.parsers;

enum NormalCommand implements ValueBearer {
    ROOM_MAIN("main"), //$NON-NLS-1$
    MAIN_COMMAND_START("start"), //$NON-NLS-1$
    MAIN_COMMAND_SYNONYM_TABLE("syntab"), //$NON-NLS-1$
    ROOM_DELIMITER("["), //$NON-NLS-1$
    RESULT_DELIMITER("="), //$NON-NLS-1$
    SPECIAL_DELIMITER("["), //$NON-NLS-1$
    SPECIAL_END_DELIMITER("]"), //$NON-NLS-1$
    OMNI_COMMAND_SHOW("show"), //$NON-NLS-1$
    OMNI_SHOW_ARG_ITEMS("items"), //$NON-NLS-1$
    OMNI_SHOW_ARG_INVENTORY("inventory"), //$NON-NLS-1$
    OMNI_SHOW_ARG_STATE("state"), //$NON-NLS-1$
    OMNI_SHOW_ARG_FORM("form"), //$NON-NLS-1$
    OMNI_SHOW_ARG_QUESTS("quests"), //$NON-NLS-1$
    OMNI_SHOW_ARG_QUEST_DETAILS("quest details"), //$NON-NLS-1$
    CHAIN_COMMAND_DELIM(" && "), //$NON-NLS-1$
    SPECIAL_COMMAND_WARP("warp"), //$NON-NLS-1$
    SPECIAL_COMMAND_KILL("kill"), //$NON-NLS-1$
    SPECIAL_COMMAND_GRAB("grab"), //$NON-NLS-1$
    SPECIAL_COMMAND_HAVE("have"), //$NON-NLS-1$
    SPECIAL_COMMAND_DROP("drop"), //$NON-NLS-1$
    SPECIAL_COMMAND_LOSE("lose"), //$NON-NLS-1$
    SPECIAL_COMMAND_ONCE_GRAB("oncegrab"), //$NON-NLS-1$
    SPECIAL_COMMAND_GAIN("gain"), //$NON-NLS-1$
    SPECIAL_COMMAND_ONCE_GAIN("oncegain"), //$NON-NLS-1$
    SPECIAL_COMMAND_ALTER_STATE("alter"), //$NON-NLS-1$
    SPECIAL_COMMAND_CHECK_STATE("check"), //$NON-NLS-1$
    SPECIAL_COMMAND_QUEST("quest"), //$NON-NLS-1$
    SPECIAL_QUEST_ARG_BEGIN("begin"), //$NON-NLS-1$
    SPECIAL_QUEST_ARG_STATUS("status"), //$NON-NLS-1$
    SPECIAL_QUEST_ARG_FINISH("finish"), //$NON-NLS-1$
    SPECIAL_QUEST_ARG_ADD("add"), //$NON-NLS-1$
    SPECIAL_QUEST_ARG_SET("set"), //$NON-NLS-1$
    SPECIAL_QUEST_ARG_GET("get"), //$NON-NLS-1$
    SPECIAL_QUEST_ARG_TEST("test"), //$NON-NLS-1$
    HAVE_MULTIPLE(" & "), //$NON-NLS-1$
    SYNONYM_SEPARATOR(", "); //$NON-NLS-1$

    private final String cmdVal;

    NormalCommand(final String value) {
        this.cmdVal = value;
    }

    @Override
    public String value() {
        return this.cmdVal;
    }
}
