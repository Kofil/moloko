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

package dev.drsoran.rtm.rest;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import dev.drsoran.Strings;
import dev.drsoran.rtm.model.RtmLocation;


public class RtmLocationContentHandler extends RtmContentHandler< RtmLocation >
{
   public RtmLocationContentHandler()
   {
      this( null );
   }
   
   
   
   public RtmLocationContentHandler(
      IRtmContentHandlerListener< RtmLocation > listener )
   {
      super( listener );
   }
   
   
   
   @Override
   public void startElement( String qName, Attributes attributes ) throws SAXException
   {
      setContentElementAndNotify( new RtmLocation( XmlAttr.getStringNotNull( attributes,
                                                                             "id" ),
                                                   XmlAttr.getStringNotNull( attributes,
                                                                             "name" ),
                                                   XmlAttr.getFloat( attributes,
                                                                     "longitude" ),
                                                   XmlAttr.getFloat( attributes,
                                                                     "latitude" ),
                                                   XmlAttr.getOptString( attributes,
                                                                         "address",
                                                                         Strings.EMPTY_STRING ),
                                                   XmlAttr.getBoolean( attributes,
                                                                       "viewable" ),
                                                   XmlAttr.getInt( attributes,
                                                                   "zoom" ) ) );
   }
}
