package jb;

import java.util.*;

import org.springframework.web.client.*;

import com.google.gson.*;

public class CheckForksForFileName {

	private List<String> forks;
	private RestTemplate template;

	public CheckForksForFileName() {
		forks = new ArrayList<>();
		template = new RestTemplate();
	}

	/**
	 * Get all forks of the requested repository
	 * 
	 * @param userName
	 * @param repoName
	 * @return
	 */
	public List<String> getForks(String userName, String repoName) {
		String url = "https://api.github.com/repos/" + userName + "/" + repoName + "/forks";

		String jsonString = template.getForObject(url, String.class);

		JsonParser parser = new JsonParser();
		JsonArray root = (JsonArray) parser.parse(jsonString);
		root.forEach(this::addUrlForFork);

		return forks;
	}

	private void addUrlForFork(JsonElement jsonElement) {
		JsonObject jsonObject = (JsonObject) jsonElement;
		forks.add(jsonObject.get("url").getAsString());
	}

	/**
	 * Check whether the given file name is in the master of that fork
	 * 
	 * @param fork
	 * @param string
	 * @return
	 */
	public boolean isFileInMasterOfFork(String fork, String fileName) {
		String url = "https://github.com/ddverni/jforumCsrf/blob/master/" + fileName;

		try {
			template.getForObject(url, String.class);
		} catch (HttpClientErrorException e) {
			return false;
		}

		return true;
	}
}
