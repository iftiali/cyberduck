package ch.cyberduck.core.openstack;

import ch.cyberduck.core.Credentials;
import ch.cyberduck.core.DisabledCancelCallback;
import ch.cyberduck.core.DisabledHostKeyCallback;
import ch.cyberduck.core.DisabledLoginCallback;
import ch.cyberduck.core.DisabledPasswordStore;
import ch.cyberduck.core.Host;
import ch.cyberduck.core.Local;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.Profile;
import ch.cyberduck.core.ProtocolFactory;
import ch.cyberduck.core.features.Location;
import ch.cyberduck.core.proxy.Proxy;
import ch.cyberduck.core.serializer.impl.dd.ProfilePlistReader;
import ch.cyberduck.test.IntegrationTest;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;

import ch.iterate.openstack.swift.model.Region;

import static org.junit.Assert.*;

@Category(IntegrationTest.class)
public class SwiftRegionServiceTest {

    @Test
    public void testLookupDefault() throws Exception {
        final Host host = new Host(new SwiftProtocol(), "identity.api.rackspacecloud.com", new Credentials(
            System.getProperties().getProperty("rackspace.key"), System.getProperties().getProperty("rackspace.secret")
        ));
        final SwiftSession session = new SwiftSession(host);
        session.open(Proxy.DIRECT, new DisabledHostKeyCallback(), new DisabledLoginCallback());
        session.login(Proxy.DIRECT, new DisabledPasswordStore(), new DisabledLoginCallback(), new DisabledCancelCallback());
        final Region lookup = new SwiftRegionService(session).lookup(Location.unknown);
        assertTrue(lookup.isDefault());
        assertEquals("DFW", lookup.getRegionId());
        assertNotNull(lookup.getCDNManagementUrl());
        assertNotNull(lookup.getStorageUrl());
    }

    @Test
    public void testFindDefaultLocationInBookmark() throws Exception {
        final ProtocolFactory factory = new ProtocolFactory(new HashSet<>(Collections.singleton(new SwiftProtocol())));
        final Profile profile = new ProfilePlistReader(factory).read(
            new Local("../profiles/Rackspace US (IAD).cyberduckprofile"));
        final SwiftSession session = new SwiftSession(
            new Host(profile, "identity.api.rackspacecloud.com",
                new Credentials(
                    System.getProperties().getProperty("rackspace.key"), System.getProperties().getProperty("rackspace.secret")
                ))) {

        };
        assertEquals("IAD", session.getHost().getRegion());
        final Region location = new SwiftRegionService(session).lookup(new Path("/test.cyberduck.ch",
            EnumSet.of(Path.Type.directory, Path.Type.volume)));
        assertNotNull(location);
        assertEquals("IAD", location.getRegionId());
    }
}
