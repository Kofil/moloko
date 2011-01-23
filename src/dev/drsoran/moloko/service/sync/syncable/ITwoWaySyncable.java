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

package dev.drsoran.moloko.service.sync.syncable;

import android.content.ContentProviderClient;
import android.util.Pair;

import com.mdt.rtm.Service;

import dev.drsoran.moloko.service.sync.operation.ISyncOperation;


public interface ITwoWaySyncable< T >
{
   public long getModifiedDate();
   


   public boolean isDeleted();
   


   public ISyncOperation computeContentProviderInsertOperation( ContentProviderClient provider,
                                                                Object... params );
   


   public ISyncOperation computeContentProviderDeleteOperation( ContentProviderClient provider,
                                                                Object... params );
   


   public ISyncOperation computeServerInsertOperation( Service service,
                                                       Object... params );
   


   public ISyncOperation computeServerDeleteOperation( Service service,
                                                       Object... params );
   


   public Pair< ISyncOperation, ISyncOperation > computeMergeOperation( Service service,
                                                                        ContentProviderClient provider,
                                                                        T element,
                                                                        Object... params );
}
