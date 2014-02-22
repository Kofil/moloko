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

import java.util.List;

import dev.drsoran.rtm.model.RtmContact;
import dev.drsoran.rtm.model.RtmLocation;
import dev.drsoran.rtm.model.RtmNote;
import dev.drsoran.rtm.model.RtmSettings;
import dev.drsoran.rtm.model.RtmTask;
import dev.drsoran.rtm.model.RtmTasksList;


public interface IRtmSyncPartner
{
   List< RtmTasksList > getTasksLists( long modifiedSinceMsUtc );
   
   
   
   List< IModification > getModificationsOfTasksList( String tasksListId );
   
   
   
   void insertTasksList( RtmTasksList tasksList );
   
   
   
   void updateTasksList( RtmTasksList currentTasksList,
                         RtmTasksList updatedTasksList );
   
   
   
   void deleteTasksList( RtmTasksList tasksList );
   
   
   
   List< RtmTask > getTasks( long modifiedSinceMsUtc );
   
   
   
   List< IModification > getModificationsOfTask( RtmTask task );
   
   
   
   void insertTask( RtmTask task );
   
   
   
   void updateTask( RtmTask currentTask, RtmTask updatedTask );
   
   
   
   void deleteTask( RtmTask task );
   
   
   
   List< RtmNote > getDeletedNotes( RtmTask task, long deletedSinceMsUtc );
   
   
   
   List< RtmLocation > getLocations();
   
   
   
   void insertLocation( RtmLocation location );
   
   
   
   void updateLocation( RtmLocation currentLocation, RtmLocation updatedLocation );
   
   
   
   void deleteLocation( RtmLocation location );
   
   
   
   List< RtmContact > getContacts();
   
   
   
   void insertContact( RtmContact contact );
   
   
   
   void updateContact( RtmContact currentContact, RtmContact updatedContact );
   
   
   
   void deleteContact( RtmContact contact );
   
   
   
   RtmSettings getSettings();
   
   
   
   void onSyncStarted( long startMillisUtc );
   
   
   
   void onSyncSuccessful();
   
   
   
   void onSyncFailed();
}
