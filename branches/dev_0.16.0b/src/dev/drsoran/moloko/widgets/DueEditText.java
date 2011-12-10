/* 
 *	Copyright (c) 2011 Ronny R�hricht
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

package dev.drsoran.moloko.widgets;

import java.util.Locale;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.Toast;
import dev.drsoran.moloko.IChangesTarget;
import dev.drsoran.moloko.MolokoApp;
import dev.drsoran.moloko.R;
import dev.drsoran.moloko.grammar.datetime.DateParserFactory;
import dev.drsoran.moloko.util.MolokoCalendar;
import dev.drsoran.moloko.util.MolokoDateUtils;
import dev.drsoran.moloko.util.UIUtils;
import dev.drsoran.moloko.util.parsing.RtmDateTimeParsing;


public class DueEditText extends ClearableEditText
{
   private final static int FORMAT = MolokoDateUtils.FORMAT_NUMERIC
      | MolokoDateUtils.FORMAT_WITH_YEAR;
   
   public final static String EDIT_DUE_TEXT = "edit_due_text";
   
   private MolokoCalendar dueCalendar = getDatelessAndTimelessInstance();
   
   private boolean isSupportingFreeTextInput;
   
   private IChangesTarget changes;
   
   
   
   public DueEditText( Context context )
   {
      this( context, null, android.R.attr.editTextStyle );
   }
   
   
   
   public DueEditText( Context context, AttributeSet attrs )
   {
      this( context, attrs, android.R.attr.editTextStyle );
   }
   
   
   
   public DueEditText( Context context, AttributeSet attrs, int defStyle )
   {
      super( context, attrs, defStyle );
      checkFreeTextInput();
      
      setEnabled( isSupportingFreeTextInput );
   }
   
   
   
   public void setDue( long millis, boolean hasDueTime )
   {
      setDueCalendarByMillis( millis, hasDueTime );
      updateEditDueText();
   }
   
   
   
   public void setDue( String dueString )
   {
      if ( isSupportingFreeTextInput )
      {
         setDueCalendarByParseString( dueString );
         updateEditDueText();
      }
   }
   
   
   
   public void putInitialValue( Bundle initialValues )
   {
      initialValues.putString( EDIT_DUE_TEXT, getText().toString() );
   }
   
   
   
   public void setChangesTarget( IChangesTarget changes )
   {
      this.changes = changes;
   }
   
   
   
   public boolean hasDate()
   {
      return isDueCalendarValid() && dueCalendar.hasDate();
   }
   
   
   
   public boolean validate()
   {
      if ( isSupportingFreeTextInput )
      {
         final MolokoCalendar cal = parseDue( getText().toString() );
         return validateCalendar( cal );
      }
      else
      {
         return true;
      }
   }
   
   
   
   public MolokoCalendar getDueCalendar()
   {
      return dueCalendar;
   }
   
   
   
   @Override
   public void onEditorAction( int actionCode )
   {
      boolean stayInEditText = false;
      
      if ( UIUtils.hasInputCommitted( actionCode ) )
      {
         setDueCalendarByParseString( getText().toString() );
         
         final boolean inputValid = validateCalendar( dueCalendar );
         stayInEditText = !inputValid;
         
         if ( inputValid )
            updateEditDueText();
      }
      
      if ( !stayInEditText )
         super.onEditorAction( actionCode );
   }
   
   
   
   @Override
   protected void onTextChanged( CharSequence text,
                                 int start,
                                 int before,
                                 int after )
   {
      super.onTextChanged( text, start, before, after );
      
      putTextChange();
      
      if ( TextUtils.isEmpty( text ) )
      {
         handleEmptyInputString();
      }
   }
   
   
   
   private void updateEditDueText()
   {
      if ( isDueCalendarValid() )
      {
         if ( !dueCalendar.hasDate() )
         {
            setText( null );
         }
         else if ( dueCalendar.hasTime() )
         {
            setText( MolokoDateUtils.formatDateTime( getContext(),
                                                     dueCalendar.getTimeInMillis(),
                                                     FORMAT ) );
         }
         else
         {
            setText( MolokoDateUtils.formatDate( getContext(),
                                                 dueCalendar.getTimeInMillis(),
                                                 FORMAT ) );
         }
      }
   }
   
   
   
   private MolokoCalendar parseDue( String dueStr )
   {
      if ( TextUtils.isEmpty( dueStr ) )
      {
         return handleEmptyInputString();
      }
      else
      {
         return RtmDateTimeParsing.parseDateTimeSpec( dueStr );
      }
   }
   
   
   
   private MolokoCalendar handleEmptyInputString()
   {
      final MolokoCalendar cal;
      
      if ( isDueCalendarValid() )
      {
         cal = dueCalendar;
         cal.setHasDate( false );
         cal.setHasTime( false );
      }
      else
      {
         cal = getDatelessAndTimelessInstance();
      }
      
      return cal;
   }
   
   
   
   private void setDueCalendarByMillis( long millis, boolean hasDueTime )
   {
      if ( !isDueCalendarValid() )
         dueCalendar = MolokoCalendar.getInstance();
      
      if ( millis != -1 )
      {
         dueCalendar.setTimeInMillis( millis );
         dueCalendar.setHasDate( true );
         dueCalendar.setHasTime( hasDueTime );
      }
   }
   
   
   
   private void setDueCalendarByParseString( String dueString )
   {
      dueCalendar = parseDue( dueString );
   }
   
   
   
   private boolean isDueCalendarValid()
   {
      return dueCalendar != null;
   }
   
   
   
   private boolean validateCalendar( MolokoCalendar cal )
   {
      final boolean valid = cal != null;
      if ( !valid )
      {
         Toast.makeText( getContext(),
                         getContext().getString( R.string.task_edit_validate_due,
                                                 getText() ),
                         Toast.LENGTH_LONG )
              .show();
         
         requestFocus();
      }
      
      return valid;
   }
   
   
   
   private void putTextChange()
   {
      if ( changes != null )
         changes.putChange( EDIT_DUE_TEXT, getText().toString(), String.class );
   }
   
   
   
   private void checkFreeTextInput()
   {
      if ( !isInEditMode() )
      {
         final Locale resLocale = MolokoApp.get( getContext() )
                                           .getActiveResourcesLocale();
         isSupportingFreeTextInput = DateParserFactory.existsDateParserWithMatchingLocale( resLocale );
      }
   }
   
   
   
   private MolokoCalendar getDatelessAndTimelessInstance()
   {
      if ( !isInEditMode() )
         return MolokoCalendar.getDatelessAndTimelessInstance();
      else
         return null;
   }
}
