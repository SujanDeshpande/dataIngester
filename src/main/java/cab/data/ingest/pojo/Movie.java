package cab.data.ingest.pojo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Movie implements CreditsOwner,SourceProvider {

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class BelongsToCollection {

		@JsonProperty("name")
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

	@JsonProperty("adult")
	private Boolean adult;

	@JsonProperty("title")
	private String title;

	@JsonProperty("popularity")
	private Double popularity;

	@JsonProperty("overview")
	private String overview;

	@JsonProperty("original_title")
	private String originalTitle;

	@JsonProperty("genres")
	private List<Genre> genres;

	@JsonProperty("release_date")
	private String releaseDate;

	@JsonProperty("vote_average")
	private Double rating;

	@JsonProperty("belongs_to_collection")
	private BelongsToCollection belongsToCollection;

	private List<String> keywords;

	private Credits credit;

	public Credits getCredit() {
		return credit;
	}

	@Override
	public void setCredit(Credits credit) {
		this.credit = credit;
	}

	public List<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}

	public BelongsToCollection getBelongsToCollection() {
		return belongsToCollection;
	}

	public void setBelongsToCollection(BelongsToCollection belongsToCollection) {
		this.belongsToCollection = belongsToCollection;
	}

	public Boolean getAdult() {
		return adult;
	}

	public void setAdult(Boolean adult) {
		this.adult = adult;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Double getPopularity() {
		return popularity;
	}

	public void setPopularity(Double popularity) {
		this.popularity = popularity;
	}

	public String getOverview() {
		return overview;
	}

	public void setOverview(String overview) {
		this.overview = overview;
	}

	public String getOriginalTitle() {
		return originalTitle;
	}

	public void setOriginalTitle(String originalTitle) {
		this.originalTitle = originalTitle;
	}

	public List<Genre> getGenres() {
		return genres;
	}

	public void setGenres(List<Genre> genres) {
		this.genres = genres;
	}

	public String getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}

	public Double getRating() {
		return rating;
	}

	public void setRating(Double rating) {
		this.rating = rating;
	}


}
