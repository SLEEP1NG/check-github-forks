package jb;

import java.util.*;

import org.springframework.http.*;
import org.springframework.web.client.*;

import com.google.gson.*;

/**
 * Before running, go to https://github.com/settings/tokens/new and generate a
 * personal access token. You don't need to check any boxes for special
 * permissions. This is so you can make 5000 requests per hour rather than 60.
 * (Per https://developer.github.com/v3/#rate-limiting). Copy that token under
 * the TODO a few lines down.
 * 
 * @author jeanne
 *
 */
public class CheckForksForFileName {

	private List<String> forks;
	private RestTemplate template;
	private String userName;
	private String repoName;
	private String fileName;

	// TODO place your token here
	String personalToken = "madeUpToken";

	// -----------------------------------------------------------------------

	public static void main(String... args) {
		if (args.length != 3) {
			System.out.println("Incorrect call: should be jb.CheckForksForFileName userName repoName targetFileName");
			System.out.println("For example: jb.CheckForksForFileName boyarsky jforumCsrf Owasp.CsrfGuard.js");
		} else {
			CheckForksForFileName checker = new CheckForksForFileName(args[0], args[1], args[2]);
			checker.printRepositoriesWithMatchingFile();
		}
	}

	// -----------------------------------------------------------------------

	public CheckForksForFileName(String userName, String repoName, String fileName) {
		forks = new ArrayList<>();
		template = new RestTemplate();
		this.userName = userName;
		this.repoName = repoName;
		this.fileName = fileName;
	}

	/**
	 * Get all forks of the requested repository
	 * 
	 */
	public List<String> getForks() {
		String url = "https://api.github.com/repos/" + userName + "/" + repoName + "/forks";
		String jsonString = callRestApi(url);

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
	 */
	public boolean isFileInMasterOfFork(String fork) {
		String url = fork + "/contents";

		String json = callRestApi(url);
		// normally better to parse JSON, but here just looking for a string
		return json.contains(fileName);
	}

	private String convertToBrowserUrl(String url) {
		return url.replace("https://api.github.com/repos", "https://github.com");
	}

	/**
	 * Pass token because github rate limits requests
	 */
	private String callRestApi(String url) {
		HttpHeaders headers = new HttpHeaders();

		headers.add("Authorization", "token " + personalToken);

		// ignore result because just testing connectivity
		HttpEntity<String> request = new HttpEntity<String>(headers);
		ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, request, String.class);
		return response.getBody();
	}

	private void printRepositoriesWithMatchingFile() {
		List<String> forks = getForks();
		forks.stream().filter(this::isFileInMasterOfFork).map(this::convertToBrowserUrl).sorted()
				.forEach(System.out::println);
	}
}
