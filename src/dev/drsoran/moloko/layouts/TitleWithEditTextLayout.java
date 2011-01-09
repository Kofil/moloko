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

package dev.drsoran.moloko.layouts;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.EditText;


public class TitleWithEditTextLayout extends TitleWithViewLayout
{
   public TitleWithEditTextLayout( Context context, AttributeSet attrs )
   {
      super( context, attrs );
      initView( context, attrs, getViewContainer() );
   }
   


   public TitleWithEditTextLayout( Context context, AttributeSet attrs,
      ViewGroup root )
   {
      super( context, attrs, root );
      initView( context, attrs, getViewContainer() );
   }
   


   public void setText( CharSequence value )
   {
      ( (EditText) findViewById( VIEW_ID ) ).setText( value );
   }
   


   private void initView( Context context,
                          AttributeSet attrs,
                          ViewGroup container )
   {
      final EditText text = new EditText( context, attrs );
      text.setLayoutParams( generateDefaultLayoutParams() );
      text.setId( VIEW_ID );
      
      container.addView( text );
   }
}
