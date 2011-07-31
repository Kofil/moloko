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

package dev.drsoran.moloko.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.mdt.rtm.data.RtmTaskNote;

import dev.drsoran.moloko.R;
import dev.drsoran.moloko.sync.util.SyncUtils;
import dev.drsoran.moloko.util.NoteEditUtils;
import dev.drsoran.moloko.util.Strings;
import dev.drsoran.moloko.util.UIUtils;


public class NoteEditActivity extends AbstractNoteActivity
{
   @SuppressWarnings( "unused" )
   private final static String TAG = "Moloko."
      + NoteEditActivity.class.getSimpleName();
   
   public final static int REQ_EDIT_NOTE = 1;
   
   public final static int RESULT_EDIT_NOTE_FAILED = 1 << 0;
   
   public final static int RESULT_EDIT_NOTE_OK = 1 << 8;
   
   public final static int RESULT_EDIT_NOTE_CHANGED = 1 << 9
      | RESULT_EDIT_NOTE_OK;
   
   private EditText title;
   
   private EditText text;
   
   
   
   @Override
   protected void onCreate( Bundle savedInstanceState )
   {
      super.onCreate( savedInstanceState );
      
      setContentView( R.layout.note_edit_activity );
      
      title = (EditText) findViewById( R.id.note_edit_title );
      text = (EditText) findViewById( R.id.note_edit_text );
      
      final Intent intent = getIntent();
      
      if ( !intent.getAction().equals( Intent.ACTION_EDIT ) )
      {
         UIUtils.initializeErrorWithIcon( this,
                                          R.string.err_unsupported_intent_action,
                                          intent.getAction() );
      }
   }
   
   
   
   public void onDone( View v )
   {
      if ( hasChanged() )
      {
         UIUtils.newApplyChangesDialog( this, new Runnable()
         {
            public void run()
            {
               final boolean ok = NoteEditUtils.setNoteTitleAndText( NoteEditActivity.this,
                                                                     getNote().getId(),
                                                                     Strings.nullIfEmpty( UIUtils.getTrimmedText( title ) ),
                                                                     Strings.nullIfEmpty( UIUtils.getTrimmedText( text ) ) );
               setResult( ok ? RESULT_EDIT_NOTE_CHANGED
                            : RESULT_EDIT_NOTE_FAILED );
               finish();
               
            }
         },
                                        null )
                .show();
      }
      else
      {
         setResult( RESULT_EDIT_NOTE_OK );
         finish();
      }
   }
   
   
   
   public void onCancel( View v )
   {
      if ( hasChanged() )
      {
         UIUtils.newCancelWithChangesDialog( this, new Runnable()
         {
            public void run()
            {
               setResult( RESULT_CANCELED );
               finish();
            }
         }, null ).show();
      }
      else
      {
         setResult( RESULT_CANCELED );
         finish();
      }
   }
   
   
   
   @Override
   public void onBackPressed()
   {
      onCancel( null );
   }
   
   
   
   @Override
   protected void displayNote( RtmTaskNote note )
   {
      super.displayNote( note );
      
      title.setText( note.getTitle() );
      text.setText( note.getText() );
   }
   
   
   
   private boolean hasChanged()
   {
      final RtmTaskNote note = getNote();
      
      return SyncUtils.hasChanged( note.getTitle(),
                                   UIUtils.getTrimmedText( title ) )
         || SyncUtils.hasChanged( note.getText(), UIUtils.getTrimmedText( text ) );
   }
}
