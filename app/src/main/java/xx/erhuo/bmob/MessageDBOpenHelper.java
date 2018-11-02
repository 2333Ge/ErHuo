package xx.erhuo.bmob;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.erhuo.utils.LogUtils;

public class MessageDBOpenHelper extends SQLiteOpenHelper {


    private final String tableContent = "id integer primary key autoincrement,"
            +"name text,"
            +"time integer,"
            +"messageType text,"
            +"fromOthers integer,"//boolean类型
            +"content text,"
            +"headImgUrl text,"
            +"isSendSuccessful integer,"
            +"userId text";
    private String recentOther = ",recentNum integer DEFAULT(0)";
    private String newTableName;
    private String createTable;
    private String createRecentMsgTable = "create table " + RECENT_MSG_TABLE +
            "(" + tableContent + recentOther + ")";
    private static final String RECENT_MSG_TABLE = "recentMsgTable";
    /**
     *
     * @param context
     * @param name
     * @param factory 查询时返回一个自定义的cursor
     * @param version
     */
    public MessageDBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    public MessageDBOpenHelper(Context context, String name,String tableName, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        newTableName = tableName;
        createTable = "create table " + newTableName + "(" + tableContent + ")";

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(createRecentMsgTable);

        if( createTable != null ){
            sqLiteDatabase.execSQL(createTable);
        }



//        if(createTable != null && !isTableExits(sqLiteDatabase,newTableName)){
//           sqLiteDatabase.execSQL(newTableName);
//            sqLiteDatabase.execSQL(createRecentMsgTable);
//             LogUtils.e("createTable","=====" +isTableExits(sqLiteDatabase,newTableName));
//        }
//        if(createRecentMsgTable != null && !isTableExits(sqLiteDatabase,RECENT_MSG_TABLE)){
//
//            LogUtils.e("RECENT_MSG_TABLE","=====" +isTableExits(sqLiteDatabase,RECENT_MSG_TABLE));
//        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if(createTable != null && !isTableExits(sqLiteDatabase,newTableName)){
            sqLiteDatabase.execSQL(createTable);
        }
    }

    public boolean isTableExits(SQLiteDatabase db,String tableName){
        Cursor cursor;
        boolean result = false;
        String sql = "select count(*) as c from Sqlite_master  where type ='table' and name ='" + tableName.trim() + "' ";
        cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            int count = cursor.getInt(0);
            if (count > 0) {
                result = true;
            }
        }
        LogUtils.e("isTableExits()",tableName + " is Exit?" + result);
        return  result;
    }



}
