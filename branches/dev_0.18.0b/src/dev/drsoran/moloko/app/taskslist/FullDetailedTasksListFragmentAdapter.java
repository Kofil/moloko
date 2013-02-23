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

package dev.drsoran.moloko.app.taskslist;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import dev.drsoran.moloko.IFilter;
import dev.drsoran.moloko.R;
import dev.drsoran.moloko.grammar.RtmSmartFilterLexer;
import dev.drsoran.moloko.ui.UiUtils;
import dev.drsoran.moloko.ui.widgets.MolokoListView;
import dev.drsoran.moloko.ui.widgets.SimpleLineView;
import dev.drsoran.moloko.util.Strings;
import dev.drsoran.moloko.util.parsing.RtmSmartFilterParsing;
import dev.drsoran.moloko.util.parsing.RtmSmartFilterToken;
import dev.drsoran.rtm.RtmSmartFilter;
import dev.drsoran.rtm.Task;


public class FullDetailedTasksListFragmentAdapter extends
         AbstractTasksListFragmentAdapter
{
   public final static int FLAG_SHOW_ALL = 1 << 0;
   
   private final RtmSmartFilter filter;
   
   private final String[] tagsToRemove;
   
   private final int flags;
   
   
   
   public FullDetailedTasksListFragmentAdapter( MolokoListView listView,
      IFilter filter, int flags )
   {
      super( listView,
             R.layout.fulldetailed_taskslist_listitem,
             R.layout.mindetailed_selectable_taskslist_listitem );
      
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
   }
   
   
   
   @Override
   public View getView( int position, View convertView, ViewGroup parent )
   {
      convertView = super.getView( position, convertView, parent );
      
      if ( !isInMultiChoiceModalActionMode() )
      {
         final SimpleLineView priority = (SimpleLineView) convertView.findViewById( R.id.taskslist_listitem_priority );
         final ViewGroup additionalsLayout = (ViewGroup) convertView.findViewById( R.id.taskslist_listitem_additionals_container );
         final TextView listName = (TextView) convertView.findViewById( R.id.taskslist_listitem_btn_list_name );
         final TextView location = (TextView) convertView.findViewById( R.id.taskslist_listitem_location );
         final ImageView recurrent = (ImageView) convertView.findViewById( R.id.taskslist_listitem_recurrent );
         final ImageView hasNotes = (ImageView) convertView.findViewById( R.id.taskslist_listitem_has_notes );
         final ImageView postponed = (ImageView) convertView.findViewById( R.id.taskslist_listitem_postponed );
         
         final Task task = getItem( position );
         
         UiUtils.setPriorityColor( getContext(), priority, task );
         
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
      }
      
      return convertView;
   }
   
   
   
   private final void setListName( TextView view, Task task )
   {
      if ( ( flags & FLAG_SHOW_ALL ) == FLAG_SHOW_ALL
         || !RtmSmartFilterParsing.hasOperatorAndValue( filter.getTokens(),
                                                        RtmSmartFilterLexer.OP_LIST,
                                                        task.getListName(),
                                                        false ) )
      {
         view.setVisibility( View.VISIBLE );
         view.setText( task.getListName() );
      }
      else
      {
         view.setVisibility( View.GONE );
      }
   }
   
   
   
   private void setLocation( TextView view, Task task )
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
      }
   }
   
   
   
   private void setTags( ViewGroup tagsLayout, Task task )
   {
      final Bundle tagsConfig = new Bundle();
      
      if ( tagsToRemove != null && tagsToRemove.length > 0 )
         tagsConfig.putStringArray( UiUtils.REMOVE_TAGS_EQUALS, tagsToRemove );
      
      UiUtils.inflateTags( getContext(), tagsLayout, task.getTags(), tagsConfig );
   }
   
   
   
   @Override
   protected boolean mustSwitchLayout( View convertView )
   {
      if ( isInMultiChoiceModalActionMode() )
      {
         return convertView.findViewById( R.id.taskslist_selectable_mindetailed_listitem ) == null;
      }
      else
      {
         return convertView.findViewById( R.id.taskslist_fulldetailed_listitem ) == null;
      }
   }
}
