package edu.calpoly.android.lab4;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import edu.calpoly.android.lab4.JokeView;
import edu.calpoly.android.lab4.JokeView.OnJokeChangeListener;

/**
 * Class that functions similarly to JokeListAdapter, but instead uses a Cursor.
 * A Cursor is a list of rows from a database that acts as a medium between the
 * database and a ViewGroup (in this case, a SQLite database table containing rows
 * of jokes and a ListView containing JokeViews).
 */
public class JokeCursorAdapter extends android.support.v4.widget.CursorAdapter {

	/** The OnJokeChangeListener that should be connected to each of the
	 * JokeViews created/managed by this Adapter. */
	private OnJokeChangeListener m_listener;

	/**
	 * Parameterized constructor that takes in the application Context in which
	 * it is being used and the Collection of Joke objects to which it is bound.
	 * 
	 * @param context
	 *            The application Context in which this JokeListAdapter is being
	 *            used.
	 * 
	 * @param jokeCursor
	 *            A Database Cursor containing a result set of Jokes which
	 *            should be bound to JokeViews.
	 *            
	 * @param flags
	 * 			  A list of flags that decide this adapter's behavior.
	 */
	public JokeCursorAdapter(Context context, Cursor jokeCursor, int flags) {
		super(context, jokeCursor, flags);
	}

	/**
	 * Mutator method for changing the OnJokeChangeListener.
	 * 
	 * @param listener
	 *            The OnJokeChangeListener that will be notified when the
	 *            internal state of any Joke contained in one of this Adapters
	 *            JokeViews is changed.
	 */
	public void setOnJokeChangeListener(OnJokeChangeListener mListener) {
		this.m_listener = mListener;
	}
	/**
	 * Binds an existing view to the data pointed to by cursor
	 * @param view Existing view, returned earlier by newView
	 * @param context Interface to application's global information
	 * @param cursor The cursor from which to get the data.  The cursor is already moved
	 * 				 to the correct position	
	 */
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		//extract a Joke out from the cursor
		String joke_text = cursor.getString(JokeTable.JOKE_COL_TEXT);
		int joke_rating = cursor.getInt(JokeTable.JOKE_COL_RATING);
		String joke_author = cursor.getString(JokeTable.JOKE_COL_AUTHOR);
		long joke_id = cursor.getLong(JokeTable.JOKE_COL_ID);
		Joke joke = new Joke(joke_text,joke_author,joke_rating,joke_id);
		
		// the view's on JokeChangeListener to null to avoid refreshing the data when the joke data
		//is not actually refreshing
		JokeView joke_view = (JokeView) view;
		joke_view.setOnJokeChangeListener(null);
		
		//set the view's Joke reference
		joke_view.setJoke(joke);
		
		//attach the view's onJokeChangeListener to m_Listener (will help with refreshing)
		joke_view.setOnJokeChangeListener(m_listener);
		
	}
	/**
	 * Creates a new view and populates it with underlying data from a cursor
	 * @param context Interface to application's global information
	 * @param cursor The cursor from which to get the data.  Cursor is already moved to the correct position.
	 * @param parent The parent to which the new view is attached to
	 * @return the newly created view
	 */
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		//extract a Joke out from the cursor
		String joke_text = cursor.getString(JokeTable.JOKE_COL_TEXT);
		int joke_rating = cursor.getInt(JokeTable.JOKE_COL_RATING);
		String joke_author = cursor.getString(JokeTable.JOKE_COL_AUTHOR);
		long joke_id = cursor.getLong(JokeTable.JOKE_COL_ID);
		
		Joke joke = new Joke(joke_text,joke_author,joke_rating,joke_id);
		
		//make a new JokeView
		JokeView joke_view = new JokeView(context,joke);
		
		//return the JokeView
		return joke_view;
	}
}