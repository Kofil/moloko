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

package dev.drsoran.moloko.test.unit.grammar.datetime;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import dev.drsoran.moloko.MolokoCalendar;
import dev.drsoran.moloko.grammar.datetime.ParseDateWithinReturn;
import dev.drsoran.moloko.test.MolokoTestCase;
import dev.drsoran.moloko.test.TestConstants;


public class ParseDateWithinReturnFixture extends MolokoTestCase
{
   @Test
   public void testParseDateWithinReturn()
   {
      final MolokoCalendar cal1 = MolokoCalendar.getInstance();
      cal1.setTimeInMillis( TestConstants.NOW );
      
      final MolokoCalendar cal2 = MolokoCalendar.getInstance();
      cal2.setTimeInMillis( TestConstants.LATER );
      
      new ParseDateWithinReturn( cal1, cal2 );
   }
   
   
   
   @Test
   public void testStartEpoch()
   {
      final MolokoCalendar cal1 = MolokoCalendar.getInstance();
      cal1.setTimeInMillis( TestConstants.NOW );
      
      final MolokoCalendar cal2 = MolokoCalendar.getInstance();
      cal2.setTimeInMillis( TestConstants.LATER );
      
      assertThat( new ParseDateWithinReturn( cal1, cal2 ).startEpoch.getTimeInMillis(),
                  is( TestConstants.NOW ) );
   }
   
   
   
   @Test
   public void testEndEpoch()
   {
      final MolokoCalendar cal1 = MolokoCalendar.getInstance();
      cal1.setTimeInMillis( TestConstants.NOW );
      
      final MolokoCalendar cal2 = MolokoCalendar.getInstance();
      cal2.setTimeInMillis( TestConstants.LATER );
      
      assertThat( new ParseDateWithinReturn( cal1, cal2 ).endEpoch.getTimeInMillis(),
                  is( TestConstants.LATER ) );
   }
}
