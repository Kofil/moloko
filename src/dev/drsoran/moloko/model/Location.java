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

package dev.drsoran.moloko.model;

public class Location
{
   private final long id;
   
   private final String name;
   
   private final float longitude;
   
   private final float latitude;
   
   private final String address;
   
   private final int zoom;
   
   
   
   public Location( long id, String name, float longitude, float latitude,
      String address, int zoom )
   {
      this.id = id;
      this.name = name;
      this.longitude = longitude;
      this.latitude = latitude;
      this.address = address;
      this.zoom = zoom;
   }
   
   
   
   public long getId()
   {
      return id;
   }
   
   
   
   public String getName()
   {
      return name;
   }
   
   
   
   public float getLongitude()
   {
      return longitude;
   }
   
   
   
   public float getLatitude()
   {
      return latitude;
   }
   
   
   
   public String getAddress()
   {
      return address;
   }
   
   
   
   public int getZoom()
   {
      return zoom;
   }
}
