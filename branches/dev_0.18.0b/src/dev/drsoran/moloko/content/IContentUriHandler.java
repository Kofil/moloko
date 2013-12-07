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

package dev.drsoran.moloko.content;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;


public interface IContentUriHandler
{
   Cursor query( Uri contentUri,
                 String[] projection,
                 String selection,
                 String[] selectionArgs,
                 String sortOrder );
   
   
   
   long insert( Uri uri, ContentValues values );
   
   
   
   int update( Uri uri,
               ContentValues values,
               String selection,
               String[] selectionArgs );
   
   
   
   int delete( Uri uri, String selection, String[] selectionArgs );
}
