/* 
 *	Copyright (c) 2014 Ronny R�hricht
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

package dev.drsoran.moloko.app.home;

import android.app.Fragment;
import android.content.Intent;
import dev.drsoran.moloko.app.baseactivities.MolokoActivity;
import dev.drsoran.moloko.state.AnnotatedConfigurationSupport;


public abstract class NavigationDrawerHandlerBase implements
         INavigationDrawerHandler
{
   private final AnnotatedConfigurationSupport annotatedConfigurationSupport = new AnnotatedConfigurationSupport();
   
   private final MolokoActivity activity;
   
   private final int fragmentId;
   
   
   
   protected NavigationDrawerHandlerBase( MolokoActivity activity,
      int fragmentId )
   {
      this.activity = activity;
      this.fragmentId = fragmentId;
   }
   
   
   
   public MolokoActivity getActivity()
   {
      return activity;
   }
   
   
   
   public int getFragmentId()
   {
      return fragmentId;
   }
   
   
   
   public final < T > void registerAnnotatedConfiguredInstance( T instance,
                                                                Class< T > clazz )
   {
      annotatedConfigurationSupport.registerInstance( instance, clazz );
   }
   
   
   
   public void configureFromIntent( Intent intent )
   {
      if ( intent.getExtras() != null )
      {
         annotatedConfigurationSupport.setInstanceStates( intent.getExtras() );
      }
   }
   
   
   
   public void disableFragmentOptionsMenu()
   {
      final Fragment fragment = getActivity().findAddedFragmentById( getFragmentId() );
      if ( fragment != null )
      {
         fragment.setHasOptionsMenu( false );
      }
   }
   
   
   
   public void restoreFragmentOptionsMenu()
   {
      final Fragment fragment = getActivity().findAddedFragmentById( getFragmentId() );
      if ( fragment != null )
      {
         fragment.setHasOptionsMenu( true );
      }
   }
}