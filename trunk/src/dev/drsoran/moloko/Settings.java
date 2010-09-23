package dev.drsoran.moloko;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;

import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.ContentObserver;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import dev.drsoran.moloko.content.RtmSettingsProviderPart;
import dev.drsoran.rtm.RtmSettings;


public class Settings implements OnSharedPreferenceChangeListener
{
   private final static class ListenerEntry
   {
      public final int mask;
      
      public final WeakReference< IOnSettingsChangedListener > listener;
      
      

      public ListenerEntry( int setting, IOnSettingsChangedListener listener )
      {
         this.mask = setting;
         this.listener = new WeakReference< IOnSettingsChangedListener >( listener );
      }
      


      boolean notifyIfMatches( int setting )
      {
         boolean ok = listener.get() != null;
         
         if ( ok && ( ( mask & setting ) != 0 ) )
            listener.get().onSettingsChanged( setting );
         
         return ok;
      }
   }
   
   public final static int DATEFORMAT_EU = 0;
   
   public final static int DATEFORMAT_US = 1;
   
   public final static int TIMEFORMAT_12 = 0;
   
   public final static int TIMEFORMAT_24 = 1;
   
   public final static String NO_DEFAULT_LIST_ID = "";
   
   public final static int STARTUP_VIEW_DEFAULT_LIST = 1 << 0;
   
   public final static int STARTUP_VIEW_LISTS = 1 << 1;
   
   public final static int STARTUP_VIEW_DEFAULT = STARTUP_VIEW_LISTS;
   
   public final static int SETTINGS_RTM_TIMEZONE = 1 << 0;
   
   public final static int SETTINGS_RTM_DATEFORMAT = 1 << 1;
   
   public final static int SETTINGS_RTM_TIMEFORMAT = 1 << 2;
   
   public final static int SETTINGS_RTM_DEFAULTLIST = 1 << 3;
   
   public final static int SETTINGS_RTM_LANGUAGE = 1 << 4;
   
   public final static int SETTINGS_ALL = Integer.MAX_VALUE;
   
   private final Context context;
   
   private final SharedPreferences preferences;
   
   // TODO: No check for double registration, no check for registration of same listener
   // with different mask. In these cases the listener gets notified multiple times.
   private final ArrayList< ListenerEntry > listeners = new ArrayList< ListenerEntry >();
   
   private RtmSettings rtmSettings;
   
   private TimeZone timeZone = TimeZone.getDefault();
   
   private int dateformat = DATEFORMAT_EU;
   
   private int timeformat = TIMEFORMAT_12;
   
   private String defaultListId = NO_DEFAULT_LIST_ID;
   
   private Locale locale = Locale.getDefault();
   
   private int startupView = STARTUP_VIEW_DEFAULT;
   
   

   public Settings( Context context, Handler handler )
   {
      this.context = context;
      
      preferences = PreferenceManager.getDefaultSharedPreferences( context );
      
      if ( preferences == null )
         throw new IllegalStateException( "SharedPreferences must not be null." );
      
      loadLocalSettings();
      loadRtmSettings();
      
      // Register after loadRtmSettings() otherwise we would
      // see our own change.
      preferences.registerOnSharedPreferenceChangeListener( this );
      
      // Watch for settings changes from background sync
      context.getContentResolver()
             .registerContentObserver( dev.drsoran.provider.Rtm.Settings.CONTENT_URI,
                                       true,
                                       new ContentObserver( handler )
                                       {
                                          // This should be called from the main thread,
                                          // see handler parameter.
                                          @Override
                                          public void onChange( boolean selfChange )
                                          {
                                             notifyListeners( loadRtmSettings() );
                                          }
                                       } );
   }
   


   public void registerOnSettingsChangedListener( int which,
                                                  IOnSettingsChangedListener listener )
   {
      if ( listener != null )
      {
         listeners.add( new ListenerEntry( which, listener ) );
      }
   }
   


   public void unregisterOnSettingsChangedListener( IOnSettingsChangedListener listener )
   {
      if ( listener != null )
      {
         removeListener( listener );
      }
   }
   


   public TimeZone getTimezone()
   {
      return timeZone;
   }
   


   public int getDateformat()
   {
      return dateformat;
   }
   


   public int getTimeformat()
   {
      return timeformat;
   }
   


   public String getDefaultListId()
   {
      return defaultListId;
   }
   


   public void setDefaultListId( String id )
   {
      if ( TextUtils.isEmpty( id ) )
         id = NO_DEFAULT_LIST_ID;
      
      defaultListId = persistSetting( context.getString( R.string.key_def_list_local ),
                                      context.getString( R.string.key_def_list_sync_with_rtm ),
                                      id,
                                      false );
   }
   


   public Locale getLocale()
   {
      return locale;
   }
   


   public RtmSettings getRtmSettings()
   {
      return rtmSettings;
   }
   


   public int getStartupView()
   {
      return startupView;
   }
   


   public void setStartupView( int value )
   {
      startupView = Integer.valueOf( persistSetting( context.getString( R.string.key_startup_view ),
                                                     null,
                                                     String.valueOf( value ),
                                                     false ) );
   }
   


   public void onSharedPreferenceChanged( SharedPreferences newValue, String key )
   {
      int settingsChanged = 0;
      
      if ( key != null && newValue != null )
      {
         if ( key.equals( context.getString( R.string.key_timezone_local ) )
            || key.equals( context.getString( R.string.key_timezone_sync_with_rtm ) ) )
         {
            settingsChanged = setTimezone();
         }
         else if ( key.equals( context.getString( R.string.key_dateformat_local ) )
            || key.equals( context.getString( R.string.key_dateformat_sync_with_rtm ) ) )
         {
            settingsChanged = setDateformat();
         }
         else if ( key.equals( context.getString( R.string.key_timeformat_local ) )
            || key.equals( context.getString( R.string.key_timeformat_sync_with_rtm ) ) )
         {
            settingsChanged = setTimeformat();
         }
         else if ( key.equals( context.getString( R.string.key_def_list_local ) )
            || key.equals( context.getString( R.string.key_def_list_sync_with_rtm ) ) )
         {
            settingsChanged = setDefaultListId();
         }
         else if ( key.equals( context.getString( R.string.key_lang_local ) )
            || key.equals( context.getString( R.string.key_lang_sync_with_rtm ) ) )
         {
            settingsChanged = setLocale();
         }
      }
      
      notifyListeners( settingsChanged );
   }
   


   private int setTimezone()
   {
      TimeZone newTimeZone;
      
      final String keySync = context.getString( R.string.key_timezone_sync_with_rtm );
      final String key = context.getString( R.string.key_timezone_local );
      final boolean useRtm = useRtmSetting( keySync );
      
      if ( useRtm )
      {
         newTimeZone = null;
         
         if ( rtmSettings != null && rtmSettings.getTimezone() != null )
         {
            newTimeZone = TimeZone.getTimeZone( rtmSettings.getTimezone() );
         }
         else
         {
            newTimeZone = timeZone;
         }
      }
      else
      {
         newTimeZone = TimeZone.getTimeZone( preferences.getString( key,
                                                                    timeZone.getID() ) );
      }
      
      if ( !newTimeZone.getID().equals( timeZone ) )
      {
         timeZone = newTimeZone;
         persistSetting( key, keySync, timeZone.getID(), useRtm );
         
         return SETTINGS_RTM_TIMEZONE;
      }
      
      return 0;
   }
   


   private int setDateformat()
   {
      int newDateformat;
      
      final String keySync = context.getString( R.string.key_dateformat_sync_with_rtm );
      final String key = context.getString( R.string.key_dateformat_local );
      final boolean useRtm = useRtmSetting( keySync );
      
      if ( useRtm )
      {
         if ( rtmSettings != null )
         {
            newDateformat = rtmSettings.getDateFormat();
         }
         else
         {
            final char[] order = DateFormat.getDateFormatOrder( context );
            
            newDateformat = ( order.length > 0 && order[ 0 ] == DateFormat.DATE )
                                                                                 ? DATEFORMAT_EU
                                                                                 : DATEFORMAT_US;
         }
      }
      else
      {
         newDateformat = Integer.valueOf( preferences.getString( key, "0" ) );
      }
      
      if ( newDateformat != dateformat )
      {
         dateformat = newDateformat;
         persistSetting( key, keySync, String.valueOf( dateformat ), useRtm );
         
         return SETTINGS_RTM_DATEFORMAT;
      }
      
      return 0;
   }
   


   private int setTimeformat()
   {
      int newTimeformat;
      
      final String keySync = context.getString( R.string.key_timeformat_sync_with_rtm );
      final String key = context.getString( R.string.key_timeformat_local );
      final boolean useRtm = useRtmSetting( keySync );
      
      if ( useRtm )
      {
         if ( rtmSettings != null )
         {
            newTimeformat = rtmSettings.getTimeFormat();
         }
         else
         {
            newTimeformat = ( DateFormat.is24HourFormat( context ) )
                                                                    ? TIMEFORMAT_24
                                                                    : TIMEFORMAT_12;
         }
      }
      else
      {
         newTimeformat = Integer.valueOf( preferences.getString( key, "0" ) );
      }
      
      if ( newTimeformat != timeformat )
      {
         timeformat = newTimeformat;
         persistSetting( key, keySync, String.valueOf( timeformat ), useRtm );
         
         return SETTINGS_RTM_TIMEFORMAT;
      }
      
      return 0;
   }
   


   private int setDefaultListId()
   {
      String newListId;
      
      final String keySync = context.getString( R.string.key_def_list_sync_with_rtm );
      final String key = context.getString( R.string.key_def_list_local );
      final boolean useRtm = useRtmSetting( keySync );
      
      if ( useRtm )
      {
         if ( rtmSettings != null && rtmSettings.getDefaultListId() != null )
         {
            newListId = rtmSettings.getDefaultListId();
         }
         else
         {
            newListId = NO_DEFAULT_LIST_ID;
         }
      }
      else
      {
         newListId = preferences.getString( key, defaultListId );
      }
      
      if ( !defaultListId.equals( newListId ) )
      {
         defaultListId = newListId;
         persistSetting( key, keySync, defaultListId, useRtm );
         
         return SETTINGS_RTM_DEFAULTLIST;
      }
      
      return 0;
   }
   


   private int setLocale()
   {
      Locale newLocale;
      
      final String keySync = context.getString( R.string.key_lang_sync_with_rtm );
      final String key = context.getString( R.string.key_lang_local );
      final boolean useRtm = useRtmSetting( keySync );
      
      if ( useRtm )
      {
         newLocale = null;
         
         if ( rtmSettings != null && rtmSettings.getLanguage() != null )
         {
            newLocale = getLocaleFromLanguage( rtmSettings.getLanguage(),
                                               locale );
         }
         else
         {
            newLocale = locale;
         }
      }
      else
      {
         newLocale = getLocaleFromLanguage( preferences.getString( key,
                                                                   locale.getLanguage() ),
                                            locale );
         
      }
      
      if ( !newLocale.equals( locale ) )
      {
         locale = newLocale;
         persistSetting( key, keySync, newLocale.getLanguage(), useRtm );
         
         return SETTINGS_RTM_LANGUAGE;
      }
      
      return 0;
   }
   


   private boolean useRtmSetting( String key )
   {
      if ( !preferences.contains( key ) )
      {
         preferences.edit()
                    .putBoolean( context.getString( R.string.key_timezone_sync_with_rtm ),
                                 true )
                    .commit();
         return true;
      }
      else
      {
         return preferences.getBoolean( key, true );
      }
   }
   


   private String persistSetting( String key,
                                  String keySync,
                                  String value,
                                  boolean useRtm )
   {
      final Editor editor = preferences.edit();
      
      if ( !preferences.getString( key, "" ).equals( value ) )
      {
         editor.putString( key, value ).commit();
      }
      
      if ( preferences.getBoolean( keySync, true ) != useRtm )
      {
         editor.putBoolean( keySync, useRtm ).commit();
      }
      
      return value;
   }
   


   private String loadLocalValue( String key, String keySync, String defValue )
   {
      String value;
      
      final Editor editor = preferences.edit();
      
      if ( !preferences.contains( key ) )
      {
         editor.putString( key, defValue ).commit();
         value = defValue;
      }
      else
      {
         value = preferences.getString( key, defValue );
      }
      
      if ( keySync != null && !preferences.contains( keySync ) )
      {
         editor.putBoolean( keySync, true ).commit();
      }
      
      return value;
   }
   


   private int loadLocalValue( String key, String keySync, int defValue )
   {
      return Integer.parseInt( loadLocalValue( key,
                                               keySync,
                                               String.valueOf( defValue ) ) );
   }
   


   private void loadLocalSettings()
   {
      timeZone = TimeZone.getTimeZone( loadLocalValue( context.getString( R.string.key_timezone_local ),
                                                       context.getString( R.string.key_timezone_sync_with_rtm ),
                                                       timeZone.getID() ) );
      dateformat = loadLocalValue( context.getString( R.string.key_dateformat_local ),
                                   context.getString( R.string.key_dateformat_sync_with_rtm ),
                                   dateformat );
      timeformat = loadLocalValue( context.getString( R.string.key_timeformat_local ),
                                   context.getString( R.string.key_timeformat_sync_with_rtm ),
                                   timeformat );
      defaultListId = loadLocalValue( context.getString( R.string.key_def_list_local ),
                                      context.getString( R.string.key_def_list_sync_with_rtm ),
                                      defaultListId );
      locale = getLocaleFromLanguage( loadLocalValue( context.getString( R.string.key_lang_local ),
                                                      context.getString( R.string.key_lang_sync_with_rtm ),
                                                      locale.getLanguage() ),
                                      null );
      startupView = loadLocalValue( context.getString( R.string.key_startup_view ),
                                    null,
                                    startupView );
   }
   


   private int loadRtmSettings()
   {
      int settingsChanged = 0;
      
      final ContentProviderClient client = context.getContentResolver()
                                                  .acquireContentProviderClient( dev.drsoran.provider.Rtm.Settings.CONTENT_URI );
      
      if ( client != null )
      {
         rtmSettings = RtmSettingsProviderPart.getSettings( client );
         
         client.release();
         
         settingsChanged |= setTimezone();
         settingsChanged |= setDateformat();
         settingsChanged |= setTimeformat();
         settingsChanged |= setDefaultListId();
         settingsChanged |= setLocale();
      }
      
      return settingsChanged;
   }
   


   private void notifyListeners( int mask )
   {
      if ( mask > 0 )
      {
         for ( Iterator< ListenerEntry > i = listeners.iterator(); i.hasNext(); )
         {
            ListenerEntry entry = i.next();
            
            // Check if we have a dead entry
            if ( !entry.notifyIfMatches( mask ) )
            {
               i.remove();
            }
         }
      }
   }
   


   private boolean removeListener( IOnSettingsChangedListener listener )
   {
      final int size = listeners.size();
      
      for ( int i = 0; i < size; i++ )
      {
         if ( listener == listeners.get( i ).listener )
         {
            listeners.remove( i );
            return true;
         }
      }
      
      return false;
   }
   


   private static Locale getLocaleFromLanguage( String language, Locale defValue )
   {
      Locale locale = null;
      
      final Locale[] locales = Locale.getAvailableLocales();
      
      for ( int i = 0; i < locales.length && locale == null; i++ )
      {
         if ( locales[ i ].getLanguage().equals( language ) )
         {
            locale = locales[ i ];
         }
      }
      
      if ( locale == null )
         locale = defValue;
      
      return locale;
   }
}