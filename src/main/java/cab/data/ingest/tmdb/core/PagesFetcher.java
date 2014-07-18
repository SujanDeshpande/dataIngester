package cab.data.ingest.tmdb.core;

import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.client.RestTemplate;

import cab.data.ingest.pojo.DiscoverResponse;
import cab.data.ingest.tmdb.core.ControlFlowManager.PHASE;
import cab.data.ingest.tmdb.core.ControlFlowManager.PHASE_STAGE;

public class PagesFetcher implements Runnable {

	private final Log logger = LogFactory.getLog(getClass());
	private Integer totalPages;

	private String fetchUrl;

	private RestTemplate template;

	private String apiKey;
	private ControlFlowManager controlFlowManager;

	public PagesFetcher(String riverName, String apiKey, Integer totalPages,
			String fetchUrl, RestTemplate template,
			ControlFlowManager controlFlowManager) {
		super();
		this.totalPages = totalPages;
		this.fetchUrl = fetchUrl;
		this.template = template;
		this.apiKey = apiKey;
		this.controlFlowManager = controlFlowManager;
	}

	@Override
	public void run() {
		loop: for (int i = 1; i <= totalPages; i++) {
			logger.info("Fetching page no - " + i);
			DiscoverResponse response = template.getForObject(fetchUrl,
					DiscoverResponse.class,
					APIUtil.getVariableVals(apiKey, i + ""));
			try {
				controlFlowManager.getPageResultQueue().offer(
						response.getResults(), 1, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				logger.error("Failed to offer results to the queue", e);
				break loop;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				logger.error("Error", e);
				break loop;
			}
		}
		logger.info("Done fetching all pages. Signalling end of phase");
		controlFlowManager.notifyPhase(PHASE.PAGE_SCRAPE, PHASE_STAGE.COMPLETE);
	}

}
