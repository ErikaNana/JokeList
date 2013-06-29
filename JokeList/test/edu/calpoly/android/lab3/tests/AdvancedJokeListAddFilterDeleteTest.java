package edu.calpoly.android.lab3.tests;

import java.lang.reflect.Field;
import java.util.ArrayList;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;

import edu.calpoly.android.lab3.AdvancedJokeList;
import edu.calpoly.android.lab3.Joke;
import edu.calpoly.android.lab3.JokeListAdapter;
import edu.calpoly.android.lab3.JokeView;
import edu.calpoly.android.lab3.R;

public class AdvancedJokeListAddFilterDeleteTest extends ActivityInstrumentationTestCase2<AdvancedJokeList> {
	
	private AdvancedJokeList ajl;
	
	public AdvancedJokeListAddFilterDeleteTest() {
		super(AdvancedJokeList.class);
	}
	
	@Override
	protected void setUp()
	{
		ajl = this.getActivity();
	}
	
	/**
	 * Tests deletion of preloaded jokes.
	 */
	@SmallTest
	public void testDeleteDefaultJokes(){
		Menu menu = null;
		ArrayList<Joke> type = null;
		JokeListAdapter jla_type = null;
		final ArrayList<Joke> m_arrFilteredJokeList = this.retrieveHiddenMember("m_arrFilteredJokeList", type,
			getActivity());
		final ArrayList<Joke> m_arrJokeList = this.retrieveHiddenMember("m_arrJokeList", type,
			getActivity());
		ListView m_vwJokeLayout = null;
		m_vwJokeLayout = this.retrieveHiddenMember("m_vwJokeLayout", m_vwJokeLayout, getActivity());
		final JokeListAdapter m_jokeAdapter = this.retrieveHiddenMember("m_jokeAdapter", jla_type, getActivity());
		assertEquals("Should be 3 default jokes", 3, m_arrFilteredJokeList.size());

		//Remove first joke in the list
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				m_arrFilteredJokeList.remove(0);
				m_arrJokeList.remove(0);
				m_jokeAdapter.notifyDataSetChanged();
			}
			});
		// wait for the request to go through
		getInstrumentation().waitForIdleSync();
		assertEquals("Should be only 2 jokes left", 2, m_arrFilteredJokeList.size());
		assertEquals("Joke 1 should be the old Joke 2", "Cartoonist found dead in home. Details are sketchy.", m_arrFilteredJokeList.get(0).getJoke());
		assertEquals("Joke 2 should be the old Joke 3", "I wondered why the baseball was getting bigger. Then it hit me.", m_arrFilteredJokeList.get(1).getJoke());
		
		//Filter to Show All jokes
		menu = this.retrieveHiddenMember("m_vwMenu", menu, getActivity());
		MenuItem filter = menu.getItem(0);
		SubMenu sm = filter.getSubMenu();
		final MenuItem showAll = sm.getItem(3);
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				ajl.onOptionsItemSelected(showAll);
			}
			});
		// wait for the request to go through
		getInstrumentation().waitForIdleSync();
		assertEquals("Should be 2 jokes in the list still", 2, m_arrFilteredJokeList.size());
		
		//Remove first joke in the list again
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				m_arrFilteredJokeList.remove(0);
				m_arrJokeList.remove(0);
				m_jokeAdapter.notifyDataSetChanged();
			}
			});
		// wait for the request to go through
		getInstrumentation().waitForIdleSync();
		assertEquals("Should be only 1 joke left", 1, m_arrFilteredJokeList.size());
		assertEquals("Joke 1 should be the old Joke 2", "I wondered why the baseball was getting bigger. Then it hit me.", m_arrFilteredJokeList.get(0).getJoke());
		
		//Remove only remaining joke in the list
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				m_arrFilteredJokeList.remove(0);
				m_arrJokeList.remove(0);
				m_jokeAdapter.notifyDataSetChanged();
			}
			});
		// wait for the request to go through
		getInstrumentation().waitForIdleSync();
		assertEquals("Should be 0 jokes left", 0, m_arrFilteredJokeList.size());
		assertEquals("Should be 0 jokes left in base list too", 0, m_arrJokeList.size());
		
		//Filter Show All again
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				ajl.onOptionsItemSelected(showAll);
			}
			});
		// wait for the request to go through
		getInstrumentation().waitForIdleSync();
		assertEquals("Should be 0 jokes in the list", 0, m_arrFilteredJokeList.size());
		assertEquals("Should be 0 jokes in the base list too", 0, m_arrJokeList.size());
	}
	
	/**
	 * Tests deletion of preloaded and user-added jokes.
	 */
	@SmallTest
	public void testDeleteNewDefaultJokes(){
		ArrayList<Joke> type = null;
		JokeListAdapter jla_type = null;
		final ArrayList<Joke> m_arrFilteredJokeList = this.retrieveHiddenMember("m_arrFilteredJokeList", type,
			getActivity());
		final ArrayList<Joke> m_arrJokeList = this.retrieveHiddenMember("m_arrJokeList", type,
			getActivity());
		ListView m_vwJokeLayout = null;
		m_vwJokeLayout = this.retrieveHiddenMember("m_vwJokeLayout", m_vwJokeLayout, getActivity());
		final JokeListAdapter m_jokeAdapter = this.retrieveHiddenMember("m_jokeAdapter", jla_type, getActivity());
		assertEquals("Should be 3 default jokes", 3, m_arrFilteredJokeList.size());

		//Remove first joke in the list
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				m_arrFilteredJokeList.remove(0);
				m_arrJokeList.remove(0);
				m_jokeAdapter.notifyDataSetChanged();
			}
			});
		// wait for the request to go through
		getInstrumentation().waitForIdleSync();
		assertEquals("Should be only 2 jokes left", 2, m_arrFilteredJokeList.size());
		assertEquals("Joke 1 should be the old Joke 2", "Cartoonist found dead in home. Details are sketchy.", m_arrFilteredJokeList.get(0).getJoke());
		assertEquals("Joke 2 should be the old Joke 3", "I wondered why the baseball was getting bigger. Then it hit me.", m_arrFilteredJokeList.get(1).getJoke());
		
		//Add new joke
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
		assertEquals("Should be 3 jokes now", 3, m_arrFilteredJokeList.size());
		assertEquals("Joke 1 should be the same", "Cartoonist found dead in home. Details are sketchy.", m_arrFilteredJokeList.get(0).getJoke());
		assertEquals("Joke 2 should be the same", "I wondered why the baseball was getting bigger. Then it hit me.", m_arrFilteredJokeList.get(1).getJoke());
		assertEquals("Joke 3 should be the newly added joke", "This is a test joke", m_arrFilteredJokeList.get(2).getJoke());
		
		//Remove newly added joke
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				m_arrFilteredJokeList.remove(2);
				m_arrJokeList.remove(2);
				m_jokeAdapter.notifyDataSetChanged();
			}
			});
		// wait for the request to go through
		getInstrumentation().waitForIdleSync();
		assertEquals("Should be only 2 jokes left", 2, m_arrFilteredJokeList.size());
		assertEquals("Joke 1 should be the same", "Cartoonist found dead in home. Details are sketchy.", m_arrFilteredJokeList.get(0).getJoke());
		assertEquals("Joke 2 should be the same", "I wondered why the baseball was getting bigger. Then it hit me.", m_arrFilteredJokeList.get(1).getJoke());
	}
	
	/**
	 * Tests deletion of preloaded and user-added jokes after rating and filtering them.
	 */
	@SmallTest
	public void testDeleteRateFilterNewDefaultJokes(){
		Menu menu = null;
		ArrayList<Joke> type = null;
		JokeListAdapter jla_type = null;
		final ArrayList<Joke> m_arrFilteredJokeList = this.retrieveHiddenMember("m_arrFilteredJokeList", type,
			getActivity());
		final ArrayList<Joke> m_arrJokeList = this.retrieveHiddenMember("m_arrJokeList", type,
			getActivity());
		ListView m_vwJokeLayout = null;
		m_vwJokeLayout = this.retrieveHiddenMember("m_vwJokeLayout", m_vwJokeLayout, getActivity());
		final JokeListAdapter m_jokeAdapter = this.retrieveHiddenMember("m_jokeAdapter", jla_type, getActivity());
		assertEquals("Should be 3 default jokes", 3, m_arrFilteredJokeList.size());

		//Remove first joke in the list
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				m_arrFilteredJokeList.remove(0);
				m_arrJokeList.remove(0);
				m_jokeAdapter.notifyDataSetChanged();
			}
			});
		// wait for the request to go through
		getInstrumentation().waitForIdleSync();
		assertEquals("Should be only 2 jokes left", 2, m_arrFilteredJokeList.size());
		assertEquals("Joke 1 should be the old Joke 2", "Cartoonist found dead in home. Details are sketchy.", m_arrFilteredJokeList.get(0).getJoke());
		assertEquals("Joke 2 should be the old Joke 3", "I wondered why the baseball was getting bigger. Then it hit me.", m_arrFilteredJokeList.get(1).getJoke());
		
		//Add new joke
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
		assertEquals("Should be 3 jokes now", 3, m_arrFilteredJokeList.size());
		assertEquals("Joke 1 should be the same", "Cartoonist found dead in home. Details are sketchy.", m_arrFilteredJokeList.get(0).getJoke());
		assertEquals("Joke 2 should be the same", "I wondered why the baseball was getting bigger. Then it hit me.", m_arrFilteredJokeList.get(1).getJoke());
		assertEquals("Joke 3 should be the newly added joke", "This is a test joke", m_arrFilteredJokeList.get(2).getJoke());
		
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
		
		//Check Like on third (newly added) JokeView
		JokeView jv3 = (JokeView)m_jokeAdapter.getView(2, null, null);
		final RadioButton m_vwLikeButton3 = (RadioButton)jv3.findViewById(R.id.likeButton);
		assertFalse("Check to make sure Like is unchecked", m_vwLikeButton3.isChecked());
		getActivity().runOnUiThread(new Runnable() {
		public void run() {
			m_vwLikeButton3.performClick();
		}
		});
		getInstrumentation().waitForIdleSync();
		assertTrue("Check to make sure Like is checked", m_vwLikeButton3.isChecked());
		
		//Filter jokes by dislike
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
		
		//Add new joke
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				m_vwJokeEditText.setText("This is another test joke");
				m_vwJokeButton.performClick();
			}
			});
		// wait for the request to go through
		getInstrumentation().waitForIdleSync();
		assertEquals("Should be 2 jokes now", 2, m_arrFilteredJokeList.size());
		assertEquals("Joke 1 should be the disliked joke", "Cartoonist found dead in home. Details are sketchy.", m_arrFilteredJokeList.get(0).getJoke());
		assertEquals("Joke 2 should be the newly added joke", "This is another test joke", m_arrFilteredJokeList.get(1).getJoke());
		
		//Remove first joke in the list
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				m_arrFilteredJokeList.remove(0);
				m_arrJokeList.remove(0);
				m_jokeAdapter.notifyDataSetChanged();
			}
			});
		// wait for the request to go through
		getInstrumentation().waitForIdleSync();
		assertEquals("Should be only 1 joke left", 1, m_arrFilteredJokeList.size());
		assertEquals("Joke 1 should be the old Joke 2", "This is another test joke", m_arrFilteredJokeList.get(0).getJoke());
		
		//Filter jokes by Show All
		final MenuItem showAll = sm.getItem(3);
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				ajl.onOptionsItemSelected(showAll);
			}
			});
		// wait for the request to go through
		getInstrumentation().waitForIdleSync();
		assertEquals("Should be 3 jokes in the list now", 3, m_arrFilteredJokeList.size());
		assertEquals("Joke 1 should be a default one", "I wondered why the baseball was getting bigger. Then it hit me.", m_arrFilteredJokeList.get(0).getJoke());
		assertEquals("Joke 2 should be an added joke", "This is a test joke", m_arrFilteredJokeList.get(1).getJoke());
		assertEquals("Joke 3 should be an added joke", "This is another test joke", m_arrFilteredJokeList.get(2).getJoke());
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
