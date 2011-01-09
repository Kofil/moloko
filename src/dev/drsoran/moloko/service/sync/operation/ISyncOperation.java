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

package dev.drsoran.moloko.service.sync.operation;

import android.content.SyncResult;


public interface ISyncOperation
{
   public boolean execute( SyncResult result );
   
   
   public class Op
   {
      public final static int NOOP = 0;
      
      

      public static String toString( int op )
      {
         switch ( op )
         {
            case NOOP:
               return "NOOP";
            default :
               return "UNKNOWN";
         }
      }
   }
}
