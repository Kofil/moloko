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

package dev.drsoran.moloko.fragments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.mdt.rtm.data.RtmTask;

import dev.drsoran.moloko.R;
import dev.drsoran.moloko.ValidationResult;
import dev.drsoran.moloko.annotations.InstanceState;
import dev.drsoran.moloko.util.Intents;
import dev.drsoran.moloko.util.Strings;
import dev.drsoran.provider.Rtm.Tasks;
import dev.drsoran.rtm.Task;


public class TaskEditMultipleFragment extends AbstractTaskEditFragment
{
   private final static String STRING_MULTI_VALUE = Strings.EMPTY_STRING;
   
   private final static String URL_MULTI_VALUE = null;
   
   public final static String TAGS_MULTI_VALUE = "multi_tag";
   
   private final static long LONG_MULTI_VALUE = Long.valueOf( -1L );
   
   /**
    * Map< Task attribute, Map< attribute value, number of tasks with attribute > >
    * 
    * e.g. Map< Tasks.TASKSERIES_NAME, Map< "Task Name", 2 > >
    */
   private final Map< String, Map< Object, Integer > > attributeCount = new HashMap< String, Map< Object, Integer > >();
   
   @InstanceState( key = Intents.Extras.KEY_TASKS )
   private final ArrayList< Task > tasks = new ArrayList< Task >();
   
   
   
   public final static TaskEditMultipleFragment newInstance( Bundle config )
   {
      final TaskEditMultipleFragment fragment = new TaskEditMultipleFragment();
      
      fragment.setArguments( config );
      
      return fragment;
   }
   
   
   
   public TaskEditMultipleFragment()
   {
      registerAnnotatedConfiguredInstance( this, TaskEditMultipleFragment.class );
   }
   
   
   
   @Override
   public Bundle determineInitialValues()
   {
      final List< Task > tasks = getTasksAssertNotNull();
      
      joinAttributes( tasks );
      
      final Bundle initialValues = new Bundle();
      
      initialValues.putString( Tasks.TASKSERIES_NAME,
                               getInitialValue( Tasks.TASKSERIES_NAME,
                                                STRING_MULTI_VALUE,
                                                String.class ) );
      initialValues.putString( Tasks.LIST_ID,
                               getInitialValue( Tasks.LIST_ID,
                                                STRING_MULTI_VALUE,
                                                String.class ) );
      initialValues.putString( Tasks.PRIORITY,
                               getInitialValue( Tasks.PRIORITY,
                                                STRING_MULTI_VALUE,
                                                String.class ) );
      initialValues.putString( Tasks.TAGS,
                               getInitialValue( Tasks.TAGS,
                                                TAGS_MULTI_VALUE,
                                                String.class ) );
      initialValues.putLong( Tasks.DUE_DATE,
                             getInitialValue( Tasks.DUE_DATE,
                                              LONG_MULTI_VALUE,
                                              Long.class ) );
      initialValues.putBoolean( Tasks.HAS_DUE_TIME,
                                getInitialValue( Tasks.HAS_DUE_TIME,
                                                 Boolean.FALSE,
                                                 Boolean.class ) );
      initialValues.putString( Tasks.RECURRENCE,
                               getInitialValue( Tasks.RECURRENCE,
                                                STRING_MULTI_VALUE,
                                                String.class ) );
      initialValues.putBoolean( Tasks.RECURRENCE_EVERY,
                                getInitialValue( Tasks.RECURRENCE_EVERY,
                                                 Boolean.FALSE,
                                                 Boolean.class ) );
      initialValues.putString( Tasks.ESTIMATE,
                               getInitialValue( Tasks.ESTIMATE,
                                                STRING_MULTI_VALUE,
                                                String.class ) );
      initialValues.putLong( Tasks.ESTIMATE_MILLIS,
                             getInitialValue( Tasks.ESTIMATE_MILLIS,
                                              LONG_MULTI_VALUE,
                                              Long.class ) );
      initialValues.putString( Tasks.LOCATION_ID,
                               getInitialValue( Tasks.LOCATION_ID,
                                                STRING_MULTI_VALUE,
                                                String.class ) );
      initialValues.putString( Tasks.URL,
                               getInitialValue( Tasks.URL,
                                                URL_MULTI_VALUE,
                                                String.class ) );
      return initialValues;
   }
   
   
   
   private void joinAttributes( List< Task > tasks )
   {
      attributeCount.put( Tasks.TASKSERIES_NAME,
                          new HashMap< Object, Integer >() );
      attributeCount.put( Tasks.LIST_ID, new HashMap< Object, Integer >() );
      attributeCount.put( Tasks.PRIORITY, new HashMap< Object, Integer >() );
      attributeCount.put( Tasks.TAGS, new HashMap< Object, Integer >() );
      attributeCount.put( Tasks.DUE_DATE, new HashMap< Object, Integer >() );
      attributeCount.put( Tasks.HAS_DUE_TIME, new HashMap< Object, Integer >() );
      attributeCount.put( Tasks.RECURRENCE, new HashMap< Object, Integer >() );
      attributeCount.put( Tasks.RECURRENCE_EVERY,
                          new HashMap< Object, Integer >() );
      attributeCount.put( Tasks.ESTIMATE, new HashMap< Object, Integer >() );
      attributeCount.put( Tasks.ESTIMATE_MILLIS,
                          new HashMap< Object, Integer >() );
      attributeCount.put( Tasks.LOCATION_ID, new HashMap< Object, Integer >() );
      attributeCount.put( Tasks.URL, new HashMap< Object, Integer >() );
      
      for ( Task task : tasks )
      {
         inc( attributeCount.get( Tasks.TASKSERIES_NAME ), task.getName() );
         inc( attributeCount.get( Tasks.LIST_ID ), task.getListId() );
         inc( attributeCount.get( Tasks.PRIORITY ),
              RtmTask.convertPriority( task.getPriority() ) );
         inc( attributeCount.get( Tasks.TAGS ),
              TextUtils.join( Tasks.TAGS_SEPARATOR, task.getTags() ) );
         inc( attributeCount.get( Tasks.DUE_DATE ),
              task.getDue() != null ? task.getDue().getTime() : -1 );
         inc( attributeCount.get( Tasks.HAS_DUE_TIME ), task.hasDueTime() );
         inc( attributeCount.get( Tasks.RECURRENCE ), task.getRecurrence() );
         inc( attributeCount.get( Tasks.RECURRENCE_EVERY ),
              task.isEveryRecurrence() );
         inc( attributeCount.get( Tasks.ESTIMATE ), task.getEstimate() );
         inc( attributeCount.get( Tasks.ESTIMATE_MILLIS ),
              task.getEstimateMillis() );
         inc( attributeCount.get( Tasks.LOCATION_ID ), task.getLocationId() );
         inc( attributeCount.get( Tasks.URL ), task.getUrl() );
      }
   }
   
   
   
   @Override
   public void initContentAfterDataLoaded( ViewGroup content )
   {
      super.initContentAfterDataLoaded( content );
      
      // Setup tasks name edit
      if ( !isCommonAttrib( Tasks.TASKSERIES_NAME ) )
      {
         nameEditText.setHint( R.string.edit_multiple_tasks_different_task_names );
         
         if ( nameEditText instanceof AutoCompleteTextView )
         {
            final List< Task > tasks = getTasksAssertNotNull();
            final Set< String > names = new HashSet< String >( tasks.size() );
            
            for ( Task task : tasks )
               names.add( task.getName() );
            
            final List< String > uniqueTaskNames = new ArrayList< String >( names );
            
            ( (AutoCompleteTextView) nameEditText ).setAdapter( new ArrayAdapter< String >( getSherlockActivity(),
                                                                                            android.R.layout.simple_dropdown_item_1line,
                                                                                            android.R.id.text1,
                                                                                            uniqueTaskNames ) );
         }
      }
      
      // Setup URL edit
      if ( !isCommonAttrib( Tasks.URL ) )
         urlEditText.setHint( R.string.edit_multiple_tasks_different_urls );
      
      // These controls are not visible in multi edit task mode
      dueContainer.setVisibility( View.GONE );
      estimateContainer.setVisibility( View.GONE );
      recurrContainer.setVisibility( View.GONE );
   }
   
   
   
   @Override
   protected void initializeHeadSection()
   {
      final List< Task > tasks = getTasksAssertNotNull();
      
      if ( !isMultiTask() && tasks.size() > 0 )
      {
         final Task task = tasks.get( 0 );
         
         defaultInitializeHeadSectionImpl( task );
      }
      else
      {
         tagsContainer.setVisibility( View.GONE );
         addedDate.setVisibility( View.GONE );
         completedDate.setVisibility( View.GONE );
         postponed.setVisibility( View.GONE );
         source.setVisibility( View.GONE );
      }
   }
   
   
   
   @Override
   protected void initializeListSpinner()
   {
      if ( isCommonAttrib( Tasks.LIST_ID ) )
      {
         super.initializeListSpinner();
      }
      else
      {
         final TaskEditDatabaseData loaderData = getLoaderData();
         
         if ( loaderData != null )
         {
            final List< String > listIds = loaderData.getListIds();
            final List< String > listNames = loaderData.getListNames();
            
            appendQuantifierToEntries( Tasks.LIST_ID, listNames, listIds );
            
            listIds.add( 0, STRING_MULTI_VALUE );
            listNames.add( 0,
                           getString( R.string.edit_multiple_tasks_different_lists ) );
            
            createListSpinnerAdapterForValues( listIds, listNames );
         }
      }
   }
   
   
   
   @Override
   protected void initializeLocationSpinner()
   {
      if ( isCommonAttrib( Tasks.LOCATION_ID ) )
      {
         super.initializeLocationSpinner();
      }
      else
      {
         final TaskEditDatabaseData loaderData = getLoaderData();
         
         if ( loaderData != null )
         {
            final List< String > locationIds = loaderData.getLocationIds();
            final List< String > locationNames = loaderData.getLocationNames();
            
            insertNowhereLocationEntry( locationIds, locationNames );
            
            appendQuantifierToEntries( Tasks.LOCATION_ID,
                                       locationNames,
                                       locationIds );
            
            locationIds.add( 0, STRING_MULTI_VALUE );
            locationNames.add( 0,
                               getString( R.string.edit_multiple_tasks_different_locations ) );
            
            createLocationSpinnerAdapterForValues( locationIds, locationNames );
         }
      }
   }
   
   
   
   @Override
   protected void initializePrioritySpinner()
   {
      if ( isCommonAttrib( Tasks.PRIORITY ) )
      {
         super.initializePrioritySpinner();
      }
      else
      {
         final List< String > priorityTexts = new ArrayList< String >( Arrays.asList( getResources().getStringArray( R.array.rtm_priorities ) ) );
         final List< String > priorityValues = new ArrayList< String >( Arrays.asList( getResources().getStringArray( R.array.rtm_priority_values ) ) );
         
         appendQuantifierToEntries( Tasks.PRIORITY,
                                    priorityTexts,
                                    priorityValues );
         
         priorityTexts.add( 0,
                            getString( R.string.edit_multiple_tasks_different_priorities ) );
         priorityValues.add( 0, STRING_MULTI_VALUE );
         
         createPrioritySpinnerAdapterForValues( priorityTexts, priorityValues );
      }
   }
   
   
   
   public List< Task > getTasksAssertNotNull()
   {
      if ( tasks == null )
         throw new AssertionError( "expected tasks to be not null" );
      
      return tasks;
   }
   
   
   
   @Override
   protected ValidationResult validateName()
   {
      if ( hasChange( Tasks.TASKSERIES_NAME ) )
         return super.validateName();
      else
         return ValidationResult.OK;
   }
   
   
   
   @Override
   protected List< Task > getEditedTasks()
   {
      return getTasksAssertNotNull();
   }
   
   
   
   private final static void inc( Map< Object, Integer > map, Object value )
   {
      final Integer cnt = map.get( value );
      
      if ( cnt != null )
         map.put( value, cnt + 1 );
      else
         map.put( value, Integer.valueOf( 1 ) );
   }
   
   
   
   private final < V > V getInitialValue( String key,
                                          V multiVal,
                                          Class< V > type )
   {
      final Map< Object, Integer > values = attributeCount.get( key );
      
      if ( values.size() == 1 )
         return type.cast( values.keySet().iterator().next() );
      else
         return multiVal;
   }
   
   
   
   private final void appendQuantifierToEntries( String attributeKey,
                                                 List< String > entries,
                                                 List< String > values )
   {
      if ( entries.size() != values.size() )
         throw new AssertionError( "expected entries and values have the same size" );
      
      for ( int i = 0, cnt = entries.size(); i < cnt; i++ )
      {
         entries.set( i,
                      getEntryWithCountString( attributeKey,
                                               values.get( i ),
                                               entries.get( i ) ) );
      }
   }
   
   
   
   private final String getEntryWithCountString( String key,
                                                 Object value,
                                                 String entry )
   {
      return getResources().getString( R.string.edit_multiple_tasks_entry_with_count,
                                       entry,
                                       getAttribValueCnt( key, value ) );
   }
   
   
   
   private final int getAttribValueCnt( String key, Object value )
   {
      final Integer cnt = attributeCount.get( key ).get( value );
      
      return cnt == null ? 0 : cnt.intValue();
   }
   
   
   
   private final boolean isCommonAttrib( String key )
   {
      return attributeCount.get( key ).size() == 1;
   }
   
   
   
   private final boolean isMultiTask()
   {
      return getTasksAssertNotNull().size() > 1;
   }
}
