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

package dev.drsoran.moloko.activities;

import java.util.ArrayList;
import java.util.List;

import dev.drsoran.moloko.fragments.SelectableTasksListsFragment;
import dev.drsoran.moloko.fragments.listeners.ISelectableTasksListListener;
import dev.drsoran.moloko.util.Intents;
import dev.drsoran.moloko.util.TaskEditUtils;
import dev.drsoran.rtm.Task;


public class SelectMultipleTasksActivity extends AbstractTasksListActivity
         implements ISelectableTasksListListener
{
   @SuppressWarnings( "unused" )
   private final static String TAG = "Moloko."
      + SelectMultipleTasksActivity.class.getSimpleName();
   
   

   @Override
   public void onOpenTask( int pos )
   {
      if ( getTasksListFragment() != null )
         getTasksListFragment().toggle( pos );
   }
   


   @Override
   public void onEditSelectedTasks( List< ? extends Task > tasks )
   {
      final int selCnt = tasks.size();
      if ( selCnt > 0 )
         if ( selCnt > 1 )
            startActivityForResult( Intents.createEditMultipleTasksIntent( this,
                                                                           getTaskIds( tasks ) ),
                                    EditMultipleTasksActivity.REQ_EDIT_TASK );
         else
            startActivityForResult( Intents.createEditTaskIntent( this,
                                                                  tasks.get( 0 )
                                                                       .getId() ),
                                    TaskEditActivity.REQ_EDIT_TASK );
      
   }
   


   @Override
   public void onCompleteSelectedTasks( List< ? extends Task > tasks )
   {
      TaskEditUtils.setTasksCompletion( this, tasks, true );
   }
   


   @Override
   public void onUncompleteSelectedTasks( List< ? extends Task > tasks )
   {
      TaskEditUtils.setTasksCompletion( this, tasks, false );
   }
   


   @Override
   public void onPostponeSelectedTasks( List< ? extends Task > tasks )
   {
      TaskEditUtils.postponeTasks( this, tasks );
      
   }
   


   @Override
   public void onDeleteSelectedTasks( List< ? extends Task > tasks )
   {
      TaskEditUtils.deleteTasks( this, tasks );
   }
   


   private List< String > getTaskIds( List< ? extends Task > tasks )
   {
      final List< String > taskIds = new ArrayList< String >( tasks.size() );
      for ( Task task : tasks )
      {
         taskIds.add( task.getId() );
      }
      return taskIds;
   }
   


   @Override
   protected SelectableTasksListsFragment getTasksListFragment()
   {
      if ( super.getTasksListFragment() instanceof SelectableTasksListsFragment )
         return (SelectableTasksListsFragment) super.getTasksListFragment();
      else
         return null;
   }
   
}