/* 
	Copyright (c) 2012 Ronny R�hricht

	This file is part of Moloko.

	Moloko is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	Moloko is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with Moloko.  If not, see <http://www.gnu.org/licenses/>.

	Contributors:
     Ronny R�hricht - implementation
 */

package dev.drsoran.moloko.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import dev.drsoran.moloko.IOnNetworkStatusChangedListener;
import dev.drsoran.moloko.NotifierContextHandler;


public class NetworkStatusReceiver extends BroadcastReceiver
{
   private final NotifierContextHandler handler;
   
   
   
   public NetworkStatusReceiver( NotifierContextHandler handler )
   {
      this.handler = handler;
   }
   
   
   
   @Override
   public void onReceive( Context context, Intent intent )
   {
      final ConnectivityManager cm = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );
      if ( cm != null )
      {
         handler.onNetworkStatusChanged( IOnNetworkStatusChangedListener.BACKGROUND_DATA_STATUS,
                                         isConnected( cm ) );
      }
   }
   
   
   
   private boolean isConnected( ConnectivityManager connectivityManager )
   {
      final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
      return activeNetworkInfo != null && activeNetworkInfo.isConnected();
   }
}
