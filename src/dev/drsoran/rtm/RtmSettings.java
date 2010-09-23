package dev.drsoran.rtm;

import java.util.Calendar;
import java.util.Locale;

import org.w3c.dom.Element;

import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;

import com.mdt.rtm.data.RtmData;

import dev.drsoran.moloko.content.RtmSettingsProviderPart;
import dev.drsoran.moloko.service.sync.operation.CompositeContentProviderSyncOperation;
import dev.drsoran.moloko.service.sync.operation.ContentProviderSyncOperation;
import dev.drsoran.moloko.service.sync.operation.IContentProviderSyncOperation;
import dev.drsoran.moloko.service.sync.syncable.IContentProviderSyncable;
import dev.drsoran.moloko.util.Queries;
import dev.drsoran.moloko.util.Strings;
import dev.drsoran.provider.Rtm.Settings;


public class RtmSettings extends RtmData implements
         IContentProviderSyncable< RtmSettings >
{
   private final String TAG = RtmSettings.class.getSimpleName();
   
   public static final Parcelable.Creator< RtmSettings > CREATOR = new Parcelable.Creator< RtmSettings >()
   {
      
      public RtmSettings createFromParcel( Parcel source )
      {
         return new RtmSettings( source );
      }
      


      public RtmSettings[] newArray( int size )
      {
         return new RtmSettings[ size ];
      }
      
   };
   
   private final Time syncTimeStamp;
   
   private final String timezone;
   
   private int dateFormat;
   
   private int timeFormat;
   
   private final String defaultListId;
   
   private final String language;
   
   

   public static RtmSettings createDefaultSettings( Context context )
   {
      final String timeZone = Calendar.getInstance().getTimeZone().getID();
      
      final char[] order = DateFormat.getDateFormatOrder( context );
      
      final int dateformat = ( order.length > 0 && order[ 0 ] == DateFormat.DATE )
                                                                                  ? 0
                                                                                  : 1;
      
      final int timeformat = ( !DateFormat.is24HourFormat( context ) ) ? 0 : 1;
      
      final String language = Locale.getDefault().getLanguage();
      
      Time time = new Time();
      time.setToNow();
      
      return new RtmSettings( time,
                              timeZone,
                              dateformat,
                              timeformat,
                              null,
                              language );
   }
   


   public RtmSettings( Time syncTimeStamp, String timezone, int dateFormat,
      int timeFormat, String defaultListId, String language )
   {
      this.syncTimeStamp = syncTimeStamp;
      this.timezone = timezone;
      this.dateFormat = dateFormat;
      this.timeFormat = timeFormat;
      this.defaultListId = defaultListId;
      this.language = language;
   }
   


   public RtmSettings( Element elt )
   {
      this.syncTimeStamp = new Time();
      this.syncTimeStamp.setToNow();
      
      if ( !elt.getNodeName().equals( "settings" ) )
      {
         throw new IllegalArgumentException( "Element " + elt.getNodeName()
            + " does not represent an Settings object." );
      }
      
      this.timezone = textNullIfEmpty( child( elt, "timezone" ) );
      
      try
      {
         this.dateFormat = Integer.parseInt( text( child( elt, "dateformat" ) ) );
      }
      catch ( NumberFormatException nfe )
      {
         this.dateFormat = 0;
         Log.e( TAG, "Invalid dateformat setting.", nfe );
      }
      
      try
      {
         this.timeFormat = Integer.parseInt( text( child( elt, "timeformat" ) ) );
      }
      catch ( NumberFormatException nfe )
      {
         this.timeFormat = 0;
         Log.e( TAG, "Invalid timeformat setting.", nfe );
      }
      
      this.defaultListId = textNullIfEmpty( child( elt, "defaultlist" ) );
      this.language = textNullIfEmpty( child( elt, "language" ) );
   }
   


   public RtmSettings( Parcel source )
   {
      this.syncTimeStamp = new Time();
      this.syncTimeStamp.set( source.readLong() );
      this.timezone = source.readString();
      this.dateFormat = source.readInt();
      this.timeFormat = source.readInt();
      this.defaultListId = source.readString();
      this.language = source.readString();
   }
   


   public Time getSyncTimeStamp()
   {
      return syncTimeStamp;
   }
   


   public String getTimezone()
   {
      return timezone;
   }
   


   public int getDateFormat()
   {
      return dateFormat;
   }
   


   public int getTimeFormat()
   {
      return timeFormat;
   }
   


   public String getDefaultListId()
   {
      return defaultListId;
   }
   


   public String getLanguage()
   {
      return language;
   }
   


   public int describeContents()
   {
      return 0;
   }
   


   public void writeToParcel( Parcel dest, int flags )
   {
      dest.writeLong( syncTimeStamp.toMillis( false ) );
      dest.writeString( timezone );
      dest.writeInt( dateFormat );
      dest.writeInt( timeFormat );
      dest.writeString( defaultListId );
      dest.writeString( language );
   }
   


   public IContentProviderSyncOperation computeContentProviderDeleteOperation( ContentProviderClient provider,
                                                                               Object... params )
   {
      return ContentProviderSyncOperation.NOOP;
   }
   


   public IContentProviderSyncOperation computeContentProviderInsertOperation( ContentProviderClient provider,
                                                                               Object... params )
   {
      return new ContentProviderSyncOperation( provider,
                                               ContentProviderOperation.newInsert( Settings.CONTENT_URI )
                                                                       .withValues( RtmSettingsProviderPart.getContentValues( this ) )
                                                                       .build(),
                                               IContentProviderSyncOperation.Op.INSERT );
   }
   


   public IContentProviderSyncOperation computeContentProviderUpdateOperation( ContentProviderClient provider,
                                                                               RtmSettings update,
                                                                               Object... params )
   {
      final Uri settingsUri = Queries.contentUriWithId( Settings.CONTENT_URI,
                                                        RtmSettingsProviderPart.getSettingsId( provider ) );
      
      final CompositeContentProviderSyncOperation result = new CompositeContentProviderSyncOperation( provider,
                                                                                                      IContentProviderSyncOperation.Op.UPDATE );
      
      result.add( ContentProviderOperation.newUpdate( settingsUri )
                                          .withValue( Settings.SYNC_TIMESTAMP,
                                                      update.getSyncTimeStamp()
                                                            .toMillis( false ) )
                                          .build() );
      
      if ( Strings.hasStringChanged( timezone, update.timezone ) )
         result.add( ContentProviderOperation.newUpdate( settingsUri )
                                             .withValue( Settings.TIMEZONE,
                                                         update.timezone )
                                             .build() );
      
      if ( update.dateFormat != dateFormat )
         result.add( ContentProviderOperation.newUpdate( settingsUri )
                                             .withValue( Settings.DATEFORMAT,
                                                         update.dateFormat )
                                             .build() );
      
      if ( update.timeFormat != timeFormat )
         result.add( ContentProviderOperation.newUpdate( settingsUri )
                                             .withValue( Settings.TIMEFORMAT,
                                                         update.timeFormat )
                                             .build() );
      
      if ( Strings.hasStringChanged( defaultListId, update.defaultListId ) )
         result.add( ContentProviderOperation.newUpdate( settingsUri )
                                             .withValue( Settings.DEFAULTLIST_ID,
                                                         update.defaultListId )
                                             .build() );
      
      if ( Strings.hasStringChanged( language, update.language ) )
         result.add( ContentProviderOperation.newUpdate( settingsUri )
                                             .withValue( Settings.LANGUAGE,
                                                         update.language )
                                             .build() );
      
      return result;
   }
   
}
