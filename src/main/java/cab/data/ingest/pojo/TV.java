package cab.data.ingest.pojo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TV implements CreditsOwner,SourceProvider {

	@JsonProperty("name")
	private String title;

	@JsonProperty("genres")
	private List<Genre> genres;

	@JsonProperty("number_of_episodes")
	private Integer numberOfEpisodes;

	@JsonProperty("number_of_seasons")
	private Integer numberOfSeasons;

	@JsonProperty("original_name")
	private String originalTitle;

	@JsonProperty("overview")
	private String overview;

	@JsonProperty("popularity")
	private Double popularity;

	@JsonProperty("vote_average")
	private Double rating;

	@JsonProperty("first_air_date")
	private String releaseDate;

	@JsonProperty("last_air_date")
	private String endDate;

	private String seriesName;

	private Credits credit;

	public Credits getCredit() {
		return credit;
	}

	@Override
	public void setCredit(Credits credit) {
		this.credit = credit;
	}

	public String getSeriesName() {
		return seriesName;
	}

	public void setSeriesName(String seriesName) {
		this.seriesName = seriesName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Genre> getGenres() {
		return genres;
	}

	public void setGenres(List<Genre> genres) {
		this.genres = genres;
	}

	public Integer getNumberOfEpisodes() {
		return numberOfEpisodes;
	}

	public void setNumberOfEpisodes(Integer numberOfEpisodes) {
		this.numberOfEpisodes = numberOfEpisodes;
	}

	public Integer getNumberOfSeasons() {
		return numberOfSeasons;
	}

	public void setNumberOfSeasons(Integer numberOfSeasons) {
		this.numberOfSeasons = numberOfSeasons;
	}

	public String getOriginalTitle() {
		return originalTitle;
	}

	public void setOriginalTitle(String originalTitle) {
		this.originalTitle = originalTitle;
	}

	public String getOverview() {
		return overview;
	}

	public void setOverview(String overview) {
		this.overview = overview;
	}

	public Double getPopularity() {
		return popularity;
	}

	public void setPopularity(Double popularity) {
		this.popularity = popularity;
	}

	public Double getRating() {
		return rating;
	}

	public void setRating(Double rating) {
		this.rating = rating;
	}

	public String getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

}
