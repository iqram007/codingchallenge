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

@Component("gitFollowerService")
@Transactional
public class GitFollowerService {
	
	public static final int LIMIT = 5;
	
	@Value("${follower.url}")
	String followerUrl;

	public Map<String,Map<String, Map<String, List<String>>>> getFollowers(final String githubid) {

		Map<String,Map<String, Map<String, List<String>>>> followers = new HashMap<>();

		List<String> firstLevelFollowers = getFollowerBasedOnLimit(githubid, LIMIT);		

		for (String first : firstLevelFollowers) {
			
			Map<String, Map<String,List<String>>> secondLevelFollowers = new HashMap<>();

			List<String> secondFollowerList = getFollowerBasedOnLimit(first, LIMIT);

			for (String second : secondFollowerList) {

				List<String> thirdFollowerList = getFollowerBasedOnLimit(second, LIMIT);
				
				Map<String,List<String>> thirdLevel = new HashMap<>();
				
				for(String third : thirdFollowerList){
					
					List<String> thirdFollowers = getFollowerBasedOnLimit(third, LIMIT);
					
					thirdLevel.put(third, thirdFollowers);
				}
				
				
				secondLevelFollowers.put(second, thirdLevel);
			}

			followers.put(first, secondLevelFollowers);
		}

		return followers;
	}

	public List<String> getFollowerBasedOnLimit(final String githubid, int limit) {
		RestTemplate restTemplate = new RestTemplate();

		String ipUrl = followerUrl.replace("githubid", githubid);

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
