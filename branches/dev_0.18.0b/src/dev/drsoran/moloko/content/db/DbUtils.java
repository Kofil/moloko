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

package dev.drsoran.moloko.content.db;

import java.util.Iterator;

import android.database.sqlite.SQLiteDatabase;


final class DbUtils
{
   private DbUtils()
   {
      throw new AssertionError( "This class should not be instantiated." );
   }
   
   
   
   public static void doTransactional( SQLiteDatabase sqliteDatabase,
                                       Runnable action )
   {
      try
      {
         sqliteDatabase.beginTransaction();
         
         action.run();
         
         sqliteDatabase.setTransactionSuccessful();
      }
      finally
      {
         sqliteDatabase.endTransaction();
      }
   }
   
   
   
   public static < T > String toColumnList( Iterable< T > set,
                                            String colName,
                                            String seperator )
   {
      final StringBuilder sb = new StringBuilder();
      
      for ( Iterator< T > i = set.iterator(); i.hasNext(); )
      {
         final T obj = i.next();
         
         sb.append( colName ).append( "=" ).append( obj.toString() );
         
         if ( i.hasNext() )
         {
            sb.append( seperator );
         }
      }
      
      return sb.toString();
   }
}
