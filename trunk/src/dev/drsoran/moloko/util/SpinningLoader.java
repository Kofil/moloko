/* 
 *	Copyright (c) 2011 Ronny R�hricht
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

package dev.drsoran.moloko.util;

import android.app.Activity;
import android.os.AsyncTask;


public class SpinningLoader extends AsyncTask< Runnable, Void, Void >
{
   private final Activity activity;
   
   private final int layoutId;
   
   

   public SpinningLoader( Activity activity, int layoutId )
   {
      if ( activity == null )
         throw new NullPointerException( "activity is null" );
      
      this.activity = activity;
      this.layoutId = layoutId;
   }
   


   @Override
   protected void onPreExecute()
   {
      // activity.setContentView( R.layout.app_loading_spinner );
   }
   


   @Override
   protected void onPostExecute( Void result )
   {
      // activity.setContentView( layoutId );
   }
   


   @Override
   protected Void doInBackground( Runnable... params )
   {
      if ( params != null )
         for ( Runnable runnable : params )
         {
            // runnable.run();
         }
      
      return null;
   }
}
