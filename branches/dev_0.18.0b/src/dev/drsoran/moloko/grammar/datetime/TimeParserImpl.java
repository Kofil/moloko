/* 
 *	Copyright (c) 2011 Ronny R�hricht
 *
 *	This file is part of Moloko.
 *
 *	Moloko is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	Moloko is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with Moloko.  If not, see <http://www.gnu.org/licenses/>.
 *
 *	Contributors:
 * Ronny R�hricht - implementation
 */

package dev.drsoran.moloko.grammar.datetime;

import java.util.Locale;

import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.RecognitionException;

import dev.drsoran.moloko.MolokoCalendar;
import dev.drsoran.moloko.grammar.ANTLRNoCaseStringStream;
import dev.drsoran.moloko.grammar.GrammarException;


public class TimeParserImpl implements ITimeParser
{
   private final AbstractANTLRTimeParser parser;
   
   private final Lexer lexer;
   
   private final Locale locale;
   
   
   
   public TimeParserImpl( Locale locale, AbstractANTLRTimeParser timeParser,
      Lexer timeLexer )
   {
      this.locale = locale;
      this.parser = timeParser;
      this.lexer = timeLexer;
   }
   
   
   
   @Override
   public ParseReturn parseTime( String time,
                                 MolokoCalendar cal,
                                 boolean adjustDay ) throws GrammarException
   {
      prepareLexerAndParser( time );
      
      try
      {
         return parser.parseTime( cal, adjustDay );
      }
      catch ( RecognitionException e )
      {
         throw new GrammarException( "Failed to parse time '" + time + "'", e );
      }
   }
   
   
   
   @Override
   public ParseReturn parseTimeSpec( String timeSpec,
                                     MolokoCalendar cal,
                                     boolean adjustDay ) throws GrammarException
   {
      prepareLexerAndParser( timeSpec );
      
      try
      {
         return parser.parseTimeSpec( cal, adjustDay );
      }
      catch ( RecognitionException e )
      {
         throw new GrammarException( "Failed to parse time spec'" + timeSpec
            + "'", e );
      }
   }
   
   
   
   @Override
   public long parseTimeEstimate( String timeEstimate ) throws GrammarException
   {
      prepareLexerAndParser( timeEstimate );
      
      try
      {
         return parser.parseTimeEstimate();
      }
      catch ( RecognitionException e )
      {
         throw new GrammarException( "Failed to parse time estimation'"
            + timeEstimate + "'", e );
      }
   }
   
   
   
   @Override
   public Locale getLocale()
   {
      return locale;
   }
   
   
   
   private void prepareLexerAndParser( String time )
   {
      final ANTLRNoCaseStringStream stream = new ANTLRNoCaseStringStream( time );
      lexer.setCharStream( stream );
      
      final CommonTokenStream antlrTokens = new CommonTokenStream( lexer );
      parser.setTokenStream( antlrTokens );
   }
}
