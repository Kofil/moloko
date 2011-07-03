/* 
 *	Copyright (c) 2011 Ronny R�hricht
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

import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import dev.drsoran.moloko.IFilter;
import dev.drsoran.moloko.IOnSettingsChangedListener;
import dev.drsoran.moloko.MolokoApp;
import dev.drsoran.moloko.R;
import dev.drsoran.moloko.adapters.FullDetailedTasksListFragmentAdapter;
import dev.drsoran.moloko.adapters.MinDetailedTasksListFragmentAdapter;
import dev.drsoran.moloko.util.Intents;
import dev.drsoran.provider.Rtm.Tasks;
import dev.drsoran.rtm.ListTask;


public class MinDetailedTasksListFragment extends AbstractTaskListFragment
         implements IOnSettingsChangedListener
{
   @SuppressWarnings( "unused" )
   private final static String TAG = "Moloko."
      + MinDetailedTasksListFragment.class.getSimpleName();
   
   private final static IntentFilter INTENT_FILTER;
   
   static
   {
      try
      {
         INTENT_FILTER = new IntentFilter( Intents.Action.TASKS_LISTS_MIN_DETAILED,
                                           "vnd.android.cursor.dir/vnd.rtm.task" );
         INTENT_FILTER.addCategory( Intent.CATEGORY_DEFAULT );
      }
      catch ( MalformedMimeTypeException e )
      {
         throw new RuntimeException( e );
      }
   }
   
   
   
   public static MinDetailedTasksListFragment newInstance( Bundle configuration )
   {
      final MinDetailedTasksListFragment fragment = new MinDetailedTasksListFragment();
      
      fragment.setArguments( configuration );
      
      return fragment;
   }
   
   
   
   public static IntentFilter getIntentFilter()
   {
      return INTENT_FILTER;
   }
   
   
   
   @Override
   public Intent newDefaultIntent()
   {
      return new Intent( INTENT_FILTER.getAction( 0 ), Tasks.CONTENT_URI );
   }
   
   
   
   @Override
   public void onActivityCreated( Bundle savedInstanceState )
   {
      super.onActivityCreated( savedInstanceState );
      
      MolokoApp.get( getActivity() )
               .registerOnSettingsChangedListener( IOnSettingsChangedListener.RTM_TIMEZONE
                                                      | IOnSettingsChangedListener.RTM_DATEFORMAT
                                                      | IOnSettingsChangedListener.RTM_TIMEFORMAT,
                                                   this );
   }
   
   
   
   @Override
   public void onDestroy()
   {
      super.onDestroy();
      
      MolokoApp.get( getActivity() ).unregisterOnSettingsChangedListener( this );
   }
   
   
   
   @Override
   public View onCreateView( LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState )
   {
      return inflater.inflate( R.layout.taskslist_fragment, container, false );
   }
   
   
   
   public void onSettingsChanged( int which,
                                  HashMap< Integer, Object > oldValues )
   {
      if ( which == IOnSettingsChangedListener.RTM_DATEFORMAT
         || which == IOnSettingsChangedListener.RTM_TIMEZONE
         || which == IOnSettingsChangedListener.RTM_TIMEFORMAT )
      {
         ( (FullDetailedTasksListFragmentAdapter) getListAdapter() ).notifyDataSetChanged();
      }
   }
   
   
   
   @Override
   protected int getDefaultTaskSort()
   {
      return MolokoApp.getSettings().getTaskSort();
   }
   
   
   
   @Override
   protected ListAdapter createEmptyListAdapter()
   {
      return new MinDetailedTasksListFragmentAdapter( getActivity(),
                                                      R.layout.mindetailed_taskslist_listitem );
   }
   
   
   
   @Override
   protected ListAdapter createListAdapterForResult( List< ListTask > result,
                                                     IFilter filter )
   {
      return new MinDetailedTasksListFragmentAdapter( getActivity(),
                                                      R.layout.mindetailed_taskslist_listitem,
                                                      result );
   }
   
}