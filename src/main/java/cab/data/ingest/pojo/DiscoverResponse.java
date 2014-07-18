package cab.data.ingest.pojo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DiscoverResponse {

	@JsonProperty("page")
	private Integer page;
	
	@JsonProperty("total_pages")
	private Integer totalPages;
	
	@JsonProperty("total_results")
	private Integer totalResults;
	
	@JsonProperty("results")
	private List<DiscoverResult> results;

	@Override
	public String toString() {
		return "DiscoverResponse [page=" + page + ", totalPages=" + totalPages
				+ ", totalResults=" + totalResults + ", results=" + results
				+ "]";
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(Integer totalPages) {
		this.totalPages = totalPages;
	}

	public Integer getTotalResults() {
		return totalResults;
	}

	public void setTotalResults(Integer totalResults) {
		this.totalResults = totalResults;
	}

	public List<DiscoverResult> getResults() {
		return results;
	}

	public void setResults(List<DiscoverResult> results) {
		this.results = results;
	}
	
}
