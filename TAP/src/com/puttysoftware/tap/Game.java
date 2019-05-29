/*  TAP: A Text Adventure Parser
Copyright (C) 2010 Eric Ahnell

Any questions should be directed to the author via email at: tap@worldwizard.net
 */
package com.puttysoftware.tap;

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

public class Game {
 // Fields
    private final JFrame guiFrame;
    private final Container guiPane, buttonPane;
    final JTextField commandInput;
    private final JTextArea commandOutput;
    private final CommandInputHandler ciHandler;
    private final int maxLineCount;
    private final JButton open, openExample;
    private boolean cleared = true;

    // Constructors
    public Game() {
        this.ciHandler = new CommandInputHandler();
        this.guiFrame = new JFrame("Text Adventure Parser (TAP)");
        this.guiPane = this.guiFrame.getContentPane();
        this.guiFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.guiPane.setLayout(new BorderLayout());
        this.commandInput = new JTextField(60);
        this.commandInput.addActionListener(this.ciHandler);
        this.commandOutput = new JTextArea("Welcome to TAP!", 30, 60);
        this.commandOutput.setEditable(false);
        this.commandOutput.setLineWrap(true);
        this.commandOutput.setWrapStyleWord(true);
        this.commandOutput.setFocusable(false);
        this.buttonPane = new Container();
        this.buttonPane.setLayout(new FlowLayout());
        this.open = new JButton("Open Adventure...");
        this.open.addActionListener(e -> {
            TAP.getAdventureManager().loadAdventure();
        });
        this.openExample = new JButton("Open Example Adventure...");
        this.openExample.addActionListener(e -> {
            TAP.getAdventureManager().loadExampleAdventure();
        });
        this.buttonPane.add(this.open);
        this.buttonPane.add(this.openExample);
        this.guiPane.add(this.buttonPane, BorderLayout.NORTH);
        this.guiPane.add(this.commandOutput, BorderLayout.CENTER);
        this.guiPane.add(this.commandInput, BorderLayout.SOUTH);
        this.guiFrame.setResizable(false);
        this.guiFrame.pack();
        this.maxLineCount = this.commandOutput.getRows();
    }

    // Methods
    public JFrame getOutputFrame() {
        if (this.guiFrame.isVisible()) {
            return this.guiFrame;
        } else {
            return null;
        }
    }

    protected void showGUI() {
        this.guiFrame.setVisible(true);
    }

    void processCommandInput(final String cmd) {
        if (TAP.getAdventureManager().getLoaded()) {
            TAP.getAdventureManager().getAdventure()
                    .parseCommand(cmd);
        } else {
            this.updateCommandOutput("No adventure opened!");
        }
    }

    public void clearCommandOutput() {
        this.commandOutput.setText("");
        this.cleared = true;
    }

    protected void updateCommandOutput(final String out) {
        if (this.cleared) {
            this.cleared = false;
            this.commandOutput.setText(out);
        } else {
            this.commandOutput.append("\n" + out);
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
                game.commandInput.setText("");
                // Process command
                game.processCommandInput(cmd);
            } catch (final RuntimeException re) {
                TAP.getErrorLogger().logError(re);
            }
        }
    }
}
