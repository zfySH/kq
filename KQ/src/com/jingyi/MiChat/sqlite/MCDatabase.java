package com.jingyi.MiChat.sqlite;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public abstract class MCDatabase {
	
	public static final int SQL_ERROR = -100;
	protected SQLiteDatabase mDB;
	private HashMap<String, Boolean> tableExist = new HashMap<String, Boolean>();
	private Lock lock = new ReentrantLock();

	public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy,String limit) {
		Cursor cursor = null;
		try {
			cursor = mDB.query(table, columns, selection, selectionArgs, groupBy, having, orderBy,limit);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cursor;
	}
	
	public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
		Cursor cursor = null;
		try {
			cursor = mDB.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cursor;
	}
	
	public boolean isOpen(){
		if (mDB == null)
			return false;
		return mDB.isOpen();
	}
	
	public long insert(String table, String nullColumnHack, ContentValues values) {
		if (!tableIsExist(table))
			return SQL_ERROR;
		long result = -1;
		try {
			if (!inTransaction()){
				lock.lock();
			}
			result = mDB.insert(table, nullColumnHack, values);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if (!inTransaction()){
				lock.unlock();
			}
		}
		return result;
	}

	public void beginTransaction() {
		try {
			lock.lock();
			mDB.beginTransaction();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean inTransaction() {
		try {
			return mDB.inTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void setTransactionSuccessful() {
		try {
			mDB.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void endTransaction() throws Exception {
		try {
			mDB.endTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			lock.unlock();
		}
	}

	public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
		if (!tableIsExist(table))
			return SQL_ERROR;
		int result = -1;
		try {
			if (!inTransaction()){
				lock.lock();
			}
			result = mDB.update(table, values, whereClause, whereArgs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (!inTransaction()){
				lock.unlock();
			}
		}
		return result;
	}

	public int delete(String table, String whereClause, String[] whereArgs) {
		if (!tableIsExist(table))
			return SQL_ERROR;
		int result = 0;
		try {
			if (!inTransaction()){
				lock.lock();
			}
			result = mDB.delete(table, whereClause, whereArgs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (!inTransaction()){
				lock.unlock();
			}
		}
		return result;
	}

	public long replace(String table, String nullColumnHack, ContentValues initialValues) {
		if (!tableIsExist(table))
			return SQL_ERROR;
		long result = -1;
		try {
			if (!inTransaction()){
				lock.lock();
			}
			result = mDB.replace(table, nullColumnHack, initialValues);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (!inTransaction()){
				lock.unlock();
			}
		}
		return result;
	}

	public void execSQL(String sql) {
		try {
			if (!inTransaction()){
				lock.lock();
			}
			mDB.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (!inTransaction()){
				lock.unlock();
			}
		}
	}

	public void execSQL(String sql, Object[] bindArgs) {
		try {
			if (!inTransaction()){
				lock.lock();
			}
			mDB.execSQL(sql, bindArgs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (!inTransaction()){
				lock.unlock();
			}
		}
	}

	public Cursor rawQuery(String sql, String[] selectionArgs) {
		Cursor cursor = null;
		try {
			cursor = mDB.rawQuery(sql, selectionArgs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cursor;
	}


	private static final String[] CONFLICT_VALUES = new String[]{"", " OR ROLLBACK ", " OR ABORT ", " OR FAIL ", " OR IGNORE ", " OR REPLACE "};

	/**
	 * General method for inserting a row into the database.
	 * 
	 * @param table
	 *            the table to insert the row into
	 * @param nullColumnHack
	 *            optional; may be <code>null</code>. SQL doesn't allow
	 *            inserting a completely empty row without naming at least one
	 *            column name. If your provided <code>initialValues</code> is
	 *            empty, no column names are known and an empty row can't be
	 *            inserted. If not set to null, the <code>nullColumnHack</code>
	 *            parameter provides the name of nullable column name to
	 *            explicitly insert a NULL into in the case where your
	 *            <code>initialValues</code> is empty.
	 * @param initialValues
	 *            this map contains the initial column values for the row. The
	 *            keys should be the column names and the values the column
	 *            values
	 * @param conflictAlgorithm
	 *            for insert conflict resolver
	 * @return the row ID of the newly inserted row OR the primary key of the
	 *         existing row if the input param 'conflictAlgorithm' =
	 *         {@link #CONFLICT_IGNORE} OR -1 if any error
	 */
	public void insertWithOnConflict(String table, String nullColumnHack, ContentValues initialValues, int conflictAlgorithm) {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT");
		sql.append(CONFLICT_VALUES[conflictAlgorithm]);
		sql.append(" INTO ");
		sql.append(table);
		sql.append('(');

		Object[] bindArgs = null;
		int size = (initialValues != null && initialValues.size() > 0) ? initialValues.size() : 0;
		if (size > 0) {
			bindArgs = new Object[size];
			int i = 0;

			Set<Map.Entry<String, Object>> entrySet = initialValues.valueSet();
			Iterator<Map.Entry<String, Object>> entriesIter = entrySet.iterator();
			while (entriesIter.hasNext()) {
				Map.Entry<String, Object> entry = entriesIter.next();
				String colName = entry.getKey();
				sql.append((i > 0) ? "," : "");
				sql.append(colName);
				bindArgs[i++] = initialValues.get(colName);
			}
			sql.append(')');
			sql.append(" VALUES (");
			for (i = 0; i < size; i++) {
				sql.append((i > 0) ? ",?" : "?");
			}
		} else {
			sql.append(nullColumnHack + ") VALUES (NULL");
		}
		sql.append(')');
		try {
			if (!inTransaction()){
				lock.lock();
			}
			mDB.execSQL(sql.toString(), bindArgs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (!inTransaction()){
				lock.unlock();
			}
		}
	}

	public void CloseDB() {
		try {
			if (!inTransaction()){
				lock.lock();
			}
			if (this.mDB != null) {
				mDB.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
			mDB = null;
		}
	}

	protected abstract void createTables();

	protected abstract void upgradeDB();

	public boolean tableIsExist(String table) {
		if (tableExist == null)
			tableExist = new HashMap<String, Boolean>();
		if (tableExist.containsKey(table) && tableExist.get(table)) {
			return true;
		}

		Cursor cursor = null;
		try {
			String sql = "SELECT COUNT(*) AS c FROM sqlite_master WHERE type ='table' AND name ='" + table + "' ";
			cursor = mDB.rawQuery(sql, null);
			if (cursor != null && cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					tableExist.put(table, true);
					return true;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
			cursor = null;
		}

		return false;
	}
	
	public SQLiteStatement compileStatement(String sql){
		return mDB.compileStatement(sql);
	}
}
