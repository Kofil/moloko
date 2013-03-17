/*
 * Copyright (c) 2010 Ronny R�hricht
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

import android.database.Cursor;
import dev.drsoran.moloko.content.db.Columns.SyncColumns;


public class SyncQuery
{
   private final RtmDatabase database;
   
   private final SyncTable syncTable;
   
   
   
   public SyncQuery( RtmDatabase database, SyncTable syncTable )
   {
      this.database = database;
      this.syncTable = syncTable;
   }
   
   
   
   public long getSyncId()
   {
      long id = -1;
      Cursor c = null;
      
      try
      {
         c = database.getReadable().query( syncTable.getTableName(),
                                           new String[]
                                           { SyncColumns._ID },
                                           null,
                                           null,
                                           null,
                                           null,
                                           null );
         
         // We only consider the first entry cause we do not expect
         // more than 1 entry in this table
         if ( c.moveToFirst() )
         {
            id = c.getLong( Columns.ID_IDX );
         }
      }
      finally
      {
         if ( c != null )
         {
            c.close();
         }
      }
      
      return id;
   }
   
   
   
   public Cursor getLastSync()
   {
      final Cursor c = database.getReadable().query( syncTable.getTableName(),
                                                     syncTable.getProjection(),
                                                     null,
                                                     null,
                                                     null,
                                                     null,
                                                     null );
      return c;
   }
}
