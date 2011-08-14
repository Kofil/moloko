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

package dev.drsoran.moloko.adapters;

import java.util.List;

import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import dev.drsoran.moloko.R;
import dev.drsoran.moloko.util.UIUtils;


public class ActionBarNavigationAdapter extends
         ArrayAdapter< Pair< Integer, String > >
{
   
   public ActionBarNavigationAdapter( Context context,
      List< Pair< Integer, String > > list )
   {
      super( context, 0, list );
   }
   


   @Override
   public View getDropDownView( int position, View convertView, ViewGroup parent )
   {
      if ( convertView == null )
         convertView = View.inflate( getContext(),
                                     R.layout.dropdown_with_icon_1line,
                                     null );
      
      final Pair< Integer, String > item = getItem( position );
      
      return UIUtils.setDropDownItemIconAndText( convertView, item );
   }
   


   @Override
   public View getView( int position, View convertView, ViewGroup parent )
   {
      if ( convertView == null )
         convertView = View.inflate( getContext(),
                                     android.R.layout.simple_spinner_item,
                                     null );
      
      final Pair< Integer, String > item = getItem( position );
      final TextView itemTextView = (TextView) convertView.findViewById( android.R.id.text1 );
      itemTextView.setText( item.second );
      
      return convertView;
   }
}
