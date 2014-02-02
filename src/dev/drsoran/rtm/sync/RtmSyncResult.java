/* 
 *	Copyright (c) 2014 Ronny R�hricht
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

package dev.drsoran.rtm.sync;

import java.util.Collection;
import java.util.Collections;

import dev.drsoran.rtm.RtmTransaction;


public class RtmSyncResult
{
   private final Exception exception;
   
   private final Collection< RtmTransaction > transactions;
   
   
   
   private RtmSyncResult( Exception exception,
      Collection< RtmTransaction > transactions )
   {
      if ( transactions == null )
      {
         throw new IllegalArgumentException( "transactions" );
      }
      
      this.exception = exception;
      this.transactions = transactions;
   }
   
   
   
   public Exception getException()
   {
      return exception;
   }
   
   
   
   public Collection< RtmTransaction > getTransactions()
   {
      return transactions;
   }
   
   
   
   public boolean hasFailed()
   {
      return !hasSucceeded();
   }
   
   
   
   public boolean hasSucceeded()
   {
      return exception == null;
   }
   
   
   
   public static RtmSyncResult newSucceeded( Collection< RtmTransaction > transactions )
   {
      return new RtmSyncResult( null, transactions );
   }
   
   
   
   public static RtmSyncResult newSucceeded()
   {
      return newSucceeded( Collections.< RtmTransaction > emptyList() );
   }
   
   
   
   public static RtmSyncResult newFailed( Exception exception )
   {
      return newFailed( exception, Collections.< RtmTransaction > emptyList() );
   }
   
   
   
   public static RtmSyncResult newFailed( Exception exception,
                                          Collection< RtmTransaction > transactions )
   {
      if ( exception == null )
      {
         throw new IllegalArgumentException( "exception" );
      }
      
      return new RtmSyncResult( exception, transactions );
   }
}
