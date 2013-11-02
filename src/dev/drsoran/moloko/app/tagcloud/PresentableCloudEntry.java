/* 
 *	Copyright (c) 2013 Ronny R�hricht
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

package dev.drsoran.moloko.app.tagcloud;

import android.widget.Button;
import dev.drsoran.moloko.domain.model.CloudEntry;
import dev.drsoran.moloko.domain.model.CloudEntryType;


abstract class PresentableCloudEntry
{
   private final CloudEntry cloudEntry;
   
   private ITagCloudFragmentListener listener;
   
   
   
   protected PresentableCloudEntry( CloudEntry cloudEntry )
   {
      this.cloudEntry = cloudEntry;
   }
   
   
   
   public String getDisplay()
   {
      return cloudEntry.getDisplay();
   }
   
   
   
   public CloudEntryType getType()
   {
      return cloudEntry.getType();
   }
   
   
   
   public int getCount()
   {
      return cloudEntry.getCount();
   }
   
   
   
   public void setTagCloudFragmentListener( ITagCloudFragmentListener listener )
   {
      this.listener = listener;
   }
   
   
   
   public ITagCloudFragmentListener getTagCloudFragmentListener()
   {
      return listener;
   }
   
   
   
   @Override
   public String toString()
   {
      return cloudEntry.toString();
   }
   
   
   
   public abstract void present( Button button );
}
