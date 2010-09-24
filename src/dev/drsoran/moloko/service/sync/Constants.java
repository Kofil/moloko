/*
Copyright (c) 2010 Ronny R�hricht   

This file is part of Moloko.

Moloko is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Moloko is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Moloko.  If not, see <http://www.gnu.org/licenses/>.

Contributors:
	Ronny R�hricht - implementation
*/

package dev.drsoran.moloko.service.sync;

public final class Constants
{
   public final static String SYNC_EXTRAS_ONLY_SETTINGS = "only_settings";
   
   public static final int SYNC_OBSERVER_TYPE_SETTINGS = 1 << 0;
   
   public static final int SYNC_OBSERVER_TYPE_PENDING = 1 << 1;
   
   public static final int SYNC_OBSERVER_TYPE_ACTIVE = 1 << 2;
   
   public static final int SYNC_OBSERVER_TYPE_STATUS = 1 << 3;
   
   public static final int SYNC_OBSERVER_TYPE_ALL = 0x7fffffff;
}
