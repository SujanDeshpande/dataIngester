package cab.data.ingest.pojo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Keyword {

	private List<String> keyWords = null;

	public List<String> getKeyWords() {
		if (null == this.keyWords) {
			keyWords = new ArrayList<String>(innerKeywords.size());
			for (InnerKeyword innerKeyword : innerKeywords) {
				keyWords.add(innerKeyword.getKeyword());
			}
		}
		return keyWords;

	}

	@JsonProperty("keywords")
	private List<InnerKeyword> innerKeywords;

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class InnerKeyword {

		@JsonProperty("name")
		private String keyword;

		public String getKeyword() {
			return keyword;
		}

		public void setKeyword(String keyword) {
			this.keyword = keyword;
		}

	}
}
