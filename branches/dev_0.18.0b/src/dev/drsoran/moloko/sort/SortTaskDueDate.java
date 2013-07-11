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

package dev.drsoran.moloko.sort;

import java.util.Comparator;

import dev.drsoran.moloko.domain.model.Due;
import dev.drsoran.moloko.domain.model.Task;


public class SortTaskDueDate implements Comparator< Task >
{
   @Override
   public int compare( Task lhs, Task rhs )
   {
      final Due lhsDue = lhs.getDue();
      final Due rhsDue = rhs.getDue();
      
      if ( lhsDue == rhsDue )
      {
         return 0;
      }
      else if ( lhsDue != null && rhsDue == null )
      {
         return -1;
      }
      else if ( lhsDue == null && rhsDue != null )
      {
         return 1;
      }
      else
      {
         return (int) ( lhsDue.getMillisUtc() - rhsDue.getMillisUtc() );
      }
   }
}
