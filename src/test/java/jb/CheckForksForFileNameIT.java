package jb;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.*;

import org.junit.*;

public class CheckForksForFileNameIT {

	private CheckForksForFileName target;

	@Before
	public void setUp() {
		target = new CheckForksForFileName();
	}

	@Test
	public void listForks() {
		List<String> actual = target.getForks("boyarsky", "jforumCsrf");
		assertThat("fork list not empty", actual, hasSize(greaterThan(1)));
		actual.forEach(this::assertFork);
	}

	private void assertFork(String actualFork) {
		assertThat("starts with generic api for repo", actualFork, startsWith("https://api.github.com/repos"));
		assertThat("ends with api for repo name", actualFork, endsWith("/jforumCsrf"));
	}

	@Test
	public void fileInFork() {
		String fork = target.getForks("boyarsky", "jforumCsrf").get(0);
		boolean actual = target.isFileInMasterOfFork(fork, "Owasp.CsrfGuard.js");
		assertTrue("file found in fork", actual);
	}

	@Test
	public void fileNotInFork() {
		String fork = target.getForks("boyarsky", "jforumCsrf").get(0);
		boolean actual = target.isFileInMasterOfFork(fork, "MadeUpFile");
		assertFalse("file not found in fork", actual);
	}
}
