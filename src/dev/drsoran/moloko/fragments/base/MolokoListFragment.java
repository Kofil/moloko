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

package dev.drsoran.moloko.fragments.base;

import java.util.HashMap;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.app.SupportActivity;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListAdapter;
import dev.drsoran.moloko.ApplyChangesInfo;
import dev.drsoran.moloko.IConfigurable;
import dev.drsoran.moloko.content.ContentProviderActionItemList;
import dev.drsoran.moloko.fragments.base.impl.LoaderListFragmentImpl;
import dev.drsoran.moloko.fragments.base.impl.MolokoListFragmentImpl;
import dev.drsoran.moloko.util.AccountUtils;


public abstract class MolokoListFragment< D > extends ListFragment implements
         IConfigurable, LoaderCallbacks< D >,
         LoaderListFragmentImpl.Support< D >
{
   private final MolokoListFragmentImpl baseImpl;
   
   private final LoaderListFragmentImpl< D > loaderImpl;
   
   private final EditFragmentImpl editImpl;
   
   
   
   protected MolokoListFragment()
   {
      baseImpl = new MolokoListFragmentImpl( this, getSettingsMask() );
      loaderImpl = new LoaderListFragmentImpl< D >( this );
      editImpl = new EditFragmentImpl( this );
   }
   
   
   
   @Override
   public void onCreate( Bundle savedInstanceState )
   {
      super.onCreate( savedInstanceState );
      baseImpl.onCreate( savedInstanceState );
      loaderImpl.onCreate( savedInstanceState );
   }
   
   
   
   @Override
   public void onAttach( SupportActivity activity )
   {
      super.onAttach( activity );
      baseImpl.onAttach( activity );
      loaderImpl.onAttach( activity );
      editImpl.onAttach( activity.asActivity() );
   }
   
   
   
   @Override
   public void onDetach()
   {
      baseImpl.onDetach();
      loaderImpl.onDetach();
      editImpl.onDetach();
      super.onDetach();
   }
   
   
   
   @Override
   public void onViewCreated( View view, Bundle savedInstanceState )
   {
      super.onViewCreated( view, savedInstanceState );
      loaderImpl.onViewCreated( view, savedInstanceState );
      editImpl.onViewCreated( view, savedInstanceState );
   }
   
   
   
   public View getEmptyView()
   {
      return baseImpl.getEmptyView();
   }
   
   
   
   public void showEmptyView( boolean show )
   {
      baseImpl.showEmptyView( show );
   }
   
   
   
   public void showListView( boolean show )
   {
      baseImpl.showListView( show );
   }
   
   
   
   public FragmentActivity getFragmentActivity()
   {
      return (FragmentActivity) getSupportActivity();
   }
   
   
   
   @Override
   public void setArguments( Bundle args )
   {
      super.setArguments( args );
      baseImpl.setArguments( args );
   }
   
   
   
   @Override
   public void onSaveInstanceState( Bundle outState )
   {
      super.onSaveInstanceState( outState );
      baseImpl.onSaveInstanceState( outState );
   }
   
   
   
   @Override
   public final < T > void registerAnnotatedConfiguredInstance( T instance,
                                                                Class< T > clazz )
   {
      baseImpl.registerAnnotatedConfiguredInstance( instance, clazz );
   }
   
   
   
   @Override
   public final Bundle getConfiguration()
   {
      return baseImpl.getConfiguration();
   }
   
   
   
   @Override
   public final void configure( Bundle config )
   {
      baseImpl.configure( config );
   }
   
   
   
   @Override
   public void clearConfiguration()
   {
      baseImpl.clearConfiguration();
   }
   
   
   
   protected void showLoadingSpinner( boolean show )
   {
      loaderImpl.showLoadingSpinner( show );
   }
   
   
   
   public void onSettingsChanged( int which,
                                  HashMap< Integer, Object > oldValues )
   {
      loaderImpl.onSettingsChanged( which, oldValues );
   }
   
   
   
   public int getSettingsMask()
   {
      return 0;
   }
   
   
   
   public boolean hasListAdapter()
   {
      return getListAdapter() != null;
   }
   
   
   
   public final boolean isRespectingContentChanges()
   {
      return loaderImpl.isRespectingContentChanges();
   }
   
   
   
   protected void invalidateOptionsMenu()
   {
      if ( getFragmentActivity() != null )
         getFragmentActivity().invalidateOptionsMenu();
   }
   
   
   
   protected void applyModifications( ContentProviderActionItemList actionItemList,
                                      ApplyChangesInfo applyChangesInfo )
   {
      editImpl.applyModifications( actionItemList, applyChangesInfo );
   }
   
   
   
   public boolean hasRtmWriteAccess()
   {
      return AccountUtils.isWriteableAccess( getFragmentActivity() );
   }
   
   
   
   @Override
   public Loader< D > onCreateLoader( int id, Bundle args )
   {
      return loaderImpl.onCreateLoader( id, args );
   }
   
   
   
   @Override
   public void onLoadFinished( Loader< D > loader, D data )
   {
      loaderImpl.onLoadFinished( loader, data );
   }
   
   
   
   @Override
   public void onLoaderReset( Loader< D > loader )
   {
      loaderImpl.onLoaderReset( loader );
   }
   
   
   
   @Override
   public boolean isReadyToStartLoader()
   {
      return true;
   }
   
   
   
   @Override
   public Bundle getLoaderConfig()
   {
      return getConfiguration();
   }
   
   
   
   @Override
   abstract public Loader< D > newLoaderInstance( int id, Bundle config );
   
   
   
   @Override
   abstract public String getLoaderDataName();
   
   
   
   @Override
   abstract public int getLoaderId();
   
   
   
   @Override
   abstract public ListAdapter createEmptyListAdapter();
   
   
   
   @Override
   abstract public ListAdapter createListAdapterForResult( D result );
}
