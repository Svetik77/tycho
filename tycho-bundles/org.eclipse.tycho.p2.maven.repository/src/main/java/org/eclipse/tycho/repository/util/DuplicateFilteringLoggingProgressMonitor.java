/*******************************************************************************
 * Copyright (c) 2013 SAP SE and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    SAP SE - initial API and implementation
 *******************************************************************************/
package org.eclipse.tycho.repository.util;

import java.util.regex.Pattern;

import org.eclipse.tycho.core.shared.MavenLogger;

/**
 * {@link LoggingProgressMonitor} which removes duplicated and other obsolete log output produced by
 * p2/ECF when downloading artifacts.
 * 
 * <p>
 * Instances of this class are not thread-safe.
 * </p>
 */
public final class DuplicateFilteringLoggingProgressMonitor extends LoggingProgressMonitor {

    private final Pattern PROGRESS_WITH_UNKNOWN_SPEED = Pattern.compile("\\(.* at 0B/s\\)");

    private static final String NON_MATCHING_LINE = "";
    private String lastLoggedFile = NON_MATCHING_LINE;
    private boolean lastLoggedFileFiltered = false;

    public DuplicateFilteringLoggingProgressMonitor(MavenLogger logger) {
        super(logger);
    }

    @Override
    protected boolean suppressOutputOf(String text) {
        if (text.equals("1 operation remaining.")) {
            // filter out
            return true;
        }
        boolean isUnneededLine = checkIfDuplicateOfLastOutput(text);
        return isUnneededLine;
    }

    private boolean checkIfDuplicateOfLastOutput(String message) {
        // special handling for "Fetching %file from %url (%bytes [of %total ]at %speed)" lines
        int startOfByteProgress = message.indexOf('(');
        if (startOfByteProgress > 0) {
            if (startOfByteProgress == lastLoggedFile.length() && message.startsWith(lastLoggedFile)) {
                if (!lastLoggedFileFiltered
                        && PROGRESS_WITH_UNKNOWN_SPEED.matcher(message.substring(startOfByteProgress)).matches()) {
                    /*
                     * p2 (or more precisely: ECF) always seems to print at least two "Fetching ..."
                     * lines for the same file. Remove the second one, which doesn't really add
                     * anything. Later lines may be interesting because they include the download
                     * speed.
                     */
                    lastLoggedFileFiltered = true;
                    return true;
                }
            } else {
                lastLoggedFile = message.substring(0, startOfByteProgress);
                lastLoggedFileFiltered = false;
            }
        } else {
            lastLoggedFile = NON_MATCHING_LINE;
            lastLoggedFileFiltered = false;
        }
        return false;
    }

}
