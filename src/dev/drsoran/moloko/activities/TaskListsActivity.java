/*
 * Copyright (c) 2012 Ronny R�hricht
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

package dev.drsoran.moloko.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.mdt.rtm.data.RtmList;

import dev.drsoran.moloko.ApplyChangesInfo;
import dev.drsoran.moloko.IEditFragment;
import dev.drsoran.moloko.R;
import dev.drsoran.moloko.activities.base.MolokoEditFragmentActivity;
import dev.drsoran.moloko.annotations.InstanceState;
import dev.drsoran.moloko.fragments.TaskListsFragment;
import dev.drsoran.moloko.fragments.dialogs.AddRenameListDialogFragment;
import dev.drsoran.moloko.fragments.listeners.IMolokoEditDialogFragmentListener;
import dev.drsoran.moloko.fragments.listeners.ITaskListsFragmentListener;
import dev.drsoran.moloko.util.Intents;
import dev.drsoran.moloko.util.RtmListEditUtils;
import dev.drsoran.moloko.util.UIUtils;
import dev.drsoran.rtm.RtmListWithTaskCount;


public class TaskListsActivity extends MolokoEditFragmentActivity implements
         ITaskListsFragmentListener, IMolokoEditDialogFragmentListener
{
   private final static class Config
   {
      public final static String LIST_TO_DELETE = "list_to_delete";
   }
   
   @InstanceState( key = Config.LIST_TO_DELETE,
                   defaultValue = InstanceState.NULL )
   private RtmList listToDelete;
   
   
   
   public TaskListsActivity()
   {
      registerAnnotatedConfiguredInstance( this, TaskListsActivity.class );
   }
   
   
   
   @Override
   public void onCreate( Bundle savedInstanceState )
   {
      super.onCreate( savedInstanceState );
      setContentView( R.layout.tasklists_activity );
   }
   
   
   
   @Override
   public boolean onActivityCreateOptionsMenu( Menu menu )
   {
      if ( isWritableAccess() )
      {
         getSupportMenuInflater().inflate( R.menu.tasklists_activity_rwd, menu );
      }
      else
      {
         getSupportMenuInflater().inflate( R.menu.tasklists_activity, menu );
      }
      
      super.onActivityCreateOptionsMenu( menu );
      
      return true;
   }
   
   
   
   @Override
   public boolean onOptionsItemSelected( MenuItem item )
   {
      switch ( item.getItemId() )
      {
         case R.id.menu_add_list:
            showAddListDialog();
            return true;
            
         default :
            return super.onOptionsItemSelected( item );
      }
   }
   
   
   
   @Override
   public void openList( int pos )
   {
      final RtmListWithTaskCount rtmList = getRtmList( pos );
      
      // Check if the smart filter could be parsed. Otherwise
      // we do not fire the intent.
      if ( rtmList.isSmartFilterValid() )
      {
         final Intent intent = Intents.createOpenListIntent( this,
                                                             rtmList,
                                                             null );
         startActivityWithHomeAction( intent, getClass() );
      }
   }
   
   
   
   @Override
   public void openChild( Intent intent )
   {
      startActivityWithHomeAction( intent, getClass() );
   }
   
   
   
   @Override
   public void deleteList( int pos )
   {
      final RtmListWithTaskCount list = getRtmList( pos );
      setListToDelete( list.getRtmList() );
      
      UIUtils.showDeleteElementDialog( this, list.getName() );
   }
   
   
   
   @Override
   public void renameList( int pos )
   {
      showRenameListDialog( getRtmList( pos ) );
   }
   
   
   
   @Override
   public void onValidateDialogFragment( IEditFragment editDialogFragment )
   {
      validateFragment( editDialogFragment );
   }
   
   
   
   @Override
   public void onFinishEditDialogFragment( IEditFragment editDialogFragment )
   {
      finishFragmentEditing( editDialogFragment );
   }
   
   
   
   @Override
   public void onCancelEditDialogFragment( IEditFragment editDialogFragment )
   {
      // In case of a dialog we cannot show a cancel query since the dialog has already gone.
      editDialogFragment.onCancelEditing();
   }
   
   
   
   private void showRenameListDialog( RtmListWithTaskCount list )
   {
      createAddRenameListDialogFragment( createRenameListFragmentConfig( list ) );
   }
   
   
   
   private Bundle createRenameListFragmentConfig( RtmListWithTaskCount list )
   {
      final Bundle config = new Bundle();
      
      config.putParcelable( Intents.Extras.KEY_LIST, list.getRtmList() );
      if ( list.getRtmList().getSmartFilter() != null )
      {
         config.putParcelable( Intents.Extras.KEY_FILTER, list.getRtmList()
                                                              .getSmartFilter() );
      }
      
      return config;
   }
   
   
   
   private void showAddListDialog()
   {
      createAddRenameListDialogFragment( Bundle.EMPTY );
   }
   
   
   
   private RtmListWithTaskCount getRtmList( int pos )
   {
      final TaskListsFragment taskListsFragment = (TaskListsFragment) getSupportFragmentManager().findFragmentById( R.id.frag_tasklists );
      return taskListsFragment.getRtmList( pos );
   }
   
   
   
   private void createAddRenameListDialogFragment( Bundle config )
   {
      final DialogFragment dialogFragment = AddRenameListDialogFragment.newInstance( config );
      UIUtils.showDialogFragment( this,
                                  dialogFragment,
                                  String.valueOf( R.id.frag_add_rename_list ) );
   }
   
   
   
   @Override
   protected void handleDeleteElementDialogClick( String tag, int which )
   {
      if ( which == Dialog.BUTTON_POSITIVE )
      {
         final ApplyChangesInfo modifications = RtmListEditUtils.deleteList( this,
                                                                             getListToDelete() );
         applyModifications( modifications );
      }
      
      setListToDelete( null );
   }
   
   
   
   private RtmList getListToDelete()
   {
      return listToDelete;
   }
   
   
   
   private void setListToDelete( RtmList listToDelete )
   {
      this.listToDelete = listToDelete;
   }
   
   
   
   @Override
   protected int[] getFragmentIds()
   {
      return new int[]
      { R.id.frag_tasklists };
   }
}
