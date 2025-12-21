package com.puttysoftware.commondialogs;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

public class CommonDialogs {
    // Fields
    private static final Icon ICON = null;
    private static String DEFAULT_TITLE = null;
    public static final int YES_OPTION = JOptionPane.YES_OPTION;
    public static final int NO_OPTION = JOptionPane.NO_OPTION;
    public static final int CANCEL_OPTION = JOptionPane.CANCEL_OPTION;

    // Constructor
    private CommonDialogs() {
        // Do nothing
    }

    // Methods
    /**
     * Displays a dialog.
     *
     * @param msg
     *            The dialog message.
     */
    public static void showDialog(final String msg) {
        JOptionPane.showMessageDialog(null, msg, CommonDialogs.DEFAULT_TITLE,
                JOptionPane.INFORMATION_MESSAGE, CommonDialogs.ICON);
    }

    /**
     * Displays a dialog with a title.
     *
     * @param msg
     *              The dialog message.
     * @param title
     *              The dialog title.
     */
    public static void showTitledDialog(final String msg, final String title) {
        JOptionPane.showMessageDialog(null, msg, title,
                JOptionPane.INFORMATION_MESSAGE, CommonDialogs.ICON);
    }

    /**
     * Displays an error dialog with a title.
     *
     * @param msg
     *              The dialog message.
     * @param title
     *              The dialog title.
     */
    public static void showErrorDialog(final String msg, final String title) {
        JOptionPane.showMessageDialog(null, msg, title,
                JOptionPane.ERROR_MESSAGE, CommonDialogs.ICON);
    }

    /**
     * Displays an input dialog, allowing the user to pick from a list.
     *
     * @param prompt
     *                      The input prompt.
     * @param title
     *                      The dialog title.
     * @param choices
     *                      The list of choices.
     * @param defaultChoice
     *                      The default choice, which should be one of the list
     *                      entries.
     * @return The choice picked
     */
    public static String showInputDialog(final String prompt,
            final String title, final Object[] choices,
            final String defaultChoice) {
        return (String) JOptionPane.showInputDialog(null, prompt, title,
                JOptionPane.QUESTION_MESSAGE, CommonDialogs.ICON, choices,
                defaultChoice);
    }

    /**
     * Displays a text input dialog, allowing the user to enter a value.
     *
     * @param prompt
     *               The input prompt.
     * @param title
     *               The dialog title.
     * @return The value the user input.
     */
    public static String showTextInputDialog(final String prompt,
            final String title) {
        return (String) JOptionPane.showInputDialog(null, prompt, title,
                JOptionPane.QUESTION_MESSAGE, CommonDialogs.ICON, null, null);
    }

    /**
     * Displays a password input dialog, allowing the user to enter a value.
     *
     * @param prompt
     *               The input prompt.
     * @param title
     *               The dialog title.
     * @return The value the user input.
     */
    public static char[] showPasswordInputDialog(final String prompt,
            final String title, final int length) {
        final JPanel panel = new JPanel();
        final JLabel label = new JLabel(prompt);
        final JPasswordField pass = new JPasswordField(length);
        panel.add(label);
        panel.add(pass);
        final int option = JOptionPane.showOptionDialog(null, panel, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                CommonDialogs.ICON, null, null);
        if (option == JOptionPane.OK_OPTION) {
            return pass.getPassword();
        }
        return null;
    }

    /**
     * Displays a text input dialog, allowing the user to enter a value.
     *
     * @param prompt
     *               The input prompt.
     * @param title
     *               The dialog title.
     * @return The value the user input.
     */
    public static String showTextInputDialogWithDefault(final String prompt,
            final String title, final String defaultValue) {
        return (String) JOptionPane.showInputDialog(null, prompt, title,
                JOptionPane.QUESTION_MESSAGE, CommonDialogs.ICON, null,
                defaultValue);
    }

    /**
     * Displays a yes/no confirm dialog.
     *
     * @param prompt
     *               The confirmation prompt.
     * @param title
     *               The dialog title.
     * @return A JOptionPane constant specifying what the user clicked.
     */
    public static int showConfirmDialog(final String prompt,
            final String title) {
        return JOptionPane.showConfirmDialog(null, prompt, title,
                JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE,
                CommonDialogs.ICON);
    }

    /**
     * Displays a yes/no/cancel confirm dialog.
     *
     * @param prompt
     *               The confirmation prompt.
     * @param title
     *               The dialog title.
     * @return A JOptionPane constant specifying what the user clicked.
     */
    public static int showYNCConfirmDialog(final String prompt,
            final String title) {
        return JOptionPane.showConfirmDialog(null, prompt, title,
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE, CommonDialogs.ICON);
    }

    public static int showCustomDialog(final String prompt, final String title,
            final String[] buttonNames, final String defaultButton) {
        return JOptionPane.showOptionDialog(null, prompt, title,
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE, CommonDialogs.ICON,
                buttonNames, defaultButton);
    }

    /**
     * Sets the default title for dialogs.
     *
     * @param title
     *              The default title
     */
    public static void setDefaultTitle(final String title) {
        CommonDialogs.DEFAULT_TITLE = title;
    }
}
