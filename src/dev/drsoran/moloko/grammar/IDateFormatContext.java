package dev.drsoran.moloko.grammar;

public interface IDateFormatContext
{
   String formatDateNumeric( long millis );
   
   
   
   String getNumericDateFormatPattern( boolean withYear );
   
   
   
   String formatDateNumeric( String part1, String part2 );
   
   
   
   String formatDateNumeric( String part1, String part2, String part3 );
}
