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

import android.database.SQLException;
import android.provider.BaseColumns;
import dev.drsoran.moloko.content.Columns.ModificationColumns;


/**
 * If a RTM element gets deleted we also delete all possible open modifications for this element.
 */
class DeleteModificationsTrigger extends AbstractTrigger
{
   private final String tableName;
   
   
   
   public DeleteModificationsTrigger( RtmDatabase database, String tableName )
   {
      super( database, tableName + "_delete_modifications" );
      this.tableName = tableName;
   }
   
   
   
   @Override
   public void create() throws SQLException
   {
      final StringBuilder builder = new StringBuilder();
      
      builder.append( "CREATE TRIGGER " );
      builder.append( getTriggerName() );
      builder.append( " AFTER DELETE ON " );
      builder.append( tableName );
      builder.append( " FOR EACH ROW BEGIN DELETE FROM " );
      builder.append( ModificationsTable.TABLE_NAME );
      builder.append( " WHERE " );
      builder.append( ModificationColumns.ENTITY_URI );
      builder.append( " = '" );
      builder.append( tableName );
      builder.append( "' || '/' || old." );
      builder.append( BaseColumns._ID );
      builder.append( ";" );
      builder.append( "END;" );
      
      getDatabase().getWritable().execSQL( builder.toString() );
   }
}