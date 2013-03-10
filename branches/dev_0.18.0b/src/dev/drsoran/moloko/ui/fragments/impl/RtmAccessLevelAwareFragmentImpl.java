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

package dev.drsoran.moloko.ui.fragments.impl;

import com.mdt.rtm.data.RtmAuth;

import dev.drsoran.moloko.app.baseactivities.MolokoFragmentActivity;


public class RtmAccessLevelAwareFragmentImpl
{
   private MolokoFragmentActivity activity;
   
   
   
   public void onAttach( MolokoFragmentActivity activity )
   {
      this.activity = activity;
   }
   
   
   
   public void onDetach()
   {
      activity = null;
   }
   
   
   
   public void reEvaluateRtmAccessLevel( RtmAuth.Perms currentAccessLevel )
   {
      if ( activity != null )
      {
         activity.supportInvalidateOptionsMenu();
      }
   }
   
   
   
   public boolean isReadOnlyAccess()
   {
      if ( activity != null )
      {
         return activity.isReadOnlyAccess();
      }
      else
      {
         return false;
      }
   }
   
   
   
   public boolean isWritableAccess()
   {
      if ( activity != null )
      {
         return activity.isWritableAccess();
      }
      else
      {
         return false;
      }
   }
}
