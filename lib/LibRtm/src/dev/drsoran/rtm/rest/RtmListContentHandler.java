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

import static dev.drsoran.rtm.content.ContentProperties.BaseProperties.ID;
import static dev.drsoran.rtm.content.ContentProperties.RtmTasksListProperties.ARCHIVED;
import static dev.drsoran.rtm.content.ContentProperties.RtmTasksListProperties.DELETED;
import static dev.drsoran.rtm.content.ContentProperties.RtmTasksListProperties.LOCKED;
import static dev.drsoran.rtm.content.ContentProperties.RtmTasksListProperties.NAME;
import static dev.drsoran.rtm.content.ContentProperties.RtmTasksListProperties.POSITION;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import dev.drsoran.rtm.Strings;
import dev.drsoran.rtm.model.RtmTasksList;


public class RtmListContentHandler extends RtmContentHandler< RtmTasksList >
{
   private Attributes listAttributes = new AttributesImpl();
   
   private String smartFilter;
   
   
   
   public RtmListContentHandler()
   {
      this( null );
   }
   
   
   
   public RtmListContentHandler(
      IRtmContentHandlerListener< RtmTasksList > listener )
   {
      super( listener );
   }
   
   
   
   @Override
   public void characters( String string ) throws SAXException
   {
      smartFilter = string;
   }
   
   
   
   @Override
   public void startElement( String qName, Attributes attributes ) throws SAXException
   {
      if ( "list".equalsIgnoreCase( qName ) )
      {
         listAttributes = XmlAttr.copy( attributes );
      }
   }
   
   
   
   @Override
   public void endElement( String qName ) throws SAXException
   {
      if ( "list".equalsIgnoreCase( qName ) )
      {
         readTasksList();
      }
      
      // Handle the case that we have a smart list with empty <filter> element.
      // This is still a smart list.
      else if ( "filter".equalsIgnoreCase( qName ) && smartFilter == null )
      {
         smartFilter = Strings.EMPTY_STRING;
      }
   }
   
   
   
   private void readTasksList() throws SAXException
   {
      setContentElementAndNotify( new RtmTasksList( listAttributes.getValue( ID ),
                                                    XmlAttr.getInt( listAttributes,
                                                                    POSITION ),
                                                    XmlAttr.getBoolean( listAttributes,
                                                                        DELETED ),
                                                    XmlAttr.getBoolean( listAttributes,
                                                                        LOCKED ),
                                                    XmlAttr.getBoolean( listAttributes,
                                                                        ARCHIVED ),
                                                    listAttributes.getValue( NAME ),
                                                    smartFilter ) );
      smartFilter = null;
   }
   
   
   
   @Override
   protected void cleanUpState()
   {
      listAttributes = null;
      smartFilter = null;
   }
}
