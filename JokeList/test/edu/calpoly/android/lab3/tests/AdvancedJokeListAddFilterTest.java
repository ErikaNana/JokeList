package edu.calpoly.android.lab3.tests;

import java.lang.reflect.Field;
import java.util.ArrayList;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;

import edu.calpoly.android.lab3.AdvancedJokeList;
import edu.calpoly.android.lab3.Joke;
import edu.calpoly.android.lab3.JokeView;
import edu.calpoly.android.lab3.R;

public class AdvancedJokeListAddFilterTest extends ActivityInstrumentationTestCase2<AdvancedJokeList> {
	
	private AdvancedJokeList ajl;
	
	public AdvancedJokeListAddFilterTest() {
		super(AdvancedJokeList.class);
	}
	
	@Override
	protected void setUp()
	{
		ajl = this.getActivity();
	}
	
	/**
	 * Basic sanity check for responsive menu.
	 */
	@SmallTest
	public void testMenuFilter(){
		Menu menu = null;
		menu = this.retrieveHiddenMember("m_vwMenu", menu, getActivity());
		MenuItem mi = menu.getItem(0);
		assertEquals(mi.getTitle(), "Filter");
	}
	
	/**
	 * Basic sanity check for responsive submenus.
	 */
	@SmallTest
	public void testSubMenus(){
		Menu menu = null;
		menu = this.retrieveHiddenMember("m_vwMenu", menu, getActivity());
		MenuItem mi = menu.getItem(0);
		assertEquals(mi.getTitle(), "Filter");
		SubMenu sm = mi.getSubMenu();
		MenuItem smi = sm.getItem(0);
		assertEquals(smi.getTitle(), "Like");
		MenuItem smi2 = sm.getItem(1);
		assertEquals(smi2.getTitle(), "Dislike");
		MenuItem smi3 = sm.getItem(2);
		assertEquals(smi3.getTitle(), "Unrated");
		MenuItem smi4 = sm.getItem(3);
		assertEquals(smi4.getTitle(), "Show All");
	}
	
	/**
	 * Sanity check for Unrated filter working as expected. Works even
	 * if the filter doesn't do anything.
	 */
	@SmallTest
	public void testFilterUnratedDefaultJokes(){
		Menu menu = null;
		ArrayList<Joke> m_arrFilteredJokeList = null;
		m_arrFilteredJokeList = this.retrieveHiddenMember("m_arrFilteredJokeList", m_arrFilteredJokeList,
			getActivity());
		assertEquals("Should be 3 default jokes", 3, m_arrFilteredJokeList.size());
		
		menu = this.retrieveHiddenMember("m_vwMenu", menu, getActivity());
		MenuItem filter = menu.getItem(0);
		SubMenu sm = filter.getSubMenu();
		final MenuItem unRated = sm.getItem(2);
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				ajl.onOptionsItemSelected(unRated);
			}
			});
		// wait for the request to go through
		getInstrumentation().waitForIdleSync();
		assertEquals("Should be 3 jokes still", 3, m_arrFilteredJokeList.size());
		assertEquals("Joke 1 should still be Unrated", Joke.UNRATED, m_arrFilteredJokeList.get(0).getRating());
		assertEquals("Joke 2 should still be Unrated", Joke.UNRATED, m_arrFilteredJokeList.get(1).getRating());
		assertEquals("Joke 3 should still be Unrated", Joke.UNRATED, m_arrFilteredJokeList.get(2).getRating());
		assertEquals("Joke 3 should be the same", "I wondered why the baseball was getting bigger. Then it hit me.",
			m_arrFilteredJokeList.get(2).getJoke());
	}
	
	/**
	 * Tests Like filtering with the preloaded default jokes, no ratings changed.
	 */
	@SmallTest
	public void testFilterLikeDefaultJokes(){
		Menu menu = null;
		ArrayList<Joke> m_arrFilteredJokeList = null;
		m_arrFilteredJokeList = this.retrieveHiddenMember("m_arrFilteredJokeList", m_arrFilteredJokeList,
			getActivity());
		assertEquals("Should be 3 default jokes", 3, m_arrFilteredJokeList.size());
		
		menu = this.retrieveHiddenMember("m_vwMenu", menu, getActivity());
		MenuItem filter = menu.getItem(0);
		SubMenu sm = filter.getSubMenu();
		final MenuItem like = sm.getItem(0);
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				ajl.onOptionsItemSelected(like);
			}
			});
		// wait for the request to go through
		getInstrumentation().waitForIdleSync();
		assertEquals("Should be 0 jokes in the list", 0, m_arrFilteredJokeList.size());
	}
	
	/**
	 * Tests Dislike filtering with the preloaded default jokes, no ratings changed.
	 */
	@SmallTest
	public void testFilterDislikeDefaultJokes(){
		Menu menu = null;
		ArrayList<Joke> m_arrFilteredJokeList = null;
		m_arrFilteredJokeList = this.retrieveHiddenMember("m_arrFilteredJokeList", m_arrFilteredJokeList,
			getActivity());
		assertEquals("Should be 3 default jokes", 3, m_arrFilteredJokeList.size());
		
		menu = this.retrieveHiddenMember("m_vwMenu", menu, getActivity());
		MenuItem filter = menu.getItem(0);
		SubMenu sm = filter.getSubMenu();
		final MenuItem dislike = sm.getItem(1);
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				ajl.onOptionsItemSelected(dislike);
			}
			});
		// wait for the request to go through
		getInstrumentation().waitForIdleSync();
		assertEquals("Should be 0 jokes in the list", 0, m_arrFilteredJokeList.size());
	}
	
	/**
	 * Tests filtering to show no jokes, then bringing them back.
	 */
	@SmallTest
	public void testFilterShowHideAllDefaultJokes(){
		Menu menu = null;
		ArrayList<Joke> m_arrFilteredJokeList = null;
		m_arrFilteredJokeList = this.retrieveHiddenMember("m_arrFilteredJokeList", m_arrFilteredJokeList,
			getActivity());
		assertEquals("Should be 3 default jokes", 3, m_arrFilteredJokeList.size());
		
		menu = this.retrieveHiddenMember("m_vwMenu", menu, getActivity());
		MenuItem filter = menu.getItem(0);
		SubMenu sm = filter.getSubMenu();
		final MenuItem dislike = sm.getItem(1);
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				ajl.onOptionsItemSelected(dislike);
			}
			});
		// wait for the request to go through
		getInstrumentation().waitForIdleSync();
		assertEquals("Should be 0 jokes in the list", 0, m_arrFilteredJokeList.size());
		final MenuItem showAll = sm.getItem(3);
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				ajl.onOptionsItemSelected(showAll);
			}
			});
		// wait for the request to go through
		getInstrumentation().waitForIdleSync();
		assertEquals("Should be 3 jokes again", 3, m_arrFilteredJokeList.size());
	}
	
	/**
	 * Rates the default preloaded jokes.
	 */
	@SmallTest
	public void testRateLikeDefaultJokes() {
		ArrayList<Joke> m_arrFilteredJokeList = null;
		m_arrFilteredJokeList = this.retrieveHiddenMember("m_arrFilteredJokeList", m_arrFilteredJokeList,
			getActivity());
		assertEquals("Should be 3 default jokes", 3, m_arrFilteredJokeList.size());
		
		ListView m_vwJokeLayout = null;
		m_vwJokeLayout = this.retrieveHiddenMember("m_vwJokeLayout", m_vwJokeLayout, getActivity());
		ListAdapter m_jokeAdapter = m_vwJokeLayout.getAdapter();
		
		//Check Like on first JokeView
		JokeView jv = (JokeView)m_jokeAdapter.getView(0, null, null);
		final RadioButton m_vwLikeButton = (RadioButton)jv.findViewById(R.id.likeButton);
		assertFalse("Check Like Unchecked", m_vwLikeButton.isChecked());
		getActivity().runOnUiThread(new Runnable() {
		public void run() {
			m_vwLikeButton.performClick();
		}
		});
		getInstrumentation().waitForIdleSync();
		assertTrue("Check Like Checked", m_vwLikeButton.isChecked());
		
		//Check Like on third jokeView
		JokeView jv3 = (JokeView)m_jokeAdapter.getView(2, null, null);
		final RadioButton m_vwLikeButton3 = (RadioButton)jv3.findViewById(R.id.likeButton);
		assertFalse("Check Like Unchecked", m_vwLikeButton3.isChecked());
		getActivity().runOnUiThread(new Runnable() {
		public void run() {
			m_vwLikeButton3.performClick();
		}
		});
		getInstrumentation().waitForIdleSync();
		assertTrue("Check Like Checked", m_vwLikeButton3.isChecked());
		
		//Check Unrated on second jokeView still
		JokeView jv2 = (JokeView)m_jokeAdapter.getView(1, null, null);
		RadioGroup m_vwLikeGroup2 = (RadioGroup)jv2.findViewById(R.id.ratingRadioGroup);
		assertEquals("Check Unrated", m_vwLikeGroup2.getCheckedRadioButtonId(), -1);
	}
	
	/**
	 * Rates the default preloaded jokes, then filters for Disliked jokes only.
	 */
	@SmallTest
	public void testRateFilterDislikeDefaultJokes() {
		ArrayList<Joke> m_arrFilteredJokeList = null;
		m_arrFilteredJokeList = this.retrieveHiddenMember("m_arrFilteredJokeList", m_arrFilteredJokeList,
			getActivity());
		assertEquals("Should be 3 default jokes", 3, m_arrFilteredJokeList.size());
		
		ListView m_vwJokeLayout = null;
		m_vwJokeLayout = this.retrieveHiddenMember("m_vwJokeLayout", m_vwJokeLayout, getActivity());
		ListAdapter m_jokeAdapter = m_vwJokeLayout.getAdapter();
		
		//Check Dislike on second JokeView
		JokeView jv2 = (JokeView)m_jokeAdapter.getView(1, null, null);
		final RadioButton m_vwDislikeButton2 = (RadioButton)jv2.findViewById(R.id.dislikeButton);
		assertFalse("Check to make sure Dislike is unchecked", m_vwDislikeButton2.isChecked());
		getActivity().runOnUiThread(new Runnable() {
		public void run() {
			m_vwDislikeButton2.performClick();
		}
		});
		getInstrumentation().waitForIdleSync();
		assertTrue("Check to make sure Dislike is checked", m_vwDislikeButton2.isChecked());
		
		//Check Like on third jokeView
		JokeView jv3 = (JokeView)m_jokeAdapter.getView(2, null, null);
		final RadioButton m_vwLikeButton3 = (RadioButton)jv3.findViewById(R.id.likeButton);
		assertFalse("Check Like Unchecked", m_vwLikeButton3.isChecked());
		getActivity().runOnUiThread(new Runnable() {
		public void run() {
			m_vwLikeButton3.performClick();
		}
		});
		getInstrumentation().waitForIdleSync();
		assertTrue("Check Like Checked", m_vwLikeButton3.isChecked());
		
		//Check Unrated on first jokeView still
		JokeView jv1 = (JokeView)m_jokeAdapter.getView(0, null, null);
		RadioGroup m_vwLikeGroup1 = (RadioGroup)jv1.findViewById(R.id.ratingRadioGroup);
		assertEquals("Check Unrated", m_vwLikeGroup1.getCheckedRadioButtonId(), -1);
		
		Menu menu = null;
		menu = this.retrieveHiddenMember("m_vwMenu", menu, getActivity());
		MenuItem filter = menu.getItem(0);
		SubMenu sm = filter.getSubMenu();
		final MenuItem dislike = sm.getItem(1);
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				ajl.onOptionsItemSelected(dislike);
			}
			});
		// wait for the request to go through
		getInstrumentation().waitForIdleSync();
		assertEquals("Should be 1 joke in the list", 1, m_arrFilteredJokeList.size());
	}
	
	/**
	 * Adds and rates several new jokes and one old one. 
	 */
	@SmallTest
	public void testAddRateNewJokes() {
		ArrayList<Joke> m_arrFilteredJokeList = null;
		m_arrFilteredJokeList = this.retrieveHiddenMember("m_arrFilteredJokeList", m_arrFilteredJokeList,
			getActivity());
		assertEquals("Should be 3 default jokes", 3, m_arrFilteredJokeList.size());
		
		EditText et = null;
		Button bt = null;
		final EditText m_vwJokeEditText = this.retrieveHiddenMember("m_vwJokeEditText", et, getActivity());
		final Button m_vwJokeButton = this.retrieveHiddenMember("m_vwJokeButton", bt, getActivity());
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				m_vwJokeEditText.setText("This is a test joke");
				m_vwJokeButton.performClick();
			}
			});
		// wait for the request to go through
		getInstrumentation().waitForIdleSync();
		assertEquals("Should be 4 jokes now", 4, m_arrFilteredJokeList.size());
		
		ListView m_vwJokeLayout = null;
		m_vwJokeLayout = this.retrieveHiddenMember("m_vwJokeLayout", m_vwJokeLayout, getActivity());
		ListAdapter m_jokeAdapter = m_vwJokeLayout.getAdapter();
		
		//Check Dislike on first JokeView
		JokeView jv = (JokeView)m_jokeAdapter.getView(0, null, null);
		final RadioButton m_vwDislikeButton = (RadioButton)jv.findViewById(R.id.dislikeButton);
		assertFalse("Check to make sure Dislike is unchecked", m_vwDislikeButton.isChecked());
		getActivity().runOnUiThread(new Runnable() {
		public void run() {
			m_vwDislikeButton.performClick();
		}
		});
		getInstrumentation().waitForIdleSync();
		assertTrue("Check to make sure Dislike is checked", m_vwDislikeButton.isChecked());
		
		//Check Dislike on fourth JokeView
		JokeView jv4 = (JokeView)m_jokeAdapter.getView(3, null, null);
		final RadioButton m_vwDislikeButton4 = (RadioButton)jv4.findViewById(R.id.dislikeButton);
		assertFalse("Check to make sure Dislike is unchecked", m_vwDislikeButton4.isChecked());
		getActivity().runOnUiThread(new Runnable() {
		public void run() {
			m_vwDislikeButton4.performClick();
		}
		});
		getInstrumentation().waitForIdleSync();
		assertTrue("Check to make sure Dislike is checked", m_vwDislikeButton4.isChecked());
		
		//Add 10 more jokes to the list
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				for(int index = 1; index <= 10; index++) {
					m_vwJokeEditText.setText("This is new joke #" + (index + 1));
					m_vwJokeButton.performClick();
				}
			}
			});
		// wait for the request to go through
		getInstrumentation().waitForIdleSync();
		assertEquals("Should be 14 jokes now", 14, m_arrFilteredJokeList.size());
		
		//Check Like on fourteenth jokeView
		JokeView jv14 = (JokeView)m_jokeAdapter.getView(13, null, null);
		assertEquals("14th joke is 'This is new joke #11'", "This is new joke #11", m_arrFilteredJokeList.get(13).getJoke());
		final RadioButton m_vwLikeButton14 = (RadioButton)jv14.findViewById(R.id.likeButton);
		assertFalse("Check Like Unchecked", m_vwLikeButton14.isChecked());
		getActivity().runOnUiThread(new Runnable() {
		public void run() {
			m_vwLikeButton14.performClick();
		}
		});
		getInstrumentation().waitForIdleSync();
		assertTrue("Check Like Checked", m_vwLikeButton14.isChecked());
		
		//Check Unrated on third jokeView still
		JokeView jv3 = (JokeView)m_jokeAdapter.getView(2, null, null);
		RadioGroup m_vwLikeGroup3 = (RadioGroup)jv3.findViewById(R.id.ratingRadioGroup);
		assertEquals("Check Unrated", m_vwLikeGroup3.getCheckedRadioButtonId(), -1);
		
		//Filter jokes by dislike
		Menu menu = null;
		menu = this.retrieveHiddenMember("m_vwMenu", menu, getActivity());
		MenuItem filter = menu.getItem(0);
		SubMenu sm = filter.getSubMenu();
		final MenuItem dislike = sm.getItem(1);
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				ajl.onOptionsItemSelected(dislike);
			}
			});
		// wait for the request to go through
		getInstrumentation().waitForIdleSync();
		assertEquals("Should be 2 jokes in the list", 2, m_arrFilteredJokeList.size());
		
		//Filter jokes by like
		final MenuItem like = sm.getItem(0);
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				ajl.onOptionsItemSelected(like);
			}
			});
		// wait for the request to go through
		getInstrumentation().waitForIdleSync();
		assertEquals("Should be 1 joke in the list", 1, m_arrFilteredJokeList.size());
		
		//Filter jokes by unrated
		final MenuItem unrated = sm.getItem(2);
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				ajl.onOptionsItemSelected(unrated);
			}
			});
		// wait for the request to go through
		getInstrumentation().waitForIdleSync();
		assertEquals("Should be 11 jokes in the list", 11, m_arrFilteredJokeList.size());
		
		//Filter jokes by show all
		final MenuItem showAll = sm.getItem(3);
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				ajl.onOptionsItemSelected(showAll);
			}
			});
		// wait for the request to go through
		getInstrumentation().waitForIdleSync();
		assertEquals("Should be 14 jokes in the list", 14, m_arrFilteredJokeList.size());
	}
	
	/**
	 * Adds two new jokes, one before and one after filtering, and makes sure new jokes are in the list when showing all Jokes. 
	 */
	@SmallTest
	public void testAddRateNewJokeAfterFiltering() {
		ArrayList<Joke> m_arrFilteredJokeList = null;
		m_arrFilteredJokeList = this.retrieveHiddenMember("m_arrFilteredJokeList", m_arrFilteredJokeList,
			getActivity());
		assertEquals("Should be 3 default jokes", 3, m_arrFilteredJokeList.size());
		
		//Add first new joke
		EditText et = null;
		Button bt = null;
		final EditText m_vwJokeEditText = this.retrieveHiddenMember("m_vwJokeEditText", et, getActivity());
		final Button m_vwJokeButton = this.retrieveHiddenMember("m_vwJokeButton", bt, getActivity());
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				m_vwJokeEditText.setText("This is a test joke");
				m_vwJokeButton.performClick();
			}
			});
		// wait for the request to go through
		getInstrumentation().waitForIdleSync();
		assertEquals("Should be 4 jokes now", 4, m_arrFilteredJokeList.size());
		
		ListView m_vwJokeLayout = null;
		m_vwJokeLayout = this.retrieveHiddenMember("m_vwJokeLayout", m_vwJokeLayout, getActivity());
		ListAdapter m_jokeAdapter = m_vwJokeLayout.getAdapter();
		
		//Check Dislike on fourth JokeView
		JokeView jv4 = (JokeView)m_jokeAdapter.getView(3, null, null);
		final RadioButton m_vwDislikeButton4 = (RadioButton)jv4.findViewById(R.id.dislikeButton);
		assertFalse("Check to make sure Dislike is unchecked", m_vwDislikeButton4.isChecked());
		getActivity().runOnUiThread(new Runnable() {
		public void run() {
			m_vwDislikeButton4.performClick();
		}
		});
		getInstrumentation().waitForIdleSync();
		assertTrue("Check to make sure Dislike is checked", m_vwDislikeButton4.isChecked());
		
		//Filter jokes by dislike
		Menu menu = null;
		menu = this.retrieveHiddenMember("m_vwMenu", menu, getActivity());
		MenuItem filter = menu.getItem(0);
		SubMenu sm = filter.getSubMenu();
		final MenuItem dislike = sm.getItem(1);
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				ajl.onOptionsItemSelected(dislike);
			}
			});
		// wait for the request to go through
		getInstrumentation().waitForIdleSync();
		assertEquals("Should be 1 joke in the list", 1, m_arrFilteredJokeList.size());
		
		//Add second joke to the filtered list
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				m_vwJokeEditText.setText("This is a second test joke");
				m_vwJokeButton.performClick();
			}
			});
		// wait for the request to go through
		getInstrumentation().waitForIdleSync();
		assertEquals("Should be 2 jokes now", 2, m_arrFilteredJokeList.size());
		
		//Check Like on new second jokeView
		JokeView jv2 = (JokeView)m_jokeAdapter.getView(1, null, null);
		final RadioButton m_vwLikeButton2 = (RadioButton)jv2.findViewById(R.id.likeButton);
		assertFalse("Check Like Unchecked", m_vwLikeButton2.isChecked());
		getActivity().runOnUiThread(new Runnable() {
		public void run() {
			m_vwLikeButton2.performClick();
		}
		});
		getInstrumentation().waitForIdleSync();
		assertTrue("Check Like Checked", m_vwLikeButton2.isChecked());
		
		//Filter jokes by like
		final MenuItem like = sm.getItem(0);
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				ajl.onOptionsItemSelected(like);
			}
			});
		// wait for the request to go through
		getInstrumentation().waitForIdleSync();
		assertEquals("Should be 1 joke in the list", 1, m_arrFilteredJokeList.size());
		
		//Filter jokes by show all
		final MenuItem showAll = sm.getItem(3);
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				ajl.onOptionsItemSelected(showAll);
			}
			});
		// wait for the request to go through
		getInstrumentation().waitForIdleSync();
		assertEquals("Should be 5 jokes in the list", 5, m_arrFilteredJokeList.size());
		
		//Check preservation of order of jokes
		assertEquals("1st joke is still the same", "A small boy swallowed some coins and was taken to a hospital. When his grandfather telephoned to ask how he was a nurse said, \'No change yet\'.", m_arrFilteredJokeList.get(0).getJoke());
		assertEquals("2nd joke is still the same", "Cartoonist found dead in home. Details are sketchy.", m_arrFilteredJokeList.get(1).getJoke());
		assertEquals("3rd joke is still the same", "I wondered why the baseball was getting bigger. Then it hit me.", m_arrFilteredJokeList.get(2).getJoke());
		assertEquals("4th joke is new", "This is a test joke", m_arrFilteredJokeList.get(3).getJoke());
		assertEquals("5th joke is new", "This is a second test joke", m_arrFilteredJokeList.get(4).getJoke());
	}

	/*************************************/
	/**	Java Friend-Class Helper Method **/
	/*************************************/
	@SuppressWarnings("unchecked")
	public <T> T retrieveHiddenMember(String memberName, T type, Object sourceObj) {
		Field field = null;
		T returnVal = null;
		//Test for proper existence
		try {
			field = sourceObj.getClass().getDeclaredField(memberName);
		} catch (NoSuchFieldException e) {
			fail("The field \"" + memberName + "\" was renamed or removed. Do not rename or remove this member variable.");
		}
		field.setAccessible(true);
		
		//Test for proper type
		try {
			returnVal = (T)field.get(sourceObj);
		} catch (ClassCastException exc) {
			fail("The field \"" + memberName + "\" had its type changed. Do not change the type on this member variable.");
		}  
		
		// Boiler Plate Exception Checking. If any of these Exceptions are 
		// throw it was because this method was called improperly.
		catch (IllegalArgumentException e) {
			fail ("This is an Error caused by the UnitTest!\n Improper user of retrieveHiddenMember(...) -- IllegalArgumentException:\n Passed in the wrong object to Field.get(...)");
		} catch (IllegalAccessException e) {
			fail ("This is an Error caused by the UnitTest!\n Improper user of retrieveHiddenMember(...) -- IllegalAccessException:\n Field.setAccessible(true) should be called.");
		}
		return returnVal; 
	}


}
