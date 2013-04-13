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

import dev.drsoran.moloko.domain.model.ITask;
import dev.drsoran.moloko.domain.model.ITasksList;


public interface IContentEditService
{
   void insertTask( ITask task ) throws ContentException;
   
   
   
   void updateTask( long taskId, ITask updatedTask ) throws NoSuchElementException,
                                                    ContentException;
   
   
   
   void deleteTask( long taskId ) throws NoSuchElementException,
                                 ContentException;
   
   
   
   void insertTasksList( ITasksList tasksList ) throws ContentException;
   
   
   
   void updateTasksList( long tasksListId, ITasksList updatedTasksList ) throws NoSuchElementException,
                                                                        ContentException;
   
   
   
   void deleteTasksList( long tasksListId ) throws NoSuchElementException,
                                           ContentException;
}
