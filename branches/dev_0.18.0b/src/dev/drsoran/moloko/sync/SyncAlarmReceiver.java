/*
 * Copyright (c) 2012 Ronny R�hricht
 * 
 * This file is part of Moloko.
 * 
 * Moloko is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Moloko is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Moloko. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 * Ronny R�hricht - implementation
 */

package dev.drsoran.moloko.sync;

import android.accounts.Account;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import dev.drsoran.moloko.app.MolokoApp;
import dev.drsoran.moloko.app.account.AccountUtils;
import dev.drsoran.moloko.sync.util.SyncUtils;


public class SyncAlarmReceiver extends BroadcastReceiver
{
   @Override
   public void onReceive( Context context, Intent intent )
   {
      final Account account = AccountUtils.getRtmAccount( context.getApplicationContext() );
      
      if ( account != null )
      {
         if ( SyncUtils.isReadyToSync( context.getApplicationContext() ) )
         {
            SyncUtils.requestScheduledSync( account );
         }
      }
      else
      {
         MolokoApp.get( context.getApplicationContext() ).stopPeriodicSync();
      }
   }
}
