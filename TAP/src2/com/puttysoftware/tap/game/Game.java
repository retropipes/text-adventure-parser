/*  TAP: A Text Adventure Parser
Copyright (C) 2010 Eric Ahnell

Any questions should be directed to the author via email at: tap@worldwizard.net
 */
package com.puttysoftware.tap.game;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import com.puttysoftware.tap.TAP;
import com.puttysoftware.tap.messages.Message;
import com.puttysoftware.tap.messages.MessageArgument;
import com.puttysoftware.tap.messages.Messages;

public class Game {
    // Fields
    private final JFrame guiFrame;
    private final Container guiPane, buttonPane;
    private final JTextField commandInput;
    private final JTextArea commandOutput;
    private final CommandInputHandler ciHandler;
    private final int maxLineCount;
    private final JButton open, openExample, close, load, save, saveAs, exit;
    private boolean cleared = true;

    // Constructors
    public Game() {
        // Create GUI
        this.guiFrame = new JFrame(Messages.getMessageValue(Message.UI_TITLE));
        this.guiPane = this.guiFrame.getContentPane();
        this.guiFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.guiPane.setLayout(new BorderLayout());
        this.commandInput = new JTextField(60);
        this.ciHandler = new CommandInputHandler();
        this.commandInput.addActionListener(this.ciHandler);
        this.commandInput.setEnabled(false);
        this.commandOutput = new JTextArea(
                Messages.getMessageValue(Game.getWelcomeMessage()), 30, 60);
        this.commandOutput.setEditable(false);
        this.commandOutput.setLineWrap(true);
        this.commandOutput.setWrapStyleWord(true);
        this.commandOutput.setFocusable(false);
        this.buttonPane = new Container();
        this.buttonPane.setLayout(new FlowLayout());
        // Create buttons
        this.open = new JButton(Messages.getMessageValue(Message.UI_OPEN));
        this.openExample = new JButton(
                Messages.getMessageValue(Message.UI_OPEN_EXAMPLE));
        this.load = new JButton(Messages.getMessageValue(Message.UI_LOAD));
        this.close = new JButton(Messages.getMessageValue(Message.UI_CLOSE));
        this.save = new JButton(Messages.getMessageValue(Message.UI_SAVE));
        this.saveAs = new JButton(Messages.getMessageValue(Message.UI_SAVE_AS));
        this.exit = new JButton(Messages.getMessageValue(Message.UI_EXIT));
        // Configure buttons
        this.open.addActionListener(e -> {
            TAP.getAdventureManager().loadAdventure();
        });
        this.openExample.addActionListener(e -> {
            TAP.getAdventureManager().loadExampleAdventure();
        });
        this.load.addActionListener(e -> {
            TAP.getAdventureManager().loadSavedAdventure();
        });
        this.close.addActionListener(e -> {
            TAP.getAdventureManager().closeAdventure();
            Game.this.showMessageAndClearOutput(Game.getWelcomeMessage());
        });
        this.close.setVisible(false);
        this.save.addActionListener(e -> {
            TAP.getAdventureManager().saveAdventure();
        });
        this.save.setVisible(false);
        this.saveAs.addActionListener(e -> {
            TAP.getAdventureManager().saveAdventureAs();
        });
        this.saveAs.setVisible(false);
        this.exit.addActionListener(e -> {
            System.exit(0);
        });
        this.buttonPane.add(this.open);
        this.buttonPane.add(this.openExample);
        this.buttonPane.add(this.load);
        this.buttonPane.add(this.close);
        this.buttonPane.add(this.save);
        this.buttonPane.add(this.saveAs);
        this.buttonPane.add(this.exit);
        this.guiPane.add(this.buttonPane, BorderLayout.NORTH);
        this.guiPane.add(this.commandOutput, BorderLayout.CENTER);
        this.guiPane.add(this.commandInput, BorderLayout.SOUTH);
        this.guiFrame.setResizable(false);
        this.guiFrame.pack();
        this.maxLineCount = this.commandOutput.getRows();
    }

    // Methods
    private static Message getWelcomeMessage() {
        if (TAP.isBigHeadModeEnabled()) {
            return Message.WELCOME_BIG_HEAD_MODE;
        } else {
            return Message.WELCOME;
        }
    }

    public JFrame getOutputFrame() {
        if (this.guiFrame.isVisible()) {
            return this.guiFrame;
        } else {
            return null;
        }
    }

    public void onOpened() {
        this.open.setVisible(false);
        this.openExample.setVisible(false);
        this.load.setVisible(false);
        this.close.setVisible(true);
        this.save.setVisible(true);
        this.saveAs.setVisible(true);
        this.commandInput.setEnabled(true);
        this.commandInput.requestFocus();
    }

    public void onClosed() {
        this.close.setVisible(false);
        this.save.setVisible(false);
        this.saveAs.setVisible(false);
        this.open.setVisible(true);
        this.openExample.setVisible(true);
        this.load.setVisible(true);
        this.commandInput.setEnabled(false);
    }

    public void showGUI() {
        this.guiFrame.setVisible(true);
    }

    private void processCommandInput(final String cmd) {
        if (TAP.getAdventureManager().isLoaded()) {
            TAP.getAdventureManager().getAdventure().parseCommand(cmd);
        } else {
            this.showMessage(Message.ERROR_NO_ADVENTURE);
        }
    }

    private void showMessageAndClearOutput(final Message msg,
            final MessageArgument... args) {
        final String out = Messages.getMessageValue(msg, args);
        this.commandOutput.setText(out);
        this.cleared = true;
    }

    public void clearOutput() {
        this.commandOutput.setText(""); //$NON-NLS-1$
        this.cleared = true;
    }

    public void showMessage(final Message msg, final MessageArgument... args) {
        final String out = Messages.getMessageValue(msg, args);
        if (this.cleared) {
            this.cleared = false;
            this.commandOutput.setText(out);
        } else {
            this.commandOutput.append("\n" + out); //$NON-NLS-1$
        }
        // Check line count
        if (this.commandOutput.getLineCount() > this.maxLineCount) {
            // Clear output
            this.commandOutput.setText(out);
        }
    }

    private class CommandInputHandler implements ActionListener {
        public CommandInputHandler() {
            // Do nothing
        }

        @Override
        public void actionPerformed(final ActionEvent arg0) {
            try {
                final Game game = Game.this;
                // Get command
                final String cmd = game.commandInput.getText();
                // Clear command input area
                game.commandInput.setText(""); //$NON-NLS-1$
                // Process command
                game.processCommandInput(cmd);
            } catch (final RuntimeException re) {
                TAP.handleException(re);
            }
        }
    }
}
