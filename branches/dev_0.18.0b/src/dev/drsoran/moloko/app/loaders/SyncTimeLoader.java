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

package dev.drsoran.moloko.app.loaders;

import android.net.Uri;
import dev.drsoran.moloko.content.ContentUris;
import dev.drsoran.moloko.domain.DomainContext;
import dev.drsoran.moloko.domain.services.ContentException;
import dev.drsoran.moloko.domain.services.IContentRepository;
import dev.drsoran.rtm.sync.SyncTime;


public class SyncTimeLoader extends AbstractLoader< SyncTime >
{
   public SyncTimeLoader( DomainContext context )
   {
      super( context );
   }
   
   
   
   @Override
   public Uri getContentUri()
   {
      return ContentUris.SYNC_CONTENT_URI;
   }
   
   
   
   @Override
   public SyncTime queryResultInBackground( IContentRepository repository ) throws ContentException
   {
      return repository.getSyncTimes();
   }
}