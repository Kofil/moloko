import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

import dev.drsoran.moloko.grammar.RecurrencePatternParser;
import dev.drsoran.moloko.grammar.en.RecurrenceLexer;
import dev.drsoran.moloko.grammar.en.RecurrenceParser;
import dev.drsoran.moloko.util.ANTLRNoCaseStringStream;


public class RecurrenceTestCase
{
   public static RecurrenceParser parseRecurrence( String string,
                                        String freq,
                                        int interval,
                                        String res,
                                        String resVal,
                                        String resEx,
                                        String resExVal,
                                        String until,
                                        int forVal,
                                        boolean isEvery )
   {
      final RecurrenceLexer lexer = new RecurrenceLexer( new ANTLRNoCaseStringStream( string ) );
      final CommonTokenStream antlrTokens = new CommonTokenStream( lexer );
      final RecurrenceParser parser = new RecurrenceParser( antlrTokens );
      
      System.out.println( ">input: " + string );
      
      try
      {
         final Map< String, Object > result = parser.parseRecurrence();
         
         final String res_freq = (String) result.get( RecurrencePatternParser.OP_FREQ_LIT );
         final int res_interval = (Integer) result.get( RecurrencePatternParser.OP_INTERVAL_LIT );
         final String res_byDay = (String) result.get( RecurrencePatternParser.OP_BYDAY_LIT );
         final String res_byMonth = (String) result.get( RecurrencePatternParser.OP_BYMONTH_LIT );
         final String res_byMonthDay = (String) result.get( RecurrencePatternParser.OP_BYMONTHDAY_LIT );
         final boolean res_isEvery = (Boolean) result.get( RecurrencePatternParser.IS_EVERY );
         final String res_until = (String) result.get( RecurrencePatternParser.OP_UNTIL_LIT );
         final Integer res_for = (Integer) result.get( RecurrencePatternParser.OP_COUNT_LIT );
         
         System.out.println( string + "_freq: " + res_freq );
         System.out.println( string + "_interval: " + res_interval );
         System.out.println( string + "_byDay: " + res_byDay );
         System.out.println( string + "_byMonth: " + res_byMonth );
         System.out.println( string + "_byMonthDay: " + res_byMonthDay );
         System.out.println( string + "_isEvery: " + res_isEvery );
         System.out.println( string + "_until: " + res_until );
         System.out.println( string + "_for: " + res_for );
         
         Asserts.assertEquals( res_freq, freq, "Freq is wrong." );
         Asserts.assertEquals( res_interval, interval, "Interval is wrong." );
         Asserts.assertEquals( res_isEvery, isEvery, "IsEvery is wrong." );
         
         if ( res != null )
         {
            if ( res.equals( RecurrencePatternParser.OP_BYDAY_LIT ) )
               Asserts.assertNonNull( res_byDay, "" );
            else if ( res.equals( RecurrencePatternParser.OP_BYMONTH_LIT ) )
               Asserts.assertNonNull( res_byMonth, "" );
            else if ( res.equals( RecurrencePatternParser.OP_BYMONTHDAY_LIT ) )
               Asserts.assertNonNull( res_byMonthDay, "" );
         }
         
         if ( resEx != null )
         {
            if ( resEx.equals( RecurrencePatternParser.OP_BYDAY_LIT ) )
               Asserts.assertNonNull( res_byDay, "" );
            else if ( resEx.equals( RecurrencePatternParser.OP_BYMONTH_LIT ) )
               Asserts.assertNonNull( res_byMonth, "" );
            else if ( resEx.equals( RecurrencePatternParser.OP_BYMONTHDAY_LIT ) )
               Asserts.assertNonNull( res_byMonthDay, "" );
         }
         
         if ( resVal != null )
         {
            final boolean foundResVal = ( res_byDay != null && res_byDay.equals( resVal ) )
               || ( res_byMonth != null && res_byMonth.equals( resVal ) )
               || ( res_byMonthDay != null && res_byMonthDay.equals( resVal ) );
            
            if ( !foundResVal )
               System.err.println( "Resolution value is wrong." );
         }
         
         if ( resExVal != null )
         {
            final boolean foundResVal = ( res_byDay != null && res_byDay.equals( resExVal ) )
               || ( res_byMonth != null && res_byMonth.equals( resExVal ) )
               || ( res_byMonthDay != null && res_byMonthDay.equals( resExVal ) );
            
            if ( !foundResVal )
               System.err.println( "ResolutionEx value is wrong." );
         }
         
         if ( until != null )
            Asserts.assertEquals( res_until, until, "Until value is wrong." );
         
         if ( forVal != -1 )
            Asserts.assertEquals( res_for, forVal, "For value is wrong." );
      }
      catch ( RecognitionException e )
      {
         System.err.println( "Parsing failed!" );
      }
   }
   


   public final static void execute() throws ParseException
   {
      parseRecurrence( "every year",
                       RecurrencePatternParser.VAL_YEARLY_LIT,
                       1,
                       null,
                       null,
                       true );
      parseRecurrence( "every 1st and 25th",
                       RecurrencePatternParser.VAL_MONTHLY_LIT,
                       1,
                       RecurrencePatternParser.OP_BYMONTHDAY_LIT,
                       "1,25",
                       true );
      parseRecurrence( "every tuesday",
                       RecurrencePatternParser.VAL_WEEKLY_LIT,
                       1,
                       RecurrencePatternParser.OP_BYDAY_LIT,
                       "TU",
                       true );
      parseRecurrence( "every monday, wednesday",
                       RecurrencePatternParser.VAL_WEEKLY_LIT,
                       1,
                       RecurrencePatternParser.OP_BYDAY_LIT,
                       "MO,WE",
                       true );
      parseRecurrence( "every 2nd friday",
                       RecurrencePatternParser.VAL_WEEKLY_LIT,
                       2,
                       RecurrencePatternParser.OP_BYDAY_LIT,
                       "FR",
                       true );
      parseRecurrence( "every weekday",
                       RecurrencePatternParser.VAL_WEEKLY_LIT,
                       1,
                       RecurrencePatternParser.OP_BYDAY_LIT,
                       "MO,TU,WE,TH,FR",
                       true );
      parseRecurrence( "every day",
                       RecurrencePatternParser.VAL_DAILY_LIT,
                       1,
                       null,
                       null,
                       true );
      parseRecurrence( "every 2 weeks",
                       RecurrencePatternParser.VAL_WEEKLY_LIT,
                       2,
                       null,
                       null,
                       true );
      parseRecurrence( "every month on the 4th",
                       RecurrencePatternParser.VAL_MONTHLY_LIT,
                       1,
                       RecurrencePatternParser.OP_BYMONTHDAY_LIT,
                       "4",
                       true );
      parseRecurrence( "every 3rd tuesday",
                       RecurrencePatternParser.VAL_WEEKLY_LIT,
                       3,
                       RecurrencePatternParser.OP_BYDAY_LIT,
                       "TU",
                       true );
      parseRecurrence( "every month on the 3rd tuesday",
                       RecurrencePatternParser.VAL_MONTHLY_LIT,
                       1,
                       RecurrencePatternParser.OP_BYDAY_LIT,
                       "3TU",
                       true );
      parseRecurrence( "every month on the last monday",
                       RecurrencePatternParser.VAL_MONTHLY_LIT,
                       1,
                       RecurrencePatternParser.OP_BYDAY_LIT,
                       "-1MO",
                       true );
      parseRecurrence( "every month on the 2nd last friday",
                       RecurrencePatternParser.VAL_MONTHLY_LIT,
                       1,
                       RecurrencePatternParser.OP_BYDAY_LIT,
                       "-2FR",
                       true );
      parseRecurrence( "every month on the 1st friday",
                       RecurrencePatternParser.VAL_MONTHLY_LIT,
                       1,
                       RecurrencePatternParser.OP_BYDAY_LIT,
                       "1FR",
                       true );
      parseRecurrence( "every year on the 1st friday, monday of january",
                       RecurrencePatternParser.VAL_YEARLY_LIT,
                       1,
                       RecurrencePatternParser.OP_BYDAY_LIT,
                       "1MO,1FR",
                       RecurrencePatternParser.OP_BYMONTH_LIT,
                       "1",
                       true );
      parseRecurrence( "every monday, wednesday until 10.10.2010",
                       RecurrencePatternParser.VAL_WEEKLY_LIT,
                       1,
                       RecurrencePatternParser.OP_BYDAY_LIT,
                       "MO,WE",
                       null,
                       null,
                       SDF_FORMAT.format( SDF_PARSE.parse( "10.10.2010" ) ),
                       -1,
                       true );
      {
         final Calendar cal = Calendar.getInstance();
         cal.roll( Calendar.DAY_OF_MONTH, true );
         
         cal.set( Calendar.HOUR, 0 );
         cal.set( Calendar.HOUR_OF_DAY, 0 );
         cal.set( Calendar.MINUTE, 0 );
         cal.set( Calendar.SECOND, 0 );
         cal.set( Calendar.MILLISECOND, 0 );
         
         parseRecurrence( "every day until tomorrow",
                          RecurrencePatternParser.VAL_DAILY_LIT,
                          1,
                          null,
                          null,
                          null,
                          null,
                          SDF_FORMAT.format( cal.getTime() ),
                          -1,
                          true );
      }
      parseRecurrence( "every day for 10 times",
                       RecurrencePatternParser.VAL_DAILY_LIT,
                       1,
                       null,
                       null,
                       null,
                       null,
                       null,
                       10,
                       true );
   }
}