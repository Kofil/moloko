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

package dev.drsoran.moloko.grammar;

import java.util.Locale;

import android.util.Pair;


class ParserLanguageDetector
{
   private ParserLanguageDetector()
   {
      throw new AssertionError();
   }
   
   
   
   public static < TParser extends ILocalizedParser, TResult > Pair< TParser, TResult > detectLanguageAndParse( TParser existingParser,
                                                                                                                Iterable< TParser > availableParsers,
                                                                                                                IParserFunc< TParser, TResult > parserFunc )
   {
      TResult result = null;
      Locale dontTryLocale = null;
      
      // If we have a parser, try the current parser first
      if ( existingParser != null )
      {
         // If parsing with the current parser fails, we don't need to try it again.
         dontTryLocale = existingParser.getLocale();
         result = TryParse( existingParser, parserFunc );
      }
      
      if ( result == null )
      {
         for ( TParser parser : availableParsers )
         {
            if ( dontTryLocale == null
               || parser.getLocale().hashCode() != dontTryLocale.hashCode() )
            {
               result = TryParse( parser, parserFunc );
               if ( result != null )
               {
                  return Pair.create( parser, result );
               }
            }
         }
      }
      
      return Pair.< TParser, TResult > create( null, null );
   }
   
   
   
   private static < TParser extends ILocalizedParser, TResult > TResult TryParse( TParser existingParser,
                                                                                  IParserFunc< TParser, TResult > parserFunc )
   {
      TResult result = null;
      
      try
      {
         result = parserFunc.call( existingParser );
      }
      catch ( GrammarException e )
      {
      }
      
      return result;
   }
}
