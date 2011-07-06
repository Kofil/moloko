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

import android.content.Context;
import android.content.res.Resources;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import dev.drsoran.moloko.R;


public class ActionBarMenuItemView extends ImageView
{
   
   public ActionBarMenuItemView( Context context )
   {
      super( context );
   }
   


   public ActionBarMenuItemView( Context context, AttributeSet attrs )
   {
      super( context, attrs );
   }
   


   public ActionBarMenuItemView( Context context, AttributeSet attrs,
      int defStyle )
   {
      super( context, attrs, defStyle );
   }
   


   public void setIcon( Drawable icon )
   {
      setImageDrawable( icon );
      setColorFilter( new LightingColorFilter( 1,
                                               getContext().getResources()
                                                           .getColor( R.color.app_actionbar_action_button_tint ) ) );
   }
   


   @Override
   public void setLayoutParams( LayoutParams params )
   {
      final Resources resources = getResources();
      params.height = resources.getDimensionPixelSize( R.dimen.app_actionbar_actionItem_heigth );
      params.width = resources.getDimensionPixelSize( R.dimen.app_actionbar_actionItem_width );
      
      super.setLayoutParams( params );
   }
}
