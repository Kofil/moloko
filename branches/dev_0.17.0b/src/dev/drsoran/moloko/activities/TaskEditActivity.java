/* 
 *	Copyright (c) 2012 Ronny R�hricht
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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import dev.drsoran.moloko.R;
import dev.drsoran.moloko.annotations.InstanceState;
import dev.drsoran.moloko.fragments.TaskEditFragment;
import dev.drsoran.moloko.util.Intents;
import dev.drsoran.rtm.Task;


public class TaskEditActivity extends AbstractTaskEditActivity
{
   @InstanceState( key = Intents.Extras.KEY_TASK,
                   defaultValue = InstanceState.NULL )
   private Task task;
   
   
   
   public TaskEditActivity()
   {
      registerAnnotatedConfiguredInstance( this, TaskEditActivity.class );
   }
   
   
   
   @Override
   protected int getContentViewResourceId()
   {
      return R.layout.task_edit_activity;
   }
   
   
   
   @Override
   protected Fragment createTaskEditFragment()
   {
      final Fragment fragment = TaskEditFragment.newInstance( createTaskEditFragmentConfig() );
      return fragment;
   }
   
   
   
   private Bundle createTaskEditFragmentConfig()
   {
      final Bundle config = new Bundle();
      
      config.putParcelable( Intents.Extras.KEY_TASK, task );
      
      return config;
   }
}
