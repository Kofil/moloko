/*
 * Copyright 2007, MetaDimensional Technologies Inc.
 * 
 * 
 * This file is part of the RememberTheMilk Java API.
 * 
 * The RememberTheMilk Java API is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 * 
 * The RememberTheMilk Java API is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.mdt.rtm;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.w3c.dom.Element;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.mdt.rtm.data.RtmAuth;
import com.mdt.rtm.data.RtmData;
import com.mdt.rtm.data.RtmFrob;
import com.mdt.rtm.data.RtmList;
import com.mdt.rtm.data.RtmLists;
import com.mdt.rtm.data.RtmLocation;
import com.mdt.rtm.data.RtmTask;
import com.mdt.rtm.data.RtmTaskList;
import com.mdt.rtm.data.RtmTaskNote;
import com.mdt.rtm.data.RtmTaskSeries;
import com.mdt.rtm.data.RtmTasks;
import com.mdt.rtm.data.RtmTimeline;
import com.mdt.rtm.data.RtmTask.Priority;

import dev.drsoran.moloko.R;
import dev.drsoran.rtm.RtmContacts;
import dev.drsoran.rtm.RtmSettings;


/**
 * A major part of the RTM API implementation is here.
 * 
 * @author Will Ross Jun 21, 2007
 * @author Edouard Mercier, since 2008.04.15
 */
public class ServiceImpl implements Service
{
   
   private final static String TAG = "Moloko.*"
      + ServiceImpl.class.getSimpleName();
   
   public final static String SERVER_HOST_NAME = "www.rememberthemilk.com";
   
   public final static int SERVER_PORT_NUMBER_HTTP = 80;
   
   public final static int SERVER_PORT_NUMBER_HTTPS = 443;
   
   public final static String REST_SERVICE_URL_POSTFIX = "/services/rest/";
   
   private final ApplicationInfo applicationInfo;
   
   private final Invoker invoker;
   
   private final Prefs prefs;
   
   private String currentAuthToken;
   
   RtmFrob tempFrob;
   
   

   public static ServiceImpl getInstance( Context context,
                                          ApplicationInfo applicationInfo ) throws ServiceInternalException
   {
      final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( context );
      
      if ( prefs == null )
      {
         Log.w( TAG, "Unable to access the settings." );
      }
      
      final boolean useHttps = prefs == null
         || prefs.getBoolean( context.getString( R.string.key_conn_use_https ),
                              true );
      
      final boolean useProxy = prefs != null
         && prefs.getBoolean( context.getString( R.string.key_conn_use_proxy ),
                              false );
      
      ProxySettings proxySettings = null;
      
      if ( useProxy )
      {
         proxySettings = ProxySettings.loadFromPreferences( context, prefs );
      }
      
      final ServiceImpl serviceImpl = new ServiceImpl( applicationInfo,
                                                       proxySettings,
                                                       !useHttps );
      
      return serviceImpl;
   }
   


   protected ServiceImpl( ApplicationInfo applicationInfo,
      ProxySettings proxySettings, boolean useHttp )
      throws ServiceInternalException
   {
      invoker = new Invoker( SERVER_HOST_NAME,
                             ( useHttp ) ? SERVER_PORT_NUMBER_HTTP
                                        : SERVER_PORT_NUMBER_HTTPS,
                             REST_SERVICE_URL_POSTFIX,
                             proxySettings,
                             useHttp,
                             applicationInfo );
      
      this.applicationInfo = applicationInfo;
      prefs = new Prefs();
      
      if ( applicationInfo.getAuthToken() != null )
      {
         currentAuthToken = applicationInfo.getAuthToken();
      }
      else
      {
         currentAuthToken = prefs.getAuthToken();
      }
   }
   


   public boolean isServiceAuthorized() throws ServiceException
   {
      if ( currentAuthToken == null )
         return false;
      
      try
      {
         /* RtmAuth auth = */auth_checkToken( currentAuthToken );
         return true;
      }
      catch ( ServiceException e )
      {
         if ( e.getResponseCode() != 98 )
         {
            throw e;
         }
         else
         {
            // Bad token.
            currentAuthToken = null;
            return false;
         }
      }
   }
   


   public String beginAuthorization( RtmAuth.Perms permissions ) throws ServiceException
   {
      // Instructions from the "User authentication for desktop applications"
      // section at
      // http://www.rememberthemilk.com/services/api/authentication.rtm
      tempFrob = auth_getFrob();
      return beginAuthorization( tempFrob, permissions );
   }
   


   public String beginAuthorization( RtmFrob frob, RtmAuth.Perms permissions ) throws ServiceException
   {
      String authBaseUrl = "http://" + SERVER_HOST_NAME + "/services/auth/";
      Param[] params = new Param[]
      { new Param( "api_key", applicationInfo.getApiKey() ),
       new Param( "perms", permissions.toString() ),
       new Param( "frob", frob.getValue() ) };
      Param sig = new Param( "api_sig", invoker.calcApiSig( params ) );
      StringBuilder authUrl = new StringBuilder( authBaseUrl );
      authUrl.append( "?" );
      for ( Param param : params )
      {
         authUrl.append( param.getName() )
                .append( "=" )
                .append( param.getValue() )
                .append( "&" );
      }
      authUrl.append( sig.getName() ).append( "=" ).append( sig.getValue() );
      return authUrl.toString();
   }
   


   public String completeAuthorization() throws ServiceException
   {
      return completeAuthorization( tempFrob );
   }
   


   public String completeAuthorization( RtmFrob frob ) throws ServiceException
   {
      currentAuthToken = auth_getToken( frob.getValue() );
      prefs.setAuthToken( currentAuthToken );
      return currentAuthToken;
   }
   


   public RtmAuth auth_checkToken( String authToken ) throws ServiceException
   {
      Element response = invoker.invoke( new Param( "method",
                                                    "rtm.auth.checkToken" ),
                                         new Param( "auth_token", authToken ),
                                         new Param( "api_key",
                                                    applicationInfo.getApiKey() ) );
      return new RtmAuth( response );
   }
   


   public RtmFrob auth_getFrob() throws ServiceException
   {
      return new RtmFrob( invoker.invoke( new Param( "method",
                                                     "rtm.auth.getFrob" ),
                                          new Param( "api_key",
                                                     applicationInfo.getApiKey() ) ) );
   }
   


   public String auth_getToken( String frob ) throws ServiceException
   {
      Element response = invoker.invoke( new Param( "method",
                                                    "rtm.auth.getToken" ),
                                         new Param( "frob", frob ),
                                         new Param( "api_key",
                                                    applicationInfo.getApiKey() ) );
      return new RtmAuth( response ).getToken();
   }
   


   public void contacts_add()
   {
      throw new UnsupportedOperationException( "Not supported yet." );
   }
   


   public void contacts_delete()
   {
      throw new UnsupportedOperationException( "Not supported yet." );
   }
   


   public RtmContacts contacts_getList() throws ServiceException
   {
      final Element response = invoker.invoke( new Param( "method",
                                                          "rtm.contacts.getList" ),
                                               new Param( "auth_token",
                                                          currentAuthToken ),
                                               new Param( "api_key",
                                                          applicationInfo.getApiKey() ) );
      return new RtmContacts( response );
   }
   


   public void groups_add()
   {
      throw new UnsupportedOperationException( "Not supported yet." );
   }
   


   public void groups_addContact()
   {
      throw new UnsupportedOperationException( "Not supported yet." );
   }
   


   public void groups_delete()
   {
      throw new UnsupportedOperationException( "Not supported yet." );
   }
   


   public void groups_getList()
   {
      throw new UnsupportedOperationException( "Not supported yet." );
   }
   


   public void groups_removeContact()
   {
      throw new UnsupportedOperationException( "Not supported yet." );
   }
   


   public RtmList lists_add( String timelineId, String listName ) throws ServiceException
   {
      Element response = invoker.invoke( new Param( "method", "rtm.lists.add" ),
                                         new Param( "auth_token",
                                                    currentAuthToken ),
                                         new Param( "api_key",
                                                    applicationInfo.getApiKey() ),
                                         new Param( "name", listName ),
                                         new Param( "timeline", timelineId ) );
      return new RtmList( response );
   }
   


   public void lists_archive()
   {
      throw new UnsupportedOperationException( "Not supported yet." );
   }
   


   public void lists_delete( String timelineId, String listId ) throws ServiceException
   {
      invoker.invoke( new Param( "method", "rtm.lists.delete" ),
                      new Param( "auth_token", currentAuthToken ),
                      new Param( "api_key", applicationInfo.getApiKey() ),
                      new Param( "timeline", timelineId ),
                      new Param( "list_id", listId ) );
   }
   


   public RtmLists lists_getList() throws ServiceException
   {
      Element response = invoker.invoke( new Param( "method",
                                                    "rtm.lists.getList" ),
                                         new Param( "auth_token",
                                                    currentAuthToken ),
                                         new Param( "api_key",
                                                    applicationInfo.getApiKey() ) );
      return new RtmLists( response );
   }
   


   public RtmList lists_getList( String listName ) throws ServiceException
   {
      RtmLists fullList = lists_getList();
      for ( Entry< String, RtmList > entry : fullList.getLists().entrySet() )
      {
         if ( entry.getValue().getName().equals( listName ) )
         {
            return entry.getValue();
         }
      }
      return null;
   }
   


   public void lists_setDefaultList()
   {
      throw new UnsupportedOperationException( "Not supported yet." );
   }
   


   public RtmList lists_setName( String timelineId,
                                 String listId,
                                 String newName ) throws ServiceException
   {
      Element response = invoker.invoke( new Param( "method",
                                                    "rtm.lists.setName" ),
                                         new Param( "timeline", timelineId ),
                                         new Param( "list_id", listId ),
                                         new Param( "name", newName ),
                                         new Param( "auth_token",
                                                    currentAuthToken ),
                                         new Param( "api_key",
                                                    applicationInfo.getApiKey() ) );
      return new RtmList( response );
   }
   


   public void lists_unarchive()
   {
      throw new UnsupportedOperationException( "Not supported yet." );
   }
   


   public void reflection_getMethodInfo()
   {
      throw new UnsupportedOperationException( "Not supported yet." );
   }
   


   public void reflection_getMethods()
   {
      throw new UnsupportedOperationException( "Not supported yet." );
   }
   


   public RtmSettings settings_getList() throws ServiceException
   {
      Element response = invoker.invoke( new Param( "method",
                                                    "rtm.settings.getList" ),
                                         new Param( "auth_token",
                                                    currentAuthToken ),
                                         new Param( "api_key",
                                                    applicationInfo.getApiKey() ) );
      return new RtmSettings( response );
   }
   


   public RtmTaskSeries tasks_add( String timelineId, String listId, String name ) throws ServiceException
   {
      Element response = invoker.invoke( new Param( "method", "rtm.tasks.add" ),
                                         new Param( "timeline", timelineId ),
                                         new Param( "list_id", listId ),
                                         new Param( "name", name ),
                                         new Param( "auth_token",
                                                    currentAuthToken ),
                                         new Param( "api_key",
                                                    applicationInfo.getApiKey() ) );
      RtmTaskList rtmTaskList = new RtmTaskList( response );
      if ( rtmTaskList.getSeries().size() == 1 )
      {
         return rtmTaskList.getSeries().get( 0 );
      }
      else if ( rtmTaskList.getSeries().size() > 1 )
      {
         throw new ServiceInternalException( "Internal error: more that one task ("
            + rtmTaskList.getSeries().size() + ") has been created" );
      }
      throw new ServiceInternalException( "Internal error: no task has been created" );
   }
   


   public void tasks_addTags()
   {
      throw new UnsupportedOperationException( "Not supported yet." );
   }
   


   public RtmTaskSeries tasks_complete( String timelineId,
                                        String listId,
                                        String taskSeriesId,
                                        String taskId ) throws ServiceException
   {
      Element response = invoker.invoke( new Param( "method",
                                                    "rtm.tasks.complete" ),
                                         new Param( "timeline", timelineId ),
                                         new Param( "list_id", listId ),
                                         new Param( "taskseries_id",
                                                    taskSeriesId ),
                                         new Param( "task_id", taskId ),
                                         new Param( "auth_token",
                                                    currentAuthToken ),
                                         new Param( "api_key",
                                                    applicationInfo.getApiKey() ) );
      RtmTaskList rtmTaskList = new RtmTaskList( response );
      return findTask( taskSeriesId, taskId, rtmTaskList );
   }
   


   public void tasks_delete( String timelineId,
                             String listId,
                             String taskSeriesId,
                             String taskId ) throws ServiceException
   {
      invoker.invoke( new Param( "method", "rtm.tasks.delete" ),
                      new Param( "timeline", timelineId ),
                      new Param( "list_id", listId ),
                      new Param( "taskseries_id", taskSeriesId ),
                      new Param( "task_id", taskId ),
                      new Param( "auth_token", currentAuthToken ),
                      new Param( "api_key", applicationInfo.getApiKey() ) );
   }
   


   public RtmTasks tasks_getList( String listId, String filter, Date lastSync ) throws ServiceException
   {
      Set< Param > params = new HashSet< Param >();
      params.add( new Param( "method", "rtm.tasks.getList" ) );
      if ( listId != null )
      {
         params.add( new Param( "list_id", listId ) );
      }
      if ( filter != null )
      {
         params.add( new Param( "filter", filter ) );
      }
      if ( lastSync != null )
      {
         params.add( new Param( "last_sync", lastSync ) );
      }
      params.add( new Param( "auth_token", currentAuthToken ) );
      params.add( new Param( "api_key", applicationInfo.getApiKey() ) );
      return new RtmTasks( invoker.invoke( params.toArray( new Param[ params.size() ] ) ) );
   }
   


   public RtmTaskSeries tasks_getTask( String taskSeriesId,
                                       String taskName,
                                       String listId ) throws ServiceException
   {
      final Set< Param > params = new HashSet< Param >();
      params.add( new Param( "method", "rtm.tasks.getList" ) );
      params.add( new Param( "auth_token", currentAuthToken ) );
      params.add( new Param( "api_key", applicationInfo.getApiKey() ) );
      if ( taskName != null )
      {
         params.add( new Param( "filter", "name:\"" + taskName + "\"" ) );
      }
      if ( listId != null )
      {
         params.add( new Param( "list_id", listId ) );
      }
      final RtmTasks rtmTasks = new RtmTasks( invoker.invoke( params.toArray( new Param[ params.size() ] ) ) );
      return RtmTaskSeries.findTask( taskSeriesId, rtmTasks );
   }
   


   public RtmTaskSeries tasks_getTask( String taskName ) throws ServiceException
   {
      return tasks_getTask( null, taskName, null );
   }
   


   public void tasks_movePriority()
   {
      throw new UnsupportedOperationException( "Not supported yet." );
   }
   


   public RtmTaskSeries tasks_moveTo( String timelineId,
                                      String fromListId,
                                      String toListId,
                                      String taskSeriesId,
                                      String taskId ) throws ServiceException
   {
      Element elt = invoker.invoke( new Param( "method", "rtm.tasks.moveTo" ),
                                    new Param( "timeline", timelineId ),
                                    new Param( "from_list_id", fromListId ),
                                    new Param( "to_list_id", toListId ),
                                    new Param( "taskseries_id", taskSeriesId ),
                                    new Param( "task_id", taskId ),
                                    new Param( "auth_token", currentAuthToken ),
                                    new Param( "api_key",
                                               applicationInfo.getApiKey() ) );
      RtmTaskList rtmTaskList = new RtmTaskList( elt );
      return findTask( taskSeriesId, taskId, rtmTaskList );
   }
   


   public void tasks_postpone()
   {
      throw new UnsupportedOperationException( "Not supported yet." );
   }
   


   public void tasks_removeTags()
   {
      throw new UnsupportedOperationException( "Not supported yet." );
   }
   


   public RtmTaskSeries tasks_setDueDate( String timelineId,
                                          String listId,
                                          String taskSeriesId,
                                          String taskId,
                                          Date due,
                                          boolean hasDueTime ) throws ServiceException
   {
      final boolean setDueDate = ( due != null );
      final Element elt;
      if ( setDueDate == true )
      {
         elt = invoker.invoke( new Param( "method", "rtm.tasks.setDueDate" ),
                               new Param( "timeline", timelineId ),
                               new Param( "list_id", listId ),
                               new Param( "taskseries_id", taskSeriesId ),
                               new Param( "task_id", taskId ),
                               new Param( "due", due ),
                               new Param( "has_due_time", hasDueTime ? "1"
                                                                    : "0" ),
                               new Param( "auth_token", currentAuthToken ),
                               new Param( "api_key",
                                          applicationInfo.getApiKey() ) );
      }
      else
      {
         elt = invoker.invoke( new Param( "method", "rtm.tasks.setDueDate" ),
                               new Param( "timeline", timelineId ),
                               new Param( "list_id", listId ),
                               new Param( "taskseries_id", taskSeriesId ),
                               new Param( "task_id", taskId ),
                               new Param( "auth_token", currentAuthToken ),
                               new Param( "api_key",
                                          applicationInfo.getApiKey() ) );
      }
      final RtmTaskList rtmTaskList = new RtmTaskList( elt );
      return findTask( taskSeriesId, taskId, rtmTaskList );
   }
   


   public void tasks_setEstimate()
   {
      throw new UnsupportedOperationException( "Not supported yet." );
   }
   


   public TimeLineMethod.Result< RtmTaskSeries > tasks_setName( String timelineId,
                                                                String listId,
                                                                String taskSeriesId,
                                                                String taskId,
                                                                String newName,
                                                                MethodCallType methodCallType ) throws ServiceException
   {
      final Element elt = invoker.invoke( new Param( "method",
                                                     "rtm.tasks.setName" ),
                                          new Param( "timeline", timelineId ),
                                          new Param( "list_id", listId ),
                                          new Param( "taskseries_id",
                                                     taskSeriesId ),
                                          new Param( "task_id", taskId ),
                                          new Param( "name", newName ),
                                          new Param( "auth_token",
                                                     currentAuthToken ),
                                          new Param( "api_key",
                                                     applicationInfo.getApiKey() ) );
      
      if ( methodCallType == MethodCallType.WITH_RESULT )
      {
         RtmTaskList rtmTaskList = new RtmTaskList( elt );
         return TimeLineMethod.newResult( elt,
                                          timelineId,
                                          findTask( taskSeriesId,
                                                    taskId,
                                                    rtmTaskList ) );
      }
      else
         return TimeLineMethod.newResult( elt, timelineId );
   }
   


   private RtmTaskSeries findTask( String taskSeriesId,
                                   String taskId,
                                   RtmTaskList rtmTaskList )
   {
      for ( RtmTaskSeries series : rtmTaskList.getSeries() )
      {
         if ( series.getId().equals( taskSeriesId ) )
         {
            final List< RtmTask > tasks = series.getTasks();
            
            for ( RtmTask rtmTask : tasks )
            {
               if ( rtmTask.getId().equals( taskId ) )
                  return series;
            }
         }
      }
      return null;
   }
   


   public RtmTaskSeries tasks_setPriority( String timelineId,
                                           String listId,
                                           String taskSeriesId,
                                           String taskId,
                                           Priority priority ) throws ServiceException
   {
      Element elt = invoker.invoke( new Param( "method",
                                               "rtm.tasks.setPriority" ),
                                    new Param( "timeline", timelineId ),
                                    new Param( "list_id", listId ),
                                    new Param( "taskseries_id", taskSeriesId ),
                                    new Param( "task_id", taskId ),
                                    new Param( "priority",
                                               RtmTask.convertPriority( priority ) ),
                                    new Param( "auth_token", currentAuthToken ),
                                    new Param( "api_key",
                                               applicationInfo.getApiKey() ) );
      RtmTaskList rtmTaskList = new RtmTaskList( elt );
      return findTask( taskSeriesId, taskId, rtmTaskList );
   }
   


   public void tasks_setRecurrence()
   {
      throw new UnsupportedOperationException( "Not supported yet." );
   }
   


   public void tasks_setTags( String timelineId,
                              String listId,
                              String taskSeriesId,
                              String taskId,
                              String[] tags ) throws ServiceException
   {
      final StringBuilder stringifiedTasks = new StringBuilder();
      if ( tags != null )
      {
         for ( int index = 0; index < tags.length; index++ )
         {
            stringifiedTasks.append( tags[ index ] );
            if ( index < tags.length - 1 )
            {
               stringifiedTasks.append( "," );
            }
         }
      }
      invoker.invoke( new Param( "method", "rtm.tasks.setTags" ),
                      new Param( "timeline", timelineId ),
                      new Param( "list_id", listId ),
                      new Param( "taskseries_id", taskSeriesId ),
                      new Param( "task_id", taskId ),
                      new Param( "tags", stringifiedTasks.toString() ),
                      new Param( "auth_token", currentAuthToken ),
                      new Param( "api_key", applicationInfo.getApiKey() ) );
   }
   


   public void tasks_setURL()
   {
      throw new UnsupportedOperationException( "Not supported yet." );
   }
   


   public RtmTaskSeries tasks_uncomplete( String timelineId,
                                          String listId,
                                          String taskSeriesId,
                                          String taskId ) throws ServiceException
   {
      Element response = invoker.invoke( new Param( "method",
                                                    "rtm.tasks.uncomplete" ),
                                         new Param( "timeline", timelineId ),
                                         new Param( "list_id", listId ),
                                         new Param( "taskseries_id",
                                                    taskSeriesId ),
                                         new Param( "task_id", taskId ),
                                         new Param( "auth_token",
                                                    currentAuthToken ),
                                         new Param( "api_key",
                                                    applicationInfo.getApiKey() ) );
      RtmTaskList rtmTaskList = new RtmTaskList( response );
      return findTask( taskSeriesId, taskId, rtmTaskList );
   }
   


   public RtmTaskNote tasks_notes_add( String timelineId,
                                       String listId,
                                       String taskSeriesId,
                                       String taskId,
                                       String title,
                                       String text ) throws ServiceException
   {
      Element elt = invoker.invoke( new Param( "method", "rtm.tasks.notes.add" ),
                                    new Param( "timeline", timelineId ),
                                    new Param( "list_id", listId ),
                                    new Param( "taskseries_id", taskSeriesId ),
                                    new Param( "task_id", taskId ),
                                    new Param( "note_title", title ),
                                    new Param( "note_text", text ),
                                    new Param( "auth_token", currentAuthToken ),
                                    new Param( "api_key",
                                               applicationInfo.getApiKey() ) );
      return new RtmTaskNote( elt, taskSeriesId );
   }
   


   public void tasks_notes_delete( String timelineId, String noteId ) throws ServiceException
   {
      invoker.invoke( new Param( "method", "rtm.tasks.notes.delete" ),
                      new Param( "timeline", timelineId ),
                      new Param( "note_id", noteId ),
                      new Param( "auth_token", currentAuthToken ),
                      new Param( "api_key", applicationInfo.getApiKey() ) );
   }
   


   public RtmTaskNote tasks_notes_edit( String timelineId,
                                        String taskSeriesId,
                                        String noteId,
                                        String title,
                                        String text ) throws ServiceException
   {
      Element elt = invoker.invoke( new Param( "method", "rtm.tasks.notes.edit" ),
                                    new Param( "timeline", timelineId ),
                                    new Param( "note_id", noteId ),
                                    new Param( "note_title", title ),
                                    new Param( "note_text", text ),
                                    new Param( "auth_token", currentAuthToken ),
                                    new Param( "api_key",
                                               applicationInfo.getApiKey() ) );
      return new RtmTaskNote( elt, taskSeriesId );
   }
   


   public RtmTaskSeries tasks_setLocation( String timelineId,
                                           String listId,
                                           String taskSeriesId,
                                           String taskId,
                                           String locationId ) throws ServiceException
   {
      Element response = invoker.invoke( new Param( "method",
                                                    "rtm.tasks.setLocation" ),
                                         new Param( "timeline", timelineId ),
                                         new Param( "list_id", listId ),
                                         new Param( "taskseries_id",
                                                    taskSeriesId ),
                                         new Param( "task_id", taskId ),
                                         new Param( "location_id", locationId ),
                                         new Param( "auth_token",
                                                    currentAuthToken ),
                                         new Param( "api_key",
                                                    applicationInfo.getApiKey() ) );
      RtmTaskList rtmTaskList = new RtmTaskList( response );
      return findTask( taskSeriesId, taskId, rtmTaskList );
   }
   


   public RtmTaskSeries tasks_setURL( String timelineId,
                                      String listId,
                                      String taskSeriesId,
                                      String taskId,
                                      String url ) throws ServiceException
   {
      Element response = invoker.invoke( new Param( "method",
                                                    "rtm.tasks.setURL" ),
                                         new Param( "timeline", timelineId ),
                                         new Param( "list_id", listId ),
                                         new Param( "taskseries_id",
                                                    taskSeriesId ),
                                         new Param( "task_id", taskId ),
                                         new Param( "url", url ),
                                         new Param( "auth_token",
                                                    currentAuthToken ),
                                         new Param( "api_key",
                                                    applicationInfo.getApiKey() ) );
      RtmTaskList rtmTaskList = new RtmTaskList( response );
      return findTask( taskSeriesId, taskId, rtmTaskList );
   }
   


   public void test_echo()
   {
      throw new UnsupportedOperationException( "Not supported yet." );
   }
   


   public void test_login()
   {
      throw new UnsupportedOperationException( "Not supported yet." );
   }
   


   public void time_convert()
   {
      throw new UnsupportedOperationException( "Not supported yet." );
   }
   


   public void time_parse()
   {
      throw new UnsupportedOperationException( "Not supported yet." );
   }
   


   public RtmTimeline timelines_create() throws ServiceException
   {
      return new RtmTimeline( invoker.invoke( new Param( "method",
                                                         "rtm.timelines.create" ),
                                              new Param( "auth_token",
                                                         currentAuthToken ),
                                              new Param( "api_key",
                                                         applicationInfo.getApiKey() ) ),
                              this );
   }
   


   public void timezones_getList()
   {
      throw new UnsupportedOperationException( "Not supported yet." );
   }
   


   public void transactions_undo( String timeline, String transactionId ) throws ServiceException
   {
      invoker.invoke( new Param( "method", "rtm.transactions.undo" ),
                      new Param( "auth_token", currentAuthToken ),
                      new Param( "api_key", applicationInfo.getApiKey() ),
                      new Param( "timeline", timeline ),
                      new Param( "transaction_id ", transactionId ) );
   }
   


   public List< RtmLocation > locations_getList() throws ServiceException
   {
      Element result = invoker.invoke( new Param( "method",
                                                  "rtm.locations.getList" ),
                                       new Param( "auth_token",
                                                  currentAuthToken ),
                                       new Param( "api_key",
                                                  applicationInfo.getApiKey() ) );
      
      List< RtmLocation > locations = new ArrayList< RtmLocation >();
      for ( Element child : RtmData.children( result, "location" ) )
      {
         locations.add( new RtmLocation( child ) );
      }
      return locations;
   }
   
}
