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

package dev.drsoran.moloko.fragments.factories;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import dev.drsoran.moloko.fragments.FullDetailedTasksListFragment;
import dev.drsoran.moloko.fragments.MinDetailedTasksListFragment;
import dev.drsoran.moloko.fragments.SelectableTasksListsFragment;
import dev.drsoran.moloko.fragments.TaskSearchResultListFragment;


public final class TasksListFragmentFactory extends AbstractFragmentFactory
{
   private final static List< Class< ? extends Fragment > > FRAGMENT_CLASSES = new ArrayList< Class< ? extends Fragment > >();
   
   static
   {
      FRAGMENT_CLASSES.add( FullDetailedTasksListFragment.class );
      FRAGMENT_CLASSES.add( MinDetailedTasksListFragment.class );
      FRAGMENT_CLASSES.add( SelectableTasksListsFragment.class );
      FRAGMENT_CLASSES.add( TaskSearchResultListFragment.class );
   }
   
   

   public final static Fragment newFragment( Context context, Intent intent )
   {
      return resolveIntentToFragment( context, intent, FRAGMENT_CLASSES );
   }
}
