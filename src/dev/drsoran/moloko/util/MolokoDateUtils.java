package dev.drsoran.moloko.util;

import java.util.Calendar;

import android.text.format.DateFormat;
import dev.drsoran.moloko.MolokoApp;
import dev.drsoran.moloko.Settings;


public class MolokoDateUtils
{
   public final static int FORMAT_WITH_YEAR = 1 << 0;
   
   public final static int FORMAT_NUMERIC = 1 << 1;
   
   public final static int FORMAT_SHOW_WEEKDAY = 1 << 2;
   
   

   public final static String formatDate( long millis, int dateStyle )
   {
      final Calendar cal = Calendar.getInstance( MolokoApp.getSettings()
                                                          .getTimezone() );
      cal.setTimeInMillis( millis );
      
      return DateFormat.format( buildPattern( true, false, dateStyle ), cal )
                       .toString();
   }
   


   public final static String formatDateTime( long millis, int dateStyle )
   {
      final Calendar cal = Calendar.getInstance( MolokoApp.getSettings()
                                                          .getTimezone() );
      cal.setTimeInMillis( millis );
      
      return DateFormat.format( buildPattern( true, true, dateStyle ), cal )
                       .toString();
   }
   


   public final static String formatTime( long millis )
   {
      final Calendar cal = Calendar.getInstance( MolokoApp.getSettings()
                                                          .getTimezone() );
      cal.setTimeInMillis( millis );
      
      return DateFormat.format( buildPattern( false, true, 0 ), cal )
                       .toString();
   }
   


   private final static String buildPattern( boolean date,
                                             boolean time,
                                             int flags )
   {
      String pattern = "";
      
      final Settings settings = MolokoApp.getSettings();
      
      // Date
      if ( date )
      {
         if ( ( flags & FORMAT_SHOW_WEEKDAY ) != 0 )
            pattern = "EEEE, ";
         
         // Date EU
         if ( settings.getDateformat() == Settings.DATEFORMAT_EU )
         {
            if ( ( flags & FORMAT_NUMERIC ) != 0 )
               if ( ( flags & FORMAT_WITH_YEAR ) != 0 )
                  pattern += "d.M.yyyy";
               else
                  pattern += "d.M";
            else if ( ( flags & FORMAT_WITH_YEAR ) != 0 )
               pattern += "d. MMMM yyyy";
            else
               pattern += "d. MMMM";
         }
         
         // Date US
         else
         {
            if ( ( flags & FORMAT_NUMERIC ) != 0 )
               if ( ( flags & FORMAT_WITH_YEAR ) != 0 )
                  pattern += "M/d/yyyy";
               else
                  pattern += "M/d";
            else if ( ( flags & FORMAT_WITH_YEAR ) != 0 )
               pattern += "MMMM d, yyyy";
            else
               pattern += "MMMM d";
         }
      }
      
      // Time
      if ( time )
      {
         // Time 12
         if ( settings.getTimeformat() == Settings.TIMEFORMAT_12 )
         {
            if ( date )
               pattern += ", h:mm a";
            else
               pattern += "h:mm a";
         }
         
         // Time 24
         else
         {
            if ( date )
               pattern += ", k:mm";
            else
               pattern += "k:mm";
         }
      }
      
      return pattern;
   }
}