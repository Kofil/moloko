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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.RemoteException;
import android.text.TextUtils;

import com.mdt.rtm.data.RtmList;
import com.mdt.rtm.data.RtmLists;
import com.mdt.rtm.data.RtmTask;
import com.mdt.rtm.data.RtmTaskList;
import com.mdt.rtm.data.RtmTaskNote;
import com.mdt.rtm.data.RtmTaskNotes;
import com.mdt.rtm.data.RtmTaskSeries;
import com.mdt.rtm.data.RtmTasks;

import dev.drsoran.moloko.MolokoApp;
import dev.drsoran.moloko.util.LogUtils;
import dev.drsoran.moloko.util.Queries;
import dev.drsoran.moloko.util.Strings;
import dev.drsoran.provider.Rtm.Lists;
import dev.drsoran.provider.Rtm.Locations;
import dev.drsoran.provider.Rtm.Notes;
import dev.drsoran.provider.Rtm.Participants;
import dev.drsoran.provider.Rtm.RawTasks;
import dev.drsoran.provider.Rtm.TaskSeries;
import dev.drsoran.rtm.ParticipantList;


public class RtmTaskSeriesProviderPart extends
         AbstractModificationsRtmProviderPart
{
   private static final Class< RtmTaskSeriesProviderPart > TAG = RtmTaskSeriesProviderPart.class;
   
   public final static HashMap< String, String > PROJECTION_MAP = new HashMap< String, String >();
   
   public final static String[] PROJECTION =
   { TaskSeries._ID, TaskSeries.TASKSERIES_CREATED_DATE,
    TaskSeries.MODIFIED_DATE, TaskSeries.TASKSERIES_NAME, TaskSeries.SOURCE,
    TaskSeries.URL, TaskSeries.RECURRENCE, TaskSeries.RECURRENCE_EVERY,
    TaskSeries.LOCATION_ID, TaskSeries.LIST_ID, TaskSeries.TAGS };
   
   public final static HashMap< String, Integer > COL_INDICES = new HashMap< String, Integer >();
   
   static
   {
      AbstractModificationsRtmProviderPart.initProjectionDependent( PROJECTION,
                                                                    PROJECTION_MAP,
                                                                    COL_INDICES );
   }
   
   
   
   public final static ContentValues getContentValues( RtmTaskSeries taskSeries,
                                                       boolean withId )
   {
      final ContentValues values = new ContentValues();
      
      if ( withId )
         values.put( TaskSeries._ID, taskSeries.getId() );
      
      if ( taskSeries.getCreatedDate() != null )
         values.put( TaskSeries.TASKSERIES_CREATED_DATE,
                     taskSeries.getCreatedDate().getTime() );
      else
         values.putNull( TaskSeries.TASKSERIES_CREATED_DATE );
      
      if ( taskSeries.getModifiedDate() != null )
         values.put( TaskSeries.MODIFIED_DATE, taskSeries.getModifiedDate()
                                                         .getTime() );
      else
         values.putNull( TaskSeries.MODIFIED_DATE );
      
      values.put( TaskSeries.TASKSERIES_NAME, taskSeries.getName() );
      
      if ( !TextUtils.isEmpty( taskSeries.getSource() ) )
         values.put( TaskSeries.SOURCE, taskSeries.getSource() );
      else
         values.putNull( TaskSeries.SOURCE );
      
      if ( !TextUtils.isEmpty( taskSeries.getURL() ) )
         values.put( TaskSeries.URL, taskSeries.getURL() );
      else
         values.putNull( TaskSeries.URL );
      
      if ( !TextUtils.isEmpty( taskSeries.getRecurrence() ) )
      {
         values.put( TaskSeries.RECURRENCE, taskSeries.getRecurrence() );
         values.put( TaskSeries.RECURRENCE_EVERY,
                     taskSeries.isEveryRecurrence() ? 1 : 0 );
      }
      else
      {
         values.putNull( TaskSeries.RECURRENCE );
         values.putNull( TaskSeries.RECURRENCE_EVERY );
      }
      
      if ( !TextUtils.isEmpty( taskSeries.getLocationId() ) )
         values.put( TaskSeries.LOCATION_ID, taskSeries.getLocationId() );
      else
         values.putNull( TaskSeries.LOCATION_ID );
      
      values.put( TaskSeries.LIST_ID, taskSeries.getListId() );
      
      if ( taskSeries.hasTags() )
         values.put( TaskSeries.TAGS, taskSeries.getTagsJoined() );
      else
         values.putNull( TaskSeries.TAGS );
      
      return values;
   }
   
   
   
   public final static RtmTaskSeries getTaskSeries( ContentProviderClient client,
                                                    String id )
   {
      RtmTaskSeries taskSeries = null;
      Cursor c = null;
      
      try
      {
         c = client.query( TaskSeries.CONTENT_URI, PROJECTION, TaskSeries._ID
            + " = " + id, null, null );
         
         if ( c != null && c.moveToFirst() )
         {
            taskSeries = createRtmTaskSeries( client, c );
         }
      }
      catch ( final RemoteException e )
      {
         MolokoApp.Log.e( TAG, "Query taskseries failed. ", e );
         taskSeries = null;
      }
      finally
      {
         if ( c != null )
            c.close();
      }
      
      return taskSeries;
   }
   
   
   
   public final static List< RtmTaskSeries > getLocalCreatedTaskSeries( ContentProviderClient client )
   {
      List< RtmTaskSeries > taskSerieses = null;
      
      final List< Creation > creations = CreationsProviderPart.getCreations( client,
                                                                             TaskSeries.CONTENT_URI );
      
      if ( creations != null )
      {
         if ( creations.size() == 0 )
            taskSerieses = new ArrayList< RtmTaskSeries >( 0 );
         else
         {
            final String selection = Queries.toColumnList( creations,
                                                           TaskSeries._ID,
                                                           " OR " );
            Cursor c = null;
            
            try
            {
               c = client.query( TaskSeries.CONTENT_URI,
                                 PROJECTION,
                                 selection,
                                 null,
                                 null );
               
               boolean ok = c != null;
               
               if ( ok )
               {
                  taskSerieses = new ArrayList< RtmTaskSeries >( c.getCount() );
                  
                  if ( c.getCount() > 0 )
                  {
                     for ( ok = c.moveToFirst(); ok && !c.isAfterLast(); c.moveToNext() )
                     {
                        final RtmTaskSeries taskSeries = createRtmTaskSeries( client,
                                                                              c );
                        ok = taskSeries != null;
                        
                        if ( ok )
                           taskSerieses.add( taskSeries );
                     }
                  }
               }
            }
            catch ( final RemoteException e )
            {
               MolokoApp.Log.e( TAG, "Query taskseries failed. ", e );
               taskSerieses = null;
            }
            finally
            {
               if ( c != null )
                  c.close();
            }
         }
      }
      
      return taskSerieses;
   }
   
   
   
   public final static RtmTaskList getAllTaskSeriesForList( ContentProviderClient client,
                                                            String listId )
   {
      RtmTaskList taskList = new RtmTaskList( listId );
      Cursor c = null;
      
      try
      {
         c = client.query( TaskSeries.CONTENT_URI,
                           PROJECTION,
                           TaskSeries.LIST_ID + " = " + listId,
                           null,
                           null );
         
         boolean ok = c != null;
         
         if ( ok && c.getCount() > 0 )
         {
            for ( ok = c.moveToFirst(); ok && !c.isAfterLast(); c.moveToNext() )
            {
               final RtmTaskSeries taskSeries = createRtmTaskSeries( client, c );
               ok = taskSeries != null;
               
               if ( ok )
                  taskList.add( taskSeries );
            }
         }
         
         if ( !ok )
            taskList = null;
      }
      catch ( final RemoteException e )
      {
         MolokoApp.Log.e( TAG, "Query taskserieses failed.", e );
         taskList = null;
      }
      finally
      {
         if ( c != null )
            c.close();
      }
      
      return taskList;
   }
   
   
   
   public final static RtmTasks getAllTaskSeries( ContentProviderClient client )
   {
      RtmTasks tasksLists = null;
      
      // Query all lists, including smart lists. So we get empty RtmTaskList instances too.
      final RtmLists lists = RtmListsProviderPart.getAllLists( client,
                                                               RtmListsProviderPart.SELECTION_EXCLUDE_DELETED_AND_ARCHIVED );
      
      if ( lists != null )
      {
         boolean ok = true;
         
         tasksLists = new RtmTasks();
         
         final List< String > listIds = lists.getListIds();
         
         // For each list
         for ( final Iterator< String > i = listIds.iterator(); ok
            && i.hasNext(); )
         {
            final String listId = i.next();
            
            final RtmTaskList taskList = getAllTaskSeriesForList( client,
                                                                  listId );
            
            ok = taskList != null;
            if ( ok )
               tasksLists.add( taskList );
         }
         
         if ( !ok )
            tasksLists = null;
      }
      
      return tasksLists;
   }
   
   
   
   public final static ArrayList< ContentProviderOperation > moveTaskSeriesToInbox( ContentResolver contentResolver,
                                                                                    String fromListId,
                                                                                    String nameInbox )
   {
      ArrayList< ContentProviderOperation > operations = null;
      RtmList inbox = null;
      
      {
         final ContentProviderClient client = contentResolver.acquireContentProviderClient( Lists.CONTENT_URI );
         if ( client != null )
         {
            inbox = RtmListsProviderPart.getListByName( client, nameInbox );
            client.release();
         }
         else
            MolokoApp.Log.e( TAG, LogUtils.GENERIC_DB_ERROR );
      }
      
      if ( inbox != null )
      {
         final ContentProviderClient client = contentResolver.acquireContentProviderClient( TaskSeries.CONTENT_URI );
         if ( client != null )
         {
            final RtmTaskList taskList = getAllTaskSeriesForList( client,
                                                                  fromListId );
            client.release();
            
            if ( taskList != null )
            {
               final List< RtmTaskSeries > tasks = taskList.getSeries();
               operations = new ArrayList< ContentProviderOperation >( tasks.size() );
               
               for ( RtmTaskSeries rtmTaskSeries : tasks )
               {
                  operations.add( ContentProviderOperation.newUpdate( Queries.contentUriWithId( TaskSeries.CONTENT_URI,
                                                                                                rtmTaskSeries.getId() ) )
                                                          .withValue( TaskSeries.LIST_ID,
                                                                      inbox.getId() )
                                                          .build() );
               }
            }
         }
         else
         {
            MolokoApp.Log.e( TAG, LogUtils.GENERIC_DB_ERROR );
         }
      }
      else
      {
         MolokoApp.Log.e( TAG, "Query Inbox list failed" );
      }
      
      return operations;
   }
   
   
   
   public final static List< ContentProviderOperation > insertTaskSeries( RtmTaskSeries taskSeries )
   {
      if ( taskSeries == null )
         throw new NullPointerException( "taskSeries is null" );
      
      if ( taskSeries.getTasks() == null )
         throw new NullPointerException( "taskSeries tasks is null" );
      
      if ( taskSeries.getTasks().size() == 0 )
         throw new IllegalStateException( "taskSeries has no tasks" );
      
      List< ContentProviderOperation > operations = new ArrayList< ContentProviderOperation >();
      
      // Insert new RtmTasks
      {
         final List< RtmTask > tasks = taskSeries.getTasks();
         
         for ( RtmTask task : tasks )
         {
            operations.add( RtmTasksProviderPart.insertTask( task ) );
         }
      }
      
      // Check for notes
      {
         final List< RtmTaskNote > notesList = taskSeries.getNotes().getNotes();
         
         for ( final RtmTaskNote rtmTaskNote : notesList )
         {
            operations.add( ContentProviderOperation.newInsert( Notes.CONTENT_URI )
                                                    .withValues( RtmNotesProviderPart.getContentValues( rtmTaskNote,
                                                                                                        true ) )
                                                    .build() );
         }
      }
      
      // Check for participants
      {
         final ParticipantList participantList = taskSeries.getParticipants();
         operations.addAll( ParticipantsProviderPart.insertParticipants( participantList ) );
      }
      
      // Insert new taskseries
      {
         operations.add( ContentProviderOperation.newInsert( TaskSeries.CONTENT_URI )
                                                 .withValues( getContentValues( taskSeries,
                                                                                true ) )
                                                 .build() );
      }
      
      return operations;
   }
   
   
   
   private final static RtmTaskSeries createRtmTaskSeries( ContentProviderClient client,
                                                           Cursor c )
   {
      final String taskSeriesId = c.getString( COL_INDICES.get( TaskSeries._ID ) );
      
      final List< RtmTask > tasks = RtmTasksProviderPart.getAllTasks( client,
                                                                      taskSeriesId,
                                                                      true );
      
      boolean ok = tasks != null;
      
      if ( ok && tasks.size() == 0 )
         throw new IllegalStateException( "taskSeries has no tasks" );
      
      RtmTaskNotes notes = null;
      if ( ok )
      {
         notes = RtmNotesProviderPart.getNotes( client, taskSeriesId );
         ok = notes != null;
      }
      
      ParticipantList participantsList = null;
      if ( ok )
      {
         participantsList = ParticipantsProviderPart.getParticipants( client,
                                                                      taskSeriesId );
         ok = participantsList != null;
      }
      
      if ( ok )
      {
         // add the current task series to the task list.
         return new RtmTaskSeries( taskSeriesId,
                                   c.getString( COL_INDICES.get( TaskSeries.LIST_ID ) ),
                                   new Date( c.getLong( COL_INDICES.get( TaskSeries.TASKSERIES_CREATED_DATE ) ) ),
                                   Queries.getOptDate( c,
                                                       COL_INDICES.get( TaskSeries.MODIFIED_DATE ) ),
                                   c.getString( COL_INDICES.get( TaskSeries.TASKSERIES_NAME ) ),
                                   Queries.getOptString( c,
                                                         COL_INDICES.get( TaskSeries.SOURCE ) ),
                                   tasks,
                                   notes,
                                   Queries.getOptString( c,
                                                         COL_INDICES.get( TaskSeries.LOCATION_ID ) ),
                                   Queries.getOptString( c,
                                                         COL_INDICES.get( TaskSeries.URL ) ),
                                   Queries.getOptString( c,
                                                         COL_INDICES.get( TaskSeries.RECURRENCE ) ),
                                   Queries.getOptBool( c,
                                                       COL_INDICES.get( TaskSeries.RECURRENCE_EVERY ),
                                                       false ),
                                   Queries.getOptString( c,
                                                         COL_INDICES.get( TaskSeries.TAGS ),
                                                         Strings.EMPTY_STRING ),
                                   participantsList );
      }
      else
         return null;
   }
   
   
   
   public RtmTaskSeriesProviderPart( Context context, SQLiteOpenHelper dbAccess )
   {
      super( context, dbAccess, TaskSeries.PATH );
   }
   
   
   
   @Override
   public Object getElement( Uri uri )
   {
      if ( matchUri( uri ) == MATCH_ITEM_TYPE )
         return getTaskSeries( aquireContentProviderClient( uri ),
                               uri.getLastPathSegment() );
      return null;
   }
   
   
   
   @Override
   public void create( SQLiteDatabase db ) throws SQLException
   {
      db.execSQL( "CREATE TABLE " + path + " ( " + TaskSeries._ID
         + " TEXT NOT NULL, " + TaskSeries.TASKSERIES_CREATED_DATE
         + " INTEGER NOT NULL, " + TaskSeries.MODIFIED_DATE + " INTEGER, "
         + TaskSeries.TASKSERIES_NAME + " TEXT NOT NULL, " + TaskSeries.SOURCE
         + " TEXT, " + TaskSeries.URL + " TEXT, " + TaskSeries.RECURRENCE
         + " TEXT, " + TaskSeries.RECURRENCE_EVERY + " INTEGER, "
         + TaskSeries.LOCATION_ID + " TEXT, " + TaskSeries.LIST_ID
         + " TEXT NOT NULL, " + TaskSeries.TAGS + " TEXT, "
         + "CONSTRAINT PK_TASKSERIES PRIMARY KEY ( \"" + TaskSeries._ID
         + "\" ), " + "CONSTRAINT list FOREIGN KEY ( " + TaskSeries.LIST_ID
         + " ) REFERENCES lists ( \"" + Lists._ID + "\" ), "
         + "CONSTRAINT location FOREIGN KEY ( " + TaskSeries.LOCATION_ID
         + " ) REFERENCES locations ( \"" + Locations._ID + "\" )" + ");" );
      
      // Triggers: If a taskseries gets deleted, we also delete:
      // - all raw tasks
      // - all referenced notes
      // - all referenced participants
      
      db.execSQL( "CREATE TRIGGER " + path
         + "_delete_taskseries AFTER DELETE ON " + path
         + " FOR EACH ROW BEGIN DELETE FROM " + RawTasks.PATH + " WHERE "
         + RawTasks.TASKSERIES_ID + " = old." + TaskSeries._ID
         + "; DELETE FROM " + Notes.PATH + " WHERE " + Notes.TASKSERIES_ID
         + " = old." + TaskSeries._ID + "; DELETE FROM " + Participants.PATH
         + " WHERE " + Participants.TASKSERIES_ID + " = old." + TaskSeries._ID
         + "; END;" );
      
      // Triggers: If a taskseries ID gets updates (e.g. after inserting on RTM side),
      // we also update:
      // - all raw tasks
      // - all referenced notes
      // - all referenced participants
      db.execSQL( "CREATE TRIGGER " + path
         + "_update_taskseries AFTER UPDATE OF " + TaskSeries._ID + " ON "
         + path + " FOR EACH ROW BEGIN UPDATE " + RawTasks.PATH + " SET "
         + RawTasks.TASKSERIES_ID + " = new." + TaskSeries._ID + " WHERE "
         + RawTasks.TASKSERIES_ID + " = old." + TaskSeries._ID + "; UPDATE "
         + Notes.PATH + " SET " + Notes.TASKSERIES_ID + " = new."
         + TaskSeries._ID + " WHERE " + Notes.TASKSERIES_ID + " = old."
         + TaskSeries._ID + "; UPDATE " + Participants.PATH + " SET "
         + Participants.TASKSERIES_ID + " = new." + TaskSeries._ID + " WHERE "
         + Participants.TASKSERIES_ID + " = old." + TaskSeries._ID + "; END;" );
      
      createModificationsTrigger( db );
   }
   
   
   
   @Override
   protected ContentValues getInitialValues( ContentValues initialValues )
   {
      if ( !initialValues.containsKey( TaskSeries.TASKSERIES_CREATED_DATE ) )
      {
         initialValues.put( TaskSeries.TASKSERIES_CREATED_DATE,
                            System.currentTimeMillis() );
      }
      
      return initialValues;
   }
   
   
   
   @Override
   protected String getContentItemType()
   {
      return TaskSeries.CONTENT_ITEM_TYPE;
   }
   
   
   
   @Override
   protected String getContentType()
   {
      return TaskSeries.CONTENT_TYPE;
   }
   
   
   
   @Override
   public Uri getContentUri()
   {
      return TaskSeries.CONTENT_URI;
   }
   
   
   
   @Override
   protected String getDefaultSortOrder()
   {
      return TaskSeries.DEFAULT_SORT_ORDER;
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
}
