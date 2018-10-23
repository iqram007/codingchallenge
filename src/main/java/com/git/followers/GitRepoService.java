package com.git.followers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Component("gitRepoService")
@Transactional
public class GitRepoService {

	public static final int LIMIT = 3;

	@Value("${repolist.url}")
	String repoListUrl;

	@Value("${repostargazer.url}")
	String repoStarGazerUrl;

	public Map<String, Map<String, Map<String, Map<String, List<String>>>>> getRepos(final String githubid) {

		Map<String, Map<String, Map<String, Map<String, List<String>>>>> followers = new HashMap<>();

		List<String> firstLevelFollowers = getReposBasedOnLimit(githubid, LIMIT);

		for (String first : firstLevelFollowers) {

			Map<String, Map<String, Map<String, List<String>>>> secondLevelFollowers = new HashMap<>();

			List<String> secondFollowerList = getFollowersBasedonRepo(githubid, first, LIMIT);

			for (String second : secondFollowerList) {

				List<String> secondRepoList = getReposBasedOnLimit(second, LIMIT);

				Map<String, Map<String, List<String>>> thirdLevel = new HashMap<>();

				for (String secondRepo : secondRepoList) {

					Map<String, List<String>> thirdFollowerMap = new HashMap<>();

					List<String> thirdFollowers = getFollowersBasedonRepo(second, secondRepo, LIMIT);

					for (String thirdFollow : thirdFollowers) {

						List<String> thirdRepoList = getReposBasedOnLimit(thirdFollow, LIMIT);

						thirdFollowerMap.put(thirdFollow, thirdRepoList);

					}

					thirdLevel.put(secondRepo, thirdFollowerMap);
				}

				secondLevelFollowers.put(second, thirdLevel);
			}

			followers.put(first, secondLevelFollowers);
		}

		return followers;
	}

	public List<String> getReposBasedOnLimit(final String githubid, int limit) {
		RestTemplate restTemplate = new RestTemplate();

		String ipUrl = repoListUrl.replace("githubid", githubid);

		// send request and parse result
		ResponseEntity<String> response = restTemplate.exchange(ipUrl, HttpMethod.GET, null, String.class);

		JSONArray firstLevelfollowers = new JSONArray(response.getBody());

		List<String> firstLevelFollowerList = new ArrayList<>();

		if (firstLevelfollowers != null) {
			int count = 0;
			for (int i = 0; i < firstLevelfollowers.length(); i++) {
				firstLevelFollowerList.add(firstLevelfollowers.getJSONObject(i).getString("name"));
				count++;
				if (count > limit) {
					break;
				}
			}
		}

		return firstLevelFollowerList;
	}

	public List<String> getFollowersBasedonRepo(final String githubid, final String repo, int limit) {
		RestTemplate restTemplate = new RestTemplate();

		String ipUrl = repoStarGazerUrl.replace("githubid", githubid).replace("reponame", repo);

		System.out.println(ipUrl);

		// send request and parse result
		ResponseEntity<String> response = restTemplate.exchange(ipUrl, HttpMethod.GET, null, String.class);

		JSONArray firstLevelfollowers = new JSONArray(response.getBody());

		List<String> firstLevelFollowerList = new ArrayList<>();

		if (firstLevelfollowers != null) {
			int count = 0;
			for (int i = 0; i < firstLevelfollowers.length(); i++) {
				firstLevelFollowerList.add(firstLevelfollowers.getJSONObject(i).getString("login"));
				count++;
				if (count > limit) {
					break;
				}
			}
		}

		return firstLevelFollowerList;
	}

}
