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

package dev.drsoran.moloko.adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import dev.drsoran.moloko.IFilter;
import dev.drsoran.moloko.R;
import dev.drsoran.moloko.grammar.RtmSmartFilterLexer;
import dev.drsoran.moloko.util.Strings;
import dev.drsoran.moloko.util.UIUtils;
import dev.drsoran.moloko.util.parsing.RtmSmartFilterParsing;
import dev.drsoran.moloko.util.parsing.RtmSmartFilterToken;
import dev.drsoran.rtm.ListTask;
import dev.drsoran.rtm.RtmSmartFilter;


public class FullDetailedTasksListFragmentAdapter extends
         MinDetailedTasksListFragmentAdapter
{
   private final static String TAG = "Moloko."
      + FullDetailedTasksListFragmentAdapter.class.getName();
   
   public final static int FLAG_SHOW_ALL = 1 << 0;
   
   public final static int FLAG_NO_CLICKABLES = 1 << 1;
   
   private final RtmSmartFilter filter;
   
   private final String[] tagsToRemove;
   
   private final int flags;
   
   private final OnClickListener onClickListener;
   
   
   
   public FullDetailedTasksListFragmentAdapter( Context context, int resourceId )
   {
      this( context,
            resourceId,
            Collections.< ListTask > emptyList(),
            null,
            0,
            null );
   }
   
   
   
   public FullDetailedTasksListFragmentAdapter( Context context,
      int resourceId, List< ListTask > tasks, IFilter filter, int flags,
      OnClickListener onClickListener )
   {
      super( context, resourceId, tasks );
      
      this.flags = flags;
      this.filter = (RtmSmartFilter) ( ( filter instanceof RtmSmartFilter )
                                                                           ? filter
                                                                           : new RtmSmartFilter( Strings.EMPTY_STRING ) );
      
      if ( ( flags & FLAG_SHOW_ALL ) != FLAG_SHOW_ALL )
      {
         final List< RtmSmartFilterToken > tokens = this.filter.getTokens();
         final List< String > tagsToRemove = new ArrayList< String >();
         
         for ( RtmSmartFilterToken token : tokens )
         {
            if ( token.operatorType == RtmSmartFilterLexer.OP_TAG
               && !token.isNegated )
            {
               tagsToRemove.add( token.value );
            }
         }
         
         this.tagsToRemove = new String[ tagsToRemove.size() ];
         tagsToRemove.toArray( this.tagsToRemove );
      }
      else
      {
         this.tagsToRemove = null;
      }
      
      this.onClickListener = onClickListener;
   }
   
   
   
   @Override
   public View getView( int position, View convertView, ViewGroup parent )
   {
      convertView = super.getView( position, convertView, parent );
      
      ViewGroup additionalsLayout;
      TextView listName;
      TextView location;
      ImageView recurrent;
      ImageView hasNotes;
      ImageView postponed;
      
      try
      {
         additionalsLayout = (ViewGroup) convertView.findViewById( R.id.taskslist_listitem_additionals_container );
         listName = (TextView) convertView.findViewById( R.id.taskslist_listitem_btn_list_name );
         location = (TextView) convertView.findViewById( R.id.taskslist_listitem_location );
         recurrent = (ImageView) convertView.findViewById( R.id.taskslist_listitem_recurrent );
         hasNotes = (ImageView) convertView.findViewById( R.id.taskslist_listitem_has_notes );
         postponed = (ImageView) convertView.findViewById( R.id.taskslist_listitem_postponed );
      }
      catch ( ClassCastException e )
      {
         Log.e( TAG, "Invalid layout spec.", e );
         throw e;
      }
      
      final ListTask task = getItem( position );
      
      if ( task.getRecurrence() != null )
         recurrent.setVisibility( View.VISIBLE );
      else
         recurrent.setVisibility( View.GONE );
      
      if ( task.getNumberOfNotes() > 0 )
         hasNotes.setVisibility( View.VISIBLE );
      else
         hasNotes.setVisibility( View.GONE );
      
      if ( task.getPosponed() > 0 )
         postponed.setVisibility( View.VISIBLE );
      else
         postponed.setVisibility( View.GONE );
      
      setListName( listName, task );
      
      setTags( additionalsLayout, task );
      
      setLocation( location, task );
      
      return convertView;
   }
   
   
   
   private final void setListName( TextView view, ListTask task )
   {
      if ( ( flags & FLAG_SHOW_ALL ) == FLAG_SHOW_ALL
         || !RtmSmartFilterParsing.hasOperatorAndValue( filter.getTokens(),
                                                        RtmSmartFilterLexer.OP_LIST,
                                                        task.getListName(),
                                                        false ) )
      {
         view.setVisibility( View.VISIBLE );
         view.setText( task.getListName() );
         
         if ( ( flags & FLAG_NO_CLICKABLES ) == FLAG_NO_CLICKABLES )
            view.setClickable( false );
         else
            view.setOnClickListener( onClickListener );
      }
      else
         view.setVisibility( View.GONE );
   }
   
   
   
   private void setLocation( TextView view, ListTask task )
   {
      // If the task has no location
      if ( ( flags & FLAG_SHOW_ALL ) != FLAG_SHOW_ALL
         && ( TextUtils.isEmpty( task.getLocationName() ) || RtmSmartFilterParsing.hasOperatorAndValue( filter.getTokens(),
                                                                                                        RtmSmartFilterLexer.OP_LOCATION,
                                                                                                        task.getLocationName(),
                                                                                                        false ) ) )
      {
         view.setVisibility( View.GONE );
      }
      else
      {
         view.setVisibility( View.VISIBLE );
         view.setText( task.getLocationName() );
         
         if ( ( flags & FLAG_NO_CLICKABLES ) == FLAG_NO_CLICKABLES )
            view.setClickable( false );
         else
            view.setOnClickListener( onClickListener );
      }
   }
   
   
   
   private void setTags( ViewGroup tagsLayout, ListTask task )
   {
      final Bundle tagsConfig = new Bundle();
      
      if ( tagsToRemove != null && tagsToRemove.length > 0 )
         tagsConfig.putStringArray( UIUtils.REMOVE_TAGS_EQUALS, tagsToRemove );
      
      if ( ( flags & FLAG_NO_CLICKABLES ) == FLAG_NO_CLICKABLES )
         tagsConfig.putBoolean( UIUtils.DISABLE_ALL_TAGS, Boolean.TRUE );
      
      UIUtils.inflateTags( getContext(),
                           tagsLayout,
                           task.getTags(),
                           tagsConfig,
                           onClickListener );
   }
}
