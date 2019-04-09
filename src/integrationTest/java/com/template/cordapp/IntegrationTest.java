package com.template.cordapp;

import com.google.common.collect.ImmutableList;
import net.corda.core.concurrent.CordaFuture;
import net.corda.core.identity.CordaX500Name;
import net.corda.testing.core.TestIdentity;
import net.corda.testing.driver.DriverParameters;
import net.corda.testing.driver.NodeHandle;
import net.corda.testing.driver.NodeParameters;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static net.corda.testing.driver.Driver.driver;
import static org.junit.Assert.assertEquals;


public class IntegrationTest {


        private final TestIdentity nodeAName = new TestIdentity(new CordaX500Name("nodeAName", "", "GB"));
        private final TestIdentity nodeBName = new TestIdentity(new CordaX500Name("nodeBName", "", "US"));

        @Test
        public void nodeTest() {
            driver(new DriverParameters().withExtraCordappPackagesToScan(Arrays.asList("com.template.cordapp.contract")).withIsDebug(true).withStartNodesInProcess(true),dsl -> {
                // Start a pair of nodes and wait for them both to be ready.
                List<CordaFuture<NodeHandle>> handleFutures = ImmutableList.of(
                        dsl.startNode(new NodeParameters().withProvidedName(nodeAName.getName())),
                        dsl.startNode(new NodeParameters().withProvidedName(nodeBName.getName()))
                );

                try {
                    NodeHandle partyAHandle = handleFutures.get(0).get();
                    NodeHandle partyBHandle = handleFutures.get(1).get();

                    // From each node, make an RPC call to retrieve another node's name from the network map, to verify that the
                    // nodes have started and can communicate.

                    // This is a very basic test: in practice tests would be starting flows, and verifying the states in the vault
                    // and other important metrics to ensure that your CorDapp is working as intended.
                    assertEquals(partyAHandle.getRpc().wellKnownPartyFromX500Name(nodeBName.getName()).getName(), nodeBName.getName());
                    assertEquals(partyBHandle.getRpc().wellKnownPartyFromX500Name(nodeAName.getName()).getName(), nodeAName.getName());
                } catch (Exception e) {
                    throw new RuntimeException("Caught exception during test: ", e);
                }

                return null;
            });
        }
    }

