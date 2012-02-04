/* 
 *	Copyright (c) 2012 Ronny R�hricht
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

package dev.drsoran.moloko.notification;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;


abstract class AbstractNotificationTasksLoader extends
         AsyncTask< Void, Void, Cursor >
{
   public final static int MSG_TASKS_LOADED_ASYNC = 1;
   
   private final Handler handler;
   
   protected final Context context;
   
   
   
   protected AbstractNotificationTasksLoader( Context context, Handler handler )
   {
      this.context = context;
      this.handler = handler;
   }
   
   
   
   @Override
   protected void onPostExecute( Cursor result )
   {
      if ( handler != null )
      {
         final Message msg = handler.obtainMessage( MSG_TASKS_LOADED_ASYNC,
                                                    result );
         handler.sendMessage( msg );
      }
   }
}
