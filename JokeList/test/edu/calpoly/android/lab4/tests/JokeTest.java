package edu.calpoly.android.lab4.tests;

import junit.framework.TestCase;
import android.test.suitebuilder.annotation.SmallTest;
import edu.calpoly.android.lab4.Joke;

public class JokeTest extends TestCase {

	@SmallTest
	/**
	 * Test Default Constructor
	 */
	public void testJoke() {
		Joke joke = new Joke();
		assertTrue("m_strJoke should be initialized to \"\".", joke.getJoke().equals(""));
		assertTrue("m_strAuthorName should be initialized to \"\".", joke.getAuthor().equals(""));
		assertEquals("m_nRating should be initialized to Joke.UNRATED.", Joke.UNRATED, joke.getRating());
	}

	@SmallTest
	/**
	 * Test Parameterized Constructor: Joke(String strJoke, String strAuthor)
	 */
	public void testJokeStringString() {
		String strJoke = "testJoke";
		String strAuthor = "testAuthor";
		Joke joke = new Joke(strJoke, strAuthor);
		assertEquals("m_strJoke should be initialized to \"testJoke\".", strJoke, joke.getJoke());
		assertEquals("m_strAuthorName should be initialized to \"testAuthor\".", strAuthor, joke.getAuthor());
		assertEquals("m_nRating should be initialized to Joke.UNRATED.", Joke.UNRATED, joke.getRating());
	}

	@SmallTest
	/**
	 * Test Parameterized Constructor: Joke(String strJoke, String strAuthor, int nRating, long id)
	 */
	public void testJokeStringStringInt() {
		String strJoke = "testJoke";
		String strAuthor = "testAuthor";
		Joke joke = new Joke(strJoke, strAuthor, Joke.DISLIKE, 10);
		assertEquals("m_strJoke should be initialized to \"testJoke\".", strJoke, joke.getJoke());
		assertEquals("m_strAuthorName should be initialized to \"testAuthor\".", strAuthor, joke.getAuthor());
		assertEquals("m_nRating should be initialized to Joke.DISLIKE.", Joke.DISLIKE, joke.getRating());
		assertEquals("m_nID should be initialized to \"10\".", 10, joke.getID());
	}
	
	@SmallTest
	/**
	 * Test Mutator Method
	 */
	public void testSetJoke() {
		String strJoke = "testJoke";
		Joke joke = new Joke();
		joke.setJoke(strJoke);
		assertEquals("m_strJoke should be set to \"testJoke\".", strJoke, joke.getJoke());
	}
	
	@SmallTest
	/**
	 * Test Mutator Method
	 */
	public void testSetAuthor() {
		Joke joke = new Joke();
		String strAuthor = "testAuthor";
		joke.setAuthor(strAuthor);
		assertEquals("m_strAuthorName should be set to \"testAuthor\".", strAuthor, joke.getAuthor());
	}

	@SmallTest
	/**
	 * Test Mutator Method
	 */
	public void testSetRating() {
		Joke joke = new Joke();
		joke.setRating(Joke.LIKE);
		assertEquals("m_nRating should be set to Joke.LIKE.", Joke.LIKE, joke.getRating());
	}

	@SmallTest
	/**
	 * Test Mutator Method
	 */
	public void testSetID() {
		Joke joke = new Joke();
		joke.setID(10);
		assertEquals("m_nID should be set to \"10\".", 10, joke.getID());
	}

	@SmallTest
	public void testEquals() {
		String strJoke = "testJoke";
		String strAuthor = "testAuthor";
		
		Joke joke = new Joke(strJoke, strAuthor, Joke.LIKE, 99);
		Joke jokeEQ = new Joke(strJoke, strAuthor, Joke.LIKE, 99);
		Joke jokeEQ2 = new Joke(strAuthor, strJoke, Joke.DISLIKE, 99);
		Joke jokeNEQ = new Joke(strJoke, strAuthor, Joke.LIKE, 1);
				
		assertFalse("equals(Object obj) should return false. Testing against null", joke.equals(null));
		assertFalse("equals(Object obj) should return false. Not comparing against another Joke object; obj is not an instance of Joke", joke.equals(strJoke));
		assertFalse("equals(Object obj) should return false. The two jokes have different m_nID values", joke.equals(jokeNEQ));
		assertTrue("equals(Object obj) should return true. Testing against itself", joke.equals(joke));
		assertTrue("equals(Object obj) should return true. Testing against different Joke containing same data", joke.equals(jokeEQ));
		assertTrue("equals(Object obj) should return true. Testing against different Joke containing different data except for m_nID which is the same", joke.equals(jokeEQ2));
	}

	@SmallTest
	public void testToString() {
		String strJoke = "testJoke";
		Joke joke = new Joke(strJoke, "author");
		assertEquals("toString() should return \"testJoke\".", strJoke, joke.toString());
	}

}
