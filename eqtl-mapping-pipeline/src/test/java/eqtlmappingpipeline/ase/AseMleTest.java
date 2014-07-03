/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eqtlmappingpipeline.ase;

import cern.colt.list.tint.IntArrayList;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author Patrick Deelen
 */
public class AseMleTest {

	public AseMleTest() {
	}

	@Test
	public void test() {

		final IntArrayList a1Counts = new IntArrayList();
		final IntArrayList a2Counts = new IntArrayList();
		
		int nrSamples = 100;
		for (int s = 0; s < nrSamples; s++) {
			int nrReads = 1000 + (int) (Math.random() * 1000d); //Simulate random number of reads, assume this is unrelated to tissue
			int readsWT = 0;
			int readsAlt = 0;
			for (int r = 0; r < nrReads; r++) {
				if (Math.random() < 0.6d) {
					readsWT++;
				} else {
					readsAlt++;
				}
			}
			
			a1Counts.add(readsWT);
			a2Counts.add(readsAlt);
			
		}
		
		AseMle mle = new AseMle(a1Counts, a2Counts);
		
		System.out.println(mle.getMaxLikelihood());
		System.out.println(mle.getMaxLikelihoodP());
		System.out.println(mle.getRatioD());
		System.out.println(mle.getRatioP());

	}
	
	@Test
	public void testLnBico(){
		
		assertEquals(AseMle.lnbico(1369, 689), 945.052036055064, 0.000001);
		assertEquals(AseMle.lnbico(12345, 6789), 8490.2927640914, 0.000001);
		assertEquals(AseMle.lnbico(123456, 67890), 84950.948114749, 0.000001);
		assertEquals(AseMle.lnbico(20, 10), 12.126791314602454, 0.000001);
		assertEquals(AseMle.lnbico(200, 10), 37.6501117434664266, 0.000001);
	}

	
}