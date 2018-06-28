/*******************************************************************************
 * Copyright (c) 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.fat.hpel.tests;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import com.ibm.websphere.simplicity.ApplicationServer;
import com.ibm.websphere.simplicity.RemoteFile;
import com.ibm.ws.fat.VerboseTestCase;
import com.ibm.ws.fat.hpel.setup.HpelSetup;
import com.ibm.ws.fat.ras.util.CommonTasks;

/**
 * Test scenario: Set the server.xml hideMessages logging attribute to hide a message and start the server.
 * The hidden message should not be displayed in the console.log, but visible in the log/trace data repository.
 */

public class HPELHideMessagesTest extends VerboseTestCase {

    private final static String loggerName = HPELHideMessagesTest.class.getName();
    private final static Logger logger = Logger.getLogger(loggerName);
    private final static String CONSOLE_LOG = "logs/console.log";

    private ApplicationServer appServ = null;

    RemoteFile backup = null;

    public HPELHideMessagesTest(String name) {
        super(name);
    }

    @Override
    public void setUp() throws Exception {
        // Call super.SetUp() cause we still want it's setup as well
        super.setUp();
        appServ = HpelSetup.getServerUnderTest();
        // Confirm HPEL is enabled
        if (!CommonTasks.isHpelEnabled(appServ)) {
            // HPEL is not enabled.
            this.logStep("HPEL is not enabled on " + appServ.getName() + ", attempting to enable.");
            CommonTasks.setHpelEnabled(appServ, true);
            // Restart now to complete switching to HPEL
            appServ.stop();
            appServ.start();
            this.logStepCompleted();
        }

        this.logStep("Configuring server for test case.");
        backup = new RemoteFile(appServ.getBackend().getMachine(), new File(appServ.getBackend().getServerRoot(), "server-backup.xml").getPath());
        if (!backup.exists()) {
            backup.copyFromSource(appServ.getBackend().getServerConfigurationFile());
        }
        // Setting the server.xml with the hideMessages logging attribute
        appServ.getBackend().updateServerConfiguration(new File(appServ.getBackend().pathToAutoFVTTestFiles, "server-HPELHideMessagesTest.xml"));
        if (appServ.getBackend().isStarted()) {
            // Restart server
            appServ.restart();
        }

        this.logStepCompleted();

    }

    /**
     * Test that server hides the messages in the console.log, when binary logging is enabled
     * Set the hideMessages logging attribute to hide message that start with "CWWKF0012I" in server.xml, start the server and check the console.log file.
     **/
    public void testHPELHideMessageLoggingAttribute() throws Exception {

        this.logVerificationPoint(" Verifying the console.log should not have the message containing CWWKF0012I messageID. ");
        logger.info(" The console.log should not have the message containing CWWKF0012I messageID");
        checkIfMessageInConsoleLogExists();
        this.logVerificationPassed();

    }

    //Check if the hidden message does not show up in the console.log
    protected void checkIfMessageInConsoleLogExists() throws Exception {
        List<String> lines = appServ.getBackend().findStringsInFileInLibertyServerRoot("CWWKF0012I:", CONSOLE_LOG);
        assertFalse(" Message CWWKF0012I did appear in console.log  ", lines.size() > 0);
    }

    @Override
    public void tearDown() throws Exception {
        // Restore values we saw before changing them in setUp()
        this.logStep("Resetting configuration to pre test values.");
        if (backup != null && backup.exists()) {
            appServ.getBackend().getServerConfigurationFile().copyFromSource(backup);
        }
        this.logStepCompleted();

        // call the super
        super.tearDown();
    }

}