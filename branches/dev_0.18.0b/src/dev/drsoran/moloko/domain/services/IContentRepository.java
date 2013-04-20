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

package dev.drsoran.moloko.domain.services;

import java.util.NoSuchElementException;

import android.database.ContentObserver;
import android.net.Uri;
import dev.drsoran.moloko.domain.model.ExtendedTaskCount;
import dev.drsoran.moloko.domain.model.IContact;
import dev.drsoran.moloko.domain.model.ITask;
import dev.drsoran.moloko.domain.model.ITasksList;
import dev.drsoran.moloko.domain.model.RtmSmartFilter;
import dev.drsoran.moloko.grammar.GrammarException;


public interface IContentRepository
{
   void registerContentObserver( ContentObserver observer, Uri contentUri );
   
   
   
   void unregisterContentObserver( ContentObserver observer );
   
   
   
   ITask getTask( long taskId, int taskContentOptions ) throws NoSuchElementException,
                                                       ContentException;
   
   
   
   Iterable< ITask > getTasks( int taskContentOptions ) throws ContentException;
   
   
   
   Iterable< ITask > getTasksInTasksList( ITasksList tasksList,
                                          int taskContentOptions ) throws ContentException,
                                                                  GrammarException;
   
   
   
   ExtendedTaskCount getTaskCountOfTasksList( ITasksList tasksList ) throws ContentException,
                                                                    GrammarException;
   
   
   
   Iterable< ITask > getTasksFromSmartFilter( RtmSmartFilter smartFilter,
                                              int taskContentOptions ) throws ContentException,
                                                                      GrammarException;
   
   
   
   ITasksList getTasksList( long tasksListId ) throws NoSuchElementException,
                                              ContentException;
   
   
   
   Iterable< ITasksList > getAllTasksLists() throws ContentException;
   
   
   
   Iterable< ITasksList > getPhysicalTasksLists() throws ContentException;
   
   
   
   IContact getContact( long contactId ) throws NoSuchElementException,
                                        ContentException;
   
   
   
   Iterable< IContact > getContacts() throws ContentException;
   
   
   
   int getNumTasksContactIsParticipating( long contactId ) throws ContentException;
   
   
   
   Iterable< String > getTags() throws ContentException;
}
