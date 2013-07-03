package edu.calpoly.android.lab3.tests;

import java.util.ArrayList;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import edu.calpoly.android.lab3.AdvancedJokeList;
import edu.calpoly.android.lab3.Joke;
import edu.calpoly.android.lab3.JokeListAdapter;
import edu.calpoly.android.lab3.JokeView;
import edu.calpoly.android.lab3.tests.FriendClass.FriendClassException;

public class AdvancedJokeListAcceptanceTest extends ActivityInstrumentationTestCase2<AdvancedJokeList> {

	public static final double TEXT_SIZE = 16.0;
	
	private Instrumentation mInstrument = null;
	
	// AdvancedJokeList member variables
	private AdvancedJokeList mActivity = null;
	private Button m_vwJokeButton = null;
	private ViewGroup m_vwJokeLayout = null;
	private EditText m_vwJokeEditText = null;
	private ArrayList<Joke> m_arrJokeList = null;
	private JokeListAdapter m_jokeAdapter;
	
	public AdvancedJokeListAcceptanceTest() {
		super(AdvancedJokeList.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();

		mInstrument = getInstrumentation();
		
		// Set up Activity
		mActivity = getActivity();

		// Gather references to Activity member variables
		try {
			m_vwJokeButton = FriendClass.retrieveHiddenMember("m_vwJokeButton", m_vwJokeButton, mActivity);
			m_vwJokeLayout = FriendClass.retrieveHiddenMember("m_vwJokeLayout", m_vwJokeLayout, mActivity);
			m_vwJokeEditText = FriendClass.retrieveHiddenMember("m_vwJokeEditText", m_vwJokeEditText, mActivity);
			m_arrJokeList = FriendClass.retrieveHiddenMember("m_arrJokeList", m_arrJokeList, getActivity());
			m_jokeAdapter = FriendClass.retrieveHiddenMember("m_jokeAdapter", m_jokeAdapter, getActivity());
		} catch (FriendClassException exc) {fail(exc.getMessage());}
	}

	@SmallTest
	/**
	 * TODO
	 */
	public void testSec1PreConditions() {
		assertNotNull("m_vwJokeEditText should not be null", m_vwJokeEditText);
		assertNotNull("m_vwJokeButton should not be null", m_vwJokeButton);

		assertNotNull("m_arrJokeList should not be null", m_arrJokeList);
		assertEquals("m_arrJokeList should be pre-populated with 3 default jokes", 3, m_arrJokeList.size());
		
		assertNotNull("m_vwm_vwJokeLayout should not be null", m_vwJokeLayout);
		assertEquals("m_vwJokeLayout should be pre-populated with 3 default jokes", 3, m_vwJokeLayout.getChildCount());
		
		View view = m_vwJokeLayout.getChildAt(1);
		if (view instanceof TextView) {
			assertEquals("Checking text size",TEXT_SIZE,((TextView)view).getTextSize(),.001);
		}
		
		for (int ndx = 0; ndx < m_vwJokeLayout.getChildCount(); ndx++) {
			if (!(m_vwJokeLayout.getChildAt(ndx) instanceof TextView) &&
			    !(m_vwJokeLayout.getChildAt(ndx) instanceof JokeView)) {
				fail("m_vwJokeLayout should contain only TextView (if you haven't implemented the JokeView class) or JokeView objects and nothing else.");
			}
		}
		assertEquals("Check Button Text","Add Joke",m_vwJokeButton.getText());
	}
	
	@SmallTest
	/**
	 * TODO
	 */
	public void testAddJokeViaButton() {
		final String jokeText = "Testing addJoke via Add Button...";

		// Add the joke
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				m_vwJokeEditText.setText(jokeText);
				m_vwJokeButton.performClick();
			}
		});
		mInstrument.waitForIdleSync();
		testForAddedJoke(jokeText);		
	}
	
	@SmallTest
	/**
	 * TODO
	 */
	public void testAddJokeViaReturn() {
		final String jokeText = "Testing addJoke via return key...";

		// Add the joke
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				m_vwJokeEditText.setText(jokeText);
			}
		});
		mInstrument.waitForIdleSync();
		sendKeys(KeyEvent.KEYCODE_ENTER);
		mInstrument.waitForIdleSync();
		testForAddedJoke(jokeText);		
	}
	
	@SmallTest
	/**
	 * TODO
	 */
	public void testAddJokeViaDPadTrackBall() {
		final String jokeText = "Testing addJoke via D-Pad Center & Track-Ball...";

		// Add the joke
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				m_vwJokeEditText.setText(jokeText);
			}
		});
		mInstrument.waitForIdleSync();
		sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
		mInstrument.waitForIdleSync();
		testForAddedJoke(jokeText);		
	}
	
	private void testForAddedJoke(String jokeText) {
		assertEquals("m_arrJokeList should have 4 jokes now", 4, m_arrJokeList.size());
		assertEquals("m_vwJokeLayout should have 4 jokes now", 4, m_vwJokeLayout.getChildCount());
		assertEquals("Checking index of joke, that it was added to the end of m_arrJokeList", jokeText, m_arrJokeList.get(3).getJoke());
		
		// App under test could be using JokeViews or TextViews at this point
		View view = m_vwJokeLayout.getChildAt(3);
		if (view instanceof TextView) {
			TextView tv = (TextView)view;
			assertEquals("Checking index of joke, that it was added to the end of m_vwJokeLayout", jokeText, tv.getText().toString());
		}
		else if (view instanceof JokeView) {
			JokeView jokeView = (JokeView)view;
			Joke joke = null;
			try {
				joke = FriendClass.retrieveHiddenMember("m_joke", joke, jokeView);
			} catch (FriendClassException exc) {fail(exc.getMessage());}
			assertEquals("Checking index of joke, that it was added to the end of m_vwJokeLayout", jokeText, joke.getJoke());
		}
		else {
			fail("m_vwJokeLayout should contain only TextView (if you haven't implemented the JokeView class) or JokeView objects and nothing else.");
		}
	}
}
