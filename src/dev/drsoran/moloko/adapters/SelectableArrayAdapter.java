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

package dev.drsoran.moloko.adapters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;


abstract class SelectableArrayAdapter< T > extends ArrayAdapter< T > implements
         ISelectableAdapter< T >
{
   private final Comparator< T > SORT_SELECTION = new Comparator< T >()
   {
      @Override
      public int compare( T lhs, T rhs )
      {
         final int valLhs = selectedItems.contains( lhs ) ? 1 : 0;
         final int valRhs = selectedItems.contains( rhs ) ? 1 : 0;
         
         return valRhs - valLhs;
      }
   };
   
   private IOnSelectionChangesListener< T > selectionChangedListener;
   
   private Set< T > selectedItems;
   
   private Set< T > unselectedItems;
   
   
   
   protected SelectableArrayAdapter( Context context, List< T > items )
   {
      super( context, View.NO_ID, items );
      
      selectedItems = new HashSet< T >();
      unselectedItems = new HashSet< T >( items );
   }
   
   
   
   @Override
   public IOnSelectionChangesListener< T > getOnSelectionChangesListener()
   {
      return selectionChangedListener;
   }
   
   
   
   @Override
   public void setOnSelectionChangesListener( IOnSelectionChangesListener< T > listener )
   {
      this.selectionChangedListener = listener;
   }
   
   
   
   @Override
   public void select( T item )
   {
      if ( unselectedItems.remove( item ) )
      {
         selectedItems.add( item );
         notifyItem( item, true );
         notifyDataSetChanged();
      }
   }
   
   
   
   @Override
   public void selectBulk( Collection< ? extends T > items )
   {
      if ( unselectedItems.removeAll( items ) )
      {
         selectedItems.addAll( items );
         notifyBulk( items, true );
         notifyDataSetChanged();
      }
   }
   
   
   
   @Override
   public Collection< T > getSelectedItems()
   {
      return selectedItems;
   }
   
   
   
   @Override
   public void deselect( T item )
   {
      if ( selectedItems.remove( item ) )
      {
         unselectedItems.add( item );
         notifyItem( item, false );
         notifyDataSetChanged();
      }
   }
   
   
   
   @Override
   public void deselectBulk( Collection< ? extends T > items )
   {
      if ( selectedItems.removeAll( items ) )
      {
         unselectedItems.addAll( items );
         notifyBulk( items, true );
         notifyDataSetChanged();
      }
   }
   
   
   
   @Override
   public Collection< T > getUnselectedItems()
   {
      return unselectedItems;
   }
   
   
   
   @Override
   public boolean isSelected( T item )
   {
      return selectedItems.contains( item );
   }
   
   
   
   @Override
   public void selectAll()
   {
      selectBulk( new ArrayList< T >( unselectedItems ) );
   }
   
   
   
   @Override
   public void deselectAll()
   {
      deselectBulk( new ArrayList< T >( selectedItems ) );
   }
   
   
   
   @Override
   public void invertSelection()
   {
      final Set< T > temp = selectedItems;
      
      selectedItems = unselectedItems;
      unselectedItems = temp;
      
      notifyBulk( selectedItems, true );
      notifyBulk( unselectedItems, false );
      notifyDataSetChanged();
   }
   
   
   
   @Override
   public boolean areAllSelected()
   {
      return unselectedItems.size() == 0 && selectedItems.size() > 0;
   }
   
   
   
   @Override
   public boolean areSomeSelected()
   {
      return selectedItems.size() > 0;
   }
   
   
   
   @Override
   public int getSelectedCount()
   {
      return selectedItems.size();
   }
   
   
   
   @Override
   public void sortBySelection()
   {
      sort( SORT_SELECTION );
   }
   
   
   
   private void notifyItem( T item, boolean isSelected )
   {
      notifyBulk( Collections.singletonList( item ), isSelected );
   }
   
   
   
   private void notifyBulk( Collection< ? extends T > items, boolean isSelected )
   {
      if ( selectionChangedListener != null )
      {
         selectionChangedListener.onSelectionChanged( items, isSelected );
      }
   }
}
