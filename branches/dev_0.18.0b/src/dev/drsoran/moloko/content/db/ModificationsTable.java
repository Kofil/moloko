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

import java.util.HashMap;
import java.util.Map;

import android.database.SQLException;
import dev.drsoran.moloko.content.db.Columns.ModificationsColumns;


class ModificationsTable extends Table
{
   public final static String TABLE_NAME = "modifications";
   
   public final static Map< String, String > PROJECTION_MAP = new HashMap< String, String >();
   
   public final static String[] PROJECTION =
   { ModificationsColumns._ID, ModificationsColumns.ENTITY_URI,
    ModificationsColumns.COL_NAME, ModificationsColumns.NEW_VALUE,
    ModificationsColumns.SYNCED_VALUE, ModificationsColumns.TIMESTAMP };
   
   public final static Map< String, Integer > COL_INDICES = new HashMap< String, Integer >();
   
   static
   {
      initProjectionDependent( PROJECTION, PROJECTION_MAP, COL_INDICES );
   }
   
   
   
   public ModificationsTable( RtmDatabase database )
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
      builder.append( ModificationsColumns._ID );
      builder.append( " INTEGER NOT NULL CONSTRAINT PK_MODIFICATIONS PRIMARY KEY AUTOINCREMENT, " );
      builder.append( ModificationsColumns.ENTITY_URI );
      builder.append( " TEXT NOT NULL, " );
      builder.append( ModificationsColumns.COL_NAME );
      builder.append( " TEXT NOT NULL, " );
      builder.append( ModificationsColumns.NEW_VALUE );
      builder.append( " TEXT, " );
      builder.append( ModificationsColumns.SYNCED_VALUE );
      builder.append( " TEXT, " );
      builder.append( ModificationsColumns.TIMESTAMP );
      builder.append( " INTEGER NOT NULL" );
      builder.append( ");" );
      
      getDatabase().getWritable().execSQL( builder.toString() );
   }
   
   
   
   @Override
   public String getDefaultSortOrder()
   {
      return ModificationsColumns.DEFAULT_SORT_ORDER;
   }
   
   
   
   @Override
   public Map< String, String > getProjectionMap()
   {
      return PROJECTION_MAP;
   }
   
   
   
   @Override
   public Map< String, Integer > getColumnIndices()
   {
      return COL_INDICES;
   }
   
   
   
   @Override
   public String[] getProjection()
   {
      return PROJECTION;
   }
}
