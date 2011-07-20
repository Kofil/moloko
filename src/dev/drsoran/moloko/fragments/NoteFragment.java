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

package dev.drsoran.moloko.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mdt.rtm.data.RtmTaskNote;

import dev.drsoran.moloko.IConfigurable;
import dev.drsoran.moloko.R;
import dev.drsoran.moloko.loaders.RtmTaskNoteLoader;
import dev.drsoran.moloko.util.MolokoDateUtils;
import dev.drsoran.moloko.util.UIUtils;


public class NoteFragment extends Fragment implements IConfigurable,
         LoaderCallbacks< RtmTaskNote >
{
   @SuppressWarnings( "unused" )
   private final static String TAG = "Moloko."
      + NoteFragment.class.getSimpleName();
   
   
   public static class Config
   {
      public final static String NOTE = "note";
      
      public final static String NOTE_ID = "note_id";
   }
   
   private final static int NOTE_LOADER_ID = 1;
   
   private Bundle configuration;
   
   private ViewGroup content;
   
   private View loadingSpinner;
   
   private boolean noteNotFound;
   
   

   public final static NoteFragment newInstance( Bundle config )
   {
      final NoteFragment fragment = new NoteFragment();
      
      fragment.setArguments( config );
      
      return fragment;
   }
   


   @Override
   public void onCreate( Bundle savedInstanceState )
   {
      super.onCreate( savedInstanceState );
      configure( getArguments() );
      
      if ( getConfiguredNote() == null )
         getLoaderManager().initLoader( NOTE_LOADER_ID, configuration, this );
   }
   


   @Override
   public View onCreateView( LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState )
   {
      final View fragmentView = inflater.inflate( R.layout.note_fragment,
                                                  container,
                                                  false );
      
      content = (ViewGroup) fragmentView.findViewById( android.R.id.content );
      loadingSpinner = fragmentView.findViewById( R.id.loading_spinner );
      
      if ( noteNotFound )
         showError();
      else if ( getConfiguredNote() != null )
         updateContentWithNote();
      else
         showLoadingSpinner();
      
      return fragmentView;
   }
   


   @Override
   public void configure( Bundle config )
   {
      if ( configuration == null )
         configuration = createDefaultConfiguration();
      
      if ( config != null )
      {
         if ( config.containsKey( Config.NOTE ) )
            configuration.putParcelable( Config.NOTE,
                                         config.getParcelable( Config.NOTE ) );
         if ( config.containsKey( Config.NOTE_ID ) )
            configuration.putString( Config.NOTE_ID,
                                     config.getString( Config.NOTE_ID ) );
      }
   }
   


   @Override
   public Bundle getConfiguration()
   {
      return new Bundle( configuration );
   }
   


   @Override
   public Bundle createDefaultConfiguration()
   {
      return new Bundle();
   }
   


   public RtmTaskNote getConfiguredNote()
   {
      return configuration.getParcelable( Config.NOTE );
   }
   


   public RtmTaskNote getConfiguredNoteAssertNotNull()
   {
      final RtmTaskNote note = getConfiguredNote();
      
      if ( note == null )
         throw new IllegalStateException( "note must not be null" );
      
      return note;
   }
   


   public String getConfiguredNoteId()
   {
      return configuration.getString( Config.NOTE_ID );
   }
   


   private boolean hasViewCreated()
   {
      return content != null;
   }
   


   private void updateContentWithNote()
   {
      if ( hasViewCreated() )
      {
         final RtmTaskNote note = getConfiguredNoteAssertNotNull();
         
         final TextView createdDate = (TextView) content.findViewById( R.id.note_created_date );
         createdDate.setText( MolokoDateUtils.formatDateTime( note.getCreatedDate()
                                                                  .getTime(),
                                                              MolokoDateUtils.FORMAT_WITH_YEAR ) );
         
         if ( !UIUtils.initializeTitleWithTextLayout( content.findViewById( R.id.note ),
                                                      note.getTitle(),
                                                      note.getText() ) )
            throw new AssertionError( "UIUtils.initializeTitleWithTextLayout" );
         
         showView( content, true );
         showView( loadingSpinner, false );
      }
   }
   


   private void showLoadingSpinner()
   {
      showView( content, false );
      showView( loadingSpinner, true );
   }
   


   private void showError()
   {
      if ( hasViewCreated() )
      {
         showView( content, true );
         showView( loadingSpinner, false );
         
         content.removeAllViews();
         UIUtils.initializeErrorWithIcon( getActivity(),
                                          content,
                                          R.string.err_entity_not_found,
                                          getString( R.string.app_note ) );
      }
   }
   


   private void showView( View view, boolean show )
   {
      if ( view != null )
         view.setVisibility( show ? View.VISIBLE : View.GONE );
   }
   


   @Override
   public Loader< RtmTaskNote > onCreateLoader( int id, Bundle args )
   {
      showLoadingSpinner();
      
      return new RtmTaskNoteLoader( getActivity(),
                                    args.getString( Config.NOTE_ID ) );
   }
   


   @Override
   public void onLoadFinished( Loader< RtmTaskNote > loader, RtmTaskNote data )
   {
      noteNotFound = data == null;
      
      if ( noteNotFound )
         showError();
      else
      {
         final Bundle newConfig = getConfiguration();
         newConfig.putParcelable( Config.NOTE, data );
         configure( newConfig );
         
         updateContentWithNote();
      }
   }
   


   @Override
   public void onLoaderReset( Loader< RtmTaskNote > loader )
   {
   }
}
