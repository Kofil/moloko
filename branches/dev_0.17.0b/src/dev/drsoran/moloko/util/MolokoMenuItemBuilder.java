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

package dev.drsoran.moloko.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;

import dev.drsoran.moloko.R;
import dev.drsoran.moloko.sync.util.SyncUtils;


public final class MolokoMenuItemBuilder
{
   private int itemId;
   
   private CharSequence title;
   
   private int order = MenuCategory.NONE;
   
   private int groupId = Menu.NONE;
   
   private int iconId = -1;
   
   private int showAsActionFlags = MenuItem.SHOW_AS_ACTION_NEVER;
   
   private Intent intent;
   
   private boolean show = true;
   
   private OnMenuItemClickListener onClickListener;
   
   
   
   public MolokoMenuItemBuilder setItemId( int itemId )
   {
      this.itemId = itemId;
      return this;
   }
   
   
   
   public MolokoMenuItemBuilder setTitle( CharSequence title )
   {
      this.title = title;
      return this;
   }
   
   
   
   public MolokoMenuItemBuilder setOrder( int order )
   {
      this.order = order;
      return this;
   }
   
   
   
   public MolokoMenuItemBuilder setGroupId( int groupId )
   {
      this.groupId = groupId;
      return this;
   }
   
   
   
   public MolokoMenuItemBuilder setIconId( int iconId )
   {
      this.iconId = iconId;
      return this;
   }
   
   
   
   public MolokoMenuItemBuilder setShowAsActionFlags( int showAsActionFlags )
   {
      this.showAsActionFlags = showAsActionFlags;
      return this;
   }
   
   
   
   public MolokoMenuItemBuilder setIntent( Intent intent )
   {
      this.intent = intent;
      return this;
   }
   
   
   
   public MolokoMenuItemBuilder setShow( boolean show )
   {
      this.show = show;
      return this;
   }
   
   
   
   public MolokoMenuItemBuilder setOnClickListener( OnMenuItemClickListener onClickListener )
   {
      this.onClickListener = onClickListener;
      return this;
   }
   
   
   
   public void build( Menu menu )
   {
      if ( show )
      {
         MenuItem item = menu.findItem( itemId );
         
         if ( item == null )
         {
            item = menu.add( groupId, itemId, order, title );
            
            if ( iconId != -1 )
               item.setIcon( iconId );
         }
         
         item.setTitle( title );
         item.setIntent( intent );
         item.setOnMenuItemClickListener( onClickListener );
         item.setShowAsAction( showAsActionFlags );
      }
      else
      {
         menu.removeItem( itemId );
      }
   }
   
   
   
   @Deprecated
   public final static MolokoMenuItemBuilder newSyncMenuItem( final FragmentActivity activity )
   {
      return new MolokoMenuItemBuilder().setItemId( R.id.menu_sync )
                                        .setTitle( activity.getString( R.string.phr_do_sync ) )
                                        .setIconId( R.drawable.ic_menu_refresh )
                                        .setOnClickListener( new OnMenuItemClickListener()
                                        {
                                           @Override
                                           public boolean onMenuItemClick( MenuItem item )
                                           {
                                              SyncUtils.requestManualSync( activity );
                                              return true;
                                           }
                                        } );
   }
   
   
   
   @Deprecated
   public final static MolokoMenuItemBuilder newSearchMenuItem( final Activity activity )
   {
      return new MolokoMenuItemBuilder().setItemId( R.id.menu_search_tasks )
                                        .setTitle( activity.getString( R.string.search_hint ) )
                                        .setIconId( R.drawable.ic_menu_search )
                                        .setOnClickListener( new OnMenuItemClickListener()
                                        {
                                           @Override
                                           public boolean onMenuItemClick( MenuItem item )
                                           {
                                              return activity.onSearchRequested();
                                           }
                                        } );
   }
   
   
   
   @Deprecated
   public final static MolokoMenuItemBuilder newSettingsMenuItem( Context context )
   {
      return new MolokoMenuItemBuilder().setItemId( R.id.menu_settings )
                                        .setTitle( context.getString( R.string.phr_settings ) )
                                        .setIconId( R.drawable.ic_menu_settings )
                                        .setIntent( Intents.createOpenPreferencesIntent( context ) );
   }
}
