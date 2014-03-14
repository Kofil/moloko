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

package dev.drsoran.moloko.test.unit.domain.model;

import static dev.drsoran.moloko.test.TestConstants.NO_ID;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;

import dev.drsoran.moloko.domain.model.Participant;
import dev.drsoran.moloko.test.MolokoTestCase;


public class ParticipantFixture extends MolokoTestCase
{
   
   @Test
   public void testParticipant()
   {
      createParticipant();
      new Participant( 1, "", "user" );
   }
   
   
   
   @Test
   public void testParticipantNoId()
   {
      new Participant( NO_ID, "name", "user" );
   }
   
   
   
   @Test( expected = IllegalArgumentException.class )
   public void testParticipantNullName()
   {
      new Participant( 1, null, "user" );
   }
   
   
   
   @Test( expected = IllegalArgumentException.class )
   public void testParticipantNullUser()
   {
      new Participant( 1, "name", null );
   }
   
   
   
   @Test( expected = IllegalArgumentException.class )
   public void testParticipantEmptyUser()
   {
      new Participant( 1, "name", "" );
   }
   
   
   
   @Test
   public void testGetId()
   {
      assertThat( createParticipant().getId(), is( 1L ) );
   }
   
   
   
   @Test
   public void testGetFullname()
   {
      assertThat( createParticipant().getFullname(), is( "name" ) );
   }
   
   
   
   @Test
   public void testGetUsername()
   {
      assertThat( createParticipant().getUsername(), is( "user" ) );
   }
   
   
   
   @Test
   public void testToString()
   {
      createParticipant().toString();
   }
   
   
   
   @Test
   public void testSerializeDeserialize() throws IOException,
                                         ClassNotFoundException
   {
      final ByteArrayOutputStream memStream = new ByteArrayOutputStream();
      final ObjectOutputStream oos = new ObjectOutputStream( memStream );
      
      final Participant participant = createParticipant();
      
      oos.writeObject( participant );
      oos.close();
      
      byte[] serialized = memStream.toByteArray();
      memStream.close();
      
      final ByteArrayInputStream memIStream = new ByteArrayInputStream( serialized );
      final ObjectInputStream ois = new ObjectInputStream( memIStream );
      
      final Participant deserialized = (Participant) ois.readObject();
      
      ois.close();
      memIStream.close();
      
      assertThat( deserialized, is( notNullValue() ) );
      assertThat( deserialized.getId(), is( 1L ) );
      assertThat( deserialized.getFullname(), is( "name" ) );
      assertThat( deserialized.getUsername(), is( "user" ) );
   }
   
   
   
   private Participant createParticipant()
   {
      return new Participant( 1, "name", "user" );
   }
}
