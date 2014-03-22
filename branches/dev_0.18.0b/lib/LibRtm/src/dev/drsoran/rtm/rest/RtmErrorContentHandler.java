/* 
 *	Copyright (c) 2014 Ronny R�hricht
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

package dev.drsoran.rtm.rest;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import dev.drsoran.rtm.Strings;
import dev.drsoran.rtm.service.RtmError;


public class RtmErrorContentHandler extends RtmContentHandler< RtmError >
{
   private RtmError error;
   
   
   
   public RtmErrorContentHandler()
   {
      this( null );
   }
   
   
   
   public RtmErrorContentHandler(
      IRtmContentHandlerListener< RtmError > listener )
   {
      super( listener );
   }
   
   
   
   @Override
   protected void startElement( String qName, Attributes attributes ) throws SAXException
   {
      if ( "err".equalsIgnoreCase( qName ) )
      {
         error = new RtmError( XmlAttr.getInt( attributes, "code" ),
                               XmlAttr.getOptString( attributes,
                                                     "msg",
                                                     Strings.EMPTY_STRING ) );
      }
   }
   
   
   
   @Override
   protected void endElement( String qName ) throws SAXException
   {
      if ( "err".equalsIgnoreCase( qName ) )
      {
         setContentElementAndNotify( error );
      }
   }
   
   
   
   @Override
   protected void cleanUpState()
   {
      error = null;
   }
}
