/*
 * Copyright (c) 2010 Ronny R�hricht
 * 
 * This file is part of Moloko.
 * 
 * Moloko is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Moloko is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Moloko. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 * Ronny R�hricht - implementation
 */

package dev.drsoran.moloko.domain;

import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import dev.drsoran.moloko.content.ContentUris;
import dev.drsoran.moloko.domain.model.Modification;
import dev.drsoran.moloko.domain.services.ContentException;


abstract class AbstractContentEditHandler< T >
{
   private final ContentResolver contentResolver;
   
   private final IContentValuesFactory contentValuesFactory;
   
   private final IModificationsApplier modificationsApplier;
   
   
   
   protected AbstractContentEditHandler( ContentResolver contentResolver,
      IContentValuesFactory contentValuesFactory,
      IModificationsApplier modificationsApplier )
   {
      this.contentResolver = contentResolver;
      this.contentValuesFactory = contentValuesFactory;
      this.modificationsApplier = modificationsApplier;
   }
   
   
   
   public Uri insertElement( Uri contentUri, T element ) throws ContentException
   {
      return performInsert( contentUri, element );
   }
   
   
   
   public Uri insertAggregatedElement( Uri contentUri, T element, long rootId ) throws ContentException
   {
      return performInsert( ContentUris.bindAggregationIdToUri( contentUri,
                                                                rootId ),
                            element );
   }
   
   
   
   public void updateElement( Uri contentUri,
                              T existingElement,
                              T updatedElement,
                              long elementId ) throws ContentException
   {
      final Collection< Modification > updateModifications = collectUpdateModifications( existingElement,
                                                                                         updatedElement );
      performUpdate( ContentUris.bindElementId( contentUri, elementId ),
                     updatedElement );
      
      applyModification( updateModifications );
   }
   
   
   
   public void updateAggregatedElement( Uri contentUri,
                                        T existingElement,
                                        T updatedElement,
                                        long rootId,
                                        long elementId ) throws ContentException
   {
      final Collection< Modification > updateModifications = collectAggregatedUpdateModifications( rootId,
                                                                                                   existingElement,
                                                                                                   updatedElement );
      performUpdate( ContentUris.bindAggregatedElementIdToUri( contentUri,
                                                               rootId,
                                                               elementId ),
                     updatedElement );
      
      applyModification( updateModifications );
   }
   
   
   
   public void deleteElement( Uri contentUri, long elementId ) throws NoSuchElementException,
                                                              ContentException
   {
      final Collection< Modification > deleteModifications = collectDeleteModifications( elementId );
      
      // If the delete resulted in no modification, then we can perform a physical delete. Otherwise,
      // the delete is performed by updating an entity property.
      if ( deleteModifications.size() == 0 )
      {
         performDelete( ContentUris.bindElementId( contentUri, elementId ) );
      }
      else
      {
         applyModification( deleteModifications );
      }
   }
   
   
   
   public void deleteAggregatedElement( Uri contentUri,
                                        long rootId,
                                        long elementId ) throws NoSuchElementException,
                                                        ContentException
   {
      final Collection< Modification > deleteModifications = collectAggregatedDeleteModifications( rootId,
                                                                                                   elementId );
      
      performDelete( ContentUris.bindAggregatedElementIdToUri( contentUri,
                                                               rootId,
                                                               elementId ) );
      applyModification( deleteModifications );
   }
   
   
   
   protected Collection< Modification > collectUpdateModifications( T existingElement,
                                                                    T updatedElement )
   {
      return Collections.emptyList();
   }
   
   
   
   protected Collection< Modification > collectAggregatedUpdateModifications( long rootId,
                                                                              T existingElement,
                                                                              T updatedElement )
   {
      return Collections.emptyList();
   }
   
   
   
   protected Collection< Modification > collectDeleteModifications( long elementId )
   {
      return Collections.emptyList();
   }
   
   
   
   protected Collection< Modification > collectAggregatedDeleteModifications( long rootId,
                                                                              long elementId )
   {
      return Collections.emptyList();
   }
   
   
   
   private Uri performInsert( Uri contentUri, T element ) throws ContentException
   {
      try
      {
         final ContentValues contentValues = contentValuesFactory.createContentValues( element );
         return contentResolver.insert( contentUri, contentValues );
      }
      catch ( Throwable e )
      {
         throw new ContentException( e );
      }
   }
   
   
   
   private void performUpdate( Uri contentUri, T element ) throws ContentException
   {
      try
      {
         final ContentValues contentValues = contentValuesFactory.createContentValues( element );
         contentResolver.update( contentUri, contentValues, null, null );
      }
      catch ( Throwable e )
      {
         throw new ContentException( e );
      }
   }
   
   
   
   private void performDelete( Uri contentUri ) throws NoSuchElementException,
                                               ContentException
   {
      final int numDeleted;
      try
      {
         numDeleted = contentResolver.delete( contentUri, null, null );
      }
      catch ( Throwable e )
      {
         throw new ContentException( e );
      }
      
      if ( numDeleted < 1 )
      {
         throw new NoSuchElementException( "No element to delete with URI '"
            + contentUri + "'." );
      }
   }
   
   
   
   private void applyModification( Collection< Modification > modifications )
   {
      modificationsApplier.applyModifications( modifications );
   }
}
