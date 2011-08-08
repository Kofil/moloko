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

package dev.drsoran.moloko.util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.support.v4.view.MenuItem.OnMenuItemClickListener;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.Time;
import android.text.method.LinkMovementMethod;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.Pair;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.BufferType;
import dev.drsoran.moloko.R;
import dev.drsoran.moloko.sync.util.SyncUtils;
import dev.drsoran.moloko.widgets.ActionBarMenuItemView;
import dev.drsoran.rtm.RtmListWithTaskCount;
import dev.drsoran.rtm.Task;


public final class UIUtils
{
   /**
    * If a tag has been clicked then it makes no sense to click it in the result again.
    */
   public static final String DISABLE_TAGS_EQUALS = "disable_tags_equals";
   
   public static final String REMOVE_TAGS_EQUALS = "remove_tags_equals";
   
   public static final String DISABLE_ALL_TAGS = "disable_all_tags";
   
   public static final String REMOVE_ALL_TAGS = "remove_all_tags";
   
   public static final int[] CHECKED_STATE_SET =
   { android.R.attr.state_checked };
   
   

   private UIUtils()
   {
      throw new AssertionError( "This class should not be instantiated." );
   }
   
   
   public static abstract class AfterTextChangedWatcher implements TextWatcher
   {
      abstract public void afterTextChanged( Editable s );
      


      public void beforeTextChanged( CharSequence s,
                                     int start,
                                     int count,
                                     int after )
      {
      }
      


      public void onTextChanged( CharSequence s,
                                 int start,
                                 int before,
                                 int count )
      {
      }
   }
   
   

   public final static String getTrimmedText( TextView textView )
   {
      return textView.getText().toString().trim();
   }
   


   public final static CharSequence getTrimmedSequence( TextView textView )
   {
      return textView.getText().toString().trim();
   }
   


   @Deprecated
   public final static void setTitle( Activity activity, String text )
   {
      // final View titleBarText = activity.findViewById( R.id.app_actionbar_title );
      //
      // if ( titleBarText instanceof TextView )
      // {
      // ( (TextView) titleBarText ).setText( text );
      // }
      // else
      // {
      // activity.setTitle( text );
      // }
   }
   


   @Deprecated
   public final static void setTitle( Activity activity, int resId )
   {
      setTitle( activity, activity.getResources().getString( resId ) );
   }
   


   @Deprecated
   public final static void setTitle( Activity activity,
                                      String text,
                                      int iconResId )
   {
      // final View view = activity.findViewById( R.id.app_actionbar_title );
      //
      // if ( view instanceof TextView )
      // {
      // final TextView titleBarText = (TextView) view;
      //
      // titleBarText.setText( text );
      //
      // BitmapDrawable bitmap = null;
      //
      // if ( iconResId != -1 )
      // {
      // bitmap = new BitmapDrawable( activity.getResources()
      // .openRawResource( iconResId ) );
      //
      // final int iconSize = activity.getResources()
      // .getDimensionPixelSize( R.dimen.app_actionbar_text_size );
      //
      // bitmap.setBounds( 0, 0, iconSize, iconSize );
      // }
      //
      // titleBarText.setCompoundDrawables( bitmap, null, null, null );
      // }
      // else
      // {
      // activity.setTitle( text );
      // }
   }
   


   public final static void hideSoftInput( View view )
   {
      if ( view != null )
      {
         final InputMethodManager imm = (InputMethodManager) view.getContext()
                                                                 .getSystemService( Context.INPUT_METHOD_SERVICE );
         imm.hideSoftInputFromWindow( view.getWindowToken(), 0 );
      }
   }
   


   public final static View setDropDownItemIconAndText( View dropDownView,
                                                        Pair< Integer, String > iconWithText )
   {
      return setDropDownItemIconAndText( dropDownView,
                                         iconWithText.first != null
                                                                   ? iconWithText.first.intValue()
                                                                   : -1,
                                         iconWithText.second );
      
   }
   


   public final static View setDropDownItemIconAndText( View dropDownView,
                                                        int iconId,
                                                        String text )
   {
      final TextView textView = (TextView) dropDownView.findViewById( android.R.id.text1 );
      textView.setText( text );
      
      final ImageView icon = (ImageView) dropDownView.findViewById( android.R.id.icon1 );
      if ( iconId != -1 )
      {
         icon.setVisibility( View.VISIBLE );
         icon.setImageResource( iconId );
      }
      else
         icon.setVisibility( View.GONE );
      
      return dropDownView;
   }
   


   public final static void setTaskDescription( TextView view,
                                                Task task,
                                                Time timeBase )
   {
      if ( timeBase == null )
      {
         timeBase = MolokoDateUtils.newTime();
      }
      
      view.setText( task.getName() );
      
      boolean setTypeFace = false;
      
      // description
      if ( task.getDue() != null )
      {
         final long dueDateMillis = task.getDue().getTime();
         
         // Make bold if the task is today
         if ( MolokoDateUtils.isToday( dueDateMillis ) )
         {
            view.setTypeface( Typeface.DEFAULT_BOLD );
            view.setText( task.getName() );
            setTypeFace = true;
         }
         
         // Make underline and bold if overdue
         else
         {
            final Time dueTime = MolokoDateUtils.newTime( task.getDue()
                                                              .getTime() );
            
            if ( timeBase.after( dueTime ) )
            {
               final SpannableString content = new SpannableString( task.getName() );
               
               content.setSpan( new UnderlineSpan(), 0, content.length(), 0 );
               view.setTypeface( Typeface.DEFAULT_BOLD );
               view.setText( content );
               setTypeFace = true;
            }
         }
      }
      
      if ( !setTypeFace )
         view.setTypeface( Typeface.DEFAULT );
   }
   


   public final static void setListTasksCountView( TextView tasksCount,
                                                   RtmListWithTaskCount list )
   {
      final int numTasks = list.getIncompleteTaskCount();
      tasksCount.setText( String.valueOf( numTasks ) );
      
      if ( list.hasSmartFilter() )
      {
         if ( list.isSmartFilterValid() )
         {
            tasksCount.setBackgroundResource( R.drawable.tasklists_group_numtasks_bgnd_smart );
         }
         else
         {
            tasksCount.setBackgroundResource( R.drawable.tasklists_group_numtasks_bgnd_smart_fail );
            tasksCount.setText( "?" );
         }
      }
      else
      {
         tasksCount.setBackgroundResource( R.drawable.tasklists_group_numtasks_bgnd );
      }
   }
   


   public final static void inflateTags( Context context,
                                         ViewGroup container,
                                         Collection< String > tags,
                                         Bundle configuration,
                                         OnClickListener listener ) throws InflateException
   {
      if ( configuration == null )
         configuration = new Bundle();
      
      final int tagPos = getTaggedViewPos( container, "tag_name" );
      
      if ( tagPos != -1 )
         container.removeViews( tagPos, container.getChildCount() - tagPos );
      
      // inflate the stub and add tags
      if ( tags.size() > 0 && !configuration.containsKey( REMOVE_ALL_TAGS ) )
      {
         try
         {
            if ( configuration.containsKey( DISABLE_ALL_TAGS ) )
            {
               for ( String tagText : tags )
               {
                  final TextView tagView = (TextView) View.inflate( context,
                                                                    R.layout.tag_button,
                                                                    null );
                  tagView.setClickable( false );
                  tagView.setText( tagText );
                  container.addView( tagView );
               }
            }
            else
            {
               final String[] tagsToDisable = configuration.getStringArray( DISABLE_TAGS_EQUALS );
               final String[] tagsToRemove = configuration.getStringArray( REMOVE_TAGS_EQUALS );
               
               for ( String tagText : tags )
               {
                  boolean remove = false;
                  
                  if ( tagsToRemove != null )
                     for ( int i = 0; i < tagsToRemove.length && !remove; i++ )
                     {
                        remove = tagsToRemove[ i ].equalsIgnoreCase( tagText );
                     }
                  
                  if ( !remove )
                  {
                     boolean disable = false;
                     
                     if ( tagsToDisable != null )
                        for ( int i = 0; i < tagsToDisable.length && !disable; i++ )
                        {
                           disable = tagsToDisable[ i ].equalsIgnoreCase( tagText );
                        }
                     
                     final TextView tagView = (TextView) View.inflate( context,
                                                                       R.layout.tag_button,
                                                                       null );
                     tagView.setClickable( !disable );
                     tagView.setText( tagText );
                     container.addView( tagView );
                     
                     if ( !disable && listener != null )
                        tagView.setOnClickListener( listener );
                  }
               }
            }
         }
         catch ( Throwable e )
         {
            throw new InflateException( e );
         }
      }
      
      if ( container.getChildCount() > 0 )
         container.setVisibility( View.VISIBLE );
      else
         container.setVisibility( View.GONE );
   }
   


   public final static void setPriorityColor( View view, Task task )
   {
      switch ( task.getPriority() )
      {
         case High:
            view.setBackgroundResource( R.color.priority_1 );
            break;
         case Medium:
            view.setBackgroundResource( R.color.priority_2 );
            break;
         case Low:
            view.setBackgroundResource( R.color.priority_3 );
            break;
         case None:
         default :
            view.setBackgroundResource( R.color.priority_none );
            break;
      }
   }
   


   public final static StringBuilder appendAtNewLine( StringBuilder stringBuilder,
                                                      String string )
   {
      if ( stringBuilder.length() > 0 )
         stringBuilder.append( "\n" );
      
      stringBuilder.append( string );
      
      return stringBuilder;
   }
   


   public final static boolean initializeTitleWithViewLayout( View layout,
                                                              String title )
   {
      boolean ok = layout != null;
      
      if ( ok )
         try
         {
            final TextView titleView = (TextView) layout.findViewById( R.id.title_with_view_title );
            
            if ( TextUtils.isEmpty( title ) )
            {
               titleView.setVisibility( View.GONE );
            }
            else
            {
               titleView.setVisibility( View.VISIBLE );
               titleView.setText( title );
            }
         }
         catch ( ClassCastException e )
         {
            ok = false;
         }
      
      return ok;
   }
   


   public final static boolean initializeTitleWithTextLayout( View layout,
                                                              String title,
                                                              String text )
   {
      boolean ok = layout != null
         && initializeTitleWithViewLayout( layout, title );
      
      if ( ok )
         try
         {
            final TextView textView = (TextView) layout.findViewById( R.id.title_with_text_text );
            
            if ( TextUtils.isEmpty( text ) )
            {
               textView.setVisibility( View.GONE );
            }
            else
            {
               textView.setVisibility( View.VISIBLE );
               textView.setText( text );
            }
         }
         catch ( ClassCastException e )
         {
            ok = false;
         }
      
      return ok;
   }
   


   public final static boolean initializeTitleWithTextLayout( View layout,
                                                              String title,
                                                              Spannable text )
   {
      boolean ok = initializeTitleWithTextLayout( layout,
                                                  title,
                                                  text.toString() );
      
      if ( ok && text != null )
         try
         {
            final TextView textView = (TextView) layout.findViewById( R.id.title_with_text_text );
            applySpannable( textView, text );
         }
         catch ( ClassCastException e )
         {
            ok = false;
         }
      
      return ok;
   }
   


   @Deprecated
   public final static void initializeErrorWithIcon( Activity activity,
                                                     int resId,
                                                     Object... params )
   {
      activity.setContentView( R.layout.error_with_icon );
      final TextView text = (TextView) activity.findViewById( R.id.title_with_text_text );
      final String msg = activity.getResources().getString( resId, params );
      text.setText( msg );
      
      Log.e( LogUtils.toTag( Activity.class ), msg );
   }
   


   public final static void initializeErrorWithIcon( Context context,
                                                     ViewGroup container,
                                                     int resId,
                                                     Object... params )
   {
      final View view = LayoutInflater.from( context )
                                      .inflate( R.layout.error_with_icon,
                                                container,
                                                true );
      final TextView text = (TextView) view.findViewById( R.id.title_with_text_text );
      final String msg = context.getResources().getString( resId, params );
      text.setText( msg );
      
      Log.e( LogUtils.toTag( Context.class ), msg );
   }
   


   public final static void applySpannable( TextView textView, Spannable text )
   {
      textView.setMovementMethod( LinkMovementMethod.getInstance() );
      textView.setText( text, BufferType.SPANNABLE );
   }
   


   public final static MenuItem addSyncMenuItem( final Context context,
                                                 Menu menu,
                                                 int itemId,
                                                 int menuOrder,
                                                 int showAsActionFlags )
   {
      final MenuItem menuItem = addOptionalMenuItem( context,
                                                     menu,
                                                     itemId,
                                                     context.getString( R.string.phr_do_sync ),
                                                     menuOrder,
                                                     Menu.NONE,
                                                     R.drawable.ic_menu_refresh,
                                                     showAsActionFlags,
                                                     true );
      
      menuItem.setOnMenuItemClickListener( new OnMenuItemClickListener()
      {
         @Override
         public boolean onMenuItemClick( MenuItem item )
         {
            SyncUtils.requestManualSync( context );
            return true;
         }
      } );
      
      return menuItem;
   }
   


   public final static MenuItem addOptionalMenuItem( Context context,
                                                     Menu menu,
                                                     int itemId,
                                                     String title,
                                                     int order,
                                                     int groupId,
                                                     int iconId,
                                                     int showAsActionFlags,
                                                     boolean show )
   {
      return addOptionalMenuItem( context,
                                  menu,
                                  itemId,
                                  title,
                                  order,
                                  groupId,
                                  iconId,
                                  showAsActionFlags,
                                  null,
                                  show );
   }
   


   public final static MenuItem addOptionalMenuItem( Context context,
                                                     Menu menu,
                                                     int itemId,
                                                     String title,
                                                     int order,
                                                     int groupId,
                                                     int iconId,
                                                     int showAsActionFlags,
                                                     Intent intent,
                                                     boolean show )
   {
      MenuItem item = null;
      
      if ( show )
      {
         item = menu.findItem( itemId );
         
         if ( item == null )
         {
            item = menu.add( groupId, itemId, order, title );
            
            if ( iconId != -1 )
               item.setIcon( iconId );
         }
         
         item.setTitle( title );
         
         if ( intent != null )
            item.setIntent( intent );
         
         item.setShowAsAction( showAsActionFlags );
         
         if ( showAsActionFlags != MenuItem.SHOW_AS_ACTION_NEVER )
         {
            final ActionBarMenuItemView actionBarMenuItemView = new ActionBarMenuItemView( context );
            actionBarMenuItemView.setIcon( item.getIcon() );
            
            item.setActionView( actionBarMenuItemView );
         }
      }
      else
      {
         menu.removeItem( itemId );
      }
      
      return item;
   }
   


   public final static void addOptionsMenuIntent( Context context,
                                                  Menu menu,
                                                  int id,
                                                  Class< ? > activityClass )
   {
      addOptionsMenuIntent( context, menu, id, new Intent( context,
                                                           activityClass ) );
   }
   


   public final static void addOptionsMenuIntent( Context context,
                                                  Menu menu,
                                                  int id,
                                                  Intent intent )
   {
      final MenuItem item = menu.findItem( id );
      
      if ( item != null )
         item.setIntent( intent );
   }
   


   public final static String convertSource( Context context, String source )
   {
      if ( source.equalsIgnoreCase( "js" ) )
         return "web";
      
      if ( source.equalsIgnoreCase( "api" ) )
         return context.getString( R.string.app_name );
      
      return source;
   }
   


   public final static Pair< Integer, Integer > getTaggedViewRange( ViewGroup container,
                                                                    String tag )
   {
      int tagStart = -1;
      int tagEnd = -1;
      
      final int cnt = container.getChildCount();
      
      for ( int i = 0; i < cnt && ( tagStart == -1 || tagEnd == -1 ); ++i )
      {
         if ( tagStart == -1 && tag.equals( container.getChildAt( i ).getTag() ) )
            tagStart = i;
         else if ( tagStart != -1
            && !tag.equals( container.getChildAt( i ).getTag() ) )
            tagEnd = i;
      }
      
      if ( tagStart != -1 )
         return Pair.create( tagStart, tagEnd != -1 ? tagEnd : cnt );
      else
         return Pair.create( 0, 0 );
   }
   


   public final static int getTaggedViewPos( ViewGroup container, String tag )
   {
      int pos = -1;
      
      for ( int i = 0, cnt = container.getChildCount(); i < cnt && pos == -1; ++i )
      {
         if ( tag.equals( container.getChildAt( i ).getTag() ) )
            pos = i;
      }
      
      return pos;
   }
   


   public final static void removeTaggedViews( ViewGroup container, String tag )
   {
      List< View > views = null;
      
      for ( int i = 0, cnt = container.getChildCount(); i < cnt; ++i )
      {
         final View v = container.getChildAt( i );
         if ( v != null && tag.equals( v.getTag() ) )
         {
            if ( views == null )
               views = new LinkedList< View >();
            
            views.add( v );
         }
      }
      
      if ( views != null )
         for ( View view : views )
            container.removeView( view );
   }
   


   public final static Dialog newCancelWithChangesDialog( Activity activity,
                                                          Runnable yesAction,
                                                          Runnable noAction )
   {
      return newDialogWithActions( activity,
                                   activity.getString( R.string.phr_edit_dlg_cancel ),
                                   android.R.string.yes,
                                   android.R.string.no,
                                   yesAction,
                                   noAction );
   }
   


   public final static Dialog newApplyChangesDialog( Activity activity,
                                                     Runnable yesAction,
                                                     Runnable noAction )
   {
      return newDialogWithActions( activity,
                                   activity.getString( R.string.phr_edit_dlg_done ),
                                   android.R.string.yes,
                                   android.R.string.no,
                                   yesAction,
                                   noAction );
   }
   


   public final static Dialog newDeleteElementDialog( Activity activity,
                                                      String elementName,
                                                      Runnable yesAction,
                                                      Runnable noAction )
   {
      return newDialogWithActions( activity,
                                   activity.getString( R.string.phr_delete_with_name,
                                                       elementName )
                                      + "?",
                                   R.string.btn_delete,
                                   R.string.btn_cancel,
                                   yesAction,
                                   noAction );
   }
   


   public final static Dialog newDialogWithActions( final Activity activity,
                                                    String message,
                                                    int positiveId,
                                                    int negativeId,
                                                    final Runnable yesAction,
                                                    final Runnable noAction )
   {
      final AlertDialog.Builder builder = new AlertDialog.Builder( activity ).setMessage( message );
      
      if ( positiveId != -1 )
         builder.setPositiveButton( positiveId,
                                    yesAction != null
                                                     ? new DialogInterface.OnClickListener()
                                                     {
                                                        @Override
                                                        public void onClick( DialogInterface dialog,
                                                                             int which )
                                                        {
                                                           yesAction.run();
                                                        }
                                                     } : null );
      if ( negativeId != -1 )
         builder.setNegativeButton( negativeId,
                                    noAction != null
                                                    ? new DialogInterface.OnClickListener()
                                                    {
                                                       @Override
                                                       public void onClick( DialogInterface dialog,
                                                                            int which )
                                                       {
                                                          noAction.run();
                                                       }
                                                    } : null );
      final Dialog dialog = builder.create();
      dialog.setOwnerActivity( activity );
      
      return dialog;
   };
   


   public final static boolean reportStatus( Context context,
                                             int resIdOk,
                                             int resIdFailed,
                                             boolean ok )
   {
      Toast.makeText( context, ok ? resIdOk : resIdFailed, Toast.LENGTH_LONG )
           .show();
      
      return ok;
   }
}