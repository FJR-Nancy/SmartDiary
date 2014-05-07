package zju.smartdiary;

import java.sql.Timestamp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDataBaseAdapter {
	// 用于打印log
	private static final String	TAG				= "MyDataBaseAdapter";

	// 表中一条数据的id
	public static final String	KEY_ID		= "_id";												

	// 表中一条数据的时间戳
	public static final String	KEY_TIMESTAMP		= "timestamp";												

	// 表中一条数据中的锁屏状态
	public static final String	KEY_SCREENSTATE		= "screenState";
	
	// 表中一条数据中的锁屏状态
	public static final String	KEY_GEOLAT		= "geoLat";
	
	// 表中一条数据中的锁屏状态
	public static final String	KEY_GEOLNG		= "geoLng";
	
	// 表中一条数据中的锁屏状态
	public static final String	KEY_ADDR		= "addr";

	// 数据库名称为data
	private static final String	DB_NAME			= "SmartDiary.db";
	
	// 数据库表名
	private static final String	DB_TABLE		= "OriginalData";
	
	// 数据库版本
	private static final int	DB_VERSION		= 1;

	// 本地Context对象
	private Context				mContext		= null;
	
	//创建一个表
	private static final String	DB_CREATE		= 
			"CREATE TABLE " + DB_TABLE + " (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
			+ KEY_TIMESTAMP + " TIMESTAMP NOT NULL DEFAULT (datetime('now','localtime')),"+ KEY_SCREENSTATE + " BOOLEAN,"
			+ KEY_GEOLAT + " DOUBLE," + KEY_GEOLNG + " DOUBLE," + KEY_ADDR + " VARCHAR(20))";

	// 执行open（）打开数据库时，保存返回的数据库对象
	private SQLiteDatabase		mSQLiteDatabase	= null;

	// 由SQLiteOpenHelper继承过来
	private DatabaseHelper		mDatabaseHelper	= null;
	
	
	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		/* 构造函数-创建一个数据库 */
		DatabaseHelper(Context context)
		{
			//当调用getWritableDatabase() 
			//或 getReadableDatabase()方法时
			//则创建一个数据库
			super(context, DB_NAME, null, DB_VERSION);						
		}

		/* 创建一个表 */
		@Override
		public void onCreate(SQLiteDatabase db)
		{
//			db.execSQL("DROP TABLE IF EXISTS OriginalData");//删！！！
			
			// 数据库没有表时创建一个
			db.execSQL(DB_CREATE);
		}

		/* 升级数据库 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			db.execSQL("DROP TABLE IF EXISTS notes");
			onCreate(db);
		}
	}
	
	/* 构造函数-取得Context */
	public MyDataBaseAdapter(Context context)
	{
		mContext = context;
	}


	// 打开数据库，返回数据库对象
	public void open() throws SQLException
	{
		mDatabaseHelper = new DatabaseHelper(mContext);
		mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();
	}


	// 关闭数据库
	public void close()
	{
		mDatabaseHelper.close();
	}

	/* 插入一条数据 */
	public long insertData(int screenState,
			double geoLat,double geoLng,String addr)
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_SCREENSTATE, screenState);
		initialValues.put(KEY_GEOLAT, geoLat);
		initialValues.put(KEY_GEOLNG, geoLng);
		initialValues.put(KEY_ADDR, addr);

		return mSQLiteDatabase.insert(DB_TABLE, KEY_ID, initialValues);

//		String INSERT_DATA = "INSERT INTO " + DB_TABLE + "("
//				  + KEY_SCREENSTATE +"," + KEY_GEOLAT +"," + KEY_GEOLNG +"," + KEY_ADDR + ")" 
//  			+ " values(" + screenState+"," + geoLat +"," + geoLng +"," + addr+")";
//		
//		mSQLiteDatabase.execSQL(INSERT_DATA);
	}

//	/* 删除一条数据 */
//	public boolean deleteData(long rowId)
//	{
//		return mSQLiteDatabase.delete(DB_TABLE, KEY_ID + "=" + rowId, null) > 0;
//	}
//
//	/* 通过Cursor查询所有数据 */
//	public Cursor fetchAllData()
//	{
//		return mSQLiteDatabase.query(DB_TABLE, new String[] { KEY_ID, KEY_NUM, KEY_DATA }, null, null, null, null, null);
//	}
//
//	/* 查询指定数据 */
//	public Cursor fetchData(long rowId) throws SQLException
//	{
//
//		Cursor mCursor =
//
//		mSQLiteDatabase.query(true, DB_TABLE, new String[] { KEY_ID, KEY_NUM, KEY_DATA }, KEY_ID + "=" + rowId, null, null, null, null, null);
//
//		if (mCursor != null)
//		{
//			mCursor.moveToFirst();
//		}
//		return mCursor;
//
//	}
//
//	/* 更新一条数据 */
//	public boolean updateData(long rowId, int num, String data)
//	{
//		ContentValues args = new ContentValues();
//		args.put(KEY_NUM, num);
//		args.put(KEY_DATA, data);
//
//		return mSQLiteDatabase.update(DB_TABLE, args, KEY_ID + "=" + rowId, null) > 0;
//	}
	
}

