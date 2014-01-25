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

package dev.drsoran.rtm.sync.db;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import dev.drsoran.db.AbstractTable;
import dev.drsoran.rtm.sync.db.TableColumns.ModificationColumns;


class ModificationsTable extends AbstractTable
{
   public final static String TABLE_NAME = "modifications";
   
   
   
   public ModificationsTable( SQLiteDatabase database )
   {
      super( database, TABLE_NAME );
   }
   
   
   
   @Override
   public void create() throws SQLException
   {
      final StringBuilder builder = new StringBuilder();
      
      builder.append( "CREATE TABLE " );
      builder.append( TABLE_NAME );
      builder.append( " ( " );
      builder.append( ModificationColumns._ID );
      builder.append( " INTEGER NOT NULL CONSTRAINT PK_MODIFICATIONS PRIMARY KEY AUTOINCREMENT, " );
      builder.append( ModificationColumns.SRC_ENTITY_URI );
      builder.append( " TEXT NOT NULL, " );
      builder.append( ModificationColumns.DST_ENTITY_URI );
      builder.append( " TEXT NOT NULL, " );
      builder.append( ModificationColumns.ATTRIBUTE );
      builder.append( " TEXT NOT NULL, " );
      builder.append( ModificationColumns.NEW_VALUE );
      builder.append( " TEXT, " );
      builder.append( ModificationColumns.SYNCED_VALUE );
      builder.append( " TEXT, " );
      builder.append( ModificationColumns.TIMESTAMP );
      builder.append( " INTEGER NOT NULL" );
      builder.append( ");" );
      
      getDatabase().execSQL( builder.toString() );
   }
   
   
   
   @Override
   public String getDefaultSortOrder()
   {
      return ModificationColumns.DEFAULT_SORT_ORDER;
   }
   
   
   
   @Override
   public String[] getProjection()
   {
      return ModificationColumns.PROJECTION;
   }
}