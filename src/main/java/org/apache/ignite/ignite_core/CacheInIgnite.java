package org.apache.ignite.ignite_core;

import java.util.Collections;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteException;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteCluster;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cluster.ClusterGroup;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.lang.IgniteRunnable;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;


public class CacheInIgnite {
    public static void main(String[] args) throws IgniteException {
        // Preparing IgniteConfiguration using Java APIs
        IgniteConfiguration cfg = new IgniteConfiguration();

        // The node will be started as a client node.
        cfg.setClientMode(true);

        // Classes of custom Java logic will be transferred over the wire from this app.
        cfg.setPeerClassLoadingEnabled(true);

        CacheConfiguration cacheCfg = new CacheConfiguration("myCache");

        cacheCfg.setCacheMode(CacheMode.PARTITIONED);

        cfg.setCacheConfiguration(cacheCfg);

        // Setting up an IP Finder to ensure the client can locate the servers.
        TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
        ipFinder.setAddresses(Collections.singletonList("127.0.0.1:47500..47509"));
        cfg.setDiscoverySpi(new TcpDiscoverySpi().setIpFinder(ipFinder));

        // Starting the node
        Ignite ignite = Ignition.start(cfg);

        // Create an IgniteCache and put some values in it.
        IgniteCache<Integer, String> cache = ignite.getOrCreateCache("myCache");
        for(int i=0;i<100;i++) {
           	cache.put(i,Integer.toString(i));
        }
        //for(int i=0;i<3;i++) {
        ClusterGroup cluster =  ignite.cluster().forRemotes().forRandom();

        // Say hello to all nodes in the cluster, including local node.
        sayHello(ignite, cluster,cache);
       // }
        ignite.compute(ignite.cluster().forServers()).broadcast(()->{
     	   for(int i=0;i<100;i++) {
    		   System.out.println(cache.get(i));
    	   }
        });
        ignite.close();
    }  
   private static void sayHello(Ignite ignite, final ClusterGroup grp, IgniteCache<Integer, String> cache) throws IgniteException {
       // Print out hello message on all cluster nodes.
       ignite.compute(grp).broadcast(
           () -> {
        	   System.out.println(">>> Hello Node: " + grp.ignite().cluster().localNode().id());
        	   for(int i=0;i<100;i++) {
        		   System.out.println(cache.get(i));
        	   }
           });
   }
}
