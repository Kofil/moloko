/* 
 *	Copyright (c) 2010 Ronny R�hricht
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

package dev.drsoran.moloko.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.format.Time;
import dev.drsoran.moloko.MolokoApp;


public class TimeTickReceiver extends BroadcastReceiver
{
   @Override
   public void onReceive( Context context, Intent intent )
   {
      final Time now = new Time();
      now.setToNow();
      
      if ( now.hour == 0 && now.minute == 0 )
      {
         // TODO: Also notify HomeActivity Calendars and taskbars
         MolokoApp.getMolokoNotificationManager()
                  .reEvaluatePermanentNotifications();
      }
   }
}
