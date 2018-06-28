/*******************************************************************************
 * Copyright (c) 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.security.fat.common.expectations;

import java.util.Arrays;
import java.util.List;

import com.ibm.websphere.simplicity.log.Log;
import com.ibm.ws.security.fat.common.Constants;

import componenttest.topology.impl.LibertyServer;

public class ServerMessageExpectation extends Expectation {

    public static final String DEFAULT_FAILURE_MSG = "Did not find the expected error message [%s] in the server log.";

    private LibertyServer server = null;

    public ServerMessageExpectation(String testAction, LibertyServer server, String searchFor) {
        this(testAction, server, searchFor, String.format(DEFAULT_FAILURE_MSG, searchFor));
    }

    public ServerMessageExpectation(String testAction, LibertyServer server, String searchFor, String failureMsg) {
        super(testAction, Constants.MESSAGES_LOG, Constants.STRING_MATCHES, searchFor, failureMsg);
        this.server = server;
    }

    @Override
    protected void validate(Object contentToValidate) throws Exception {
        addMessageToIgnoredErrors();
        if (!isMessageLogged()) {
            throw new Exception(failureMsg);
        }
    }

    void addMessageToIgnoredErrors() {
        List<String> msgs = Arrays.asList(validationValue);
        server.addIgnoredErrors(msgs);
    }

    boolean isMessageLogged() {
        String errorMsg = server.waitForStringInLogUsingMark(validationValue, 100);
        Log.info(getClass(), "isMessageLogged", "Found message: " + errorMsg);
        return (errorMsg != null);
    }

}