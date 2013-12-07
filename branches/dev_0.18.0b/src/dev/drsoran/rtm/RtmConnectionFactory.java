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

package dev.drsoran.rtm;

import dev.drsoran.moloko.ILog;


public class RtmConnectionFactory implements IRtmConnectionFactory
{
   private final ILog log;
   
   private final IConnectionFactory connectionFactory;
   
   private final IRtmRequestLimiter requestLimiter;
   
   
   
   public RtmConnectionFactory( ILog log, IConnectionFactory connectionFactory,
      IRtmRequestLimiter requestLimiter )
   {
      this.log = log;
      this.connectionFactory = connectionFactory;
      this.requestLimiter = requestLimiter;
   }
   
   
   
   @Override
   public IRtmConnection createRtmConnection( RtmClientInfo clientInfo )
   {
      return new RtmConnection( log,
                                connectionFactory,
                                requestLimiter,
                                clientInfo );
   }
}
