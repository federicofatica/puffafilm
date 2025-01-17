package org.steamshaper.ai.runtime.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.steamshaper.ai.puffafilm.etl.KnowledgeExtractor;
import org.steamshaper.ai.puffafilm.etl.entity.EUserTaggedMovie;
import org.steamshaper.ai.puffafilm.etl.loader.relationship.simple.UTMMovieLoader;

public class GSRUTMTagLoader extends AGRelationLoader {

	private final Logger log = Logger.getLogger("service.impl.TagUTMRelation");
	
	private int errorCount = 0;
	
	private String getUTMid(EUserTaggedMovie source){
		return "u"+source.getUserID()+"t"+source.getTagID()+"m"+source.getMovieID();
	}
	
	@Override
	protected void loadRelation(KnowledgeExtractor ke) {
		if (shouldRun()) {
			UTMMovieLoader loader = UTMMovieLoader.getInstance();
			List<EUserTaggedMovie> datList = ke.geteUserTaggedMovieList();
			// Transaction tx = Help.me.toStartTransaction();
			log.info("Ready to wire [" + datList.size() + "] relationship ...");
			long start = System.currentTimeMillis();
			for (EUserTaggedMovie eUTM : datList) {
				try {
					loader.findRelationShipMemberThenWireIt(eUTM.getMovieID(),
							getUTMid(eUTM));
				} catch (InstantiationException e) {
					this.errorCount++;
					log.error("Ops... UTMid [" + getUTMid(eUTM)
							+ "] MovieId[" + eUTM.getMovieID() + "]");
				} catch (IllegalAccessException e) {
					this.errorCount++;
					log.error("Ops... UTMid [" + getUTMid(eUTM)
							+ "] MovieId[" + eUTM.getMovieID() + "]");
				}
			}
			if (errorCount > 0) {
				log.error("[" + errorCount + "] occurred");
			}
			// tx.success();
			// tx.finish();
			log.info("Wired [" + datList.size() + "] relationship in ["
					+ (System.currentTimeMillis() - start) + "] with error/s ["
					+ errorCount + "]");
		} else {
			log.debug("Db Load SKIPPED!");
		}
	}

	private Condition shouldRun;

	public boolean shouldRun() {
		if (shouldRun != null) {
			return shouldRun.isConditionTrue();
		}
		return true;
	}

	public void setShouldRun(Condition shouldRun) {
		this.shouldRun = shouldRun;
	}

}
