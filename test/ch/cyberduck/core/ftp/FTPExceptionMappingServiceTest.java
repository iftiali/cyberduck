package ch.cyberduck.core.ftp;

import ch.cyberduck.core.AbstractTestCase;
import ch.cyberduck.core.exception.ConnectionCanceledException;
import ch.cyberduck.core.exception.LoginFailureException;
import ch.cyberduck.core.exception.NotfoundException;
import ch.cyberduck.core.exception.QuotaException;

import org.junit.Test;

import java.net.SocketException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @version $Id$
 */
public class FTPExceptionMappingServiceTest extends AbstractTestCase {

    @Test
    public void testMap() throws Exception {
        assertEquals(ConnectionCanceledException.class,
                new FTPExceptionMappingService().map(new SocketException("Software caused connection abort")).getClass());
        assertEquals(ConnectionCanceledException.class,
                new FTPExceptionMappingService().map(new SocketException("Socket closed")).getClass());
    }

    @Test
    public void testQuota() throws Exception {
        assertTrue(new FTPExceptionMappingService().map(new FTPException(452, "")) instanceof QuotaException);
    }

    @Test
    public void testLogin() throws Exception {
        assertTrue(new FTPExceptionMappingService().map(new FTPException(530, "")) instanceof LoginFailureException);
    }

    @Test
    public void testFile() throws Exception {
        assertTrue(new FTPExceptionMappingService().map(new FTPException(550, "")) instanceof NotfoundException);
    }

    @Test
    public void testTrim() throws Exception {
        assertEquals("m. Please contact your web hosting service provider for assistance.", new FTPExceptionMappingService().map(new FTPException(500, "m\n")).getDetail());
    }
}
