package com.puttysoftware.tap;

import com.puttysoftware.commondialogs.CommonDialogs;
import com.puttysoftware.errorlogger.ErrorLogger;
import com.puttysoftware.tap.messages.Message;
import com.puttysoftware.tap.messages.Messages;

final class ErrorHandler implements Thread.UncaughtExceptionHandler {
    private static final String LOG_NAME = Messages
            .getMessageValue(Message.LOG_NAME);
    private static final String ERROR_MESSAGE = Messages
            .getMessageValue(Message.ERROR_MESSAGE);
    private static final String ERROR_TITLE = Messages
            .getMessageValue(Message.ERROR_TITLE);
    private static final String WARNING_MESSAGE = Messages
            .getMessageValue(Message.WARNING_MESSAGE);
    private static final String WARNING_TITLE = Messages
            .getMessageValue(Message.WARNING_TITLE);
    private final ErrorLogger logger;

    ErrorHandler() {
        this.logger = new ErrorLogger(ErrorHandler.LOG_NAME);
    }

    @Override
    public void uncaughtException(final Thread inT, final Throwable inE) {
        this.handle(inE);
    }

    void handle(final Throwable inE) {
        if (inE instanceof RuntimeException) {
            try {
                this.logWarning(inE);
            } catch (final Throwable inE2) {
                inE.addSuppressed(inE2);
                this.logWarningDirectly(inE);
            }
        } else {
            try {
                this.logError(inE);
            } catch (final Throwable inE2) {
                inE.addSuppressed(inE2);
                this.logErrorDirectly(inE);
            }
        }
    }

    void silentlyLog(final Throwable inE) {
        this.logger.logWarning(inE);
    }

    private void logError(final Throwable t) {
        CommonDialogs.showErrorDialog(ErrorHandler.ERROR_MESSAGE,
                ErrorHandler.ERROR_TITLE);
        this.logger.logError(t);
    }

    private void logErrorDirectly(final Throwable t) {
        this.logger.logError(t);
    }

    private void logWarning(final Throwable t) {
        CommonDialogs.showErrorDialog(ErrorHandler.WARNING_MESSAGE,
                ErrorHandler.WARNING_TITLE);
        this.logger.logWarning(t);
    }

    private void logWarningDirectly(final Throwable t) {
        this.logger.logWarning(t);
    }

    void handleWithMessage(final Throwable inE, final String msg) {
        if (inE instanceof RuntimeException) {
            try {
                this.logWarningWithMessage(inE, msg);
            } catch (final Throwable inE2) {
                inE.addSuppressed(inE2);
                this.logWarningDirectly(inE);
            }
        } else {
            try {
                this.logErrorWithMessage(inE, msg);
            } catch (final Throwable inE2) {
                inE.addSuppressed(inE2);
                this.logErrorDirectly(inE);
            }
        }
    }

    private void logErrorWithMessage(final Throwable t, final String msg) {
        CommonDialogs.showErrorDialog(msg, ErrorHandler.ERROR_TITLE);
        this.logger.logError(t);
    }

    private void logWarningWithMessage(final Throwable t, final String msg) {
        CommonDialogs.showErrorDialog(msg, ErrorHandler.WARNING_TITLE);
        this.logger.logWarning(t);
    }
}
