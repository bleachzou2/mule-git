/*
 * Copyright (c) 2006 Your Corporation. All Rights Reserved.
 */
package com.icegreen.greenmail.test;

import com.icegreen.greenmail.util.ServerSetup;
import com.icegreen.greenmail.util.Servers;
import com.icegreen.greenmail.util.Retriever;
import com.icegreen.greenmail.util.ServerSetupTest;
import junit.framework.TestCase;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayOutputStream;

/**
 * @author Wael Chatila
 * @version $Id: Pop3ServerTest.java 2860 2006-08-30 15:28:53Z holger $
 * @since Jan 28, 2006
 */
public class Pop3ServerTest extends TestCase {

    Servers servers;

    protected void tearDown() throws Exception {
        try {
            servers.stop();
        } catch (NullPointerException ignored) {
            //empty
        }
        super.tearDown();
    }

    public void testRetreive() throws Exception {
        servers = new Servers(ServerSetupTest.SMTP_POP3);
        assertNotNull(servers.getPop3());
        servers.start();
        final String subject = servers.util().random();
        final String body = servers.util().random() + "\r\n" + servers.util().random() + "\r\n" + servers.util().random();
        String to = "test@localhost.com";
        servers.util().sendTextEmailTest(to, "from@localhost.com", subject, body);
        servers.waitForIncomingEmail(5000, 1);

        Retriever retriever = new Retriever(servers.getPop3());
        Message[] messages = retriever.getMessages(to);
        assertEquals(1, messages.length);
        assertEquals(subject, messages[0].getSubject());
        assertEquals(body, servers.util().getBody(messages[0]).trim());
    }

    public void testPop3sReceive() throws Throwable {
        servers = new Servers(new ServerSetup[]{ServerSetupTest.SMTPS, ServerSetupTest.POP3S});
        assertNull(servers.getPop3());
        assertNotNull(servers.getPop3s());
        servers.start();
        final String subject = servers.util().random();
        final String body = servers.util().random();
        String to = "test@localhost.com";
        servers.util().sendTextEmailSecureTest(to, "from@localhost.com", subject, body);
        servers.waitForIncomingEmail(5000, 1);

        Retriever retriever = new Retriever(servers.getPop3s());
        Message[] messages = retriever.getMessages(to);
        assertEquals(1, messages.length);
        assertEquals(subject, messages[0].getSubject());
        assertEquals(body, servers.util().getBody(messages[0]).trim());
    }

    public void testRetreiveWithNonDefaultPassword() throws Exception {
        servers = new Servers(ServerSetupTest.SMTP_POP3);
        assertNotNull(servers.getPop3());
        final String to = "test@localhost.com";
        final String password = "donotharmanddontrecipricateharm";
        servers.setUser(to, password);
        servers.start();
        final String subject = servers.util().random();
        final String body = servers.util().random();
        servers.util().sendTextEmailTest(to, "from@localhost.com", subject, body);
        servers.waitForIncomingEmail(5000, 1);

        Retriever retriever = new Retriever(servers.getPop3());
        boolean login_failed = false;
        try {
            Message[] messages = retriever.getMessages(to, "wrongpassword");
        } catch (Throwable e) {
            login_failed = true;
        }
        assertTrue(login_failed);

        Message[] messages = retriever.getMessages(to, password);
        assertEquals(1, messages.length);
        assertEquals(subject, messages[0].getSubject());
        assertEquals(body, servers.util().getBody(messages[0]).trim());
    }

    public void testRetriveMultipart() throws Exception {
        servers = new Servers(ServerSetupTest.SMTP_POP3);
        assertNotNull(servers.getPop3());
        servers.start();

        String subject = servers.util().random();
        String body = servers.util().random();
        String to = "test@localhost.com";
        servers.util().sendAttachmentEmail(to, "from@localhost.com", subject, body, new byte[]{0, 1, 2}, "image/gif", "testimage_filename", "testimage_description", ServerSetupTest.SMTP);
        servers.waitForIncomingEmail(5000, 1);

        Retriever retriever = new Retriever(servers.getPop3());
        Message[] messages = retriever.getMessages(to);

        Object o = messages[0].getContent();
        assertTrue(o instanceof MimeMultipart);
        MimeMultipart mp = (MimeMultipart) o;
        assertEquals(2, mp.getCount());
        BodyPart bp;
        bp = mp.getBodyPart(0);
        assertEquals(body, servers.util().getBody(bp).trim());

        bp = mp.getBodyPart(1);
        assertEquals("AAEC", servers.util().getBody(bp).trim());

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        servers.util().copyStream(bp.getInputStream(), bout);
        byte[] gif = bout.toByteArray();
        for (int i = 0; i < gif.length; i++) {
            assertEquals(i, gif[i]);
        }
    }
}
