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

package dev.drsoran.moloko.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.RemoteException;
import android.text.TextUtils;

import com.mdt.rtm.data.RtmTask;

import dev.drsoran.moloko.MolokoApp;
import dev.drsoran.moloko.util.Queries;
import dev.drsoran.provider.Rtm.RawTasks;
import dev.drsoran.provider.Rtm.TaskSeries;


public class RtmTasksProviderPart extends AbstractModificationsRtmProviderPart
{
   private static final Class< RtmTasksProviderPart > TAG = RtmTasksProviderPart.class;
   
   public final static HashMap< String, String > PROJECTION_MAP = new HashMap< String, String >();
   
   public final static String[] PROJECTION =
   { RawTasks._ID, RawTasks.TASKSERIES_ID, RawTasks.DUE_DATE,
    RawTasks.HAS_DUE_TIME, RawTasks.ADDED_DATE, RawTasks.COMPLETED_DATE,
    RawTasks.DELETED_DATE, RawTasks.PRIORITY, RawTasks.POSTPONED,
    RawTasks.ESTIMATE, RawTasks.ESTIMATE_MILLIS };
   
   public final static HashMap< String, Integer > COL_INDICES = new HashMap< String, Integer >();
   
   static
   {
      AbstractModificationsRtmProviderPart.initProjectionDependent( PROJECTION,
                                                                    PROJECTION_MAP,
                                                                    COL_INDICES );
   }
   
   
   
   public final static ContentValues getContentValues( RtmTask task,
                                                       boolean withId )
   {
      final ContentValues values = new ContentValues();
      
      if ( withId )
         values.put( RawTasks._ID, task.getId() );
      
      values.put( RawTasks.TASKSERIES_ID, task.getTaskSeriesId() );
      
      if ( task.getDue() != null )
         values.put( RawTasks.DUE_DATE, task.getDue().getTime() );
      else
         values.putNull( RawTasks.DUE_DATE );
      
      values.put( RawTasks.HAS_DUE_TIME, task.getHasDueTime() );
      values.put( RawTasks.ADDED_DATE, task.getAdded().getTime() );
      
      if ( task.getCompleted() != null )
         values.put( RawTasks.COMPLETED_DATE, task.getCompleted().getTime() );
      else
         values.putNull( RawTasks.COMPLETED_DATE );
      
      if ( task.getDeletedDate() != null )
         values.put( RawTasks.DELETED_DATE, task.getDeletedDate().getTime() );
      else
         values.putNull( RawTasks.DELETED_DATE );
      
      values.put( RawTasks.PRIORITY,
                  RtmTask.convertPriority( task.getPriority() ) );
      values.put( RawTasks.POSTPONED, task.getPostponed() );
      
      if ( !TextUtils.isEmpty( task.getEstimate() ) )
         values.put( RawTasks.ESTIMATE, task.getEstimate() );
      else
         values.putNull( RawTasks.ESTIMATE );
      
      values.put( RawTasks.ESTIMATE_MILLIS, task.getEstimateMillis() );
      
      return values;
   }
   
   
   
   public final static ContentProviderOperation insertTask( RtmTask task )
   {
      if ( task.getId() == null )
         throw new NullPointerException( "task ID is null" );
      
      return ContentProviderOperation.newInsert( RawTasks.CONTENT_URI )
                                     .withValues( getContentValues( task, true ) )
                                     .build();
   }
   
   
   
   public final static RtmTask getTask( ContentProviderClient client, String id )
   {
      RtmTask task = null;
      Cursor c = null;
      
      try
      {
         // Only non-deleted tasks
         c = client.query( RawTasks.CONTENT_URI,
                           PROJECTION,
                           new StringBuilder( RawTasks._ID ).append( "=" )
                                                            .append( id )
                                                            .append( " AND " )
                                                            .append( RawTasks.DELETED_DATE )
                                                            .append( " IS NULL" )
                                                            .toString(),
                           null,
                           null );
         
         if ( c != null && c.moveToFirst() )
         {
            task = createTask( c );
         }
      }
      catch ( RemoteException e )
      {
         MolokoApp.Log.e( TAG, "Query rawtask failed. ", e );
         task = null;
      }
      finally
      {
         if ( c != null )
            c.close();
      }
      
      return task;
   }
   
   
   
   public final static List< RtmTask > getAllTasks( ContentProviderClient client,
                                                    String taskSeriesId )
   {
      return getAllTasks( client, taskSeriesId, false );
   }
   
   
   
   public final static List< RtmTask > getAllTasks( ContentProviderClient client,
                                                    String taskSeriesId,
                                                    boolean includeDeleted )
   {
      ArrayList< RtmTask > tasks = null;
      
      if ( !TextUtils.isEmpty( taskSeriesId ) )
      {
         Cursor c = null;
         
         try
         {
            final StringBuilder selection = new StringBuilder( RawTasks.TASKSERIES_ID ).append( "=" )
                                                                                       .append( taskSeriesId );
            
            if ( !includeDeleted )
            {
               selection.append( " AND " )
                        .append( RawTasks.DELETED_DATE )
                        .append( " IS NULL" );
            }
            
            // Only non-deleted tasks
            c = client.query( RawTasks.CONTENT_URI,
                              PROJECTION,
                              selection.toString(),
                              null,
                              null );
            
            boolean ok = c != null;
            
            if ( ok )
            {
               tasks = new ArrayList< RtmTask >( c.getCount() );
               
               if ( c.getCount() > 0 )
               {
                  for ( ok = c.moveToFirst(); ok && !c.isAfterLast(); c.moveToNext() )
                  {
                     final RtmTask task = createTask( c );
                     ok = task != null;
                     
                     if ( ok )
                        tasks.add( task );
                  }
               }
               
               if ( !ok )
                  tasks = null;
            }
         }
         catch ( final RemoteException e )
         {
            MolokoApp.Log.e( TAG, "Query rawtasks failed. ", e );
            tasks = null;
         }
         finally
         {
            if ( c != null )
               c.close();
         }
      }
      
      return tasks;
   }
   
   
   
   public final static int getDeletedTasksCount( ContentProviderClient client )
   {
      int cnt = -1;
      
      Cursor c = null;
      
      try
      {
         c = client.query( RawTasks.CONTENT_URI, new String[]
         { RawTasks._ID }, RawTasks.DELETED_DATE + " IS NOT NULL", null, null );
         
         boolean ok = c != null;
         
         if ( ok )
         {
            cnt = c.getCount();
         }
      }
      catch ( final RemoteException e )
      {
         MolokoApp.Log.e( TAG, "Query rawtasks failed. ", e );
      }
      finally
      {
         if ( c != null )
            c.close();
      }
      
      return cnt;
   }
   
   
   
   public RtmTasksProviderPart( Context context, SQLiteOpenHelper dbAccess )
   {
      super( context, dbAccess, RawTasks.PATH );
   }
   
   
   
   @Override
   public void create( SQLiteDatabase db ) throws SQLException
   {
      db.execSQL( "CREATE TABLE " + path + " ( " + RawTasks._ID
         + " TEXT NOT NULL, " + RawTasks.TASKSERIES_ID + " TEXT NOT NULL, "
         + RawTasks.DUE_DATE + " INTEGER, " + RawTasks.HAS_DUE_TIME
         + " INTEGER NOT NULL DEFAULT 0, " + RawTasks.ADDED_DATE
         + " INTEGER NOT NULL, " + RawTasks.COMPLETED_DATE + " INTEGER, "
         + RawTasks.DELETED_DATE + " INTEGER, " + RawTasks.PRIORITY
         + " CHAR(1) NOT NULL DEFAULT 'n', " + RawTasks.POSTPONED
         + " INTEGER DEFAULT 0, " + RawTasks.ESTIMATE + " TEXT, "
         + RawTasks.ESTIMATE_MILLIS + " INTEGER DEFAULT -1, "
         + "CONSTRAINT PK_TASKS PRIMARY KEY ( \"" + RawTasks._ID + "\" ), "
         + "CONSTRAINT rawtasks_taskseries_ref FOREIGN KEY ( "
         + RawTasks.TASKSERIES_ID + " ) REFERENCES " + TaskSeries.PATH + "( \""
         + TaskSeries._ID + "\" )" + " );" );
      
      // TRIGGER: If a RawTask gets deleted, also delete the associated
      // taskseries if it contains no RawTasks anymore
      db.execSQL( "CREATE TRIGGER " + path + "_delete_rawtask AFTER DELETE ON "
         + path + " FOR EACH ROW BEGIN DELETE FROM " + TaskSeries.PATH
         + " WHERE " + TaskSeries.PATH + "." + TaskSeries._ID + " = old."
         + RawTasks.TASKSERIES_ID + " AND NOT EXISTS (SELECT " + RawTasks._ID
         + " FROM " + path + " WHERE old." + RawTasks.TASKSERIES_ID + " = "
         + RawTasks.TASKSERIES_ID + "); END;" );
      
      // TRIGGER: If a RawTask's taskseries_id gets updated, delete the former
      // taskseries if it contains no RawTasks anymore
      db.execSQL( "CREATE TRIGGER " + path
         + "_update_taskseries_id AFTER UPDATE OF " + RawTasks.TASKSERIES_ID
         + " ON " + path + " FOR EACH ROW BEGIN DELETE FROM " + TaskSeries.PATH
         + " WHERE " + TaskSeries.PATH + "." + TaskSeries._ID + " = old."
         + RawTasks.TASKSERIES_ID + " AND NOT EXISTS (SELECT " + RawTasks._ID
         + " FROM " + path + " WHERE old." + RawTasks.TASKSERIES_ID + " = "
         + RawTasks.TASKSERIES_ID + "); END;" );
      
      createModificationsTrigger( db );
   }
   
   
   
   @Override
   protected ContentValues getInitialValues( ContentValues initialValues )
   {
      // Make sure that the fields are all set
      if ( initialValues.containsKey( RawTasks.ADDED_DATE ) == false )
      {
         final Long now = Long.valueOf( System.currentTimeMillis() );
         
         initialValues.put( RawTasks.ADDED_DATE, now );
      }
      
      return initialValues;
   }
   
   
   
   @Override
   protected String getContentItemType()
   {
      return RawTasks.CONTENT_ITEM_TYPE;
   }
   
   
   
   @Override
   protected String getContentType()
   {
      return RawTasks.CONTENT_TYPE;
   }
   
   
   
   @Override
   public Uri getContentUri()
   {
      return RawTasks.CONTENT_URI;
   }
   
   
   
   @Override
   protected String getDefaultSortOrder()
   {
      return RawTasks.DEFAULT_SORT_ORDER;
   }
   
   
   
   @Override
   public HashMap< String, String > getProjectionMap()
   {
      return PROJECTION_MAP;
   }
   
   
   
   @Override
   public HashMap< String, Integer > getColumnIndices()
   {
      return COL_INDICES;
   }
   
   
   
   @Override
   public String[] getProjection()
   {
      return PROJECTION;
   }
   
   
   
   private final static RtmTask createTask( Cursor c )
   {
      return new RtmTask( c.getString( COL_INDICES.get( RawTasks._ID ) ),
                          c.getString( COL_INDICES.get( RawTasks.TASKSERIES_ID ) ),
                          Queries.getOptDate( c,
                                              COL_INDICES.get( RawTasks.DUE_DATE ) ),
                          c.getInt( COL_INDICES.get( RawTasks.HAS_DUE_TIME ) ),
                          Queries.getOptDate( c,
                                              COL_INDICES.get( RawTasks.ADDED_DATE ) ),
                          Queries.getOptDate( c,
                                              COL_INDICES.get( RawTasks.COMPLETED_DATE ) ),
                          Queries.getOptDate( c,
                                              COL_INDICES.get( RawTasks.DELETED_DATE ) ),
                          RtmTask.convertPriority( c.getString( COL_INDICES.get( RawTasks.PRIORITY ) ) ),
                          c.getInt( COL_INDICES.get( RawTasks.POSTPONED ) ),
                          Queries.getOptString( c,
                                                COL_INDICES.get( RawTasks.ESTIMATE ) ),
                          c.getLong( COL_INDICES.get( RawTasks.ESTIMATE_MILLIS ) ) );
   }
}
