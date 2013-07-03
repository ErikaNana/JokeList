package edu.calpoly.android.lab4;

import java.util.Arrays;
import java.util.HashSet;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Class that provides content from a SQLite database to the application.
 * Provides joke information to a ListView through a CursorAdapter. The database stores
 * jokes in a two-dimensional table, where each row is a joke and each column is a property
 * of a joke (ID, joke text, joke rating, joke author).
 * 
 * Note that CursorLoaders require a ContentProvider, which is why this application wraps a
 * SQLite database into a content provider instead of managing the database<-->application
 * transactions manually.
 */
public class JokeContentProvider extends ContentProvider {

	/** The joke database. */
	private JokeDatabaseHelper database;
	
	/** Values for the URIMatcher. */
	private static final int JOKE_ID = 1;
	private static final int JOKE_FILTER = 2;
	
	/** The authority for this content provider. */
	private static final String AUTHORITY = "edu.calpoly.android.lab4.contentprovider";
	
	/** The database table to read from and write to, and also the root path for use in the URI matcher.
	 * This is essentially a label to a two-dimensional array in the database filled with rows of jokes
	 * whose columns contain joke data. */
	private static final String BASE_PATH = "joke_table";
	
	/** This provider's content location. Used by accessing applications to
	 * interact with this provider. */
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
	
	/** Matches content URIs requested by accessing applications with possible
	 * expected content URI formats to take specific actions in this provider. */
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	
	
	static {
		//sURI variable initialization
/*		URI format is content://edu.calpoly.android.lab4.contentprovider/joke_table/jokes/#
 * 		URIMatcher accepts URIs that deal with jokes a provide a joke ID
		matches the URI format to the value JOKE_ID = value that URIMatcher will return after finding a match
		example: jokes/5*/
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/jokes/#", JOKE_ID);
		
/*		add second URI format to URIMatcher for URIs that deal with filters and provide a filter ID
		URIMatcher correspond that URI format with the JOKE_FILTER value
		example: filters/1*/
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/filters/#", JOKE_FILTER);
	}
	
	
	@Override
	public boolean onCreate() {
		// initialize database class variable as a new JokeDatabaseHelper
		database = new JokeDatabaseHelper(getContext(), JokeDatabaseHelper.DATABASE_NAME, null, JokeDatabaseHelper.DATABASE_VERSION);
		return true;
	}

	/**
	 * Fetches rows from the joke table. Given a specified URI that contains a
	 * filter, returns a list of jokes from the joke table matching that filter in the
	 * form of a Cursor.<br><br>
	 * 
	 * Overrides the built-in version of <b>query(...)</b> provided by ContentProvider.<br><br>
	 * 
	 * For more information, read the documentation for the built-in version of this
	 * method by hovering over the method name in the method signature below this
	 * comment block in Eclipse and clicking <b>query(...)</b> in the Overrides details.
	 * 
	 * Formats a database query with special arguments to indicate which rows we want to get out of Joke table
	 * Then perform the actual query call on the database itself.
	 * This is a wrapper for the query database operation call
	 * */
	@Override
	/**
	 * URI = contains information about how to tweak the operation (query) before actually making it
	 * projection = set of column names that going to place into the Cursor we return.  cursors are lists that contain
	 * 				any table rows returned after making a database query
	 * selection = defines how to format each database operation (WHERE clause in SQL statements)
	 * 		example:
	 * 			rating = 1
	 * 			when actual call to query is made it will prove the joke table for all rows that contain rating 1 and return them
	 * 			inside of a cursor
	 * 		note: if a selection is set to null, then all rows will be returned
	 */
	  public Cursor query(Uri uri, String[] projection, String selection,
	 String[] selectionArgs, String sortOrder) {
	 
	 /** Use SQLiteQueryBuilder to perform a query for us. */
	 SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
	 
	 /** Make sure the projection is proper before querying. */
	 checkColumns(projection);
	 
	 /** Set up helper to query our jokes table. */
	 queryBuilder.setTables(JokeTable.JOKE_TABLE);
	 
	 /** Match the passed-in URI to an expected URI format. */
	 int uriType = sURIMatcher.match(uri);
	 
	 switch(uriType) {
		 case JOKE_FILTER:
		 
		 /** Fetch the last segment of the URI, which should be a filter number. */
		 String filter = uri.getLastPathSegment();
		 
		 /** Leave selection as null to fetch all rows if filter is Show All. Otherwise,
		  * fetch rows with a specific rating according to the parsed filter. */
		 if(!filter.equals(AdvancedJokeList.SHOW_ALL_FILTER_STRING)) {
			 //adds to end of selection variable when query is finally being made (don't have to make any changes to selection)
			 queryBuilder.appendWhere(JokeTable.JOKE_RATING + "=" + filter);
		 } 
		 else {
		 selection = null;
		 }
		 break;
	 
	 default:
		 throw new IllegalArgumentException("Unknown URI: " + uri);
	 }
	 
	 /** Perform the database query. */
	 SQLiteDatabase db = this.database.getWritableDatabase();
	 //get cursor, which contains rows in the joke table whose rating value matches whatever filter value we obtained from the URI and placed
	 //in the selection statement
	 Cursor cursor = queryBuilder.query(db, projection, selection, null, null, null, null);
	 
	 /** Set the cursor to automatically alert listeners (ListView) for content/view refreshing. */
	 cursor.setNotificationUri(getContext().getContentResolver(), uri);
	 
	 return cursor;
	 }
	
	/** We don't really care about this method for this application. */
	@Override
	public String getType(Uri uri) {
		return null;
	}
	
	/**
	 * Inserts a joke into the joke table. Given a specific URI tha contains a
	 * joke and the values of that joke, writes a new row in the table filled
	 * with that joke's information and gives the joke a new ID, then returns a URI
	 * containing the ID of the inserted joke.<br><br>
	 * 
	 * Overrides the built-in version of <b>insert(...)</b> provided by ContentProvider.<br><br>
	 * 
	 * For more information, read the documentation for the built-in version of this
	 * method by hovering over the method name in the method signature below this
	 * comment block in Eclipse and clicking <b>insert(...)</b> in the Overrides details.
	 * 
	 * @param uri The content://URI of the insertion request
	 * @param values A set of column_name/value pairs to add to the database
	 * */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		
		/** Open the database for writing. */
		SQLiteDatabase sqlDB = this.database.getWritableDatabase();
		
		/** Will contain the ID of the inserted joke. */
		long id = 0;
		
		/** Match the passed-in URI to an expected URI format. */
		int uriType = sURIMatcher.match(uri);
		
		/** Don't want to insert anything if the URI implies filtering and contains a filter's value, which is why
		 * JOKE_FILTER is left out */
		switch(uriType)	{
		
		/** Expects a joke ID, but we will do nothing with the passed-in ID since
		 * the database will automatically handle ID assignment and incrementation.
		 * IMPORTANT: joke ID cannot be set to -1 in passed-in URI; -1 is not interpreted
		 * as a numerical value by the URIMatcher. */
		case JOKE_ID:
/*			Parameters
			table  the table to insert the row into 
			nullColumnHack  optional; may be null. 
			values  this map contains the initial column values for the row. 
					The keys should be the column names and the values the column values */

			/** Perform the database insert, placing the joke at the bottom of the table. */
			id = sqlDB.insert(JokeTable.JOKE_TABLE, null, values);
			break;
			
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		/*set the ContentResolver to notify components attached to the joke table
		this refreshes the ListView once we give it a CursorAdapter and bind its JokeViews to a Cursor
		This will automatically go off because we just made a change to the joke table (insert new joke)*/
		getContext().getContentResolver().notifyChange(uri, null);
		
		
		/**Return a URI that contains the ID of the joke we just inserted into the joke table
		 * Database was created with statement that automatically increments and assigns IDs to rows, so ID
		 * being returned in the URI will be utilized by AdvancedJokeList later to set each Joke's ID variable */
		return Uri.parse(BASE_PATH + "/" + id);
	}

	/**
	 * Removes a row from the joke table. Given a specific URI containing a joke ID,
	 * removes rows in the table that match the ID and returns the number of rows removed.
	 * Since IDs are automatically incremented on insertion, this will only ever remove
	 * a single row from the joke table.<br><br>
	 * 
	 * Overrides the built-in version of <b>delete(...)</b> provided by ContentProvider.<br><br>
	 * 
	 * For more information, read the documentation for the built-in version of this
	 * method by hovering over the method name in the method signature below this
	 * comment block in Eclipse and clicking <b>delete(...)</b> in the Overrides details.
	 * */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		//open the database for writing
		SQLiteDatabase db = this.database.getWritableDatabase();
		
		//integer variable that will keep track of the number of rows that get deleted when delete operation is made
		int rows_deleted = 0;
		
		//match the URI format denoted by JOKE_ID
		int uriType = sURIMatcher.match(uri);
		
		switch(uriType)	{
		case JOKE_ID:
			//obtain the last path segment in the URI and store it 
			 String id = uri.getLastPathSegment();
			 //get WHERE clause for delete
			 String where_clause = JokeTable.KEY_ID + "=" + id;
			 //third parameter is string array for use in substituting in where clause, but didn't use it here
			 rows_deleted = db.delete(JokeTable.JOKE_TABLE, where_clause, null);
			break;
			
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		//set ContentResolver to notify components attached to joke tables
		//null parameter = for observer that originated the change
		//done if the number of rows delete is greater than o
		if (rows_deleted > 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		//return the number of rows delete
		return rows_deleted;
	}

	/**
	 * Updates a row in the joke table. Given a specific URI containing a joke ID and the
	 * new joke values, updates the values in the row with the matching ID in the table. 
	 * Since IDs are automatically incremented on insertion, this will only ever update
	 * a single row in the joke table.<br><br>
	 * 
	 * Overrides the built-in version of <b>update(...)</b> provided by ContentProvider.<br><br>
	 * 
	 * For more information, read the documentation for the built-in version of this
	 * method by hovering over the method name in the method signature below this
	 * comment block in Eclipse and clicking <b>update(...)</b> in the Overrides details.
	 * 
	 * @param uri The URI to query
	 * @param values A Bundle mapping from column names to new column values (NULL is a valid value)
	 * @param selection An optional filter to match rows to update
	 * */
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		//open the database for writing
		SQLiteDatabase db = this.database.getWritableDatabase();
		
		//match the URI format denoted by JOKE_ID
		int uriType = sURIMatcher.match(uri);
		
		switch(uriType)	{
		case JOKE_ID:
			//obtain the last path segment in the URI and store it 
			 String id = uri.getLastPathSegment();
			 //get WHERE clause for update
			 String where_clause = JokeTable.KEY_ID + "=" + id;
			 //update the row
			 db.update(JokeTable.JOKE_TABLE, values, where_clause, null);
			break;
			
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		return 0;
	}

	/**
	 * Verifies the correct set of columns to return data from when performing a query.
	 * It is a sanity check to make sure that the projection values match the expected values
	 * We're always going to pass in all columns into projection
	 * @param projection
	 * 						The set of columns about to be queried.
	 */
	private void checkColumns(String[] projection) {
		String[] available = { JokeTable.KEY_ID, JokeTable.JOKE_TEXT, JokeTable.JOKE_RATING,
				JokeTable.JOKE_AUTHOR };
		
		if(projection != null) {
			HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
			
			if(!availableColumns.containsAll(requestedColumns))	{
				throw new IllegalArgumentException("Unknown columns in projection");
			}
		}
	}
}
