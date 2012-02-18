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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.app.SupportActivity;
import android.support.v4.content.Loader;
import dev.drsoran.moloko.IConfigurable;
import dev.drsoran.moloko.annotations.InstanceState;
import dev.drsoran.moloko.fragments.listeners.ILoaderFragmentListener;
import dev.drsoran.moloko.fragments.listeners.NullLoaderFragmentListener;
import dev.drsoran.moloko.loaders.AbstractLoader;


public abstract class LoaderFragmentImplBase< D > implements
         LoaderCallbacks< D >
{
   protected static interface Support< D >
   {
      int getLoaderId();
      
      
      
      String getLoaderDataName();
      
      
      
      Loader< D > newLoaderInstance( int id, Bundle config );
   }
   
   private final Fragment fragment;
   
   private final IConfigurable config;
   
   private final Support< D > support;
   
   private ILoaderFragmentListener loaderListener;
   
   @InstanceState( key = "loader_respect_content_changes", defaultValue = "false" )
   private boolean respectContentChanges;
   
   
   
   @SuppressWarnings( "unchecked" )
   protected LoaderFragmentImplBase( Fragment fragment, IConfigurable config )
   {
      this.fragment = fragment;
      this.config = config;
      this.support = (Support< D >) fragment;
   }
   
   
   
   public void onCreate( Bundle savedInstanceState )
   {
      config.registerAnnotatedConfiguredInstance( this, savedInstanceState );
   }
   
   
   
   public void onAttach( SupportActivity activity )
   {
      if ( activity instanceof ILoaderFragmentListener )
         loaderListener = (ILoaderFragmentListener) activity;
      else
         loaderListener = new NullLoaderFragmentListener();
   }
   
   
   
   public void onDetach()
   {
      loaderListener = null;
   }
   
   
   
   public void setRespectContentChanges( boolean respect )
   {
      respectContentChanges = respect;
      
      if ( fragment.isAdded() )
      {
         final Loader< D > loader = fragment.getLoaderManager()
                                            .getLoader( support.getLoaderId() );
         
         if ( loader instanceof AbstractLoader< ? > )
            ( (AbstractLoader< ? >) loader ).setRespectContentChanges( respect );
      }
   }
   
   
   
   public boolean isRespectingContentChanges()
   {
      return respectContentChanges;
   }
   
   
   
   @Override
   public Loader< D > onCreateLoader( int id, Bundle args )
   {
      final Loader< D > loader = support.newLoaderInstance( id, args );
      
      if ( loader instanceof AbstractLoader< ? > )
         ( (AbstractLoader< ? >) loader ).setRespectContentChanges( isRespectingContentChanges() );
      
      loaderListener.onFragmentLoadStarted( fragment.getId(), fragment.getTag() );
      
      return loader;
   }
   
   
   
   @Override
   public void onLoadFinished( Loader< D > loader, D data )
   {
      loaderListener.onFragmentLoadFinished( fragment.getId(),
                                             fragment.getTag(),
                                             data != null );
   }
   
   
   
   @Override
   public void onLoaderReset( Loader< D > loader )
   {
   }
   
   
   
   public void startLoader()
   {
      startLoaderWithConfiguration( config.getConfiguration() );
   }
   
   
   
   public void startLoaderWithConfiguration( Bundle config )
   {
      fragment.getLoaderManager().initLoader( support.getLoaderId(),
                                              config,
                                              this );
   }
}
