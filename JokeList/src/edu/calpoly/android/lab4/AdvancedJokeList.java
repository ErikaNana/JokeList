package edu.calpoly.android.lab4;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;

import edu.calpoly.android.lab4.JokeView.OnJokeChangeListener;
//import android.util.Log;

//need to extend SherlockFragmentActivity instead, otherwise can't use a LoaderManager
public class AdvancedJokeList extends SherlockFragmentActivity implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>, OnJokeChangeListener {
	
	/** Contains the name of the Author for the jokes. */
	protected String m_strAuthorName;

	/** Adapter used to bind an AdapterView to List of Jokes. */
	protected JokeCursorAdapter m_jokeAdapter;

	/** ViewGroup used for maintaining a list of Views that 99each display Jokes. */
	protected ListView m_vwJokeLayout;

	/** EditText used for entering text for a new Joke to be added to m_arrJokeList. */
	protected EditText m_vwJokeEditText;

	/** Button used for creating and adding a new Joke to m_arrJokeList using the
	 *  text entered in m_vwJokeEditText. */
	protected Button m_vwJokeButton;
	
	/** Menu used for filtering Jokes. */
	protected Menu m_vwMenu;

	/** Background Color values used for alternating between light and dark rows
	 *  of Jokes. Add a third for text color if necessary. */
	protected int m_nDarkColor;
	protected int m_nLightColor;

	protected int m_nTextColor;
	
	/** Used to get specific JokeView when list of jokes is long-clicked */
	protected JokeView current_JokeView;
	
	/** Saved Filter Value*/
	protected static String SAVED_FILTER_VALUE;
	/**
	 * Context-Menu MenuItem IDs.
	 * IMPORTANT: You must use these when creating your MenuItems or the tests
	 * used to grade your submission will fail. These are commented out for now.
	 * These help identify which type of filter has been chosen when a user selects a SubMenu item
	 */
	protected static final int FILTER = Menu.FIRST;
	protected static final int FILTER_LIKE = SubMenu.FIRST;
	protected static final int FILTER_DISLIKE = SubMenu.FIRST + 1;
	protected static final int FILTER_UNRATED = SubMenu.FIRST + 2;
	protected static final int FILTER_SHOW_ALL = SubMenu.FIRST + 3;
	
	//filter value
	protected int filter = FILTER_SHOW_ALL;
	
	/** The String representation of the Show All filter. The Show All case
	 * needs a String representation of a value that is different from
	 * Joke.LIKE, Joke.DISLIKE and Joke.UNRATED. The actual value doesn't
	 * matter as long as it's different, since the WHERE clause is set to
	 * null when making database operations under this setting. */
	public static final String SHOW_ALL_FILTER_STRING = "" + FILTER_SHOW_ALL;
	
	/** Key to store text m_vwJokeEditText in SharedPreferences */
	protected static final String SAVED_EDIT_TEXT = "saved_edit_text";
	
	/** The ID of the CursorLoader to be initialized in the LoaderManager and used to load a Cursor. */
	private static final int LOADER_ID = 1;
	
	//implement the ActionMode.Callback
	protected com.actionbarsherlock.view.ActionMode actionMode;
	protected com.actionbarsherlock.view.ActionMode.Callback callback = new com.actionbarsherlock.view.ActionMode.Callback() {
				
		/**
		 *Called when the action mode is created; startActionMode() was called
		 */
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			//inflate action menu, for the context menu items
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.actionmenu, menu);
			return true;
		}

		/**
		 * Called each time the action mode is shown.  Always called after on CreateAction
		 */
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false; //Return false since nothing is done
		}
		
		/**
		 * Called when the user selects a contextual menu item
		 */
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			/*set ListView to have an OnItemLongClickListener to trigger the firing of
			 * the Action Mode Callback*/
			
			switch(item.getItemId()) {
				case R.id.menu_remove:
					//retrieve the Joke from the currently selected JokeView
					Joke current_joke = current_JokeView.getJoke();
					removeJoke(current_joke);
					mode.finish(); //Action done, so close the CAB
					return true;
				default:
					return false;
			}
		}
		
		/**
		 * Called when the user exits the action mode
		 */
		@Override
		public void onDestroyActionMode(ActionMode mode) {
			//Set Action Mode to terminate after the Remove item is selected
			actionMode = null;
			
		}
	};

	/** Refreshes the JokeViews and their corresponding values in the database automatically
	 * Call this method whenever there are changes made to any of the jokes in the list, such as rating
	 * being changed or a new joke being added or joke being removed*/
	public void fillData() {
		//restart the CursorLoader and refresh the cursor in JokeCursorAdapter
		/*
		 * restart loader: params: id (unique identifier for loader) args (optional args to supply loader) callback 
		 * (interface of the LoaderManger will call to report about changes in the state of the loader
		 */

		this.getSupportLoaderManager().restartLoader(AdvancedJokeList.LOADER_ID, null, this);
		
		//set layout's adapter to m_jokeAdapter because JokeCursorAdapter is refreshed and new 
		this.m_vwJokeLayout.setAdapter(m_jokeAdapter);
	}
	
	/**
	 * Set ListView to have an OnItemLongClickListener
	 */
	protected void initLongClickListener() {
		m_vwJokeLayout.requestFocus();
		m_vwJokeLayout.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> av, View view,
					int pos, long id) {
				if (actionMode != null) {
					return false;
				}
				
				//for future reference
				current_JokeView = (JokeView) view;

				//Start the CAB using the ActionMode.Callback defined above
				actionMode = startActionMode(callback);
				return true;
			}
		});
	}
	
	/**
	 * Initializes everything when app is created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.initLayout();
		
		//getting the colors from the XML file, which automatically generates code in
		//R.java.  Then use R.java to get the colors
		Resources resources = this.getResources();
		this.m_nDarkColor = resources.getInteger(R.color.dark);
		this.m_nLightColor = resources.getInteger(R.color.light);
		this.m_nTextColor = resources.getInteger(R.color.text);
		
	
		//change type of m_jokeAdapter to JokeCursorAdapter
		/**
		 * No cursor to give it yet, so null
		 * flags is used to determine the behavior of the adapter, but can't use the constants so just put it to 0
		 */
		this.m_jokeAdapter = new JokeCursorAdapter(this,null,0);
		
		//set to onJokeChangeListenr for the adapter to "this"
		this.m_jokeAdapter.setOnJokeChangeListener(this);
		/**
		 * prepare a loader
		 * 
		 * initLoader parameters
		 * id: a unique id that identifies the loader
		 * null: optional argument to supply to the loader
		 * this: a LoaderManager.LoaderCallbacks implementation, which the LoadManager calls to report loader events.
		 * 		 AdvancedJokeList implements the LoaderManager.LoaderCallbacks interface, so pass in this
		 * This method will initialize whatever loader we want in the onCreateLoader() method, but need to specify that it is 
		 * indeed a CursorLoader being loaded
		 */
		
		//set m_vwJokeLayout's adapter to be m_jokeAdapter
		this.m_vwJokeLayout.setAdapter(m_jokeAdapter);
		
		//restoring SharedPreference Data
		
		//retrieve the private SharedPreferences belong to this activity
		SharedPreferences preferences = getPreferences(Activity.MODE_PRIVATE);
		//retrieve the text that was saved
		String retrieved_text = preferences.getString(SAVED_EDIT_TEXT, "");
		//set text in m_vwJokeEditText to the text retrieved
		this.m_vwJokeEditText.setText(retrieved_text);
		this.m_jokeAdapter.notifyDataSetChanged();	
		
		//refresh so can "show all" with jokes from before
		fillData();
	}
	
	/**
	 * Filters the jokes based on the filter selection in the Action Bar and sets the filter
	 * variable
	 */
	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		switch(item.getItemId()) {
			//set the filter for sorting and display the appropriate jokes
			case R.id.submenu_like:
				filter = AdvancedJokeList.FILTER_LIKE;
				//refresh 
				fillData();
				//change menu's text
				onPrepareOptionsMenu(m_vwMenu);
				this.m_jokeAdapter.notifyDataSetChanged();				
				return true;

			case R.id.submenu_dislike:
				filter = AdvancedJokeList.FILTER_DISLIKE;
				fillData();
				onPrepareOptionsMenu(m_vwMenu);
				this.m_jokeAdapter.notifyDataSetChanged();	
				return true;
				
			case R.id.submenu_unrated:
				filter = AdvancedJokeList.FILTER_UNRATED;
				fillData();
				onPrepareOptionsMenu(m_vwMenu);
				this.m_jokeAdapter.notifyDataSetChanged();	
				return true;
				
			case R.id.submenu_show_all:
				filter = AdvancedJokeList.FILTER_SHOW_ALL;
				fillData();
				onPrepareOptionsMenu(m_vwMenu);
				this.m_jokeAdapter.notifyDataSetChanged();
				return true;
		}
		return false;
	};
	
	/**
	 *Restore instance data and override the default implementation 
	 */
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		//call super version to ensure that other UI state is preserved as well
		super.onRestoreInstanceState(savedInstanceState);
	}
	/**
	 * Create the filter menu
	 */
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		//use ABS's compatibility package
        MenuInflater inflater = getSupportMenuInflater();
        
        //menu = menu to inflate to
        inflater.inflate(R.menu.mainmenu, menu);
        //initialize m_vwMenu
        this.m_vwMenu = menu;
        return true;
    }
	/**
	 * Save the instance data and override default implementation
	 */
	@Override
	 protected void onSaveInstanceState(Bundle outState) {
		 //store the current value of filter in outState
		 outState.putInt(AdvancedJokeList.SAVED_FILTER_VALUE, filter);
		 //call super version of method to ensure that other UI state is preserved as well
		 super.onSaveInstanceState(outState);
	 }
	/**
	 * Method is used to encapsulate the code that initializes and sets the
	 * Layout for this Activity.
	 */
	protected void initLayout() {
		setContentView(R.layout.advanced);
		this.m_vwJokeLayout = (ListView) findViewById(R.id.jokeListViewGroup);
		this.m_vwJokeEditText = (EditText) findViewById(R.id.newJokeEditText);
		this.m_vwJokeButton = (Button) findViewById(R.id.addJokeButton);
		initAddJokeListeners();
		initLongClickListener();
	}

	/**
	 * Method is used to encapsulate the code that initializes and sets the
	 * Event Listeners which will respond to requests to "Add" a new Joke to the
	 * list.
	 */
	protected void initAddJokeListeners() {
		/**
		 * setup the onClickListener for the "Add Joke" button.  
		 * pass in reference to an Anonymous Inner Class that implements the OnClickListener interface
		 * anonymous inner class = one-time use class that implements some interface
		 * you declare the class and instantiate it in one motion*/
		
		m_vwJokeButton.setOnClickListener(new OnClickListener() {
			  public void onClick(View view) {
				  //retrieve text entered by user
				  String input_text = m_vwJokeEditText.getText().toString();

				  Joke joke = new Joke(input_text, m_strAuthorName);
				  //clear the text in EditText
				  m_vwJokeEditText.setText("");
				  //add joke
				  addJoke(joke);
				  
				 //hide the Soft Keyboard that appears when the EditText has focus:
				 InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromInputMethod(m_vwJokeEditText.getWindowToken(), 0);
			 } 
			});
		//implementation for the enter key
        m_vwJokeEditText.setOnKeyListener(new OnKeyListener(){

            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
                if (arg2.getAction() == KeyEvent.ACTION_DOWN){
                	if (arg1 == KeyEvent.KEYCODE_ENTER) {
                        String input_text = m_vwJokeEditText.getText().toString();
                        Joke joke = new Joke(input_text, m_strAuthorName);
                        addJoke(joke);
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                        imm.hideSoftInputFromWindow(
                                m_vwJokeEditText.getWindowToken(), 0);
                	}
                }
                m_vwJokeEditText.setText("");
                return false;

            }

        });
	}
	/**
	 * Save data in a private SharedPreferences object
	 */
	@Override
	protected void onPause() {
		//always call the superclass method first
		super.onPause();
		//retrieve the private SharedPreferences belonging to this Activity
		SharedPreferences preferences = getPreferences(Activity.MODE_PRIVATE);
	
		//create a new Editor for these preferences, through which you can make modifications
		//to the data in the preferences
		Editor editor = preferences.edit();
		
		//store the text in m_vwJokeEditText in the SharedPreferences
		String text = this.m_vwJokeEditText.getText().toString();
		editor.putString(AdvancedJokeList.SAVED_EDIT_TEXT, text);
		/*need to call this to have any changes performed in the Editor show up in the 
		* Shared preferences*/
		editor.commit();
	}
	
	/**
	 * Returns the proper String of the passed in filter
	 */
	protected String getMenuTitleChange() {
		//get the titles from the resources file
		Resources resources = this.getResources();

		switch(filter) {
			case(FILTER_LIKE):{
				return resources.getString(R.string.like_menuitem);
			}
			case(FILTER_DISLIKE):{
				return resources.getString(R.string.dislike_menuitem);
			}
			case(FILTER_UNRATED):{
				return resources.getString(R.string.unrated_menuitem);
			}
			default:{
				return resources.getString(R.string.show_all_menuitem);
			}
		}
	}
	
	/**
	 * Changes the title text of the Filter menu item
	 * @param menu	options menu as last shown
	 */
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		//get filter menu item
		MenuItem filter = menu.findItem(R.id.menu_filter);
	
		//get the name for the action bar based on the current filter
		String name = getMenuTitleChange();
		//set the title text of the filter
		filter.setTitle(name);
		
		//set the m_vwMenu variable
		m_vwMenu = menu;
		
		//ensure that other menu state is preserved as well
		super.onPrepareOptionsMenu(menu);
		
		//return true so menu is displayed
		return true;
	}
	/**
	 * Method used for encapsulating the logic necessary to properly add a new
	 * Joke to m_arrJokeList, and display it on screen.
	 * @param joke The Joke to add to list of Jokes.
	 */
	protected void addJoke(Joke joke) {
		Uri uri;
		uri = Uri.withAppendedPath(JokeContentProvider.CONTENT_URI, "/jokes/" + joke.getID());
		ContentValues contents = new ContentValues();
		contents.put(JokeTable.JOKE_TEXT, joke.getJoke());
		contents.put(JokeTable.JOKE_AUTHOR, joke.getAuthor());
		contents.put(JokeTable.JOKE_RATING, joke.getRating());
		
		//put the contents in the database for that particular joke
		Uri newUri = this.getContentResolver().insert(uri, contents);
		
		//set joke ID to the return value of insertion call (insert returns the ID of the newly inserted row
		Long automated_joke_id = Long.valueOf(newUri.getLastPathSegment());
		joke.setID(automated_joke_id);
		
		//call fillData() to complete the refreshing cycle
		fillData();
	}
	/**
	 * Will use the LoaderManager's powers to initialize a loader for us, but the CursorLoader requires a ContentProvider
	 * Instantiate and return a new Loader for the given id
	 * @param id The ID whose loader is to be created
	 * @param args Any arguments supplied by the caller
	 * @return A new loader instance that is ready to start loading
	 */
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		//serve as projection for the CursorLoader, name in order as they appear in JokeTable
		String [] projection = {JokeTable.KEY_ID, JokeTable.JOKE_TEXT,
								JokeTable.JOKE_RATING, JokeTable.JOKE_AUTHOR};
		
		//get the rating in string form
		String rating = ratingAsString();
		//based on the rating, create the URI for appropriate filter
		Uri filter_uri = Uri.withAppendedPath(JokeContentProvider.CONTENT_URI, "/filters/" + rating);
		
		/*create new Cursor Loader and initialize it with the uri and projection
		 * CursorLoader(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
		 * Creates a fully-specified CursorLoader.
		 * The CursorLoader will load the Cursor after making a hidden automated query call*/
		CursorLoader cursor_loader = new CursorLoader(this.getBaseContext(),filter_uri,projection, null, null, null);
		return cursor_loader;
	}
	
	/**
	 * Called when a previously created loader has finished its load
	 * @param loader The Loader that has finished
	 * @param data The data generated by the Loader
	 */
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		//call swapCursor on adapter since cursor is being placed in CursorAdapter (just how it works so that old Cursor is not closed)
		//swapCursor swaps in a new Cursor, returning the old Cursor but doesn't close the old Cursor (param newCursor, which is cursor to be used)
		this.m_jokeAdapter.swapCursor(data);
		//set it back to "this"because made it null in onJokeChanged
		this.m_jokeAdapter.setOnJokeChangeListener(this);
	}

	/**
	 * Called when a previously created loader is being reset, and thus making the data unavailable
	 * The application at this point should remove any references it has to the Loader's data
	 * @param loader The Loader that is being reset
	 */
	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		//pass in null since loaded object is about to be reloaded and therefore the data behind it needs to be invalidated
		this.m_jokeAdapter.swapCursor(null);
	}
	/** Helper method for URI
	 * Depending on the currently selected filter (m_nFilter), it responds the corresponding
	 * rating as a string (Like = 1, Dislike = 2, Unrated = 0)
	 */
	public String ratingAsString () {
		switch(filter) {
			case (FILTER_LIKE):{
				return "" + Joke.LIKE;
			}
			case (FILTER_DISLIKE):{
				return "" + Joke.DISLIKE;
			}
			case (FILTER_UNRATED):{
				return "" + Joke.UNRATED;
			}

			case (FILTER_SHOW_ALL):{
				//this should be "4"
				return "4";
			}
			default:
				return "13";
		}
	}

	/**
	 * Whenever a value changes in a JokeView the change will immediately sync back to the database
	 */
	@Override
	public void onJokeChanged(JokeView view, Joke joke) {
		// uri tells content provider's URIMatcher in update() that the row with joke.getId() needs to be updated
		Uri uri;
		//does joke.getID() have to be an int?
		uri = Uri.withAppendedPath(JokeContentProvider.CONTENT_URI, "/jokes/" + joke.getID());
		
		//put joke text, rating and author inside of ContentValues, will be added to database for designated joke
		ContentValues contents = new ContentValues();
		contents.put(JokeTable.JOKE_TEXT, joke.getJoke());
		contents.put(JokeTable.JOKE_RATING,joke.getRating());
		contents.put(JokeTable.JOKE_AUTHOR, joke.getAuthor());


		//update the database with the changed joke
		this.getContentResolver().update(uri, contents, null, null);
		
		/** 
		 * need to set the listener in the adapter to null
		 * otherwise app will loop definitely due to the OnJokeChangeListener both 
		 * receiving news of a change and sending one itself*/
		
		this.m_jokeAdapter.setOnJokeChangeListener(null);
		
		//refreshes everything
		this.fillData();
	}
	
	/**
	 * Removes the joke
	 */
	public void removeJoke(Joke joke) {
		//URI that will tell content provider's URIMatcher in delete() that the row with joke.getID() needs to be deleted
		Uri uri;
		uri = Uri.withAppendedPath(JokeContentProvider.CONTENT_URI, "/jokes/" + joke.getID());
		
		this.getContentResolver().delete(uri, null, null);
		
		//refresh everything
		fillData();
	}

}