package jb;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

import org.junit.*;

public class CheckForksForFileNameIT {

	private CheckForksForFileName target;
	private PrintStream originalSystemOut;
	private ByteArrayOutputStream mockSystemOut;

	@Before
	public void redirectSystemOut() {
		originalSystemOut = System.out;
		mockSystemOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(mockSystemOut));
	}

	@After
	public void restoreSystemOut() {
		System.setOut(originalSystemOut);
	}

	// -------------------------------------------------------------------------------

	@Test
	public void listForks() {
		target = new CheckForksForFileName("boyarsky", "jforumCsrf", "test");
		List<String> actual = target.getForks();
		assertThat("fork list not empty", actual, hasSize(greaterThan(1)));
		actual.forEach(this::assertFork);
	}

	private void assertFork(String actualFork) {
		assertThat("starts with generic api for repo", actualFork, startsWith("https://api.github.com/repos"));
		assertThat("ends with api for repo name", actualFork, endsWith("/jforumCsrf"));
	}

	// -------------------------------------------------------------------------------

	@Test
	public void fileInFork() {
		target = new CheckForksForFileName("boyarsky", "jforumCsrf", "Owasp.CsrfGuard.js");
		String fork = target.getForks().get(0);
		boolean actual = target.isFileInMasterOfFork(fork);
		assertTrue("file found in fork", actual);
	}

	@Test
	public void fileNotInFork() {
		target = new CheckForksForFileName("boyarsky", "jforumCsrf", "MadeUpFile");
		String fork = target.getForks().get(0);
		boolean actual = target.isFileInMasterOfFork(fork);
		assertFalse("file not found in fork", actual);
	}

	// -------------------------------------------------------------------------------

	@Test
	public void notEnoughParameters() {
		CheckForksForFileName.main("user", "repo");
		assertValidationFails();
	}

	@Test
	public void tooManyParameters() {
		CheckForksForFileName.main("user", "repo", "targetFile", "other");
		assertValidationFails();
	}

	private void assertValidationFails() {
		String actualOutput = mockSystemOut.toString();
		assertThat("should fail validation", actualOutput,
				startsWith("Incorrect call: should be jb.CheckForksForFileName"));
	}

	// -------------------------------------------------------------------------------

	@Test
	public void endToEndForMatches() {
		CheckForksForFileName.main("boyarsky", "jforumCsrf", "Owasp.CsrfGuard.js");
		assertThat("should not be any output", mockSystemOut.toString(), startsWith("https://github.com"));
	}

	@Test
	public void endToEndForNoMatches() {
		CheckForksForFileName.main("boyarsky", "jforumCsrf", "NotAFile");
		assertThat("should be no matches", mockSystemOut.toString(), isEmptyString());
	}
}
