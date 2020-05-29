package org.apache.ignite.ignite_core;

import org.apache.ignite.configuration.IgniteConfiguration;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCompute;
import org.apache.ignite.Ignition;
import org.apache.ignite.compute.ComputeJob;
import org.apache.ignite.compute.ComputeJobAdapter;
import org.apache.ignite.compute.ComputeJobResult;
import org.apache.ignite.compute.ComputeTaskSplitAdapter;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.jetbrains.annotations.Nullable;

public class Demo {
	public static void main(String[] args) {
        IgniteConfiguration cfg = new IgniteConfiguration();

        cfg.setClientMode(true);

        cfg.setPeerClassLoadingEnabled(true);

        TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
        ipFinder.setAddresses(Collections.singletonList("127.0.0.1:47500..47509"));
        cfg.setDiscoverySpi(new TcpDiscoverySpi().setIpFinder(ipFinder));

        Ignite ignite = Ignition.start(cfg);
        
             
        int cnt = ignite.compute().execute(SplitExampleCharacterCountTask.class, "Hello Ignite Enabled World!");

        System.out.println();
        System.out.println(">>> Total number of characters in the phrase is '" + cnt + "'.");
        System.out.println(">>> Check all nodes for output (this node is also part of the cluster).");
	}
    private static class SplitExampleCharacterCountTask extends ComputeTaskSplitAdapter<String, Integer> {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		/**
         * Splits the received string to words, creates a child job for each word, and sends
         * these jobs to other nodes for processing. Each such job simply prints out the received word.
         *
         * @param clusterSize Number of available cluster nodes. Note that returned number of
         *      jobs can be less, equal or greater than this cluster size.
         * @param arg Task execution argument. Can be {@code null}.
         * @return The list of child jobs.
         */
        @Override protected Collection<? extends ComputeJob> split(int clusterSize, String arg) {
            Collection<ComputeJob> jobs = new LinkedList<>();

            for (final String word : arg.split(" ")) {
                jobs.add(new ComputeJobAdapter() {
                    /**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Nullable @Override public Object execute() {
                        System.out.println();
                        System.out.println(">>> Printing '" + word + "' on this node from ignite job.");

                        // Return number of letters in the word.
                        return word.length();
                    }
                });
            }

            return jobs;
        }
        @Nullable @Override public Integer reduce(List<ComputeJobResult> results) {
            int sum = 0;

            for (ComputeJobResult res : results)
                sum += res.<Integer>getData();

            return sum;
        }
    }
}
