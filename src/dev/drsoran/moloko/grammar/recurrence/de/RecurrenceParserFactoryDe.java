/* 
 *	Copyright (c) 2013 Ronny R�hricht
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

package dev.drsoran.moloko.grammar.recurrence.de;

import java.util.Locale;

import dev.drsoran.moloko.domain.parsing.IDateTimeParsing;
import dev.drsoran.moloko.grammar.recurrence.AbstractANTLRRecurrenceParser;
import dev.drsoran.moloko.grammar.recurrence.IRecurrenceParser;
import dev.drsoran.moloko.grammar.recurrence.IRecurrenceParserFactory;
import dev.drsoran.moloko.grammar.recurrence.RecurrenceParserImpl;


public class RecurrenceParserFactoryDe implements IRecurrenceParserFactory
{
   private final IDateTimeParsing dateTimeParsing;
   
   
   
   public RecurrenceParserFactoryDe( IDateTimeParsing dateTimeParsing )
   {
      this.dateTimeParsing = dateTimeParsing;
   }
   
   
   
   @Override
   public IRecurrenceParser createRecurrenceParser()
   {
      final AbstractANTLRRecurrenceParser antlrRecurrenceParser = new RecurrenceParser();
      antlrRecurrenceParser.setDateTimeParsing( dateTimeParsing );
      
      return new RecurrenceParserImpl( Locale.ENGLISH,
                                       antlrRecurrenceParser,
                                       new RecurrenceLexer() );
   }
   
   
   
   @Override
   public Locale getParserLocale()
   {
      return Locale.GERMAN;
   }
}
