package novoda.lib.sqliteprovider.provider;

import novoda.lib.sqliteprovider.sqlite.ExtendedSQLiteOpenHelper;
import novoda.lib.sqliteprovider.util.UriUtils;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class SQLiteProvider extends ContentProvider {

	private ExtendedSQLiteOpenHelper db;

	private static final String ID = "_id";
	private static final String GROUP_BY = "groupBy";
	private static final String HAVING = "having";
	private static final String LIMIT = "limit";

	/**
	 * @see android.content.ContentProvider#delete(Uri,String,String[])
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase database = getWritableDatabase();
		int count = database.delete(UriUtils.getItemDirID(uri), selection,
				selectionArgs);
		notifyUriChange(uri);
		return count;
	}

	/**
	 * @see android.content.ContentProvider#getType(Uri)
	 */
	@Override
	public String getType(Uri uri) {
		return null;
	}

	/**
	 * @see android.content.ContentProvider#insert(Uri,ContentValues)
	 */
	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		ContentValues insertValues = (initialValues != null) ? new ContentValues(
				initialValues)
				: new ContentValues();
		if (UriUtils.hasParent(uri)) {
			if (!insertValues.containsKey(UriUtils.getParentId(uri) + "_id")) {
				insertValues.put(UriUtils.getParentColumnName(uri) + "_id", UriUtils
						.getParentId(uri));
			}
		}
		SQLiteDatabase database = getWritableDatabase();
		long rowId = database.insert(UriUtils.getItemDirID(uri), null,
				insertValues);
		if (rowId > 0) {
			Uri newUri = ContentUris.withAppendedId(uri, rowId);
			notifyUriChange(newUri);
			return newUri;
		}
		throw new SQLException("Failed to insert row into " + uri);
	}

	public void notifyUriChange(Uri uri) {
		getContext().getContentResolver().notifyChange(uri, null);
	}

	/**
	 * @see android.content.ContentProvider#onCreate()
	 */
	@Override
	public boolean onCreate() {
		db = new ExtendedSQLiteOpenHelper(getContext());
		return true;
	}

	/**
	 * @see android.content.ContentProvider#query(Uri,String[],String,String[],String)
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		final SQLiteQueryBuilder builder = getSQLiteQueryBuilder();
		final String tableName = UriUtils.getItemDirID(uri);
		String groupBy = uri.getQueryParameter(GROUP_BY);
		String having = uri.getQueryParameter(HAVING);
		String limit = uri.getQueryParameter(LIMIT);
		builder.setTables(tableName);
		if (UriUtils.isItem(uri)) {
			builder.appendWhere(ID + "=" + uri.getLastPathSegment());
		} else {
			if (UriUtils.hasParent(uri)) {
				builder.appendWhereEscapeString(UriUtils.getParentColumnName(uri)
						+ ID + "=" + UriUtils.getParentId(uri));
			}
		}
		return builder.query(getReadableDatabase(), projection, selection,
				selectionArgs, groupBy, having, sortOrder, limit);
	}

	/**
	 * @see android.content.ContentProvider#update(Uri,ContentValues,String,String[])
	 */
	@Override
	public int update(Uri uri, ContentValues initialValues, String selection,
			String[] selectionArgs) {
		ContentValues insertValues = (initialValues != null) ? new ContentValues(
				initialValues)
				: new ContentValues();
		SQLiteDatabase database = getWritableDatabase();
		int rowId = database.update(UriUtils.getItemDirID(uri), insertValues,	selection, selectionArgs);
		if (rowId > 0) {
			Uri insertUri = ContentUris.withAppendedId(uri, rowId);
			notifyUriChange(insertUri);
			return rowId;
		}
		throw new SQLException("Failed to update row into " + uri);
	}

	protected SQLiteDatabase getReadableDatabase() {
		return db.getReadableDatabase();
	}

	protected SQLiteDatabase getWritableDatabase() {
		return db.getWritableDatabase();
	}

	// for testing
	SQLiteQueryBuilder getSQLiteQueryBuilder() {
		return new SQLiteQueryBuilder();
	}
}
