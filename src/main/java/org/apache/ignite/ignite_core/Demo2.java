package org.apache.ignite.ignite_core;

import java.util.Collections;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCompute;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;

public class Demo2 {
	public static void main(String[] args) {
        IgniteConfiguration cfg = new IgniteConfiguration();

        cfg.setClientMode(true);

        cfg.setPeerClassLoadingEnabled(true);

        TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
        ipFinder.setAddresses(Collections.singletonList("127.0.0.1:47500..47509"));
        cfg.setDiscoverySpi(new TcpDiscoverySpi().setIpFinder(ipFinder));

        Ignite ignite = Ignition.start(cfg);
        
        IgniteCompute compute = ignite.compute();
        
        for (String word : "Print words on different cluster nodes".split(" "))
        	compute.run(() -> System.out.println(word));
	}
}
