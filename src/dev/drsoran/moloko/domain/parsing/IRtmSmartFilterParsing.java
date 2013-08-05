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

package dev.drsoran.moloko.domain.parsing;

import dev.drsoran.moloko.domain.parsing.rtmsmart.IRtmSmartFilterEvaluator;
import dev.drsoran.moloko.domain.parsing.rtmsmart.RtmSmartFilterParsingReturn;
import dev.drsoran.moloko.domain.parsing.rtmsmart.RtmSmartFilterTokenCollection;


public interface IRtmSmartFilterParsing
{
   RtmSmartFilterParsingReturn evaluateRtmSmartFilter( String filterString ) throws GrammarException;
   
   
   
   RtmSmartFilterParsingReturn evaluateRtmSmartFilter( String filterString,
                                                       IRtmSmartFilterEvaluator evaluator ) throws GrammarException;
   
   
   
   RtmSmartFilterTokenCollection getSmartFilterTokens( String filterString ) throws GrammarException;
   
   
   
   boolean isParsableSmartFilter( String filterString );
}
