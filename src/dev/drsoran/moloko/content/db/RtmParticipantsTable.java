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

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import dev.drsoran.db.AbstractTable;
import dev.drsoran.moloko.content.Columns.ParticipantColumns;
import dev.drsoran.moloko.content.db.TableColumns.RtmContactColumns;
import dev.drsoran.moloko.content.db.TableColumns.RtmParticipantColumns;
import dev.drsoran.moloko.content.db.TableColumns.RtmTaskSeriesColumns;


class RtmParticipantsTable extends AbstractTable
{
   public final static String TABLE_NAME = "participants";
   
   
   
   public RtmParticipantsTable( SQLiteDatabase database )
   {
      super( database, TABLE_NAME );
   }
   
   
   
   @Override
   public void create() throws SQLException
   {
      final StringBuilder builder = new StringBuilder();
      
      builder.append( "CREATE TABLE " );
      builder.append( TABLE_NAME );
      builder.append( "( " );
      builder.append( RtmParticipantColumns._ID );
      builder.append( " INTEGER NOT NULL CONSTRAINT PK_PARTICIPANTS PRIMARY KEY AUTOINCREMENT, " );
      builder.append( RtmParticipantColumns.CONTACT_ID );
      builder.append( " INTEGER NOT NULL, " );
      builder.append( RtmParticipantColumns.FULLNAME );
      builder.append( " TEXT NOT NULL, " );
      builder.append( RtmParticipantColumns.USERNAME );
      builder.append( " TEXT NOT NULL, " );
      builder.append( RtmParticipantColumns.TASKSERIES_ID );
      builder.append( " INTEGER NOT NULL, " );
      builder.append( "CONSTRAINT participant FOREIGN KEY ( " );
      builder.append( RtmParticipantColumns.TASKSERIES_ID );
      builder.append( " ) REFERENCES " );
      builder.append( RtmTaskSeriesTable.TABLE_NAME );
      builder.append( " (\"" );
      builder.append( RtmTaskSeriesColumns._ID );
      builder.append( "\"), " );
      builder.append( "CONSTRAINT participates FOREIGN KEY ( " );
      builder.append( ParticipantColumns.CONTACT_ID );
      builder.append( " ) REFERENCES " );
      builder.append( RtmContactsTable.TABLE_NAME );
      builder.append( " (\"" );
      builder.append( RtmContactColumns._ID );
      builder.append( "\") " );
      builder.append( " );" );
      
      getDatabase().execSQL( builder.toString() );
   }
   
   
   
   @Override
   public String getDefaultSortOrder()
   {
      return RtmParticipantColumns.DEFAULT_SORT_ORDER;
   }
   
   
   
   @Override
   public String[] getProjection()
   {
      return RtmParticipantColumns.TABLE_PROJECTION;
   }
}
