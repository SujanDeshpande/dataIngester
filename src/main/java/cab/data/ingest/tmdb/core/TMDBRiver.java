package cab.data.ingest.tmdb.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.client.RestTemplate;

import cab.data.ingest.pojo.DiscoverResponse;
import cab.data.ingest.pojo.DiscoverResult;
import cab.data.ingest.pojo.Movie;
import cab.data.ingest.pojo.TV;
import cab.data.ingest.tmdb.core.ControlFlowManager.PHASE;
import cab.data.ingest.tmdb.core.ControlFlowManager.PHASE_STAGE;
import cab.data.ingest.tmdb.core.ControlFlowManager.PhaseStageListener;

public class TMDBRiver implements PhaseStageListener {


	private String apiKey;

	private Integer maxPages;

	private boolean lastPageFetched = false;

	private Map<String, Object> mapping;

	private Integer bulkAPIThreshold = 100000;
	
	private Log logger = LogFactory.getLog(getClass());

	public static enum DISCOVERY_TYPE {
		MOVIE("/discover/movie", "movie", Constants.TYPE, Movie.class), TV(
				"/discover/tv", "tv", Constants.TYPE, TV.class), ALL(null,
				null, null, null);

		public final String path;

		public final String contentPath;

		public final String esType;

		public final Class sourceClass;

		private DISCOVERY_TYPE(String path, String contentPath, String esType,
				Class sourceClass) {
			this.path = path;
			this.esType = esType;
			this.contentPath = contentPath;
			this.sourceClass = sourceClass;
		}

		public String getPath() {
			return this.path;
		}

		public String getEsType() {
			return this.esType;
		}

		public String getContentPath() {
			return this.contentPath;
		}

	}

	private DISCOVERY_TYPE discoveryType = DISCOVERY_TYPE.MOVIE;

	private ControlFlowManager controlFlowManager;

	private BlockingQueue<List<DiscoverResult>> queues = new ArrayBlockingQueue<List<DiscoverResult>>(
			1);

	private Map<String, String> filters;

	private int upperYearBound = -1;

	private int lowerYearBound = -1;

	private boolean canTerminate = true;

	protected TMDBRiver() {
		this.controlFlowManager = new ControlFlowManager();
		this.controlFlowManager.registerPhaseStageListener(
				PHASE.CONTENT_SCRAPE, PHASE_STAGE.COMPLETE, this);
		Map<String, Object> settingMap = new HashMap<String, Object>();
		if (settingMap.containsKey("api_key")) {
			this.apiKey = (String) settingMap.get("api_key");
		}

		if (settingMap.containsKey("discovery_type")) {
			String discovery_type = (String) settingMap.get("discovery_type");
			if (discovery_type.equals("tv")) {
				discoveryType = DISCOVERY_TYPE.TV;
			} else if (discovery_type.equals("movie")) {
				discoveryType = DISCOVERY_TYPE.MOVIE;
			}
		}
		if (settingMap.containsKey("max_pages")) {
			maxPages = (Integer) settingMap.get("max_pages");
		}

		if (settingMap.containsKey("content_mapping")) {
			logger.info("Found user defined mapping");
			Map<String, Object> map = (Map<String, Object>) settingMap
					.get("content_mapping");
			this.mapping = new HashMap<String, Object>();
			this.mapping.put(Constants.TYPE, map);
		}
		if (settingMap.containsKey("bulk_api_threshold")) {
			bulkAPIThreshold = (Integer) settingMap.get("bulk_api_threshold");
		}
		if (settingMap.containsKey("filters")) {
			this.filters = (Map<String, String>) settingMap.get("filters");
		}
		if (settingMap.containsKey("year_range")) {
			String[] range = ((String) settingMap.get("year_range")).split("~");
			this.lowerYearBound = Integer.parseInt(range[0]);
			this.upperYearBound = Integer.parseInt(range[1]);
		}
		// print all the settings that have been extracted. Assert that we
		// Received the api key. Don;t print it out for security reasons.
		logger.info(String.format("Recieved apiKey -->  %s",
				(null != apiKey && !apiKey.equals(""))));
		logger.info(String.format("Discovery Type --> %s", discoveryType));
		logger.info("String max_pages --> " + maxPages);
		logger.info("mapping --> " + mapping);
		logger.info("bulk_api_threshold --> " + bulkAPIThreshold);
		logger.info("Filters -- > " + filters);
		logger.info("Lower/Upper year bounds --> " + this.lowerYearBound + "/"
				+ this.upperYearBound);
	}

	
	public void start() {
		// check if the apiKey has been signalled. There is no point of
		// proceeding if that is not there
	  	if (null != apiKey && !apiKey.equals("")) {
			
			RestTemplate template = APIUtil.initTemplate();
			if (this.lowerYearBound != -1 && this.upperYearBound != -1) {
				this.canTerminate = false;
				addYearRange();
			}
			String fetchUrl = buildFetchURL();

			controlFlowManager.startContentScrape(apiKey, discoveryType,
					bulkAPIThreshold);

			computeMaxPage(template, fetchUrl);

			this.controlFlowManager.startPageScrape(apiKey, fetchUrl, maxPages);
		} else {
			logger.error("No API Key found. Nothing being pulled");
		}

	}

	private String buildFetchURL() {
		String fetchUrl = Constants.basePath + discoveryType.getPath()
				+ "?api_key={api_key}&page={page_no}";

		if (filters != null && !filters.isEmpty()) {
			fetchUrl = APIUtil.addFilters(fetchUrl, filters);
		}
		logger.info("Fetch URL for discovery --> " + fetchUrl);
		return fetchUrl;
	}

	private void computeMaxPage(RestTemplate template, String fetchUrl) {
		DiscoverResponse response = template.getForObject(fetchUrl,
				DiscoverResponse.class, APIUtil.getVariableVals(apiKey, "1"));
		logger.info(String.format(
				"Received response for %d content. Fetching %d pages ",
				response.getTotalResults(), response.getTotalPages()));
		maxPages = this.maxPages == null ? response.getTotalPages()
				: (this.maxPages < response.getTotalPages() ? this.maxPages
						: response.getTotalPages());
		logger.info("Max page computed --> " + maxPages);
	}

	private void addYearRange() {
		if (this.filters == null) {
			this.filters = new HashMap<String, String>();
		}
		String lte = this.lowerYearBound + "-12-31";
		String gte = this.lowerYearBound + "-01-01";
		this.lowerYearBound++;
		if (discoveryType.equals(DISCOVERY_TYPE.MOVIE)) {
			filters.put("release_date.lte", lte);
			filters.put("release_date.gte", gte);
		} else if (discoveryType.equals(DISCOVERY_TYPE.TV)) {
			filters.put("first_air_date.lte", lte);
			filters.put("first_air_date.gte", gte);
		}

	}

	public void close() {
		logger.info("close called");
		controlFlowManager.close();
	}


	@Override
	public void onPhase(PHASE phase, PHASE_STAGE stage) {
		if (phase.equals(PHASE.CONTENT_SCRAPE)
				&& stage.equals(PHASE_STAGE.COMPLETE) && canTerminate) {
			logger.debug("Done scrapping. Deleting mapping");
			// delete the mapping. We are done with the scrape
			
		} else if (phase.equals(PHASE.PAGE_SCRAPE)
				&& stage.equals(PHASE_STAGE.COMPLETE)) {
			if (lowerYearBound > upperYearBound) {
				canTerminate = true;
				logger.info("Fetched complete year range. Can terminate");
			} else {

				addYearRange();
				String fetchUrl = buildFetchURL();
				computeMaxPage(APIUtil.initTemplate(), fetchUrl);
				this.controlFlowManager.startPageScrape(apiKey, fetchUrl,
						maxPages);
			}
		}
	}
	
	public static void main(String[] args) {
		TMDBRiver river = new TMDBRiver();
		river.start();
	}

}
