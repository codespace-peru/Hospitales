package pe.com.codespace.hospitales;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Carlos on 7/01/14.
 */
public class SQLiteHelperHospitales extends SQLiteOpenHelper {
    private final Context myContext;
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "hospitales.db";
    private static final String DATABASE_PATH = "databases/";
    private static File DATABASE_FILE = null;
    private boolean mInvalidDatabaseFile = false;
    private boolean mIsUpgraded  = false;
    private int mOpenConnections=0;
    private static SQLiteHelperHospitales mInstance;
    private static final int ESSALUD = 2;
    private static final int MINSA = 3;

    public synchronized static SQLiteHelperHospitales getInstance (Context context){
        if(mInstance == null){
            mInstance = new SQLiteHelperHospitales(context.getApplicationContext());
        }
        return mInstance;
    }

    private SQLiteHelperHospitales(Context context)  {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext = context;

        SQLiteDatabase db = null;
        try{
            db = getReadableDatabase();
            DATABASE_FILE = context.getDatabasePath(DATABASE_NAME);
            if(mInvalidDatabaseFile){
                copyDatabase();
            }
            if(mIsUpgraded){
                doUpgrade();
            }
        }
        catch(SQLiteException ex){
            throw ex;
        }
        finally {
            if(db != null && db.isOpen()){
                db.close();
            }
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        mInvalidDatabaseFile = true;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        mInvalidDatabaseFile = false;
        mIsUpgraded = true;
    }

    @Override
    public synchronized void onOpen(SQLiteDatabase db){
        super.onOpen(db);
        mOpenConnections++;
        if(!db.isReadOnly()){
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public synchronized void close(){
        mOpenConnections--;
        if(mOpenConnections == 0){
            super.close();
        }
    }

    public void copyDatabase()  {
        AssetManager assetManager = myContext.getResources().getAssets();
        InputStream myInput = null;
        OutputStream myOutput = null;
        try{
            myInput = assetManager.open(DATABASE_PATH +DATABASE_NAME);
            myOutput = new FileOutputStream(DATABASE_FILE);
            byte[] buffer = new byte[1024];
            int read=0;
            while ((read = myInput.read(buffer)) != -1) {
                myOutput.write(buffer, 0, read);
            }
        }
        catch (IOException ex){
        }
        finally {
            if(myInput != null){
                try{ myInput.close(); }
                catch(IOException ex){ }
            }
            if(myOutput!=null){
                try{ myOutput.close(); }
                catch (IOException ex){ }
            }
            setDataBaseVersion();
            mInvalidDatabaseFile = false;
        }
    }

    private void setDataBaseVersion(){
        SQLiteDatabase db = null;
        try{
            db = SQLiteDatabase.openDatabase(DATABASE_FILE.getAbsolutePath(),null,SQLiteDatabase.OPEN_READWRITE);
            db.execSQL("PRAGMA user_version=" + DATABASE_VERSION);
        }
        catch (SQLiteException ex){
            throw ex;
        }
        finally {
            if(db != null && db.isOpen()){
                db.close();
            }
        }
    }

    private void doUpgrade(){

    }

    public String[][] getRedEsSalud(int red) {
        SQLiteDatabase db = null;
        String[] array = new String[2];
        array[0] = String.valueOf(red);
        array[1] = String.valueOf(ESSALUD);
        try{
            db = getReadableDatabase();
            Cursor cursor = db.rawQuery("select id, red from redes ORDER BY id", null);
            String[][] arrayOfString = (String[][])Array.newInstance(String.class, new int[] { cursor.getCount(),2});
            int i = 0;
            if (cursor.moveToFirst()) {
                while ( !cursor.isAfterLast() ) {
                    arrayOfString[i][0] = cursor.getString(0);
                    arrayOfString[i][1] = cursor.getString(1);
                    i++;
                    cursor.moveToNext();
                }
            }
            cursor.close();
            return arrayOfString;
        }
        catch (SQLiteException ex){
            throw ex;
        }
        finally {
            if(db != null && db.isOpen()){
                db.close();
            }
        }
    }

    public String[][] getRedesEsSalud() {
        SQLiteDatabase db = null;
        try{
            db = getReadableDatabase();
            Cursor cursor = db.rawQuery("select id, red from redes ORDER BY id", null);
            String[][] arrayOfString = (String[][])Array.newInstance(String.class, new int[] { cursor.getCount(),2});
            int i = 0;
            if (cursor.moveToFirst()) {
                while ( !cursor.isAfterLast() ) {
                    arrayOfString[i][0] = cursor.getString(0);
                    arrayOfString[i][1] = cursor.getString(1);
                    i++;
                    cursor.moveToNext();
                }
            }
            cursor.close();
            return arrayOfString;
        }
        catch (SQLiteException ex){
            throw ex;
        }
        finally {
            if(db != null && db.isOpen()){
                db.close();
            }
        }
    }

    public String[][] getDepartamentos(){
        SQLiteDatabase db = null;
        try{
            db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT id, departamento FROM departamentos ORDER BY id", null);
            String[][] arrayOfString = (String[][]) Array.newInstance(String.class, new int[] { cursor.getCount(),2});
            int i = 0;
            if (cursor.moveToFirst()) {
                while ( !cursor.isAfterLast() ) {
                    arrayOfString[i][0] = cursor.getString(0);
                    arrayOfString[i][1] = cursor.getString(1);
                    i++;
                    cursor.moveToNext();
                }
            }
            cursor.close();
            return arrayOfString;
        }
        catch (SQLiteException ex){
            throw ex;
        }
        finally {
            if(db != null && db.isOpen()){
                db.close();
            }
        }
    }

    public String[][] getCentrosEsSaludxRedes(int red){
        SQLiteDatabase db = null;
        String[] array = new String[2];
        array[0] = String.valueOf(red);
        array[1] = String.valueOf(ESSALUD);
        try{
            db = getReadableDatabase();
            Cursor cursor = db.rawQuery("select id, nombre, latitud, longitud, direccion, telefono1, telefono2 FROM centros WHERE red=? AND institucion = ?", array);
            String[][] arrayOfString = (String[][]) Array.newInstance(String.class, new int[] { cursor.getCount(),7 });
            int i = 0;
            if (cursor.moveToFirst()) {
                while ( !cursor.isAfterLast() ) {
                    arrayOfString[i][0] = cursor.getString(0);
                    arrayOfString[i][1] = cursor.getString(1);
                    arrayOfString[i][2] = cursor.getString(2);
                    arrayOfString[i][3] = cursor.getString(3);
                    arrayOfString[i][4] = cursor.getString(4);
                    arrayOfString[i][5] = cursor.getString(5);
                    arrayOfString[i][6] = cursor.getString(6);
                    i++;
                    cursor.moveToNext();
                }
            }
            cursor.close();
            return arrayOfString;
        }
        catch (SQLiteException ex){
            throw ex;
        }
        finally {
            if(db != null && db.isOpen()){
                db.close();
            }
        }
    }

    public String[][] getCentrosEsSaludxDepartamento(int departamento){
        SQLiteDatabase db = null;
        String[] array = new String[2];
        array[0] = String.valueOf(departamento);
        array[1] = String.valueOf(ESSALUD);
        try{
            db = getReadableDatabase();
            Cursor cursor = db.rawQuery("select id, nombre, latitud, longitud, direccion, telefono1, telefono2 FROM centros WHERE departamento=? AND institucion = ?", array);
            String[][] arrayOfString = (String[][]) Array.newInstance(String.class, new int[] { cursor.getCount(),7 });
            int i = 0;
            if (cursor.moveToFirst()) {
                while ( !cursor.isAfterLast() ) {
                    arrayOfString[i][0] = cursor.getString(0);
                    arrayOfString[i][1] = cursor.getString(1);
                    arrayOfString[i][2] = cursor.getString(2);
                    arrayOfString[i][3] = cursor.getString(3);
                    arrayOfString[i][4] = cursor.getString(4);
                    arrayOfString[i][5] = cursor.getString(5);
                    arrayOfString[i][6] = cursor.getString(6);
                    i++;
                    cursor.moveToNext();
                }
            }
            cursor.close();
            return arrayOfString;
        }
        catch (SQLiteException ex){
            throw ex;
        }
        finally {
            if(db != null && db.isOpen()){
                db.close();
            }
        }
    }

    public String[][] getCentrosCercanos(double latitud, double longitud){
        SQLiteDatabase db = null;
        double lat1,lat2;
        double long1,long2;
        double delta = 0.05;
        lat1 = latitud - delta; lat2=latitud + delta;
        long1 = longitud - delta ; long2 = longitud + delta;
        String[] array = new String[4];
        array[0] = String.valueOf(lat1);
        array[1] = String.valueOf(lat2);
        array[2] = String.valueOf(long1);
        array[3] = String.valueOf(long2);
        try{
            db = getReadableDatabase();
            Cursor cursor = db.rawQuery("select id, nombre, latitud, longitud, direccion, telefono1, telefono2, institucion FROM centros WHERE latitud BETWEEN ? AND ? AND longitud BETWEEN ? AND ?", array);
            String[][] arrayOfString = (String[][]) Array.newInstance(String.class, new int[] { cursor.getCount(),8 });
            int i = 0;
            if (cursor.moveToFirst()) {
                while ( !cursor.isAfterLast() ) {
                    arrayOfString[i][0] = cursor.getString(0);
                    arrayOfString[i][1] = cursor.getString(1);
                    arrayOfString[i][2] = cursor.getString(2);
                    arrayOfString[i][3] = cursor.getString(3);
                    arrayOfString[i][4] = cursor.getString(4);
                    arrayOfString[i][5] = cursor.getString(5);
                    arrayOfString[i][6] = cursor.getString(6);
                    arrayOfString[i][7] = cursor.getString(7);
                    i++;
                    cursor.moveToNext();
                }
            }
            cursor.close();
            return arrayOfString;
        }
        catch (SQLiteException ex){
            throw ex;
        }
        finally {
            if(db != null && db.isOpen()){
                db.close();
            }
        }
    }

    public String[][] getCentro(int centro){
        SQLiteDatabase db = null;
        String[] array = new String[1];
        array[0] = String.valueOf(centro);
        try{
            db = getReadableDatabase();
            Cursor cursor = db.rawQuery("select id, nombre, latitud, longitud, direccion, telefono1, telefono2 FROM centros WHERE id = ?", array);
            String[][] arrayOfString = (String[][]) Array.newInstance(String.class, new int[] { cursor.getCount(),7 });
            int i = 0;
            if (cursor.moveToFirst()) {
                while ( !cursor.isAfterLast() ) {
                    arrayOfString[i][0] = cursor.getString(0);
                    arrayOfString[i][1] = cursor.getString(1);
                    arrayOfString[i][2] = cursor.getString(2);
                    arrayOfString[i][3] = cursor.getString(3);
                    arrayOfString[i][4] = cursor.getString(4);
                    arrayOfString[i][5] = cursor.getString(5);
                    arrayOfString[i][6] = cursor.getString(6);
                    i++;
                    cursor.moveToNext();
                }
            }
            cursor.close();
            return arrayOfString;
        }
        catch (SQLiteException ex){
            throw ex;
        }
        finally {
            if(db != null && db.isOpen()){
                db.close();
            }
        }
    }
}
