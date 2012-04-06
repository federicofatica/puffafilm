package org.steamshaper.ai.runtime.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.steamshaper.ai.puffafilm.etl.KnowledgeExtractor;
import org.steamshaper.ai.puffafilm.etl.entity.EUserRatedMovie;
import org.steamshaper.ai.puffafilm.etl.loader.relationship.HasRatedLoader;

public class GRelationHasRatedLoader extends AGRelationLoader {

	private final Logger log = Logger
			.getLogger("service.impl.HasRatedRelation");

	@Override
	protected void loadRelation(KnowledgeExtractor ke) {
		if (shouldRun()) {
			HasRatedLoader loader = HasRatedLoader.getInstance();
			List<EUserRatedMovie> datList = ke.geteUserRatedMovieList();
			// Transaction tx = Help.me.toStartTransaction();
			int errorCount = 0;
			log.info("Ready to wire [" + datList.size() + "] relationship ...");
			long start = System.currentTimeMillis();
			for (EUserRatedMovie instance : datList) {
				try {
					loader.findRelationShipMemberThenWireIt(
							instance.getUserID(), instance.getMovieID(),
							instance);
				} catch (InstantiationException e) {
					errorCount++;
					log.error("Ops... UserId [" + instance.getUserID()
							+ "] MovieId[" + instance.getMovieID() + "] dat["
							+ instance + "]");
				} catch (IllegalAccessException e) {
					errorCount++;
					log.error("Ops... UserId [" + instance.getUserID()
							+ "] MovieId[" + instance.getMovieID() + "] dat["
							+ instance + "]");
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
