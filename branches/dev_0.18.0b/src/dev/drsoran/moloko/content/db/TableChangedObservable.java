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

package dev.drsoran.moloko.content.db;

import java.util.ArrayList;

import android.database.Observable;


class TableChangedObservable extends Observable< ITableChangedObserver >
{
   public void notifyTableChanged( String tableName )
   {
      for ( ITableChangedObserver observer : getObservers() )
      {
         observer.onTableChanged( tableName );
      }
   }
   
   
   
   public void notifyTableChanged( String tableName, long id )
   {
      for ( ITableChangedObserver observer : getObservers() )
      {
         observer.onTableChanged( tableName, id );
      }
   }
   
   
   
   private Iterable< ITableChangedObserver > getObservers()
   {
      Iterable< ITableChangedObserver > observers;
      synchronized ( mObservers )
      {
         // Make a thread local copy of the observers to not hold the lock while
         // notifying.
         observers = new ArrayList< ITableChangedObserver >( mObservers );
      }
      
      return observers;
   }
}
