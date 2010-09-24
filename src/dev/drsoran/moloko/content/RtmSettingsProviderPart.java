package dev.drsoran.moloko.content;

import java.util.HashMap;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.RemoteException;
import android.text.format.Time;
import dev.drsoran.moloko.util.Queries;
import dev.drsoran.provider.Rtm.Lists;
import dev.drsoran.provider.Rtm.Settings;
import dev.drsoran.rtm.RtmSettings;


public class RtmSettingsProviderPart extends AbstractRtmProviderPart
{
   @SuppressWarnings( "unused" )
   private static final String TAG = RtmSettingsProviderPart.class.getSimpleName();
   
   public final static HashMap< String, String > PROJECTION_MAP = new HashMap< String, String >();
   
   public final static String[] PROJECTION =
   { Settings._ID, Settings.SYNC_TIMESTAMP, Settings.TIMEZONE,
    Settings.DATEFORMAT, Settings.TIMEFORMAT, Settings.DEFAULTLIST_ID,
    Settings.LANGUAGE };
   
   public final static HashMap< String, Integer > COL_INDICES = new HashMap< String, Integer >();
   
   static
   {
      AbstractRtmProviderPart.initProjectionDependent( PROJECTION,
                                                       PROJECTION_MAP,
                                                       COL_INDICES );
   }
   
   

   public final static ContentValues getContentValues( RtmSettings settings )
   {
      ContentValues values = null;
      
      if ( settings != null )
      {
         values = new ContentValues();
         
         values.put( Settings._ID, "1" );
         values.put( Settings.SYNC_TIMESTAMP, settings.getSyncTimeStamp()
                                                      .toMillis( false ) );
         
         if ( settings.getTimezone() != null )
            values.put( Settings.TIMEZONE, settings.getTimezone() );
         else
            values.putNull( Settings.TIMEZONE );
         
         values.put( Settings.DATEFORMAT, settings.getDateFormat() );
         values.put( Settings.TIMEFORMAT, settings.getTimeFormat() );
         
         if ( settings.getDefaultListId() != null )
            values.put( Settings.DEFAULTLIST_ID, settings.getDefaultListId() );
         else
            values.putNull( Settings.DEFAULTLIST_ID );
         
         if ( settings.getLanguage() != null )
            values.put( Settings.LANGUAGE, settings.getLanguage() );
         else
            values.putNull( Settings.LANGUAGE );
      }
      
      return values;
   }
   


   public final static String getSettingsId( ContentProviderClient client )
   {
      String id = null;
      
      try
      {
         final Cursor c = client.query( Settings.CONTENT_URI,
                                        PROJECTION,
                                        null,
                                        null,
                                        null );
         
         // We only consider the first entry cause we do not expect
         // more than 1 entry in this table
         boolean ok = c != null && c.getCount() > 0 && c.moveToFirst();
         
         if ( ok )
         {
            id = c.getString( COL_INDICES.get( Settings._ID ) );
         }
         
         if ( c != null )
            c.close();
      }
      catch ( RemoteException e )
      {
         id = null;
      }
      
      return id;
   }
   


   public final static RtmSettings getSettings( ContentProviderClient client )
   {
      RtmSettings settings = null;
      
      try
      {
         final Cursor c = client.query( Settings.CONTENT_URI,
                                        PROJECTION,
                                        null,
                                        null,
                                        null );
         
         // We only consider the first entry cause we do not expect
         // more than 1 entry in this table
         boolean ok = c != null && c.getCount() > 0 && c.moveToFirst();
         
         if ( ok )
         {
            Time time = new Time();
            time.set( c.getLong( COL_INDICES.get( Settings.SYNC_TIMESTAMP ) ) );
            
            settings = new RtmSettings( time,
                                        Queries.getOptString( c,
                                                              COL_INDICES.get( Settings.TIMEZONE ) ),
                                        c.getInt( COL_INDICES.get( Settings.DATEFORMAT ) ),
                                        c.getInt( COL_INDICES.get( Settings.TIMEFORMAT ) ),
                                        Queries.getOptString( c,
                                                              COL_INDICES.get( Settings.DEFAULTLIST_ID ) ),
                                        Queries.getOptString( c,
                                                              COL_INDICES.get( Settings.LANGUAGE ) ) );
         }
         
         if ( c != null )
            c.close();
      }
      catch ( RemoteException e )
      {
         settings = null;
      }
      
      return settings;
   }
   


   public RtmSettingsProviderPart( SQLiteOpenHelper dbAccess )
   {
      super( dbAccess, Settings.PATH );
   }
   


   public void create( SQLiteDatabase db ) throws SQLException
   {
      db.execSQL( "CREATE TABLE "
         + path
         + " ( "
         + Settings._ID
         + " INTEGER NOT NULL CONSTRAINT PK_SETTINGS PRIMARY KEY AUTOINCREMENT, "
         + Settings.SYNC_TIMESTAMP + " INTEGER NOT NULL, " + Settings.TIMEZONE
         + " TEXT, " + Settings.DATEFORMAT + " INTEGER NOT NULL DEFAULT 0, "
         + Settings.TIMEFORMAT + " INTEGER NOT NULL DEFAULT 0, "
         + Settings.DEFAULTLIST_ID + " INTEGER, " + Settings.LANGUAGE
         + " TEXT, CONSTRAINT defaultlist FOREIGN KEY ( "
         + Settings.DEFAULTLIST_ID + ") REFERENCES " + Lists.PATH + "( "
         + Lists._ID + " ) );" );
   }
   


   @Override
   protected String getContentItemType()
   {
      return Settings.CONTENT_ITEM_TYPE;
   }
   


   @Override
   protected String getContentType()
   {
      return Settings.CONTENT_TYPE;
   }
   


   @Override
   protected Uri getContentUri()
   {
      return Settings.CONTENT_URI;
   }
   


   @Override
   protected String getDefaultSortOrder()
   {
      return null;
   }
   


   public HashMap< String, String > getProjectionMap()
   {
      return PROJECTION_MAP;
   }
   


   public HashMap< String, Integer > getColumnIndices()
   {
      return COL_INDICES;
   }
   


   public String[] getProjection()
   {
      return PROJECTION;
   }
}