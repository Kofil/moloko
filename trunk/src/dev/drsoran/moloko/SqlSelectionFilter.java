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

package dev.drsoran.moloko;

import android.os.Parcel;
import android.os.Parcelable;


public class SqlSelectionFilter implements IFilter
{
   public static final Parcelable.Creator< SqlSelectionFilter > CREATOR = new Parcelable.Creator< SqlSelectionFilter >()
   {
      
      public SqlSelectionFilter createFromParcel( Parcel source )
      {
         return new SqlSelectionFilter( source );
      }
      


      public SqlSelectionFilter[] newArray( int size )
      {
         return new SqlSelectionFilter[ size ];
      }
      
   };
   
   private final String selectionSql;
   
   

   public SqlSelectionFilter()
   {
      selectionSql = null;
   }
   


   public SqlSelectionFilter( String selection )
   {
      selectionSql = selection;
   }
   


   public SqlSelectionFilter( Parcel source )
   {
      selectionSql = source.readString();
   }
   


   public String getSqlSelection()
   {
      return selectionSql;
   }
   


   public int describeContents()
   {
      return 0;
   }
   


   public void writeToParcel( Parcel dest, int flags )
   {
      dest.writeString( selectionSql );
   }
}
