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

package dev.drsoran.moloko.connection;

import android.os.Build;
import dev.drsoran.moloko.ILog;
import dev.drsoran.moloko.MolokoApp;
import dev.drsoran.rtm.IConnection;
import dev.drsoran.rtm.IConnectionFactory;


public class DefaultConnectionFactory implements IConnectionFactory
{
   private final ILog log;
   
   private final String userAgent;
   
   
   
   public DefaultConnectionFactory( ILog log, String userAgent )
   {
      this.log = log;
      this.userAgent = userAgent;
   }
   
   
   
   @Override
   public IConnection createConnection( String scheme, String hostname, int port )
   {
      if ( MolokoApp.isApiLevelSupported( Build.VERSION_CODES.GINGERBREAD ) )
      {
         return new HttpUrlConnection( log, scheme, hostname, port );
      }
      
      return new ApacheHttpClientConnection( log,
                                             userAgent,
                                             scheme,
                                             hostname,
                                             port );
   }
}