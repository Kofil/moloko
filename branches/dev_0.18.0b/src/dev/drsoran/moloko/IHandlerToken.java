/* 
 *	Copyright (c) 2012 Ronny R�hricht
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

public interface IHandlerToken
{
   boolean post( Runnable r );
   
   
   
   boolean postAtTime( Runnable r, long uptimeMillis );
   
   
   
   boolean postDelayed( Runnable r, long delayMillis );
   
   
   
   void removeRunnable( Runnable r );
   
   
   
   void sendEmptyMessage( int what );
   
   
   
   void removeMessages( int what );
   
   
   
   void removeRunnablesAndMessages();
   
   
   
   boolean isReleased();
   
   
   
   void release();
}
