/*
 * Copyright (c) 2012 Ronny R�hricht
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

package dev.drsoran.moloko.app.home;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import dev.drsoran.moloko.R;
import dev.drsoran.moloko.app.Intents;
import dev.drsoran.moloko.app.contactslist.ContactsListActivity;
import dev.drsoran.moloko.app.tagcloud.TagCloudActivity;


class HomeAdapter extends BaseAdapter
{
   private final List< IMolokoHomeWidget > widgets = new ArrayList< IMolokoHomeWidget >();
   
   
   
   public HomeAdapter( Context context )
   {
      widgets.add( new LastSyncWidget( context ) );
      
      widgets.add( new CalendarWidget( context,
                                       CalendarWidget.TODAY,
                                       R.string.home_label_today ) );
      
      widgets.add( new CalendarWidget( context,
                                       CalendarWidget.TOMORROW,
                                       R.string.home_label_tomorrow ) );
      
      widgets.add( new OverdueWidget( context ) );
      
      widgets.add( new WidgetWithIcon( context,
                                       R.drawable.ic_home_tag,
                                       R.string.app_tagcloud,
                                       new Intent( context,
                                                   TagCloudActivity.class ) ) );
      
      widgets.add( new WidgetWithIcon( context,
                                       R.drawable.ic_home_user,
                                       R.string.app_contacts,
                                       new Intent( context,
                                                   ContactsListActivity.class ) ) );
      
      widgets.add( new WidgetWithIcon( context,
                                       R.drawable.ic_home_settings,
                                       R.string.app_preferences,
                                       Intents.createOpenPreferencesIntent( context ) ) );
      
      registerDataSetObserver( new DataSetObserver()
      {
         @Override
         public void onChanged()
         {
            updateWidgets();
         }
      } );
   }
   
   
   
   @Override
   public int getCount()
   {
      return widgets.size();
   }
   
   
   
   @Override
   public Object getItem( int position )
   {
      return widgets.get( position );
   }
   
   
   
   public IMolokoHomeWidget getWidget( int position )
   {
      return (IMolokoHomeWidget) getItem( position );
   }
   
   
   
   @Override
   public long getItemId( int position )
   {
      return 0;
   }
   
   
   
   @Override
   public View getView( int position, View convertView, ViewGroup parent )
   {
      return getWidget( position ).getView( convertView );
   }
   
   
   
   public Intent getIntentForWidget( int position )
   {
      return getWidget( position ).getIntent();
   }
   
   
   
   public void updateWidgets()
   {
      for ( IMolokoHomeWidget widget : widgets )
      {
         widget.setDirty();
      }
   }
   
   
   
   public void addWidget( IMolokoHomeWidget widget )
   {
      widgets.add( widget );
      notifyDataSetChanged();
   }
   
   
   
   public void removeWidget( IMolokoHomeWidget widget )
   {
      widget.stop();
      widgets.remove( widget );
      
      notifyDataSetChanged();
   }
   
   
   
   public void stopWidgets()
   {
      for ( IMolokoHomeWidget widget : widgets )
      {
         widget.stop();
      }
   }
}
