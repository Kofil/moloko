/* 
 *	Copyright (c) 2013 Ronny R�hricht
 *
 *	This file is part of MolokoTest.
 *
 *	MolokoTest is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	MolokoTest is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with MolokoTest.  If not, see <http://www.gnu.org/licenses/>.
 *
 *	Contributors:
 * Ronny R�hricht - implementation
 */

package dev.drsoran.test.unit.rtm.rest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.easymock.EasyMock;
import org.junit.ClassRule;
import org.junit.Test;

import dev.drsoran.moloko.test.XmlFileResource;
import dev.drsoran.rtm.rest.IRtmContentHandlerListener;
import dev.drsoran.rtm.rest.RtmContentHandler;
import dev.drsoran.rtm.rest.RtmFrobContentHandler;
import dev.drsoran.rtm.service.RtmFrob;


public class RtmFrobContentHandlerFixture extends
         RtmContentHandlerTestCase< RtmFrob >
{
   @ClassRule
   public static final XmlFileResource testFile = new XmlFileResource( RtmFrobContentHandlerFixture.class,
                                                                       "RtmFrob.xml" );
   
   
   
   @SuppressWarnings( "unchecked" )
   @Test
   public void testRtmFrobContentHandlerIRtmContentHandlerListenerOfRtmFrob()
   {
      new RtmFrobContentHandler( EasyMock.createNiceMock( IRtmContentHandlerListener.class ) );
   }
   
   
   
   @Test
   public void testReadFrob() throws Exception
   {
      final RtmFrob frob = readContent( testFile );
      
      assertThat( frob.getValue(), is( "123456" ) );
   }
   
   
   
   @Override
   protected RtmContentHandler< RtmFrob > createHandler()
   {
      return new RtmFrobContentHandler( null );
   }
   
   
   
   @Override
   protected RtmContentHandler< RtmFrob > createHandlerWithListener( IRtmContentHandlerListener< RtmFrob > listener )
   {
      return new RtmFrobContentHandler( listener );
   }
   
   
   
   @Override
   protected RtmFrob createDummyContent()
   {
      return new RtmFrob( "1" );
   }
}
