package org.gcube.application.aquamaps.ecomodelling.generators.test;

import org.gcube.application.aquamaps.ecomodelling.generators.configuration.EngineConfiguration;
import org.gcube.application.aquamaps.ecomodelling.generators.processing.DistributionGenerator;

public class TestHspecbyHspen {
	/**
	 * example of parallel processing on a single machine
	 * the procedure will generate a new table for a distribution on suitable species
	 *  
	 */
	
	public static void main(String[] args) throws Exception{

		EngineConfiguration e = new EngineConfiguration();
		//path to the cfg directory containing default parameters
		e.setConfigPath("./cfg/");
		e.setDatabaseUserName("utente");
		e.setDatabasePassword("d4science");
		e.setDatabaseURL("jdbc:postgresql://dbtest.research-infrastructures.eu/aquamapsorgupdated");
		e.setHcafTable("hcaf_d");
		e.setHspenTable("hspen_validation");
		e.setDistributionTable("hspec_validation");
		e.setNativeGeneration(false);
		e.setType2050(false);
		e.setNumberOfThreads(16);
		e.setCreateTable(true);
		
		DistributionGenerator dg = new DistributionGenerator(e);
		dg.generateHSPEC();
	}
}
