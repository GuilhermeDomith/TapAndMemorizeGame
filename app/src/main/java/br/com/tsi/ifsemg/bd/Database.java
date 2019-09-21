package br.com.tsi.ifsemg.bd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

public abstract class Database<T> {

    protected static final String DATABASE_NAME = "jogo_memorizacao_db2";
    protected static final int DATABASE_ACCESS = 0;
    protected static final String SQL_CLEAR= "DROP TABLE IF EXISTS '%s'";

    protected String tableName;
    protected SQLiteDatabase database;

    public Database(Context context, String tableName, String SQLStruct) {
        this.database = context.openOrCreateDatabase(DATABASE_NAME, DATABASE_ACCESS, null);
        this.tableName = tableName;
        this.database.execSQL(SQLStruct);
    }

    public void close(){
        database.close();
    }
    public void clear(){
        database.execSQL(String.format(SQL_CLEAR, tableName));
    }
    public abstract boolean insert(T obj);
    public abstract List<T> all();

}
