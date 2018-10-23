package com.git.followers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GitRepoController {

	@Autowired
	GitRepoService gitRepoService;

	@RequestMapping(value = "/repo/{githubid}", method = RequestMethod.GET)
	public Map<String, Map<String, Map<String, Map<String, List<String>>>>> getRepoInfo(
			@PathVariable(value = "githubid") final String githubid) {

		Map<String, Map<String, Map<String, Map<String, List<String>>>>> followerList = gitRepoService
				.getRepos(githubid);

		return followerList;

	}

}
