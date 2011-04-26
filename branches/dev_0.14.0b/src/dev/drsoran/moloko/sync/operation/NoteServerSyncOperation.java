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

package dev.drsoran.moloko.sync.operation;

import java.util.Collections;

import com.mdt.rtm.data.RtmTaskNote;

import dev.drsoran.moloko.sync.elements.SyncRtmTaskNotesList;


public class NoteServerSyncOperation extends ServerSyncOperation< RtmTaskNote >
{
   private SyncRtmTaskNotesList resultList;
   
   

   public NoteServerSyncOperation( Builder< RtmTaskNote > builder )
   {
      super( builder );
   }
   


   @Override
   protected RtmTaskNote handleResultElement( RtmTaskNote resultElement )
   {
      final RtmTaskNote result = super.handleResultElement( resultElement );
      
      if ( result != null )
      {
         if ( resultList == null )
         {
            resultList = new SyncRtmTaskNotesList( Collections.singletonList( result ) );
         }
         else
         {
            // Finally we want to have one list with RtmTaskNotes containing
            // all changes from all server operations. So we merge the current
            // change to the final resultList.
            resultList.update( result );
         }
      }
      
      return result;
   }
}
