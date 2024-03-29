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

package dev.drsoran.moloko.fragments.base.impl;

import java.util.Collections;
import java.util.List;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.text.Spanned;
import android.view.View;
import dev.drsoran.moloko.adapters.base.SwappableArrayAdapter;
import dev.drsoran.moloko.fragments.base.MolokoListFragment;


public class LoaderListFragmentImpl< D > extends
         LoaderFragmentImplBase< List< D > >
{
   public static interface Support< D > extends
            LoaderFragmentImplBase.Support< List< D > >
   {
      SwappableArrayAdapter< D > createListAdapter();
   }
   
   private final MolokoListFragment< D > fragment;
   
   private final LoaderListFragmentViewManager viewManager;
   
   private final Support< D > support;
   
   
   
   public LoaderListFragmentImpl( MolokoListFragment< D > fragment )
   {
      super( fragment, fragment, fragment );
      
      this.fragment = fragment;
      this.viewManager = new LoaderListFragmentViewManager( fragment );
      this.support = fragment;
   }
   
   
   
   public void onViewCreated( View view, Bundle savedInstanceState )
   {
      viewManager.onViewCreated( view, savedInstanceState );
      fragment.setListAdapter( support.createListAdapter() );
      
      if ( support.isReadyToStartLoader() )
      {
         startLoader();
      }
   }
   
   
   
   public void onSettingsChanged( int which )
   {
      notifyDataSetChanged();
   }
   
   
   
   @Override
   public Loader< List< D > > onCreateLoader( int id, Bundle args )
   {
      viewManager.onCreateLoader();
      return super.onCreateLoader( id, args );
   }
   
   
   
   @Override
   public void onLoadFinished( Loader< List< D > > loader, List< D > data )
   {
      @SuppressWarnings( "unchecked" )
      final SwappableArrayAdapter< D > listAdapter = (SwappableArrayAdapter< D >) fragment.getListAdapter();
      
      if ( data != null )
      {
         listAdapter.swap( data );
      }
      else
      {
         listAdapter.swap( Collections.< D > emptyList() );
         fragment.getLoaderManager().destroyLoader( support.getLoaderId() );
      }
      
      super.onLoadFinished( loader, data );
      
      viewManager.onLoadFinished( isLoaderDataFound() );
   }
   
   
   
   public int getNoElementsResourceId()
   {
      return viewManager.getNoElementsResourceId();
   }
   
   
   
   public void setNoElementsResourceId( int resId )
   {
      viewManager.setNoElementsResourceId( resId );
   }
   
   
   
   public void showError( int messageResId )
   {
      viewManager.showError( messageResId );
   }
   
   
   
   public void showError( CharSequence message )
   {
      viewManager.showError( message );
   }
   
   
   
   public void showError( Spanned message )
   {
      viewManager.showError( message );
   }
   
   
   
   private void notifyDataSetChanged()
   {
      ( (SwappableArrayAdapter< ? >) fragment.getListAdapter() ).notifyDataSetChanged();
   }
}
