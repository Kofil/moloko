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

package dev.drsoran.rtm.sync;

import java.util.Comparator;

import dev.drsoran.rtm.model.RtmContact;
import dev.drsoran.rtm.model.RtmLocation;
import dev.drsoran.rtm.model.RtmTask;
import dev.drsoran.rtm.model.RtmTasksList;


public class RtmSyncHandlerFactory implements IRtmSyncHandlerFactory
{
   private final IRtmSyncPartner syncPartner;
   
   
   
   public RtmSyncHandlerFactory( IRtmSyncPartner syncPartner )
   {
      if ( syncPartner == null )
      {
         throw new IllegalArgumentException( "syncPartner" );
      }
      
      this.syncPartner = syncPartner;
   }
   
   
   
   @Override
   public IRtmSyncHandler createRtmTasksListsSyncHandler( Comparator< RtmTasksList > comparator )
   {
      return new RtmTasksListsSyncHandler( syncPartner, comparator );
   }
   
   
   
   @Override
   public IRtmSyncHandler createRtmTasksSyncHandler( Comparator< RtmTask > comparator )
   {
      return new RtmTasksSyncHandler( syncPartner, comparator );
   }
   
   
   
   @Override
   public IRtmSyncHandler createRtmLocationsSyncHandler( Comparator< RtmLocation > comparator )
   {
      return new RtmLocationsSyncHandler( syncPartner, comparator );
   }
   
   
   
   @Override
   public IRtmSyncHandler createRtmContactsSyncHandler( Comparator< RtmContact > comparator )
   {
      return new RtmContactsSyncHandler( syncPartner, comparator );
   }
   
   
   
   @Override
   public IRtmSyncHandler createRtmSettingsSyncHandler()
   {
      return new RtmSettingsSyncHandler( syncPartner );
   }
}
