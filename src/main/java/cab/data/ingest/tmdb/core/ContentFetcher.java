package cab.data.ingest.tmdb.core;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.client.RestTemplate;

import cab.data.ingest.pojo.Credits;
import cab.data.ingest.pojo.CreditsOwner;
import cab.data.ingest.pojo.DiscoverResult;
import cab.data.ingest.pojo.Keyword;
import cab.data.ingest.pojo.Movie;
import cab.data.ingest.pojo.SourceProvider;
import cab.data.ingest.tmdb.core.ControlFlowManager.PHASE;
import cab.data.ingest.tmdb.core.ControlFlowManager.PHASE_STAGE;
import cab.data.ingest.tmdb.core.ControlFlowManager.PhaseStageListener;
import cab.data.ingest.tmdb.core.TMDBRiver.DISCOVERY_TYPE;

public class ContentFetcher implements Runnable, PhaseStageListener {

	private final String fetchUrl = Constants.basePath
			+ "/{type}/{id}?api_key={api_key}";

	private final String additionalDataFetchUrl = Constants.basePath
			+ "/{type}/{id}/{data_type}?api_key={api_key}";

	private DISCOVERY_TYPE discoveryType;

	private RestTemplate template = APIUtil.initTemplate();


	private Log logger = LogFactory.getLog(getClass());

	private String apiKey;

	private boolean running = true;

	private ControlFlowManager controlFlowManager;

	private boolean can_stop = false;

	private Integer bulkAPiThreshold;;

	@SuppressWarnings("unchecked")
	private void fetchContents(List<DiscoverResult> results) {
		logger.info(String.format("Fetching %s - %s",
				discoveryType.contentPath, results));
		for (DiscoverResult result : results) {
			Object sourceProvider = template.getForObject(fetchUrl,
					discoveryType.sourceClass, discoveryType.getContentPath(),
					result.getId().toString(), apiKey);
			Credits credits = template.getForObject(additionalDataFetchUrl,
					Credits.class, discoveryType.getContentPath(), result
							.getId().toString(), "credits", apiKey);

			((CreditsOwner) sourceProvider).setCredit(credits);

			if (discoveryType.equals(DISCOVERY_TYPE.MOVIE)) {
				Keyword keyword = template.getForObject(additionalDataFetchUrl,
						Keyword.class, discoveryType.getContentPath(), result
								.getId().toString(), "keywords", apiKey);
				((Movie) sourceProvider).setKeywords(keyword.getKeyWords());

			}

		}
	}

	@Override
	public void run() {
		while (running) {
			try {
				List<DiscoverResult> results = controlFlowManager
						.getPageResultQueue().take();

				fetchContents(results);

				// check that we are done with all pages
				if (controlFlowManager.getPageResultQueue().isEmpty()
						&& can_stop) {
					running = false;
				}

			} catch (InterruptedException e) {
				logger.error("Failed to take next from queue", e);
				running = false;
			}
		}
		// done scrape/ Flush any documents that are queues for indexing

		logger.info("Done scrapping all contents. Signalling complete phase");
		controlFlowManager.notifyPhase(PHASE.CONTENT_SCRAPE,
				PHASE_STAGE.COMPLETE);
	}

	public ContentFetcher(String riverName, DISCOVERY_TYPE discoveryType,
			 String apiKey,
			ControlFlowManager controlFlowManager, Integer bulkAPIThreshold) {
		super();
		this.discoveryType = discoveryType;
		this.apiKey = apiKey;
		this.controlFlowManager = controlFlowManager;
		this.controlFlowManager.registerPhaseStageListener(PHASE.PAGE_SCRAPE,
				PHASE_STAGE.COMPLETE, this);
		this.bulkAPiThreshold = bulkAPIThreshold;
	}

	@Override
	public void onPhase(PHASE phase, PHASE_STAGE stage) {
		if (phase.equals(PHASE.PAGE_SCRAPE)
				&& stage.equals(PHASE_STAGE.COMPLETE)) {
			logger.info("Recieved complete page scrape signal");
			this.can_stop = true;
			// this is the case where we page scrape and content scrape finish
			// at the same time
			if (controlFlowManager.getPageResultQueue().isEmpty()) {
				logger.info("Stopping content scrape. All pages are fetched");
				running = false;
			}
		}
	}

}
